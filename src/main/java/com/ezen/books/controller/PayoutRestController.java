package com.ezen.books.controller;

import com.ezen.books.domain.*;
import com.ezen.books.service.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RequestMapping("/payment/payout/*")
@RestController
@RequiredArgsConstructor
@PropertySource("classpath:application-secrets.properties")
// NOTE: 결제 api 관련 비즈니스 로직을 포함하여 /payout에서 발생하는 모든 RESTful api를 처리함.
public class PayoutRestController {

    @Autowired
    private Environment env;

    private String iamportApiKey;
    private String iamportApiSecret;

    private final IamportClient iamportClient;
    private PayoutService payoutService;

    private final MemberService memberService;
    private final GradeService gradeService;
    private final PointService pointService;
    private final CouponService couponService;

    @Autowired
    public PayoutRestController(
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret, PayoutService payoutService, MemberService memberService, GradeService gradeService, PointService pointService, CouponService couponService) {
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
        this.payoutService = payoutService;
        this.memberService = memberService;
        this.gradeService = gradeService;
        this.pointService = pointService;
        this.couponService = couponService;
        this.iamportClient = new IamportClient(iamportApiKey, iamportApiSecret);
    }

    @PostMapping("/register-address")
    public String registerDefaultAddress(@RequestBody AddressVO addressData) {
        log.info(" >>> PaymentRestController: registerDefaultAddress start.");
        log.info("The address data from the client: {}", addressData);

        int isDone = payoutService.registerDefaultAddress(addressData);

        return (0 < isDone) ? "1" : "0";
    }

    @PostMapping("/prepare")
    public ResponseEntity<Map<String, String>> sendDataToClient(@RequestBody String pgData) {
        // The received pgVal: {"pg":"kakaopay"}
        log.info("The received pgVal: {}", pgData);

        String pgVal = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode pgNode = objectMapper.readTree(pgData);
            pgVal = pgNode.get("pg").asText();
        } catch (Exception e) {
            log.info("Exception occurred. Content: {}", e);
        }

        Map<String, String> pgMap = getChannelKeyAndMethod(pgVal);
//        pgMap.put("merchantUid", (UUID.randomUUID()).toString());

        log.info("The channel key: {}", pgMap.get("channelKey"));
        log.info("The pay method: {}", pgMap.get("payMethod"));
//        log.info("The merchant uid: {}", pgMap.get("merchantUid"));

        return ResponseEntity.ok(pgMap);
    }

    @PostMapping("/result")
    public String getPaymentResultFromClient(HttpServletRequest request)
            throws IOException, URISyntaxException, InterruptedException {
        log.info(" >>> PaymentRestController: getPaymentResultFromClient start.");

        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("The result body: {}", requestBody);

        // 단건 조회만 해볼 거라서 imp_uid를 제외한 모든 결제 데이터를 받지 않았다.
        String impUid = "";
        String amount = "";

        // Jackson을 이용하여 JSON 문자열 파싱
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode bodyNode = objectMapper.readTree(requestBody);

            impUid = bodyNode.get("imp_uid").asText();
            amount = bodyNode.get("paid_amount").asText();
        } catch (Exception e) {
            log.info("Exception occurred. content: {}", e);
        }

        // 단건조회 메서드 호출
        boolean checkResult = checkSinglePayment(impUid, amount);
        return (checkResult) ? "1" : "0";
    }

    @PostMapping("/preserve-orders")
    public ResponseEntity<String> saveOrdersToServer(@RequestBody OrdersVO ordersVO,
                                                     HttpSession session) {
        log.info(" >>> PaymentRestController: saveOrdersToServer start.");
        // ordersVO: OrdersVO(orno=nobody_1734487688880, mno=1, status=completed, totalPrice=60000, orderAt=null, isPickup=N)
        log.info("ordersVO: {}", ordersVO);

        /* yh-------------- */
        String orno = (String) session.getAttribute("orno");
        if(orno == null || orno.isEmpty()){
            orno = UUID.randomUUID().toString();
            session.setAttribute("orno",orno);
        }
        ordersVO.setOrno(orno);
        /* ---------------- */

        int isDone = payoutService.saveOrdersToServer(ordersVO);

        /* yh-------------- */
        if(isDone > 0){
            earnPoints(ordersVO);

            int totalPrice = calculateTotalPrice(ordersVO);

            Map<String, Object> response = new HashMap<>();
            response.put("orno", ordersVO.getOrno()); // 주문 번호
            response.put("totalPrice", totalPrice);  // 계산된 총 금액
        /* ---------------- */
            return new ResponseEntity<>("1", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/preserve-order-detail")
    public ResponseEntity<String> saveOrderDetailToServer(@RequestBody String orderDetailArr) {
        log.info(" >>> PaymentRestController: saveOrderDetailToServer start.");
        // The orderDetailArr from JavaScript: [{"orno":"nobody_1734504395832","prno":"1","bookQty":"5","price":"10000"},{"orno":"nobody_1734504395832","prno":"3","bookQty":"2","price":"30000"}]
        log.info("The orderDetailArr from JavaScript: {}", orderDetailArr);

        // 가져온 orderDetailArr 문자열을 List<OrderDetailVO>로 파싱
        List<OrderDetailVO> orderDetailList = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            orderDetailList = objectMapper.readValue(orderDetailArr, new TypeReference<>() {});
        } catch (Exception e) {
            log.info("Error during parsing orderDetailArr. Content: {}", e);
        }

        // orderDetailList의 각 OrderDetailVO들을 하나씩 DB에 저장
        List<Integer> resultList = new ArrayList<>();
        for(OrderDetailVO orderDetail : orderDetailList) {
            int isDone = payoutService.saveOrderDetailToServer(orderDetail);
            resultList.add(isDone);
        }

        return (resultList.stream().allMatch(n -> n == 1)) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/preserve-payment")
    public ResponseEntity<String> savePaymentToServer(@RequestBody PaymentVO paymentData) {
        log.info(" >>> PaymentRestController: savePaymentToServer start.");
        log.info("The payment data from the client: {}", paymentData);

        int isDone = payoutService.savePaymentToServer(paymentData);

        return (isDone > 0) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/remove-cart")
    public ResponseEntity<String> removeCartToServer(@RequestBody String cartListData) {
        log.info(" >>> PaymentRestController: removeCartToServer start.");
        // The cartListData from the client: [{"mno":"15","prno":"83"},{"mno":"15","prno":"82"},{"mno":"15","prno":"65"}]
        log.info("The cartListData from the client: {}", cartListData);

        List<CartVO> cartList =  parseCartVoArray(cartListData);
        log.info("Is cartList List: {}", cartList instanceof List<CartVO>);
        List<Integer> resultList = new ArrayList<>();

        for(CartVO cartVO : cartList) {
            long mno = cartVO.getMno();
            long prno = cartVO.getPrno();
            log.info("mno: ", mno);
            log.info("prno: ", prno);
            resultList.add(payoutService.removeCartToServer(mno, prno));
        }

        return (resultList.stream().allMatch(n -> n == 1)) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/preserve-delivery")
    public ResponseEntity<String> saveDeliveryToServer(@RequestBody DeliveryVO deliveryData) {
        log.info(" >>> PaymentRestController: saveDeliveryToServer start.");
        log.info("The deliveryData from the client: {}", deliveryData);

        int isDone = payoutService.saveDeliveryToServer(deliveryData);

        return (isDone > 0) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/preserve-pickup")
    public ResponseEntity<String> savePickupToServer(@RequestBody PickUpVO pickupData) {
        log.info(" >>> PaymentRestController: savePickupToServer start.");
        log.info("The pickupData from the client: {}", pickupData);

        int isDone = payoutService.savePickupToServer(pickupData);

        return (isDone > 0) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException {
        boolean verifyResult = payoutService.checkSinglePayment(impUid, amount);
        return verifyResult;
    }

    private Map<String, String> getChannelKeyAndMethod(String pgVal) {
        String channelKey = "";
        String payMethod = "";

        switch (pgVal) {
            case "kakaopay":
                channelKey = "iamport_kakaopay_general_payment_channel_key";
                payMethod = "card";
                break;
            case "danal":
                channelKey = "iamport_danal_phone_payment_channel_key";
                payMethod = "phone";
                break;
            case "tosspay_v2":
                channelKey = "iamport_tosspay_v2_general_payment_channel_key";
                payMethod = "tosspay";
                break;
            case "payco":
                channelKey = "iamport_payco_payment_channel_key";
                payMethod = "card";
                break;
            case "html5_inicis":
                channelKey = "iamport_kginicis_general_payment_channel_key";
                payMethod = "card";
                break;
            default:
                channelKey = "Unknown channelKey";
                payMethod = "Unknown payMethod";
        }

        // NOTE: Argument 'env. getProperty(channelKey)' might be null
        Map<String, String> pgMap = new HashMap<>();
        pgMap.put("channelKey", env.getProperty(channelKey));
        pgMap.put("payMethod", payMethod);

        return pgMap;
    }

    // NOTE: 똑같은 메서드가 PaymentController에도 있음.
    private List<CartVO> parseCartVoArray(String cartListData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // json String을 List<CartVO>로 변환
            List<CartVO> cartList = objectMapper.readValue(cartListData, new TypeReference<>() {});

            // 결과 확인하고 싶을 때 주석 풀어서 볼 것.
//            for (int i = 0; i < cartList.size(); i++) {
//                log.info("The mno from cartList: {}", cartList.get(i).getMno());
//                log.info("The prno from cartList: {}", cartList.get(i).getPrno());
//                log.info("The bookQty from cartList: {}", cartList.get(i).getBookQty());
//            }

            return cartList;
        } catch (Exception e) {
            log.info("Exception occurred. Content: {}", e);
            return null;
        }
    }

    /* yh-------------- */
    private void earnPoints(OrdersVO ordersVO){
        MemberVO memberVO = memberService.getMemberById(ordersVO.getMno());

        GradeVO gradeVO = gradeService.getGradeByGno(memberVO.getGno());
        double pointRate = gradeVO.getPointRate();
        log.info(">>>>>> PointRate : {}", pointRate);

        int earnedPoints = (int) (ordersVO.getTotalPrice() * pointRate);
        int balancePoint = pointService.getBalance(memberVO.getMno());

        // earned와 used가 모두 0이면 저장하지 않음
        if (earnedPoints == 0 && balancePoint == 0) {
            log.info(">> Earned points are 0 and balance is 0, skipping point save.");
            return;
        }

        PointsVO pointsVO = new PointsVO();
        pointsVO.setMno(memberVO.getMno());
        pointsVO.setOrno(ordersVO.getOrno());
        pointsVO.setEarned(earnedPoints);
        pointsVO.setBalance(balancePoint + earnedPoints);
        pointsVO.setRegAt(new Date());

        pointService.savePoint(pointsVO);

        log.info(">> 적립된 포인트 {}", earnedPoints);
        log.info(">> 현재 포인트 {}", balancePoint);
    }

    private int calculateTotalPrice(OrdersVO ordersVO){
        return ordersVO.getTotalPrice();
    }


    @PostMapping("/use-points")
    public ResponseEntity<Map<String, Object>> usePoints(@RequestBody Map<String, Object> requestData,
                                                         HttpSession session) {
        int usedPoints = (Integer) requestData.get("usedPoints");
        log.info(">>> 사용할 포인트 {}", usedPoints);

        String mnoNo = (String) requestData.get("mno");
        Long mno = Long.parseLong(mnoNo);

        String orno = (String) session.getAttribute("orno");

        MemberVO memberVO = memberService.getMemberById(mno);

        int balancePoint = pointService.getBalance(memberVO.getMno());

        if (usedPoints > balancePoint) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Insufficient points."));
        }

        // earned와 used가 모두 0일 경우 저장하지 않음
        if (usedPoints == 0 && balancePoint == 0) {
            log.info(">> Earned and used points are both 0, skipping point usage save.");
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("newPointsBalance", balancePoint); // 잔액 그대로 반환
            return ResponseEntity.ok(response);
        }

        int remainingPoints = balancePoint - usedPoints;

        PointsVO pointsVO = new PointsVO();
        pointsVO.setMno(memberVO.getMno());
        pointsVO.setOrno(orno);
        pointsVO.setUsed(usedPoints);
        pointsVO.setBalance(remainingPoints);
        pointsVO.setRegAt(new Date());
        pointService.savePoint(pointsVO);


        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newPointsBalance", remainingPoints);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/use-coupon")
    public ResponseEntity<Map<String, Object>> useCoupon(@RequestBody Map<String, Object> requestData,
                                                         HttpSession session) {
        String mnoNo = (String) requestData.get("mno");
        Long mno = Long.parseLong(mnoNo); // 회원 번호
        String orno = (String) session.getAttribute("orno"); // 주문 번호

        Object cnoObj = requestData.get("cno");
        Long cno = null;
        if (cnoObj != null) {
            cno = Long.parseLong(cnoObj.toString()); // 쿠폰 번호
        }

        if (cno == null || cno == 0) {
            // 쿠폰을 사용하지 않음 처리
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "쿠폰 미적용");
            return ResponseEntity.ok(response);
        }

        // 쿠폰 로그 확인
        CouponLogVO couponLogVO = couponService.getCouponLogByMnoAndCno(mno, cno);
        if (couponLogVO != null && couponLogVO.getStatus().equals("사용 완료")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Coupon already used."));
        }

        // 쿠폰 사용 내역 기록
        CouponLogVO newCouponLog = new CouponLogVO();
        newCouponLog.setMno(mno);
        newCouponLog.setCno(cno);
        newCouponLog.setOrno(orno);
        newCouponLog.setStatus("사용 완료");
        newCouponLog.setUsedAt(new Date());
        couponService.updateCouponLog(newCouponLog);

        // 쿠폰 할인 금액 반환
        CouponVO couponVO = couponService.getCouponByCno(cno);
        int couponDiscount = couponVO.getDisAmount();

        // 응답 데이터
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("discountAmount", couponDiscount);

        return ResponseEntity.ok(response);
    }

    /* ---------------- */

}

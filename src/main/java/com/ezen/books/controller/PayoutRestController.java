package com.ezen.books.controller;

import com.ezen.books.domain.*;
import com.ezen.books.service.PayoutService;
import com.ezen.books.service.PayoutServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    public PayoutRestController(
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret, PayoutService payoutService) {
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
        this.payoutService = payoutService;
        this.iamportClient = new IamportClient(iamportApiKey, iamportApiSecret);
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
        pgMap.put("merchantUid", (UUID.randomUUID()).toString());

        log.info("The channel key: {}", pgMap.get("channelKey"));
        log.info("The pay method: {}", pgMap.get("payMethod"));
        log.info("The merchant uid: {}", pgMap.get("merchantUid"));

        return ResponseEntity.ok(pgMap);
    }

    @PostMapping("/result")
    public String getPaymentResultFromClient(HttpServletRequest request)
            throws IOException, URISyntaxException, InterruptedException {
        log.info(" >>> PaymentRestController: getPaymentResultFromClient start.");

        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("The result body: {}", requestBody);

        // 일단 단건 조회만 해볼 거라서 imp_uid를 제외한 모든 결제 데이터를 받지 않았다.
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
    public ResponseEntity<String> saveOrdersToServer(@RequestBody OrdersVO ordersVO) {
        log.info(" >>> PaymentRestController: saveOrdersToServer start.");
        // ordersVO: OrdersVO(orno=nobody_1734487688880, mno=1, status=completed, totalPrice=60000, orderAt=null, isPickup=N)
        log.info("ordersVO: {}", ordersVO);
        int isDone = payoutService.saveOrdersToServer(ordersVO);

        return (0 < isDone) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/preserve-order-detail")
    public ResponseEntity<String> saveOrderDetailToServer(@RequestBody String orderDetailArr) {
        log.info(" >>> PaymentRestController: saveOrderDetailToServer start.");
        // The orderDetailArr from JavaScript: [{"orno":"nobody_1734504395832","prno":"1","bookQty":"5","price":"10000"},{"orno":"nobody_1734504395832","prno":"3","bookQty":"2","price":"30000"}]
        log.info("The orderDetailArr from JavaScript: {}", orderDetailArr);

        List<OrderDetailVO> orderDetailList = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            orderDetailList = objectMapper.readValue(orderDetailArr, new TypeReference<>() {});
        } catch (Exception e) {
            log.info("Error during parsing orderDetailArr. Content: {}", e);
        }

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

//    @PostMapping("/remove-cart")
//    public ResponseEntity<String> removeCartToServer(@RequestBody String mnoData) {
//        log.info(" >>> PaymentRestController: removeCartToServer start.");
//        // The mnoData from the client: {"mno":"1"}
//        log.info("The mnoData from the client: {}", mnoData);
//
//        long mno = 0L;
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode mnoNode = objectMapper.readTree(mnoData);
//            mno = mnoNode.get("mno").asLong();
//        } catch (Exception e) {
//            log.info("Error during parsing mnoNode. Content: {}", e);
//        }
//
//        int isDone = payoutService.removeCartToServer(mno);
//
//        return (isDone == 1) ?
//                new ResponseEntity<>("1", HttpStatus.OK) :
//                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

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
            resultList.add(payoutService.removeCartToServer(mno, prno));
        }

        return (resultList.stream().allMatch(n -> n == 1)) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException {
        boolean verifyResult = payoutService.checkSinglePayment(impUid, amount);
        return verifyResult;
    }

    private Map<String, String> getChannelKeyAndMethod(String pgVal) {
        String channelKey = null;
        String payMethod = null;

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

}

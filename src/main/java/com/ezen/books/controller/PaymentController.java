package com.ezen.books.controller;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.CartProductDTO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.service.*;
import com.ezen.books.domain.*;
import com.ezen.books.service.CartService;
import com.ezen.books.service.OrderListService;
import com.ezen.books.service.PayoutService;
import com.ezen.books.service.PayoutServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Slf4j
@RequestMapping("/payment/*")
@Controller
@RequiredArgsConstructor
// NOTE: /payout, /shoppingcart 화면 띄우기 관련 로직은 여기서 처리함.
public class PaymentController {

    private final PayoutService payoutService;
    private final CartService cartService;
    private List<CartVO> cartList;
    private List<OfflineStoreVO> storeList;

    private final PointService pointService;
    private final MemberService memberService;
    private final CouponService couponService;

    @PostMapping("/header-cart")
    @ResponseBody
    public int provideCartAmount(@RequestBody long mno) {
        log.info(" >>> PaymentController: provideCartAmount start.");
        log.info("The mno from the client: {}", mno);

        int cartAmount = cartService.getCartAmount(mno);
        return cartAmount;
    }

    // /cart의 "주문하기", "픽업하기"를 눌렀을 시 cart list를 구성하는 메서드
    @PostMapping("/provide-cart-list/{pathString}")
    @ResponseBody
    public String getCartList(Model model,
                              @RequestBody String cartListData,
                              @PathVariable("pathString") String pathString) {
        log.info(" >>> PaymentController: getCartList start.");
        // cartList: [{"mno":"1","prno":"1","bookQty":"5"},{"mno":"1","prno":"2","bookQty":"1"}]
        log.info(" >>> getCartList: cartList: {}", cartListData);

        List<CartVO> cartList =  parseCartVoArray(cartListData);

        this.cartList = cartList;

        if(pathString.equals("orderBtn")) {
            return "1";
        } else if (pathString.equals("pickUpBtn")) {
            return "2";
        } else {
            return "-1";
        }
    }

    // /product/detail에서 "바로구매", "바로픽업" 버튼을 눌렀을 시 단일 cart 정보를 cart list에 넣어주는 메서드
    @PostMapping("/single-purchase")
    @ResponseBody
    public String prepareCartList(@RequestBody CartVO cartData) {
        log.info(" >>> PaymentController: prepareCartList start.");

        List<CartVO> cartList = new ArrayList<>();
        cartList.add(cartData);
        this.cartList = cartList;
        return "1";
    }

    @GetMapping("/cart")
    public String showCartItems(PagingVO pagingVO, Model model) {
        log.info(" >>> PaymentController: showCartItems start.");

        // 로그인 된 사용자의 mno를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();
        log.info("The mno of showCartItems: {}", mno);

        List<CartVO> cartList = cartService.getAllCartItems(mno);
        log.info(" >>> cartList: {}", cartList);

        // 페이지네이션 영역
        int totalCount = payoutService.getTotalCount(pagingVO, mno);
        PagingHandler ph = new PagingHandler(pagingVO, totalCount);
        log.info("The ph: {}", ph);

        List<CartProductDTO> cartProductList = buildCartProductList(cartList);
        log.info(" >>> showCartItems: cartProductList: {}", cartProductList);

        // 사용자의 장바구니가 비었는지 아닌지 판별하기 위한 값
        boolean isCartEmpty;
        if(cartProductList.isEmpty()) {
            isCartEmpty = true;
        } else {
            isCartEmpty = false;
        }

        model.addAttribute("mno", mno);
        model.addAttribute("cartProductList", cartProductList);
        model.addAttribute("isCartEmpty", isCartEmpty);
        model.addAttribute("ph", ph);
        return "/payment/cart";
    }

    @GetMapping("/pickUp")
    public String goToPickUP(Model model) {
        // (차민주)장바구니에서 가져온 정보를 통해 픽업 가능한 매장 추출하기
        // cartList 예시 > [CartVO(mno=1, prno=251, bookQty=1), CartVO(mno=1, prno=250, bookQty=2)]
        List<OfflineStoreVO> storeList = payoutService.getPickupStores(cartList);
        log.info(">>>> storeList > {}", storeList);
        this.storeList = storeList;

        model.addAttribute("storeList", storeList);
        model.addAttribute("cartList", cartList);
        return "/payment/pickUp";
    }

    @GetMapping("/payout/{osno}")
    public String goToPayout(Model model, @PathVariable("osno") long osno,
                             HttpSession session) {
        log.info(" >>> PaymentController: goToPayout start.");
        log.info(">>>> cartList > {}", cartList);

        List<CartProductDTO> cartProductList = buildCartProductList(cartList);

        // mno는 단독적으로 쓰이는 경우가 많아 편의상 따로 빼서 model로 보냄.
        long mno = cartProductList.get(0).getCartVO().getMno();
        log.info("mno: {}", mno);

        /* yh-------------- */
        String orno = UUID.randomUUID().toString();
        log.info(">>>>>> 주문 번호(orno) 생성 : {}", orno);
        session.setAttribute("orno", orno);
        /* ---------------- */

        // 주문/결제 페이지에서 보여줄 사용자의 기본 배송지를 가져옴
        AddressVO defaultAddress = getDefaultAddress(mno);
        log.info("The default address: {}", defaultAddress);

        // 기본 배송지가 null인지 아닌지 가리는 값
        boolean isDefaultAddrNull = defaultAddress == null;

        // 포인트와 쿠폰이 도입되어 merchant_uid(UUID)를 여기서 보내는 것으로 바뀜.
        String merchantUid = orno;

        PickUpVO pickUpVO = new PickUpVO();
        OfflineStoreVO offlineStoreVO = new OfflineStoreVO();
        // (차민주-픽업)**
        if(osno != 0){
            pickUpVO.setOsno(osno);
            pickUpVO.setStatus("주문완료");
            pickUpVO.setOrno(orno);
            log.info(">>>> pickUpVO > {}", pickUpVO);
            // 픽업이라면? 루트 짜서 delevery 대신 pickUp 테이블 데이터 저장하기
            // insert into pickUp (osno, orno, status) values (#{osno}, #{orno}, #{status});
            // 밑에 Y로 바꾸는 것도 osno가 0이 아니라면으로 조건 걸어서 부여할것

            // 픽업이면 화면에 띄울 offlineStore를 가져옴
            offlineStoreVO = payoutService.getStoreInfo(osno);
        }

        // Path variable로 받은 osno가 0이면 배달로, 0이 아니면 픽업 결제로 설정함.
        String isPickup = "";
        if(osno == 0) {
            isPickup = "N";
        } else {
            isPickup = "Y";
        }

        // 기본 배송지가 있냐 없냐에 따라 보낼 값이 달라짐. defaultAddress가 null이면 /payout에서 에러 뜸.
        Map<String, Object> modelAttrs = new HashMap<>();
        if(defaultAddress == null) {
            modelAttrs = Map.of(
                    "mno", mno,
                    "cartProductList", cartProductList,
                    "defaultAddress", "empty",
                    "isDefaultAddrNull", isDefaultAddrNull,
                    "isPickup", isPickup,
                    "merchantUid", merchantUid,
                    "pickUpVO", pickUpVO,
                    "offlineStoreVO", offlineStoreVO
            );
        } else {
            modelAttrs = Map.of(
                    "mno", mno,
                    "cartProductList", cartProductList,
                    "defaultAddress", defaultAddress,
                    "isDefaultAddrNull", isDefaultAddrNull,
                    "isPickup", isPickup,
                    "merchantUid", merchantUid,
                    "pickUpVO", pickUpVO,
                    "offlineStoreVO", offlineStoreVO
            );
        }

        log.info("CartProductList from PaymentController: {}", cartProductList);
        model.addAllAttributes(modelAttrs);

        /* yh-------------- */
        int balancePoint = pointService.getBalance(mno);
        model.addAttribute("balancePoint", balancePoint);
        /* ---------------- */

        /* yh-------------- */
        model.addAttribute("mno", mno);

        // 사용자 쿠폰 목록 조회
        List<CouponLogVO> couponList = couponService.findMemberCoupons(mno);
        model.addAttribute("coupons", couponList);
        log.info("쿠폰들 {}", couponList);
        /* ---------------- */

        return "/payment/payout";
    }

    /* yh-------------- */
    @GetMapping("/start-checkout")
    public ResponseEntity<Map<String, String>> startCheckout(HttpSession session){
        String orno = (String) session.getAttribute("orno");
        if(orno == null){
            orno = UUID.randomUUID().toString();
            session.setAttribute("orno", orno);
        }

        Map<String, String> response = new HashMap<>();
        response.put("orno", orno);

        return ResponseEntity.ok(response);
    }
    /* ---------------- */


    @GetMapping("/go-to-index")
    public String goToIndex() {
        return "redirect:/";
    }

    private AddressVO getDefaultAddress(long mno) {
        AddressVO defaultAddress = payoutService.getDefaultAddress(mno);
        return defaultAddress;
    }

    private List<CartProductDTO> buildCartProductList(List<CartVO> cartList) {
        // cart 테이블의 prno를 바탕으로 product 테이블에서 title, discount, discount_rate를 가져옴
        List<ProductVO> productList = new ArrayList<>();
        for(CartVO cartVO : cartList) {
            productList.add(cartService.getProductInfo(cartVO.getPrno()));
        }
        log.info(" >>> productList: {}", productList);

        // productList에 realPrice 설정: ProductController의 /detail에 있는 공식 사용
        for(ProductVO productVO : productList) {
            double discountedPrice = productVO.getDiscount() * (1 - productVO.getDiscountRate() / 100.0);
            int roundedPrice = (int) Math.floor(discountedPrice / 10.0) * 10;
            productVO.setRealPrice(roundedPrice);
        }

        // CartDTO와 그에 맞는 ProductDTO를 담을 List<CartProductDTO> 생성
        List<CartProductDTO> cartProductList = new ArrayList<>();
        for(int i=0; i < cartList.size(); i++) {
            CartProductDTO temp = new CartProductDTO();
            temp.setCartVO(cartList.get(i));
            temp.setProductVO(productList.get(i));

            cartProductList.add(temp);
        }

        return cartProductList;
    }

    private List<CartVO> parseCartVoArray(String cartListData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // json String을 List<CartVO>로 변환
            List<CartVO> cartList = objectMapper.readValue(cartListData, new TypeReference<>() {});

            // 결과 잘 나오는지 확인
            for (int i = 0; i < cartList.size(); i++) {
                log.info("The mno from cartList: {}", cartList.get(i).getMno());
                log.info("The prno from cartList: {}", cartList.get(i).getPrno());
                log.info("The bookQty from cartList: {}", cartList.get(i).getBookQty());
            }

            return cartList;
        } catch (Exception e) {
            log.info("Exception occurred. Content: {}", e);
            return null;
        }
    }



}

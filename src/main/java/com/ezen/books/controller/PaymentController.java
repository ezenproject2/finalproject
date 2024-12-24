package com.ezen.books.controller;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.CartProductDTO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.service.CartService;
import com.ezen.books.service.PayoutService;
import com.ezen.books.service.PayoutServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/payment/*")
@Controller
@RequiredArgsConstructor
// NOTE: /payout, /shoppingcart 화면 띄우기 관련 로직은 여기서 처리함.
public class PaymentController {

    private final PayoutService payoutService;
    private final CartService cartService;
    private List<CartVO> cartList;

    @PostMapping("/header-cart")
    @ResponseBody
    public int provideCartAmount(@RequestBody long mno) {
        log.info(" >>> PaymentController: provideCartAmount start.");
        log.info("The mno from the client: {}", mno);

        int cartAmount = cartService.getCartAmount(mno);
        return cartAmount;
    }

    @GetMapping("/cart")
    public String showCartItems(@RequestParam("mno") long mno, Model model) {
        log.info(" >>> PaymentController: showCartItems start.");
        log.info("The mno of showCartItems: {}", mno);
        List<CartVO> cartList = cartService.getAllCartItems(mno);
        log.info(" >>> cartList: {}", cartList);

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
        return "/payment/cart";
    }

    @PostMapping("/provide-cart-list")
    @ResponseBody
    public String getCartList(Model model, @RequestBody String cartListData) {
        log.info(" >>> PaymentController: getCartList start.");
        // cartList: [{"mno":"1","prno":"1","bookQty":"5"},{"mno":"1","prno":"2","bookQty":"1"}]
        log.info(" >>> getCartList: cartList: {}", cartListData);
        List<CartVO> cartList =  parseCartVoArray(cartListData);
        this.cartList = cartList;

        // TODO: pickup이면 return을 2로 하든가 해서 구분하기.
        return "1";
    }

//    @PostMapping("/prepare-cart-list")
//    public String prepareCartList(@RequestBody long mno) {
//        log.info(" >>> PaymentController: prepareCartList start.");
//
//        List<CartVO> cartList = payoutService.getCartList(mno);
//        this.cartList = cartList;
//        return "1";
//    }

    @GetMapping("/payout")
    public String goToPayout(Model model) {
        log.info(" >>> PaymentController: goToPayout start.");
        List<CartProductDTO> cartProductList = buildCartProductList(cartList);
        // mno는 단독적으로 쓰이는 경우가 많아 편의상 따로 빼서 model로 보냄.
        long mno = cartProductList.get(0).getCartVO().getMno();
        log.info("mno: {}", mno);

        // 주문/결제 페이지에서 보여줄 사용자의 기본 배송지를 가져옴
        AddressVO defaultAddress = getDefaultAddress(mno);
        log.info("The default address: {}", defaultAddress);

        // 기본 배송지가 null인지 아닌지 가리는 값
        boolean isDefaultAddrNull = (defaultAddress == null) ? true : false;

        // TODO: pickup 주문이면 isPickup을 Y로 보낼 것.
        // 기본 배송지가 있냐 없냐에 따라 보낼 값이 달라짐
        Map<String, Object> modelAttrs = new HashMap<>();
        if(defaultAddress == null) {
            modelAttrs = Map.of(
                    "mno", mno,
                    "cartProductList", cartProductList,
                    "defaultAddress", "empty",
                    "isDefaultAddrNull", isDefaultAddrNull,
                    "isPickup", "N"
            );
        } else {
            modelAttrs = Map.of(
                    "mno", mno,
                    "cartProductList", cartProductList,
                    "defaultAddress", defaultAddress,
                    "isDefaultAddrNull", isDefaultAddrNull,
                    "isPickup", "N"
            );
        }

        log.info("CartProductList from PaymentController: {}", cartProductList);
        model.addAllAttributes(modelAttrs);
        return "/payment/payout";
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

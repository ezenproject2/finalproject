package com.ezen.books.controller;

import com.ezen.books.domain.CartDTO;
import com.ezen.books.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequestMapping("/payment/*")
@Controller
@RequiredArgsConstructor
// NOTE: /payout, /shoppingcart 화면 띄우기 관련 로직은 여기서 처리함.
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/shoppingcart")
    public String showCartItems(Model model) {
        // TODO: 나중에 MERGE 후 사용자의 mno, 상품의 prno를 받아올 것.
        // 현재는 테스트 데이터를 cart에 넣은 후 작업하는 중임.
        // 다른 사용자의 장바구니를 보고 싶으면 여기서 직접 mno에 다른 값을 할당해야 함.
        long mno = 1;
        List<CartDTO> cartList = paymentService.getAllCartItems(mno);
        log.info("cartList: {}", cartList);
        model.addAttribute("cartList", cartList);
        model.addAttribute("mno", mno);
        return "payment/shoppingcart";
    }

    @GetMapping("/payout")
    public String goToPayout(Model model, @RequestParam("mno") long mno) {
        model.addAttribute("mno", mno);
        return "/payment/payout";
    }
}

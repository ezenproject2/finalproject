package com.ezen.books.controller;

import com.ezen.books.domain.CartDto;
import com.ezen.books.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequestMapping("/payment/*")
@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/shoppingcart")
    public String showCartItems(Model model) {
        long mno = 13; // 현재 로그인한 사용자의 mno를 가져와야 하지만, 일단은 임시로 값을 주었음.
        List<CartDto> cartList = paymentService.getAllCartItems(mno);
        log.info("cartList: {}", cartList);
        model.addAttribute("cartList", cartList);
        model.addAttribute("mno", mno);
        return "payment/shoppingcart";
    }
}

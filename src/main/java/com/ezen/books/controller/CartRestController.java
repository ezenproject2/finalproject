package com.ezen.books.controller;

import com.ezen.books.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/payment/cart/*")
@Controller
@RequiredArgsConstructor
public class CartRestController {
    // NOTE: /cart에서 일어나는 모든 RESTful api를 처리함.

    private final CartService cartService;

}

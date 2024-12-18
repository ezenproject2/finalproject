package com.ezen.books.controller;

import com.ezen.books.domain.CartVO;
import com.ezen.books.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/payment/cart/*")
@Controller
@RequiredArgsConstructor
public class CartRestController {
    // NOTE: /cart에서 일어나는 모든 RESTful api를 처리함.

    private final CartService cartService;

    @PostMapping("/insert")
    public ResponseEntity<String> storeCartDataToServer(@RequestBody CartVO cartData) {
        log.info(" >>> CartRestController: storeCartDataToServer start.");
        // The cartData from the client: CartVO(mno=4, prno=83, bookQty=6)
        log.info("The cartData from the client: {}", cartData);

        int isDone = cartService.storeCartDataToServer(cartData);

        return new ResponseEntity<>("1", HttpStatus.OK);
    }

}

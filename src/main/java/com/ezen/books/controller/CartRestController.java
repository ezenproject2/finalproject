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
        log.info("isDone: {}", isDone);

        if(isDone == 1) {
            return new ResponseEntity<>("1", HttpStatus.OK);
        } else if (isDone == 2) {
            return new ResponseEntity<>("2", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCartToServer(@RequestBody CartVO cartData) {
        log.info(" >>> CartRestController: deleteCartToServer start.");
        log.info("The cart data from the client: {}", cartData);

        int isDone = cartService.deleteCartToServer(cartData.getMno(), cartData.getPrno());

        return (isDone > 0) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/delete-all")
    public ResponseEntity<String> deleteAllCartToServer(@RequestBody long mnoVal) {
        log.info(" >>> CartRestController: deleteAllCartToServer start.");
        log.info("The mnoVal from the client: {}", mnoVal);

        int isDone = cartService.deleteAllCartToServer(mnoVal);

        return (isDone > 0) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

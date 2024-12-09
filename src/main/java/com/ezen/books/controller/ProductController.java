package com.ezen.books.controller;

import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.BookVO;
import com.ezen.books.handler.BookAPIHandler;
import com.ezen.books.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/product/*")
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final BookAPIHandler bookAPIHandler;

    @GetMapping("/register")
    public void register(){}

    @ResponseBody
    @GetMapping(value = "/search/{keyword}")
    public BookVO search(@PathVariable("keyword") String keyword) {
        String res = bookAPIHandler.search(keyword);
        BookVO bookVO = bookAPIHandler.getBookVO(res);
        JSONObject jsonObject = null;

        int isValid = productService.isValid(bookVO.getIsbn());
        bookVO.setIsValid(isValid);

        return bookVO;
    }

    @ResponseBody
    @PostMapping("/register")
    public String register(@RequestBody BookProductDTO bookProductDTO){
        log.info(">>>> bookProductDTO > {}", bookProductDTO);
        int isOk = productService.register(bookProductDTO);
        return isOk>0? "1" : "0";
    }

}

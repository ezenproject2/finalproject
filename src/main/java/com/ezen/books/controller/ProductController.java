package com.ezen.books.controller;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.handler.BookAPIHandler;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequestMapping("/product/*")
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final BookAPIHandler bookAPIHandler;

    @GetMapping("/register")
    public void register(){}

    @PostMapping("/register")
    public String register(ProductVO productVO){
        log.info(">>>> productVO > {}", productVO);
        int isOk = productService.register(productVO);
        log.info(">>>> 상품 등록 > {}", (isOk>0? "성공" : "실패"));
        return "redirect:/product/list";
    }

    @ResponseBody
    @GetMapping(value = "/search/{keyword}")
    public ProductVO search(@PathVariable("keyword") String keyword) {
        String res = bookAPIHandler.search(keyword);
        ProductVO productVO = bookAPIHandler.getProductVO(res);

        int isValid = productService.isValid(productVO.getIsbn());
        productVO.setIsValid(isValid);

        return productVO;
    }

    @GetMapping("/list")
    public void list(Model model, PagingVO pagingVO){
        log.info(">>>> pagingVO > {}", pagingVO);
        int totalCount = productService.getTotalCount(pagingVO);
        PagingHandler ph = new PagingHandler(pagingVO, totalCount);
        List<ProductVO> list = productService.getList(pagingVO);

        model.addAttribute("ph", ph);
        model.addAttribute("list", list);
    }

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("isbn") String isbn){
        log.info(">>>> isbn > {}", isbn);
        ProductVO productVO = productService.getDetail(isbn);

        BookInfo bookInfo = null;
        try {
            bookInfo = bookAPIHandler.getDetailDate(productVO.getLink());
            log.info(">>>> detail > {}", bookInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("productVO", productVO);
    }

}

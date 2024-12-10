package com.ezen.books.controller;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.BookVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.handler.BookAPIHandler;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
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

    @ResponseBody
    @PostMapping("/register")
    public String register(@RequestBody BookProductDTO bookProductDTO){
        log.info(">>>> bookProductDTO > {}", bookProductDTO);
        int isOk = productService.register(bookProductDTO);
        return isOk>0? "1" : "0";
    }

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

    @GetMapping("/list")
    public void list(Model model, PagingVO pagingVO){
        log.info(">>>> pagingVO > {}", pagingVO);
        int totalCount = productService.getTotalCount(pagingVO);
        PagingHandler ph = new PagingHandler(pagingVO, totalCount);
        List<BookProductDTO> list = productService.getList(pagingVO);

        model.addAttribute("ph", ph);
        model.addAttribute("list", list);
    }

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("prno") long prno){
        log.info(">>>> prno > {}", prno);
        BookProductDTO bookProductDTO = productService.getDetail(prno);
        BookInfo bookInfo = null;
        try {
            bookInfo = bookAPIHandler.getDetailDate(bookProductDTO.getBookVO().getLink());
            log.info(">>>> detail > {}", bookInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("bookProductDTO", bookProductDTO);
    }

}

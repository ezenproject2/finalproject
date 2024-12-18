package com.ezen.books.controller;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.handler.BookAPIHandler;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        // 관리자의 상품 등록
        log.info(">>>> productVO > {}", productVO);
        int isOk = productService.register(productVO);
        log.info(">>>> 상품 등록 > {}", (isOk>0? "성공" : "실패"));
        return "redirect:/product/list";
    }

    @ResponseBody
    @GetMapping(value = "/search/{keyword}")
    public ProductVO search(@PathVariable("keyword") String keyword) {
        // 키워드를 통한 네이버 도서 API 검색 기능
        String res = bookAPIHandler.search(keyword);
        ProductVO productVO = bookAPIHandler.getProductVO(res);

        if(productVO.getDiscount()>0){
            double originalPrice = productVO.getDiscount() / 0.9;
            // 100원 단위로 반올림
            int roundedPrice = (int) Math.round(originalPrice / 100.0) * 100;
            productVO.setDiscount(roundedPrice);
        }

        if(productVO.getIsbn() == null || productVO.getIsbn().equals("")){
            // 빈 값일 때
            productVO.setIsValid(2);
        }else{
            int isValid = productService.isValid(productVO.getIsbn());
            productVO.setIsValid(isValid);
        }

        return productVO;
    }

    @ResponseBody
    @GetMapping(value = "/duplCheck/{isbn}")
    public String duplCheck(@PathVariable("isbn") String isbn) {
        int isValid = productService.isValid(isbn);
        log.info(">>>> 충복검사 결과 > {}", (isValid>0? "중복O" : "중복X"));
        return isValid>0? "1" : "0";
    }

    @GetMapping("/list")
    public void list(Model model, PagingVO pagingVO){
        // 상품 목록 출력 (페이징)
//        log.info(">>>> 상품 목록 pagingVO > {}", pagingVO);
        int totalCount = productService.getTotalCount(pagingVO);
        PagingHandler ph = new PagingHandler(pagingVO, totalCount);
        List<ProductVO> list = productService.getList(pagingVO);
//        log.info(">>>> 상품 목록 list > {}", list);
        log.info(">>>> 상품 목록 ph > {}", ph);
        model.addAttribute("ph", ph);
        model.addAttribute("list", list);
    }

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("isbn") String isbn){
        // 상품 상세 페이지
//        log.info(">>>> isbn > {}", isbn);
        ProductVO productVO = productService.getDetail(isbn);
//        log.info(">>>> productVO > {}", productVO);
        BookInfo bookInfo = new BookInfo();

        // 할인율을 적용한 가격 계산
        double discountedPrice = productVO.getDiscount() * (1 - productVO.getDiscountRate() / 100.0);
        // 가격을 10으로 나누고 내림한 후, 다시 10을 곱해서 10원 단위로 맞춘다.
        int roundedPrice = (int) Math.floor(discountedPrice / 10.0) * 10;
        productVO.setRealPrice(roundedPrice);

        // 자주 테스트할 때 켜두면 네이버가 화내용...
//        try {
//            bookInfo = bookAPIHandler.getDetailDate(productVO.getLink());
//            log.info(">>>> detail > {}", bookInfo);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        model.addAttribute("bookInfo", bookInfo);

        model.addAttribute("productVO", productVO);
    }

}

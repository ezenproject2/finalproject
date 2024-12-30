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
import java.util.Map;

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

    @GetMapping(value = "/specialList/{type}")
    public String specialList(Model model, @PathVariable("type") String type){
        List<ProductVO> list = productService.getSpecialList(type);
        model.addAttribute("list", list);

        String returnType = "상품";
        if(type.equals("best")){
            returnType = "베스트 상품";
        }else if(type.equals("new")){
            returnType = "최근 출판 상품";
        }else if(type.equals("hot")){
            returnType = "MD 추천 상품";
        }

        model.addAttribute("type", returnType);

        return "/product/specialList";
    }

    @GetMapping("/detail")
    public void detail(Model model, @RequestParam("isbn") String isbn){
        // 상품 상세 페이지
        ProductVO productVO = productService.getDetail(isbn);

        // 할인율을 적용한 가격 계산
        double discountedPrice = productVO.getDiscount() * (1 - productVO.getDiscountRate() / 100.0);
        int roundedPrice = (int) Math.floor(discountedPrice / 10.0) * 10;
        productVO.setRealPrice(roundedPrice);

        // 리뷰의 각 별점별 인원수와 퍼센트를 bookInfo에 담아서 같이 보냄
        BookInfo bookInfo = productService.getReviewInfo(productVO.getPrno());

        // 상품 상세 정보를 bookInfo에 추가함
//        try {
//            bookInfo = bookAPIHandler.getDetailDate(productVO.getLink());
//            log.info(">>>> detail > {}", bookInfo);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        model.addAttribute("bookInfo", bookInfo);
        model.addAttribute("productVO", productVO);
    }

    @ResponseBody
    @GetMapping(value = "/getData/{prno}")
    public ProductVO getData(@PathVariable("prno") long prno){
        ProductVO productVO = productService.getData(prno);
        return productVO;
    }

}

package com.ezen.books.controller;

import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ReviewPagingDTO;
import com.ezen.books.domain.ReviewVO;
import com.ezen.books.handler.FileHandler;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review/*")
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final FileHandler fileHandler;

    @PostMapping("/register")
    public String register(@RequestParam("content") String content,
                           @RequestParam("rating") double rating,
                           @RequestParam("prno") long prno,
                           @RequestParam("mno") long mno,
                           @RequestParam(value = "file", required = false) MultipartFile file){
        // 리뷰 등록을 위한 메서드
        ReviewVO reviewVO = ReviewVO.builder()
                .content(content)
                .rating(rating)
                .prno(prno)
                .mno(mno)
                .build();

        log.info(">>>> reviewVO > {}", reviewVO);
        log.info(">>>> file > {}", file);
        // 1. 파일이 존재한다면? 파일 저장, 파일 경로 문자열 가져오기
        if(file != null){
            String fileAddr = fileHandler.uploadReview(file);
            log.info(">>>> fileAddr > {}", fileAddr);
            reviewVO.setFileAddr(fileAddr);
        }
        // 2. 리뷰 테이블에 데이터 저장
        int isOk = reviewService.register(reviewVO);

        return isOk>0? "1" : "0";
    }

    @ResponseBody
    @GetMapping(value = "list/{prno}/{pageNo}/{mno}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReviewPagingDTO getList(@PathVariable("prno") long prno,
                                   @PathVariable("pageNo") int pageNo,
                                   @PathVariable("mno") long mno){
        // 리뷰 출력을 위한 메서드
        // 상품 상세페이지에서 해당 상품의 리뷰를 출력한다. 페이징 있음
        PagingVO pagingVO = new PagingVO(pageNo, 8, prno, mno);
        int totalCount = reviewService.getTotalCount(pagingVO);
        PagingHandler pagingHandler = new PagingHandler(pagingVO, totalCount);
        List<ReviewVO> list = reviewService.getList(pagingVO);

        return new ReviewPagingDTO(list, pagingHandler);
    }

    @ResponseBody
    @GetMapping(value = "/doLike/{rno}/{mno}")
    public int doLike(@PathVariable("rno") long rno, @PathVariable("mno") long mno){
        // 좋아요 ㄱㄱ 메서드
        int isOk = reviewService.doLike(rno, mno);

        return isOk>=0? isOk : -1;
    }

    @ResponseBody
    @GetMapping(value = "/cancel/{rno}/{mno}")
    public int cancel(@PathVariable("rno") long rno, @PathVariable("mno") long mno){
        // 좋아요 취소 메서드
        int isOk = reviewService.cancel(rno, mno);

        return isOk>=0? isOk : -1;
    }

    @ResponseBody
    @GetMapping("/insertTest/{mno}/{message}")
    public String insertTest(@PathVariable("mno") long mno, @PathVariable("message") String message){
        reviewService.createAndSendNotification(mno, message);

        return "1";
    }

    @ResponseBody
    @GetMapping("/delete/{rno}")
    public String delete(@PathVariable("rno") long rno){
        int isOk = reviewService.delete(rno);

        return isOk>0? "1" : "0";
    }


}

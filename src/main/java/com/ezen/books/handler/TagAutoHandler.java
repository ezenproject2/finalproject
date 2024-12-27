package com.ezen.books.handler;

import com.ezen.books.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TagAutoHandler {

    private final ProductService productService;

    // 베스트(판매량), 신상품, 인기상품을 매주 스케줄러로 구현

    @Scheduled(cron = "0 0 12 * * ?")
    public void tagInsert(){
        log.info(">>>> FileSweeper Running Start > {}", LocalDateTime.now());

        productService.setBestTag();
        productService.setNewTag();
        productService.setHotTag();

        log.info(">>>> FileSweeper Running End > {}", LocalDateTime.now());
    }





}

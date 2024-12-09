package com.ezen.books.controller;

import com.ezen.books.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/booksTest/*")
@RequiredArgsConstructor
@Slf4j
@Controller
public class TestController {
    private final TestService testService;
}

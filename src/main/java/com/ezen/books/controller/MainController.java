package com.ezen.books.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@PropertySource("classpath:/application-secrets.properties")
@Slf4j
@RequestMapping("/*")
@RequiredArgsConstructor
@Controller
public class MainController {

    @Value("${KAKAO_JAVASCRIPT_APPKEY}") // test
    private String appkey;

    @GetMapping("/store")
    public String showMap(Model model) {
        model.addAttribute("appkey", appkey);
        return "/store";

        // 주석이애용
    }

    @GetMapping("/benefit")
    public void benefit(){}

}

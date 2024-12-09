package com.ezen.books.controller;

import com.ezen.books.service.PaymentService;
import com.ezen.books.service.PaymentServiceImpl;
import com.siot.IamportRestClient.IamportClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/payment/*")
@RestController
@RequiredArgsConstructor
public class PaymentRestController {

    private final IamportClient iamportClient;
    private PaymentService paymentService;

    //생성자를 통해 REST API 와 REST API secret 입력
    @Autowired
    public PaymentRestController(PaymentServiceImpl paymentService, @Value("${iamport_rest_api_key}") String apiKey, @Value("${iamport_rest_api_secret}") String apiSecret) {
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }

//    @PostMapping("/")

}

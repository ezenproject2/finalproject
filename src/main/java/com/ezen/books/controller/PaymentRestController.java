package com.ezen.books.controller;

import com.ezen.books.service.PaymentService;
import com.ezen.books.service.PaymentServiceImpl;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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

    @PostMapping("/verify-iamport/webhook")
    public ResponseBody verifyByWebhook(HttpServletRequest request) throws IOException {
        // 웹훅을 수신하고 반드시 결제내역 단건조회 API를 통해 결제건을 조회하여 웹훅의 내용을 검증해야 합니다.
        // 웹훅이 수신되지 않은 경우에도 결제 취소를 하기 이전에 결제내역 단건조회 API를 통해 결제건의 상태를 조회하여, 결제 상태에 따라 취소 처리를 해야 합니다.
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        // The body: {"imp_uid":"imp_669033532600","merchant_uid":"15f2c13a-55bc-48d5-8356-8fc03ba9f41b"}
        log.info("The body: {}", body);

        // 토큰 발급받기
//        getIamportAccessToken();
        return null;
    }

    private AccessToken getIamportAccessToken(@Value("${iamport_rest_api_key}") String apiKey, String imp_uid) {
        RestTemplate restTemplate = new RestTemplate();
        // restTemplate.exchange()가 비동기 요청을 하는 메서드인 듯.
        String url = "https://api.portone.io/v2/signin/api-key";
        return null;
    }



    @PostMapping("/verify-iamport/{imp_uid}")
    public ResponseEntity<String> replyToIamport(@PathVariable String imp_uid, HttpServletRequest request) throws IOException {
        log.info("replyToIamport operating. imp_uid: {}", imp_uid);
        AccessToken accessToken = new AccessToken();

        return new ResponseEntity<String>("1", HttpStatus.OK);
    }

    // iamport로 결제를 완료했을 때. 코드에 오류는 없는데 얘가 뭐 하는 코드인지 모르겠음.
//    @PostMapping("/verify-iamport/{imp_uid}")
//    public IamportResponse<Payment> verifyByImpUid(@PathVariable String imp_uid, HttpServletRequest request) throws IamportResponseException, IOException {
//        log.info("verifyByImpUid 시작");
//        IamportResponse<Payment> paymentIamportResponse = iamportClient.paymentByImpUid(imp_uid);
//        Payment payment = paymentIamportResponse.getResponse();
//        return paymentIamportResponse;
//    }

    // 예전에 JS에서 값 받았을 때 참고
//    @PostMapping("insert")
//    public ResponseEntity<String> insertComment(@RequestBody CommentDTO commentDTO) {
//        log.info(" CommentController insertComment operating. commentDTO: {}", commentDTO);
//        Long cno = commentService.storeComment(commentDTO);
//        // JS에서 결과값이 "1"인지 아닌지 볼 거라서 그냥 반환 값을 String으로 하고 잘 되면 "1", 아니면 "0"을 줘도 된다.
//        return cno != null ?
//                new ResponseEntity<String>("1", HttpStatus.OK) :
//                new ResponseEntity<String>("0", HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}

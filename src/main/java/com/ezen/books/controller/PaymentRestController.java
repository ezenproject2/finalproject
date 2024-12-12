package com.ezen.books.controller;

import com.ezen.books.domain.IamportAccessTokenVO;
import com.ezen.books.domain.PaymentDTO;
import com.ezen.books.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.AccessToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequestMapping("/payment/*")
@RestController
@RequiredArgsConstructor
@PropertySource("classpath:application-secrets.properties")
public class PaymentRestController {

    private String iamportApiKey; // api 키와 시크릿을 여기서 지금 쓰고 있지는 않음. 추후에는 쓸지도.
    private String iamportApiSecret;

    private final IamportClient iamportClient; // Private field 'iamportClient' is assigned but never accessed
    private PaymentService paymentService;

    @Autowired
    public PaymentRestController(
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret, PaymentService paymentService) {
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
        this.paymentService = paymentService;
        this.iamportClient = new IamportClient(iamportApiKey, iamportApiSecret);
    }

    @PostMapping("/result")
    public String getPaymentResultFromClient(HttpServletRequest request)
            throws IOException, URISyntaxException, InterruptedException {
        log.info(" >>> PaymentRestController: getPaymentResultFromClient start.");

        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("The result body: {}", requestBody);

        // 일단 단건 조회만 해볼 거라서 imp_uid를 제외한 모든 결제 데이터를 받지 않았다.
        String impUid = "";
        String amount = "";

        // Jackson을 이용하여 JSON 문자열 파싱
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode bodyNode = objectMapper.readTree(requestBody);

            impUid = bodyNode.get("imp_uid").asText();
            amount = bodyNode.get("paid_amount").asText();
        } catch (Exception e) {
            log.info("Exception occurred. content: {}", e);
        }

        // 단건조회 메서드 호출
        boolean checkResult = checkSinglePayment(impUid, amount);
        return (checkResult) ? "1" : "0";
    }

    private boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException {
        boolean verifyResult = paymentService.checkSinglePayment(impUid, amount);
        return verifyResult;
    }

    @PostMapping("/preserve")
    public ResponseEntity<String> getPaymentIntoToPreserve(@RequestBody PaymentDTO paymentDTO) throws IOException {
        log.info(" >>> PaymentRestController: getPaymentIntoToPreserve start.");

//        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("Received payment data to be conserved: {}", paymentDTO);
        return new ResponseEntity<String>("1", HttpStatus.OK);
    }



    // PaymentService로 옮길 예정.
//    private IamportAccessTokenVO issueIamportToken()
//            throws IOException, URISyntaxException, InterruptedException {
//        log.info(">> PaymentRestController: getIamportToken start.");
//        HttpClient httpClient = HttpClient.newHttpClient();
//
//        String requestJson = "{" +
//                "\"imp_key\": \"" + iamportApiKey + "\", " +
//                "\"imp_secret\": \"" + iamportApiSecret + "\"" +
//                "}";
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI("https://api.iamport.kr/users/getToken"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
//                .build();
//
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        log.info(">> Response Code: {}", response.statusCode());
//
//        // 응답 body 문자열을 json으로 파싱 후 토큰에 접근
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode responseJson = objectMapper.readTree(response.body());
//
//        IamportAccessTokenVO iamportToken = new IamportAccessTokenVO();
//        iamportToken.setToken(responseJson.get("response").get("access_token").asText());
//        iamportToken.setNow(responseJson.get("response").get("now").asInt());
//        iamportToken.setExpiredAt(responseJson.get("response").get("expired_at").asInt());
//
//        return iamportToken;
//    }

}

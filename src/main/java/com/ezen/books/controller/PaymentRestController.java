package com.ezen.books.controller;

import com.ezen.books.domain.IamportAccessTokenVO;
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
import java.net.*;
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

    private String iamportApiKey;

    private String iamportApiSecret;

    private final IamportClient iamportClient;
    private PaymentService paymentService;


    @Autowired
    public PaymentRestController(
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret) {
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
        this.iamportClient = new IamportClient(iamportApiKey, iamportApiSecret);
    }

    @PostMapping("/verify-iamport/webhook")
    public void verifyByWebhook(HttpServletRequest request)
            throws IOException, URISyntaxException, InterruptedException {
        log.info("PaymentRestController: vefiryByWebhook start.");

       String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        log.info("The request body: {}", requestBody);

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode bodyNode = objectMapper.readTree(requestBody);

            String impUid = bodyNode.get("imp_uid").asText();
            String merchantUid = bodyNode.get("merchant_uid").asText();
            log.info(">> verifyByWebhook: The imp_uid: {}", impUid);
            log.info(">> verifyByWebhook: The merchant_uid: {}", merchantUid);
        } catch (Exception e) {
            log.info("Exception occurred in verifyByWebhook, content: {}", e);
        }

        // 토큰 발급받기
        IamportAccessTokenVO iamportToken = getIamportToken();
    }


    private IamportAccessTokenVO getIamportToken()
            throws IOException, URISyntaxException, InterruptedException {
        log.info(">> PaymentRestController: getIamportToken start.");
        HttpClient httpClient = HttpClient.newHttpClient();

        String requestJson = "{" +
                "\"imp_key\": \"" + iamportApiKey + "\", " +
                "\"imp_secret\": \"" + iamportApiSecret + "\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.iamport.kr/users/getToken"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info(">> Response Code: {}", response.statusCode());

        // 응답 body 문자열을 json으로 파싱 후 토큰에 접근
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(response.body());

        IamportAccessTokenVO iamportToken = new IamportAccessTokenVO();
        iamportToken.setToken(responseJson.get("response").get("access_token").asText());
        iamportToken.setNow(responseJson.get("response").get("now").asInt());
        iamportToken.setExpiredAt(responseJson.get("response").get("expired_at").asInt());

        return iamportToken;
    }

    // JavaScript에게 주기 위한 용도라는 점을 빼면 위의 getIamportToken과 똑같다.
    @GetMapping("/get-token")
    public IamportAccessTokenVO giveTokenToJS()
            throws IOException, URISyntaxException, InterruptedException {
        log.info("PaymentRestController: giveTokenToJS start.");
        HttpClient httpClient = HttpClient.newHttpClient();

        String requestJson = "{" +
                "\"imp_key\": \"" + iamportApiKey + "\", " +
                "\"imp_secret\": \"" + iamportApiSecret + "\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.iamport.kr/users/getToken"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Response Code: {}", response.statusCode());

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(response.body());

        IamportAccessTokenVO iamportToken = new IamportAccessTokenVO();
        iamportToken.setToken(responseJson.get("response").get("access_token").asText());
        iamportToken.setNow(responseJson.get("response").get("now").asInt());
        iamportToken.setExpiredAt(responseJson.get("response").get("expired_at").asInt());

        return iamportToken;
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

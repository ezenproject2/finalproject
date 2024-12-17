package com.ezen.books.controller;

import com.ezen.books.domain.PaymentVO;
import com.ezen.books.service.PayoutService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siot.IamportRestClient.IamportClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/payment/payout/*")
@RestController
@RequiredArgsConstructor
@PropertySource("classpath:application-secrets.properties")
// NOTE: 결제 api 관련 비즈니스 로직을 포함하여 /payout에서 발생하는 모든 RESTful api를 처리함.
public class PayoutRestController {

    @Autowired
    private Environment env;

    private String iamportApiKey; // api 키와 시크릿을 여기서 지금 쓰고 있지는 않음. 추후에는 쓸지도.
    private String iamportApiSecret;

    private final IamportClient iamportClient; // Private field 'iamportClient' is assigned but never accessed
    private PayoutService payoutService;

    @Autowired
    public PayoutRestController(
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret, PayoutService payoutService) {
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
        this.payoutService = payoutService;
        this.iamportClient = new IamportClient(iamportApiKey, iamportApiSecret);
    }

    @PostMapping("/prepare")
    public ResponseEntity<String> sendDataToClient(@RequestBody String pgData) {
        // The received pgVal: {"pg":"kakaopay"}
        log.info("The received pgVal: {}", pgData);

        String pgVal = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode pgNode = objectMapper.readTree(pgData);
            pgVal = pgNode.get("pg").asText();
        } catch (Exception e) {
            log.info("Exception occurred. Content: {}", e);
        }

        Map<String, String> pgMap = getChannelKeyAndMethod(pgVal);
        String channelKey = pgMap.get("channelKey");
        String payMethod = pgMap.get("payMethod");
        log.info("The channel key: {}", channelKey);

        UUID merchantUid = UUID.randomUUID();

        return new ResponseEntity<String>("1", HttpStatus.OK);
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

    // TODO: 화면에서 넘어온 실제 결제 정보를 바탕으로 결제 정보를 저장할 것.
    @PostMapping("/preserve")
    public ResponseEntity<String> getPaymentInfoToPreserve(@RequestBody PaymentVO paymentVO) throws IOException {
        log.info(" >>> PaymentRestController: getPaymentIntoToPreserve start.");
        log.info("Received payment data to be conserved: {}", paymentVO);
        return new ResponseEntity<String>("1", HttpStatus.OK);
    }


    private boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException {
        boolean verifyResult = payoutService.checkSinglePayment(impUid, amount);
        return verifyResult;
    }

    private Map<String, String> getChannelKeyAndMethod(String pgVal) {
        String channelKey = null;
        String payMethod = null;

        switch (pgVal) {
            case "kakaopay":
                channelKey = "iamport_kakaopay_general_payment_channel_key";
                payMethod = "card";
                break;
            case "danal":
                channelKey = "iamport_danal_phone_payment_channel_key";
                payMethod = "phone";
                break;
            case "tosspay_v2":
                channelKey = "iamport_tosspay_v2_general_payment_channel_key";
                payMethod = "tosspay";
                break;
            default:
                channelKey = "Unknown channelKey";
                payMethod = "Unknown payMethod";
        }

        // NOTE: Argument 'env. getProperty(channelKey)' might be null
        Map<String, String> pgMap = Map.of(
                "channelKey", env.getProperty(channelKey),
                "payMethod", payMethod
        );

        return pgMap;
    }

}

package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.CartProductDTO;
import com.ezen.books.domain.IamportAccessToken;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.repository.PayoutMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutServiceImpl implements PayoutService {

    private PayoutMapper payoutMapper;
    private String iamportApiKey;
    private String iamportApiSecret;

    @Autowired
    public PayoutServiceImpl(
            PayoutMapper payoutMapper,
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret) {
        this.payoutMapper = payoutMapper;
        this.iamportApiKey = iamportApiKey;
        this.iamportApiSecret = iamportApiSecret;
    }

    @Override
    public AddressVO getDefaultAddress(long mno) {
        AddressVO defaultAddress = payoutMapper.getDefaultAddress(mno);
        return defaultAddress;
    }

    @Override
    public int saveOrdersToServer(OrdersVO ordersVO) {
        int isDone = payoutMapper.saveOrdersToServer(ordersVO);
        return isDone;
    }

    @Override
    public boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException {
        log.info(" >>> PaymentServiceImpl: checkSinglePayment start.");
        IamportAccessToken iamportToken = PayoutService.super.issueIamportToken(iamportApiKey, iamportApiSecret);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.set("Authorization", "Bearer " + iamportToken.getToken());

        HttpEntity<String> sendImpUidAndToken = new HttpEntity<>(null, headers);

        // NOTE: 한글을 인식하지 못해 result body의 사용자 이름이 유니코드로 나옴.
        // 이 메서드에서 사용자 이름을 사용하지는 않지만 주의 요망.
        ResponseEntity<String> result = restTemplate.exchange(
                url,
                HttpMethod.GET,
                sendImpUidAndToken,
                String.class
        );

        log.info("The response status: {}", result.getStatusCode());

        String responseBody = result.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        String resultImpUid = "";
        String resultAmount = "";
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            resultImpUid = jsonNode.get("response").get("imp_uid").asText();
            resultAmount = jsonNode.get("response").get("amount").asText();
        } catch (Exception e) {
            log.info("Exception occurred. Content: {}", e);
        }

        // 클라이언트가 보낸 imp_uid, amount와 응답으로 받은 imp_uid, amount 비교
        if(impUid.equals(resultImpUid) && amount.equals(resultAmount)) {
            return true;
        } else {
            return false;
        }
    }

}

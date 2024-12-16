package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.IamportAccessToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@PropertySource("classpath:application-secrets.properties")
public interface PayoutService {

    Logger log = LoggerFactory.getLogger(PayoutService.class);

    // PaymentRestController에 있던 토큰 발급 메서드를 그대로 옮겨옴.
    default IamportAccessToken issueIamportToken(String iamportApiKey, String iamportApiSecret)
            throws IOException, URISyntaxException, InterruptedException {
        log.info(">> PaymentService: getIamportToken start.");
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

        IamportAccessToken iamportToken = new IamportAccessToken();
        iamportToken.setToken(responseJson.get("response").get("access_token").asText());
        iamportToken.setNow(responseJson.get("response").get("now").asInt());
        iamportToken.setExpiredAt(responseJson.get("response").get("expired_at").asInt());

        return iamportToken;
    }

    boolean checkSinglePayment(String impUid, String amount) throws IOException, URISyntaxException, InterruptedException;

    AddressVO getDefaultAddress(long mno);
}

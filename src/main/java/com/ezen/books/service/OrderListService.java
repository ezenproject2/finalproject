package com.ezen.books.service;

import com.ezen.books.domain.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderListService {

    Logger log = LoggerFactory.getLogger(PayoutService.class);

    // PaymentRestController에 있던 토큰 발급 메서드를 그대로 옮겨옴.
    default IamportAccessToken issueIamportToken(
            @Value("${iamport_rest_api_key}") String iamportApiKey,
            @Value("${iamport_rest_api_secret}") String iamportApiSecret)
            throws IOException, URISyntaxException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        String requestJson = "{" +
                "\"imp_key\": \"" + iamportApiKey + "\", " +
                "\"imp_secret\": \"" + iamportApiSecret + "\"" +
                "}";
        log.info("The JSON body: {}", requestJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.iamport.kr/users/getToken"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Response Code: {}", response.statusCode());
        log.info("Response Code: {}", response.body());

        // 응답 body 문자열을 json으로 파싱 후 토큰에 접근
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());

        String token = jsonNode.get("response").get("access_token").asText();
        log.info("The token is: {}", token);

        IamportAccessToken iamportToken = new IamportAccessToken();
        iamportToken.setToken(token);
        // 둘 다 null. 필요한 게 토큰이니까 다른 것들은 주석처리함.
//        iamportToken.setNow(jsonNode.get("now").asInt());
//        iamportToken.setExpiredAt(jsonNode.get("expired_at").asInt());
        return iamportToken;
    }

    List<OrdersVO> getAllOrderList(long mno);

    List<OrderDetailVO> getOrderDetailList(String orno);

    List<List<OrderDetailProductDTO>> getOrderDetailProductList(long mno);

    boolean isOrderEmpty(long mno);

    int refundWithIamport(long odno, String orno, int qtyPrice) throws IOException, URISyntaxException, InterruptedException;

    MemberVO getMember(long mno);

    GradeVO getMemberGrade(Long gno);

    LocalDateTime getOrderDate(String orno);

    OrdersVO getOrder(String orno);
}
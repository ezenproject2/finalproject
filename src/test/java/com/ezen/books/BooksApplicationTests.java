package com.ezen.books;

import com.ezen.books.controller.PaymentRestController;
import com.ezen.books.domain.CartDTO;
import com.ezen.books.domain.IamportAccessTokenVO;
import com.ezen.books.repository.PaymentMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@SpringBootTest
@PropertySource("classpath:application-secrets.properties")
class BooksApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(BooksApplicationTests.class);
	@Autowired
	private PaymentMapper paymentMapper;

	@InjectMocks
	private PaymentRestController paymentRestController;

	private String iamportApiKey;

	private String iamportApiSecret;

	@Test
	void contextLoads() {
		// 임시로 장바구니에 값 넣기
		for (int i=0; i < 20; i++) {
			CartDTO cartDto = CartDTO.builder()
					.mno((long) (Math.random()*20 +1))
					.prno((long) (Math.random()*20 +1))
					.bookQty((int) (Math.random()*10 +1))
					.build();

			paymentMapper.addCart(cartDto);
		}
	}

	/* 결제: iamport api에서 토큰 받는 메서드 테스트 */
	@Test
	public void testGetIamportToken(
			@Value("${iamport_rest_api_key}") String iamportApiKey,
			@Value("${iamport_rest_api_secret}") String iamportApiSecret)
			throws URISyntaxException, IOException, InterruptedException {
		HttpClient httpClient = HttpClient.newHttpClient();

		// TODO: HttpRequest 생성 후 토큰 받아오기.
		// 400 응답을 받았음. 이유 모름. ????? 근데 포트원의 테스트 페이지에서 하면 토큰 잘 받아짐.
		// 설마 이거 키랑 시크릿에 따옴표 안붙였다고 안되는거야??????
		// .... 진짜 따옴표 안 붙였다고 안 되는 거였음...... (허탈
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

		// 응답 body를 받으면 자동적으로 Java 클래스와 mapping 되게 하고 싶었는데 왠지 안 됨. 나중에 도전해볼 것.
//		IamportAccessTokenVO iamportToken = objectMapper.readValue(jsonNode.get("response").asText(), IamportAccessTokenVO.class);
//		log.info("The token value of the token object: {}", iamportToken.getToken());
	}

}

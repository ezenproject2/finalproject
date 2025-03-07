package com.ezen.books;

import com.ezen.books.controller.PayoutRestController;
import com.ezen.books.domain.DataUrlDTO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.handler.BookAPIHandler;
import com.ezen.books.repository.CartMapper;
import com.ezen.books.repository.PayoutMapper;
import com.ezen.books.service.OfflineService;
import com.ezen.books.service.ProductService;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PropertySource("classpath:application-secrets.properties")
@SpringBootTest(classes = BooksApplication.class)
class BooksApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(BooksApplicationTests.class);
	@Autowired
	private BookAPIHandler bookAPIHandler;
	@Autowired
	private ProductService productService;
	@Autowired
	private OfflineService offlineService;

	@Autowired
	private PayoutMapper payoutMapper;
	@Autowired
	private CartMapper cartMapper;
	@InjectMocks
	private PayoutRestController payoutRestController;
	private String iamportApiKey;
	private String iamportApiSecret;

	@Test
	void contextLoads() {
		// 네이버 도서에서 1차 카테고리, 2차 카테고리 선택 후 카테고리 명과 링크를 정확히 기입해주세요.
		// 한번에 10~15개의 링크만 사용할 것을 권장합니다.
		// 반복해서 입력하지 말고 하루에 한번만 사용해주세요.
		List<DataUrlDTO> dataUrlList = new ArrayList<>();
		int i = 1;
		dataUrlList.add(new DataUrlDTO("소설", "세계 각국 소설", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005568&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("소설", "고전/문학", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005569&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("소설", "장르소설", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005570&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("소설", "테마문학", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005571&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("소설", "한국소설", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50010002&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("시/에세이", "한국시", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50011720&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("시/에세이", "외국시", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50011740&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("시/에세이", "그림/사진 에세이", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005579&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("시/에세이", "독서 에세이", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005577&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("시/에세이", "명상 에세이", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005578&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("컴퓨터/IT", "그래픽/멀티미디어", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005692&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("컴퓨터/IT", "오피스활용도서", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50005693&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("컴퓨터/IT", "웹사이트", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50010881&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("컴퓨터/IT", "컴퓨터 입문/활용", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50010702&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));
		dataUrlList.add(new DataUrlDTO("컴퓨터/IT", "게임", "https://search.shopping.naver.com/book/search/category?bookTabType=ALL&catId=50010862&goodsType=PAPER&pageIndex=" + i + "&pageSize=40&sort=REL"));



		for(DataUrlDTO dataUrlDTO : dataUrlList){
			try {
				List<String> titles = bookAPIHandler.getTitles(dataUrlDTO.getUrl());
				if (titles != null && !titles.isEmpty()) {

					for(String title : titles){
						// 책 제목으로 API 검색 > bookVO 생성 > productDTO 생성 > DB 저장
						String res = bookAPIHandler.search(title);

						ProductVO productVO = null;
						productVO = bookAPIHandler.getTestData(res);

						if(productVO != null && productVO.getIsbn() != null){
							productVO.setStock(1000);
							productVO.setDiscountRate(10);
							productVO.setPrimaryCtg(dataUrlDTO.getPctg());
							productVO.setSecondaryCtg(dataUrlDTO.getSctg());

							if(!productVO.getPrimaryCtg().equals("소설") && !productVO.getPrimaryCtg().equals("컴퓨터/IT")){
								productVO.setDescription("시연 발표 제외로 인한 생략");
							}

							if(productVO.getDiscount()>0){
								double originalPrice = productVO.getDiscount() / 0.9;
								// 100원 단위로 반올림
								int roundedPrice = (int) Math.round(originalPrice / 100.0) * 100;
								productVO.setDiscount(roundedPrice);
							}

							// DB 저장!!!!
							if(productVO.getIsbn() != null && productVO.getIsbn() != ""){
								productService.testDataInsert(productVO);
							}
						}
					}

				} else {
					System.out.println("No titles found for link: " + dataUrlDTO.getUrl());
				}
			} catch (IOException e) {
				System.err.println("Error while processing link: " + dataUrlDTO.getUrl());
				e.printStackTrace();
			}
		}
	}

	/* 결제: iamport api에서 토큰 받는 메서드 테스트 */
	@Test
	public void testGetIamportToken(
			@Value("${iamport_rest_api_key}") String iamportApiKey,
			@Value("${iamport_rest_api_secret}") String iamportApiSecret)
			throws URISyntaxException, IOException, InterruptedException {
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
//		log.info("Response Code: {}", response.statusCode());
//		log.info("Response Code: {}", response.body());

		// 응답 body 문자열을 json으로 파싱 후 토큰에 접근
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(response.body());

		String token = jsonNode.get("response").get("access_token").asText();
		log.info("The token is: {}", token);
	}

	@Test
	void insertOfflineStock() {
		// prno를 제공하면 해당 상품의 '오프라인' 재고를 랜덤(0~5) 생성해주는 메서드
		// 상품 상세 페이지를 시연을 위해 만들었어요.
        long prno = 251;
        for(int i=1; i <= 77; i++){
            int isOk = offlineService.testStockInsert(i);
        }
	}

}

package com.ezen.books.handler;

import com.ezen.books.domain.BookInfo;
import com.ezen.books.domain.BookProductDTO;
import com.ezen.books.domain.BookVO;
import com.ezen.books.domain.ProductVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PropertySource("classpath:/application-secrets.properties")
@Component
@Slf4j
public class BookAPIHandler {

    // 네이버 클라이언트 아이디 + 비밀번호
    @Value("${NAVER_CLIENT_ID}")
    private String clientId;
    @Value("${NAVER_CLIENT_SECRET}")
    private String clientSecret;

    public String search(String title){
        // 상품 등록에 사용하는 메서드
        // 도서 이름 또는 ISBN을 입력하면 API를 통해 도서 데이터를 가져옴!

        // 검색어 인코딩 작업
        String keyword = null;
        try {
            keyword = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        // 검색 URL + 검색어 결합 + 조건(상위 1개만 가져옴)
        String apiURL = "https://openapi.naver.com/v1/search/book.json?query=" + keyword + "&display=1";    // JSON 결과

        // (네이버 도서 API) 검색 메서드(get)을 위한 데이터 준비 및 responseBody 가져옴
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        return responseBody;
    }

    public BookVO getBookVO(String responseBody){
        // 관리자의 상품 등록 페이지에서 사용할 메서드
        BookVO bookVO = new BookVO();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode itemsNode = rootNode.path("items");

            // items가 존재하면? (= 검색 결과가 존재한다면?) List로 변환
            if (itemsNode.isArray()) {
                List<BookVO> items = objectMapper
                        .readValue(itemsNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BookVO.class));

                // BookVO가 잘 변환되었다면?
                if (!items.isEmpty()) {
                    // bookVO 객체!
                    bookVO = items.get(0);
                    log.info(">>>> bookVO > {}", bookVO);

                    return bookVO;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing response body", e);
        }
        return null;
    }

    public BookProductDTO getDetail(String responseBody){
        // API를 통해 가져온 responseBody(책 정보 데이터)를 활용해 BookProductDTO를 제공
        // 화면 X, 테스트 코드에서 사용할 것.
        // 수정 시 테스트 코드 변동 위험 있음.

        BookVO bookVO = new BookVO();
        ProductVO productVO = new ProductVO();

        try {
            // JSON 응답에서 items만 List<BookVO>로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode itemsNode = rootNode.path("items");

            // items가 존재하면? (= 검색 결과가 존재한다면?) List로 변환
            if (itemsNode.isArray()) {
                List<BookVO> items = objectMapper
                        .readValue(itemsNode.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, BookVO.class));

                // BookVO가 잘 변환되었다면?
                if (!items.isEmpty()) {
                    // bookVO 객체!
                    bookVO = items.get(0);
                    log.info(">>>> bookVO > {}", bookVO);

                    // productVO 객체!
                    // bookVO 객체를 통해 productVO 객체 생성
//                    productVO = getProductData(bookVO);

                    BookProductDTO bookProductDTO = new BookProductDTO(bookVO, productVO);
                    return bookProductDTO;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing response body", e);
        }
        return null;
    }

    public ProductVO getProductData(BookVO bookVO) throws IOException {
        // bookVO 객체를 통해 productVO 객체 생성

        // User-Agent를 변경하여 웹 브라우저처럼 보이게 함
        Connection connection = Jsoup.connect(bookVO.getLink())
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

        // 연결 시도 및 HTML 파싱
        Document document = connection.get();

        // (웹 스크래핑) 도서 정보에서 가져온 link를 통한 상세 페이지 접속
//        Document document = Jsoup.connect(bookVO.getLink()).get();

        // isbn, 재고 설정
        ProductVO productVO = ProductVO.builder()
                .isbn(bookVO.getIsbn())
                .stock(1000)
                .build();

        // 대분류, 소분류
        List<String> categories = document.select(".bookCatalogTop_category__DkzRk")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());

        if(categories.get(1).equals("유아") || categories.get(1).equals("어린이")){
            // 화면 변경사항 준수
            productVO.setPrimaryCtg("유아/어린이");
        }else if(categories.get(1).equals("국어/외국어")){
            productVO.setPrimaryCtg("언어");
        }else{
            productVO.setPrimaryCtg(categories.get(1));
        }

        productVO.setSecondaryCtg(categories.get(2));

        // 제품 표지 이미지 정보
        Element imgElement1 = document.selectFirst("div.bookImage_img_wrap__lEXb2 img"); // img 태그 선택
        if (imgElement1 != null) {
            String imageSrc1 = imgElement1.attr("src");  // src 속성 추출
            productVO.setProfileLink(imageSrc1);
        } else {
            log.info(">>>> Image not found!");
        }

        // 제품 상세 이미지 정보
        Element imgElement2 = document.selectFirst("div.detailImage_image__4yD9v img"); // img 태그 선택
        if (imgElement2 != null) {
            // src 속성 추출
            String imageSrc2 = imgElement2.attr("src");
            productVO.setDetailLink(imageSrc2);
        } else {
            log.info(">>>> Image not found!");
        }

        return productVO;
    }

    public BookInfo getDetailDate(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        // BookInfo 객체 생성
        BookInfo bookInfo = new BookInfo();

        // 책 소개 (책 소개는 "책 소개" 제목을 기준으로 찾기)
        extractSectionInfo(document, "책 소개", bookInfo::setBookIntroTitle, bookInfo::setBookIntroContent);

        // 출판사 서평 (출판사 서평은 "출판사 서평" 제목을 기준으로 찾기)
        extractSectionInfo(document, "출판사 서평", bookInfo::setPublisherReviewTitle, bookInfo::setPublisherReviewContent);

        // 목차 (목차는 "목차" 제목을 기준으로 찾기)
        extractSectionInfo(document, "목차", bookInfo::setTableOfContentsTitle, bookInfo::setTableOfContentsContent);

        return bookInfo;
    }

    // 특정 섹션의 제목과 내용을 추출하는 함수
    private void extractSectionInfo(Document document, String sectionTitle,
                                    java.util.function.Consumer<String> setTitle, java.util.function.Consumer<String> setContent) {
        // 섹션의 제목을 기준으로 해당 제목을 찾기
        Elements titles = document.select(".infoItem_title_area__5Xml_ h3");

        for (Element titleElement : titles) {
            if (titleElement.text().equals(sectionTitle)) {
                // 해당 제목에 해당하는 내용을 찾기
                Element contentElement = titleElement.closest(".infoItem_info_item__eaYqw")
                        .select(".infoItem_data_text__m9_Az").first();

                // 제목과 내용이 존재한다면 세팅
                setTitle.accept(titleElement.text());
                if (contentElement != null) {
                    setContent.accept(contentElement.html());  // html()을 사용하여 HTML 태그를 유지
                } else {
                    setContent.accept("");  // 내용이 없으면 빈 문자열을 세팅
                }
                break;
            }
        }
    }

    public List<String> getTitles(String url) throws IOException {
        // (웹 스크래핑) 네이버 도서 사이트에서 책 제목 데이터 수집을 위한 코드

        // 1. URL에서 HTML 문서를 가져옴
        Document document = Jsoup.connect(url).get();

        // 2. 반복되는 <div>에서 첫 번째 <span>의 첫 번째 자식(책 제목)만 추출
        List<String> titles = document.select(".bookListItem_item_inner__9LmpD")
                .stream()
                // 각 div에서 bookListItem_text__SL9m9 클래스를 가진 span을 찾아 첫 번째 span의 첫 번째 자식만 텍스트로 추출
                .map(element -> element.select(".bookListItem_text__SL9m9 span").first().text())
                // 괄호와 그 안의 내용을 제거
                .map(title -> title.replaceAll("\\(.*?\\)", "").trim())
                .collect(Collectors.toList()); // 리스트에 제목을 모음

        return titles;
    }

    public String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    public HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    public String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }

}

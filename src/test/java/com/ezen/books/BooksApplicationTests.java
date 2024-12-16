package com.ezen.books;

import com.ezen.books.domain.DataUrlDTO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.handler.BookAPIHandler;
import com.ezen.books.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = BooksApplication.class)
class BooksApplicationTests {

	@Autowired
	private BookAPIHandler bookAPIHandler;
	@Autowired
	private ProductService productService;

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
}

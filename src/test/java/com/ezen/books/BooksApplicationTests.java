package com.ezen.books;

import com.ezen.books.domain.CartDto;
import com.ezen.books.repository.PaymentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BooksApplicationTests {

	@Autowired
	private PaymentMapper paymentMapper;

	@Test
	void contextLoads() {
		// 임시로 장바구니에 값 넣기
		for (int i=0; i < 20; i++) {
			CartDto cartDto = CartDto.builder()
					.mno((long) (Math.random()*20 +1))
					.prno((long) (Math.random()*20 +1))
					.bookQty((int) (Math.random()*10 +1))
					.build();

			paymentMapper.addCart(cartDto);
		}
	}

}

package com.ezen.books.repository;

import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.ProductVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {

    int testAddCart(CartVO cartVO);

    List<CartVO> getAllCartItems(long mno);

    ProductVO getProductInfo(long prno);

    int storeCartDataToServer(CartVO cartData);

    int checkMnoAndPrno(long mno, long prno);

    void increaseBookQty(CartVO cartData);
}

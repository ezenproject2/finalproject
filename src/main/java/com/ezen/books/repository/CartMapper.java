package com.ezen.books.repository;

import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.ProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {

    int testAddCart(CartVO cartVO);

    List<CartVO> getAllCartItems(long mno);

    ProductVO getProductInfo(long prno);

    int storeCartDataToServer(CartVO cartData);

    int checkMnoAndPrno(@Param("mno") long mno, @Param("prno") long prno);

    void increaseBookQty(CartVO cartData);

    void deleteCartToServer(@Param("mno") long mno, @Param("prno") long prno);

    int getCartAmount(long mno);

    void deleteAllCartToServer(long mno);
}

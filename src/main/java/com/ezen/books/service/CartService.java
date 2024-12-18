package com.ezen.books.service;

import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.ProductVO;

import java.util.List;

public interface CartService {

    List<CartVO> getAllCartItems(long mno);

    ProductVO getProductInfo(long prnoFromCartDto);

    int storeCartDataToServer(CartVO cartData);
}

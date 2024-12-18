package com.ezen.books.service;

import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.repository.CartMapper;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Service
@RequiredArgsConstructor // final이 붙은 필드만 취급함.
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    @Override
    public List<CartVO> getAllCartItems(long mno) {
        return cartMapper.getAllCartItems(mno);
    }

    @Override
    public ProductVO getProductInfo(long prnoFromCartDto) {
        return cartMapper.getProductInfo(prnoFromCartDto);
    }

    @Override
    public int storeCartDataToServer(CartVO cartData) { return cartMapper.storeCartDataToServer(cartData); }
}

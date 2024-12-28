package com.ezen.books.service;

import com.ezen.books.domain.CartVO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.repository.CartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public int storeCartDataToServer(CartVO cartData) {
        long mnoData = cartData.getMno();
        long prnoData = cartData.getPrno();

        int alreadyExist = cartMapper.checkMnoAndPrno(mnoData, prnoData);
        if (0 < alreadyExist) {
            cartMapper.increaseBookQty(cartData);
            return 2;
        } else {
            cartMapper.storeCartDataToServer(cartData);
            return 1;
        }
    }

    @Override
    public int deleteCartToServer(long mno, long prno) {
        try{
            cartMapper.deleteCartToServer(mno, prno);
            return 1;
        } catch (Exception e) {
            log.info("Error in deleteCartToServer. Content: {}", e);
        }
        return 0;
    }

    @Override
    public int getCartAmount(long mno) {
        return cartMapper.getCartAmount(mno);
    }
}

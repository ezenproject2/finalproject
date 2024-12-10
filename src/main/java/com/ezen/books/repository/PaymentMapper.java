package com.ezen.books.repository;

import com.ezen.books.domain.CartDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapper {
    int addCart(CartDTO cartDto);

    List<CartDTO> getAllCartItems(long mno);
}

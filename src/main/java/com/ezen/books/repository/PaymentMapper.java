package com.ezen.books.repository;

import com.ezen.books.domain.CartDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentMapper {
    int addCart(CartDto cartDto);

    List<CartDto> getAllCartItems(long mno);
}

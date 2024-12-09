package com.ezen.books.service;

import com.ezen.books.domain.CartDto;

import java.util.List;

public interface PaymentService {
    List<CartDto> getAllCartItems(long mno);
}

package com.ezen.books.service;

import com.ezen.books.domain.CartDTO;

import java.util.List;

public interface PaymentService {
    List<CartDTO> getAllCartItems(long mno);
}

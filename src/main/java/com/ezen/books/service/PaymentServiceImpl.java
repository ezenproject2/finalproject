package com.ezen.books.service;

import com.ezen.books.domain.CartDTO;
import com.ezen.books.repository.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;

    @Override
    public List<CartDTO> getAllCartItems(long mno) {
        return paymentMapper.getAllCartItems(mno);
    }
}

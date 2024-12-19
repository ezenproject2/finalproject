package com.ezen.books.service;

import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.repository.OfflineMapper;
import com.ezen.books.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatbotServiceImpl implements ChatbotService{

    private final ProductMapper productMapper;
    private final OfflineMapper offlineMapper;

    @Override
    public ProductVO chatGetProductVO(String userMessage) {
        return productMapper.chatGetProductVO(userMessage);
    }

    @Override
    public OfflineStoreVO chatGetOfflineStoreVO(String userMessage) {
        return offlineMapper.chatGetOfflineStoreVO(userMessage);
    }
}

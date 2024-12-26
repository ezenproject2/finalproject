package com.ezen.books.service;

import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.domain.ProductVO;

import java.util.Map;

public interface ChatbotService {

    ProductVO chatGetProductVO(String userMessage);

    OfflineStoreVO chatGetOfflineStoreVO(String userMessage);
}

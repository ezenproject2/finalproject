package com.ezen.books.service;

import com.ezen.books.domain.OfflineBookVO;
import com.ezen.books.domain.OfflineStoreVO;

import java.util.List;

public interface OfflineService {

    OfflineStoreVO getDetail(String name);

    int testStockInsert(long prno);

    List<OfflineBookVO> getStock(long prno);
}

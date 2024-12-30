package com.ezen.books.repository;

import com.ezen.books.domain.OfflineBookVO;
import com.ezen.books.domain.OfflineStoreVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OfflineMapper {

    OfflineStoreVO getDetail(String name);

    long getOfflineQty();

    int isValid(@Param("osno") long i, @Param("prno") long prno);

    int testStockUpdate(@Param("osno") long i, @Param("prno") long prno);

    int testStockInsert(@Param("osno") long i, @Param("prno") long prno);

    OfflineBookVO getOfflineBookVO(@Param("osno") long i, @Param("prno") long prno);

    OfflineStoreVO chatGetOfflineStoreVO(String userMessage);

    List<Long> getPickupStoreOsno(@Param("prno") long prno, @Param("bookQty") int bookQty);

    OfflineStoreVO getStoreVOByOsno(Long osno);

    String getStoreName(long osno);
}

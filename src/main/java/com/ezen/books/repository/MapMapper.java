package com.ezen.books.repository;

import com.ezen.books.domain.OfflineStoreVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MapMapper {

    OfflineStoreVO getDetail(String name);

}

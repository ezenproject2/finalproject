package com.ezen.books.repository;

import com.ezen.books.domain.InquiryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InquiryMapper {
    int insert(InquiryVO inquiryVO);
}

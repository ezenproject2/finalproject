package com.ezen.books.repository;

import com.ezen.books.domain.InquiryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InquiryMapper {
    int insert(InquiryVO inquiryVO);

    List<InquiryVO> getInquiriesByMno(long mno);

    List<InquiryVO> getAllInquiries(String status);

    InquiryVO getInquiryByIno(long ino);

    int updateInquiry(InquiryVO inquiryVO);
}

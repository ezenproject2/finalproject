package com.ezen.books.service;

import com.ezen.books.domain.InquiryVO;

import java.util.List;

public interface InquiryService {
    int insert(InquiryVO inquiryVO);

    List<InquiryVO> getInquiriesByMno(long mno);

    List<InquiryVO> getAllInquiries(String status);

    InquiryVO getInquiriesByIno(long ino);

    int updateInquiry(InquiryVO inquiryVO);
}

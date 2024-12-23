package com.ezen.books.service;

import com.ezen.books.domain.InquiryVO;
import com.ezen.books.repository.InquiryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class InquiryServiceImpl implements InquiryService{

    private final InquiryMapper inquiryMapper;

    @Override
    public int insert(InquiryVO inquiryVO) {
        int isOk = inquiryMapper.insert(inquiryVO);
        return isOk;
    }
}

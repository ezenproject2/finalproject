package com.ezen.books.service;

import com.ezen.books.domain.InquiryVO;
import com.ezen.books.repository.InquiryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<InquiryVO> getInquiriesByMno(long mno) {
        return inquiryMapper.getInquiriesByMno(mno);
    }

    @Override
    public List<InquiryVO> getAllInquiries(String status) {

        List<InquiryVO> inquiryVOList = inquiryMapper.getAllInquiries(status);

        for(InquiryVO inquiryVO : inquiryVOList){
            String loginId = inquiryMapper.getLoginId(inquiryVO.getIno());
            inquiryVO.setLoginId(loginId);
        }

        return inquiryVOList;
    }

    @Override
    public InquiryVO getInquiriesByIno(long ino) {
        return inquiryMapper.getInquiryByIno(ino);
    }

    @Override
    public int updateInquiry(InquiryVO inquiryVO) {
        return inquiryMapper.updateInquiry(inquiryVO);
    }
}

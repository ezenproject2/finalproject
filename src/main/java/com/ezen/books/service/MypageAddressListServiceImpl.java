package com.ezen.books.service;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.repository.MypageAddressListMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageAddressListServiceImpl implements MypageAddressListService {

    private final MypageAddressListMapper mypageAddressListMapper;

    @Override
    public List<AddressVO> getAllAddr(long mno) {
        return mypageAddressListMapper.getAllAddr(mno);
    }

    @Override
    public MemberVO getMember(long mno) {
        return mypageAddressListMapper.getMember(mno);
    }

    @Override
    public GradeVO getMemberGrade(Long gno) {
        return mypageAddressListMapper.getMemberGrade(gno);
    }

    @Override
    public String registerAddr(AddressVO addressData) {
//        log.info("registerAddr start.");
//        log.info("The addrData from registerAddr: {}", addressData);
//        log.info("The isDefault of addrData: {}", addressData.getIsDefault());

        // 기본 배송지가 아니면 그냥 넣고, 기본 배송지면 기존의 기본 배송지를 isDefault = N으로 바꾼 후 넣음.
        try {
            if(addressData.getIsDefault().equals("Y")) {
                log.info("The addr is default.");
                mypageAddressListMapper.setAllAddrNotDefault();
            }

            log.info("registering the addr.");
            mypageAddressListMapper.registerAddr(addressData);
            return "succeeded";
        } catch (Exception e) {
            log.info("Error during registering the addr. Content: {}", e);
            return "failed";
        }
    }

    @Override
    public int deleteAddr(long adnoData) {
        try {
            mypageAddressListMapper.deleteAddr(adnoData);
            return 1;
        } catch (Exception e) {
            log.info("Error during deleting the addr. Content: {}", e);
        }
        return 0;
    }

    @Transactional
    @Override
    public int modifyAddr(AddressVO addressData) {

        try {
            // 수정된 배송지가 신규 기본 배송지가 되었다면 기존의 기본 배송지의 isDefault = "N"로 함.
            if(addressData.getIsDefault().equals("Y")) {
                mypageAddressListMapper.setAllAddrNotDefault();
            }
            // 수정되기 전의 배송지의 adno를 찾아 삭제한 다음 수정된 배송지(addressData)를 삽입함
            mypageAddressListMapper.deleteAddr(addressData.getAdno());
            mypageAddressListMapper.registerAddr(addressData);
            return 1;
        } catch (Exception e) {
            log.info("Error modifying the addr. Content: {}", e);
        }
        return 0;
    }
}

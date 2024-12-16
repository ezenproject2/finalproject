package com.ezen.books.repository;

import com.ezen.books.domain.AddressVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayoutMapper {

    AddressVO getDefaultAddress(long mno);
}

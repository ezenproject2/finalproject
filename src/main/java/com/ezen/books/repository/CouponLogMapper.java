package com.ezen.books.repository;

import com.ezen.books.domain.CouponLogVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponLogMapper {
    void insertCouponLog(CouponLogVO couponLogVO);

}

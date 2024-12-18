package com.ezen.books.repository;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.OrdersVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayoutMapper {

    AddressVO getDefaultAddress(long mno);

    int saveOrdersToServer(OrdersVO ordersVO);
}

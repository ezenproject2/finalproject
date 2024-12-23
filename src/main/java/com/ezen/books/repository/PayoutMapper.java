package com.ezen.books.repository;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.OrderDetailVO;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.domain.PaymentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayoutMapper {

    AddressVO getDefaultAddress(long mno);

    int saveOrdersToServer(OrdersVO ordersVO);

    int saveOrderDetailToServer(OrderDetailVO orderDetail);

    int savePaymentToServer(PaymentVO paymentData);

    void removeCartToServer(@Param("mno") long mno, @Param("prno") long prno);
}

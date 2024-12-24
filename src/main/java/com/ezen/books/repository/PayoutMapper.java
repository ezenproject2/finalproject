package com.ezen.books.repository;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.OrderDetailVO;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.domain.PaymentVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayoutMapper {

    AddressVO getDefaultAddress(long mno);

    int saveOrdersToServer(OrdersVO ordersVO);

    int saveOrderDetailToServer(OrderDetailVO orderDetail);

    int savePaymentToServer(PaymentVO paymentData);

    void removeCartToServer(long mno, long prno);

    int registerDefaultAddress(AddressVO addressData);
}

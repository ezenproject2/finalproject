package com.ezen.books.repository;

import com.ezen.books.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PayoutMapper {

    AddressVO getDefaultAddress(long mno);

    int saveOrdersToServer(OrdersVO ordersVO);

    int saveOrderDetailToServer(OrderDetailVO orderDetail);

    int savePaymentToServer(PaymentVO paymentData);

    void removeCartToServer(@Param("mno") long mno,@Param("prno") long prno);

    int registerDefaultAddress(AddressVO addressData);

    int saveDeliveryToServer(DeliveryVO deliveryData);

    int savePickupToServer(PickUpVO pickupData);

    int getTotalCount(@Param("pagingVO") PagingVO pagingVO, @Param("mno") long mno);

    long getMnoByOrno(String orno);
}

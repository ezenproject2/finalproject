package com.ezen.books.repository;

import com.ezen.books.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MypageOrderListMapper {
    List<OrdersVO> getAllOrderList(long mno);

    List<OrderDetailVO> getOrderDetailList(String orno);

    List<String> getOrnoList(long mno);

    OrderDetailVO getOrderDetail(String orno);

    ProductVO getProduct(long prno);

    String getImpUid(String orno);

    int applyRefundToOrderDetail(@Param("odno") long odno,@Param("orno") String orno);

    MemberVO getMember(long mno);

    GradeVO getMemberGrade(Long gno);

    OrdersVO getOrder(String orno);

    List<Long> getOdnoList(String orno);

    OrderDetailVO findOrderDetail(Long odno);

    DeliveryVO getDelivery(String orno);

    PickUpVO getPickUp(String orno);

    OfflineStoreVO getOfflineStore(long osno);

    PaymentVO getPayment(String orno);
}

package com.ezen.books.repository;

import com.ezen.books.domain.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderListMapper {
    List<OrdersVO> getAllOrderList(long mno);

    List<OrderDetailVO> getOrderDetailList(String orno);

    List<String> getOrnoList(long mno);

    OrderDetailVO getOrderDetail(String orno);

    ProductVO getProduct(long prno);

    String getImpUid(String orno);

    int applyRefundToOrderDetail(long odno, String orno);

    MemberVO getMember(long mno);

    GradeVO getMemberGrade(Long gno);
}

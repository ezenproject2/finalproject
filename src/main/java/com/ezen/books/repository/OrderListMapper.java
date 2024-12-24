package com.ezen.books.repository;

import com.ezen.books.domain.OrderDetailVO;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.domain.ProductVO;
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
}

package com.ezen.books.domain;

import com.ezen.books.constants.OrdersStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrdersDTO {
    private long orno;
    private long mno;
    private OrdersStatus ordersStatus;
    private int totalPrice;
    private LocalDateTime orderAt;
    private String isPickup;
}

package com.ezen.books.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public enum OrdersStatus {
    PENDING, PROCESSING, DELIVERING, CANCELLED, RETURNED, COMPLETED
}

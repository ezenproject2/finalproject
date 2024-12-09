package com.ezen.books.constants;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public enum DeliveryStatus {
    SHIPPING, PREPARED, DELIVERING, FAILED, COMPLETED
}

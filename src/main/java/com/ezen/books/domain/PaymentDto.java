package com.ezen.books.domain;

import com.ezen.books.constants.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private long pano;
    private long orno;
    private String measure;
    private int price;
    private PaymentStatus paymentStatus;
    private LocalDateTime regAt;
    private String cardName;
    private String imp_uid;
}

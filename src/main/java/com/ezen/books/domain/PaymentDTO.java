package com.ezen.books.domain;

import com.ezen.books.constants.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private long pano;
    private long orno;
    private String measure;
    private int price;
    private PaymentStatus status;
    private LocalDateTime regAt;
    private String cardName;
    private String impUid;
}

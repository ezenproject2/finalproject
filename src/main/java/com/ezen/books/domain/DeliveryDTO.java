package com.ezen.books.domain;

import com.ezen.books.constants.DeliveryStatus;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {
    private long deno;
    private long orno;
    private String recName;
    private String recPhone;
    private String addrCode;
    private String addr;
    private String addrDetail;
    private String addrName;
    private String memo;
    private DeliveryStatus deliveryStatus;
}

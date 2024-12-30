package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class PickUpVO {

    private long puno;
    private String orno;
    private long osno;
    private String status;
    private String staff;
    private String regAt;

}

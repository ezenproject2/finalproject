package com.ezen.books.domain;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthVO {
    private long ano;
    private long mno;
    private String auth;
}
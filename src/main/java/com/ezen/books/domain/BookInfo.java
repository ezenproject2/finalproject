package com.ezen.books.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class BookInfo {
    // 책소개
    private String bookIntroTitle;
    private String bookIntroContent;

    // 출판사 서평
    private String publisherReviewTitle;
    private String publisherReviewContent;

    // 목차
    private String tableOfContentsTitle;
    private String tableOfContentsContent;

    // 별점별 인원수, 비율
    private int cnt1;
    private int cnt2;
    private int cnt3;
    private int cnt4;
    private int cnt5;

    private int per1;
    private int per2;
    private int per3;
    private int per4;
    private int per5;


}

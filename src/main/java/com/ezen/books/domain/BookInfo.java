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
}

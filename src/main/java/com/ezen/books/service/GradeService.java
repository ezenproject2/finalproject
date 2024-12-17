package com.ezen.books.service;

public interface GradeService {
    //회원 등급에 따른 포인트 비율
    int getPointRate(long mno);
}

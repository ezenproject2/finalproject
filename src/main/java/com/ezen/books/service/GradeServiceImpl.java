package com.ezen.books.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GradeServiceImpl implements GradeService{
    @Override
    public int getPointRate(long mno) {
        return 0;
    }
}

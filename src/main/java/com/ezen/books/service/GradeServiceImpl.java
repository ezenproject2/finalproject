package com.ezen.books.service;

import com.ezen.books.domain.GradeVO;
import com.ezen.books.repository.GradeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GradeServiceImpl implements GradeService{
    private final GradeMapper gradeMapper;

    @Override
    public int getPointRate(long mno) {
        return 0;
    }

    @Override
    public GradeVO getGradeByGno(Long gno) {
        return gradeMapper.getGradeByGrade(gno);
    }
}

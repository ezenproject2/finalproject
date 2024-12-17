package com.ezen.books.repository;

import com.ezen.books.domain.GradeVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GradeMapper {
    GradeVO getGradeById(Long gno);

    GradeVO getGradeByMember(long mno);

    /*------------------------------------------------------------*/
    int getPointRateByGrade(long mno);
}

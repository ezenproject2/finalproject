package com.ezen.books.repository;

import com.ezen.books.domain.NoticeFileVO;
import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {

    int register(NoticeVO noticeVO);

    long getLastNtno();

    NoticeTempFileVO findByAddr(String fileAddr);

    void registerFile(NoticeFileVO noticeFileVO);

    void deleteTempFile(long fino);

    int registerTempFile(NoticeTempFileVO noticeTempFileVO);
}

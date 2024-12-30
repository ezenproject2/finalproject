package com.ezen.books.repository;

import com.ezen.books.domain.NoticeFileVO;
import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {

    int register(NoticeVO noticeVO);

    long getLastNtno();

    NoticeTempFileVO findTempByAddr(String fileAddr);

    NoticeFileVO findFileByAddr(String path);

    int deleteTempFile(long fino);

    int deleteFile(long fino);

    int registerTempFile(NoticeTempFileVO noticeTempFileVO);

    int registerFile(NoticeFileVO noticeFileVO);


    NoticeFileVO findFileByUuid(String uuid);

    NoticeTempFileVO findTempByUuid(String uuid);
}

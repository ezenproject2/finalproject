package com.ezen.books.service;

import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import com.ezen.books.domain.PagingVO;

import java.util.List;

public interface NoticeService {

    int register(NoticeVO noticeVO, List<String> fileAddrList);

    int tempSave(NoticeTempFileVO noticeTempFileVO);

    int deleteFile(String uuid);

    int getTotalCount(PagingVO pagingVO);

    List<NoticeVO> getList(PagingVO pagingVO);
}

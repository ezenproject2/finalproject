package com.ezen.books.service;

import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;

import java.util.List;

public interface NoticeService {

    int register(NoticeVO noticeVO, List<String> fileAddrList);

    int tempSave(NoticeTempFileVO noticeTempFileVO);

    int deleteFile(String uuid);
}

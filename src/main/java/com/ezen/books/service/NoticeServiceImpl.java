package com.ezen.books.service;

import com.ezen.books.domain.NoticeFileVO;
import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import com.ezen.books.repository.NoticeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService{

    private final NoticeMapper noticeMapper;

    @Transactional
    @Override
    public int register(NoticeVO noticeVO, List<String> fileAddrList) {
        log.info(">>>> noticeServiceImpl In!! > {}", noticeVO);
        int isOk = noticeMapper.register(noticeVO);

        if(isOk>0 && !fileAddrList.isEmpty()){

            long ntno = noticeMapper.getLastNtno();

            for(String fileAddr : fileAddrList){
                NoticeTempFileVO noticeTempFileVO = noticeMapper.findByAddr(fileAddr);
                if(noticeTempFileVO != null){
                    NoticeFileVO noticeFileVO = NoticeFileVO.builder()
                            .ntno(ntno)
                            .fileAddr(fileAddr)
                            .build();
                    // 최종 선택된 파일 경로만 저장
                    noticeMapper.registerFile(noticeFileVO);
                    // 임시 파일은 삭제
                    noticeMapper.deleteTempFile(noticeTempFileVO.getFino());
                }
            }
        }
        return isOk;
    }

    @Override
    public int tempSave(NoticeTempFileVO noticeTempFileVO) {
        int isOk = noticeMapper.registerTempFile(noticeTempFileVO);
        return isOk;
    }
}

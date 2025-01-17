package com.ezen.books.service;

import com.ezen.books.domain.NoticeFileVO;
import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import com.ezen.books.domain.PagingVO;
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
                NoticeTempFileVO noticeTempFileVO = noticeMapper.findTempByAddr(fileAddr);
                if(noticeTempFileVO != null){
                    NoticeFileVO noticeFileVO = NoticeFileVO.builder()
                            .ntno(ntno)
                            .fileAddr(fileAddr)
                            .build();
                    // 최종 선택된 파일 경로만 저장
                    isOk *= noticeMapper.registerFile(noticeFileVO);
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

    @Override
    public int deleteFile(String uuid) {
        int isOk = 1;

        NoticeFileVO noticeFileVO = noticeMapper.findFileByUuid(uuid);
        if(noticeFileVO != null){
            isOk *= noticeMapper.deleteFile(noticeFileVO.getFino());
        }

        NoticeTempFileVO noticeTempFileVO = noticeMapper.findTempByUuid(uuid);
        if(noticeTempFileVO != null){
            isOk *= noticeMapper.deleteTempFile(noticeTempFileVO.getFino());
        }

        return isOk;
    }

    @Override
    public int getTotalCount(PagingVO pagingVO) {
        return noticeMapper.getTotalCount(pagingVO);
    }

    @Override
    public List<NoticeVO> getList(PagingVO pagingVO) {
        return noticeMapper.getList(pagingVO);
    }

    @Override
    public NoticeVO getDetail(long ntno) {
        return noticeMapper.getDetail(ntno);
    }

    @Override
    public int update(NoticeVO noticeVO, List<String> fileAddrList) {
        int isOk = noticeMapper.update(noticeVO);

        if(isOk>0 && !fileAddrList.isEmpty()){
            long ntno = noticeVO.getNtno();

            for(String fileAddr : fileAddrList){
                NoticeTempFileVO noticeTempFileVO = noticeMapper.findTempByAddr(fileAddr);
                if(noticeTempFileVO != null){
                    NoticeFileVO noticeFileVO = NoticeFileVO.builder()
                            .ntno(ntno)
                            .fileAddr(fileAddr)
                            .build();
                    // 최종 선택된 파일 경로만 저장
                    isOk *= noticeMapper.registerFile(noticeFileVO);
                    // 임시 파일은 삭제
                    noticeMapper.deleteTempFile(noticeTempFileVO.getFino());
                }
            }
        }
        return isOk;
    }

    @Override
    public List<NoticeVO> getMain() {
        return noticeMapper.getMain();
    }

    @Override
    public int delete(long ntno) {
        return noticeMapper.delete(ntno);
    }


}

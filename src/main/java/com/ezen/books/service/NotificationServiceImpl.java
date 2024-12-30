package com.ezen.books.service;

import com.ezen.books.controller.NotificationController;
import com.ezen.books.domain.NotificationVO;
import com.ezen.books.repository.NotificationMapper;
import com.ezen.books.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService{

    private final NotificationMapper notificationMapper;
    private final ProductMapper productMapper;

    @Override
    public List<NotificationVO> getNotificationsByMno(long mno) {
        List<NotificationVO> list = notificationMapper.getNotificationsByMno(mno);
        for(NotificationVO notificationVO : list){
            if(notificationVO.getType().equals("리뷰")){
                notificationVO.setIsbn(productMapper.getIsbnByRno(notificationVO.getRno()));
            }
        }
        return list;
    }

    @Override
    public int updateNotificationStatus(long nfno) {
        return notificationMapper.updateNotificationStatus(nfno);
    }

}

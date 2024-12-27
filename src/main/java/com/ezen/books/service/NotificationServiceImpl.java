package com.ezen.books.service;

import com.ezen.books.controller.NotificationController;
import com.ezen.books.domain.NotificationVO;
import com.ezen.books.repository.NotificationMapper;
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

    @Override
    public List<NotificationVO> getNotificationsByMno(long mno) {
        return notificationMapper.getNotificationsByMno(mno);
    }

    @Override
    public void updateNotificationStatus(long nfno) {
        notificationMapper.updateNotificationStatus(nfno);
    }

}

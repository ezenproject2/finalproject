package com.ezen.books.service;

import com.ezen.books.domain.Notification;
import com.ezen.books.repository.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService{

    private final NotificationMapper notificationMapper;

    @Override
    public SseEmitter getNotificationsStream(long mno) {
        SseEmitter emitter = new SseEmitter(30L * 1000);  // 타임아웃 30초 설정

        // 알림을 클라이언트로 전송
        try {
            List<Notification> notifications = notificationMapper.findNotificationsByMno(mno);
            for (Notification notification : notifications) {
                emitter.send(notification);
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Override
    public List<Notification> getNotifications(long mno) {
        return notificationMapper.findNotificationsByMno(mno);
    }

    @Override
    public void addNotification(Notification notification) {

    }
}

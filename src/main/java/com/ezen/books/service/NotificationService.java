package com.ezen.books.service;

import com.ezen.books.domain.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    SseEmitter getNotificationsStream(long mno);

    List<Notification> getNotifications(long mno);

    void addNotification(Notification notification);
}

package com.ezen.books.service;

import com.ezen.books.domain.NotificationVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotificationService {

    List<NotificationVO> getNotificationsByMno(long mno);

    void updateNotificationStatus(long nfno);

}

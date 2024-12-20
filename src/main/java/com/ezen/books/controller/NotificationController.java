package com.ezen.books.controller;

import com.ezen.books.domain.Notification;
import com.ezen.books.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notification/*")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 연결을 위한 엔드포인트
    @GetMapping("/stream/{mno}")
    public SseEmitter getNotificationsStream(@PathVariable("mno") long mno) {
        return notificationService.getNotificationsStream(mno);
    }

    // 알림 목록을 가져오는 엔드포인트
    @GetMapping("/notification/{mno}")
    public List<Notification> getNotifications(@PathVariable long mno) {
        return notificationService.getNotifications(mno);
    }

}
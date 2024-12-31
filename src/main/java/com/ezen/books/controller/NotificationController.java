package com.ezen.books.controller;

import com.ezen.books.domain.NotificationVO;
import com.ezen.books.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import retrofit2.http.Path;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notification/*")
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final ConcurrentHashMap<Long, SseEmitter> emitterStorage = new ConcurrentHashMap<>();


    // SSE를 통해 실시간 알림 스트리밍
    @GetMapping(value = "/subscribe/{mno}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("mno") long mno) {
        SseEmitter sseEmitter = new SseEmitter(6000000L); // 10분 타임아웃

        // 연결 수명 관리
        sseEmitter.onCompletion(() -> {
            log.info(">>>> SSE 연결 완료: mno=" + mno);
        });

        sseEmitter.onTimeout(() -> {
            log.info(">>>> SSE 타임아웃 발생: mno=" + mno);
            sseEmitter.complete(); // 연결 종료
        });

        sseEmitter.onError((e) -> {
            log.error(">>>> SSE 연결 오류: mno=" + mno, e);
            sseEmitter.completeWithError(e);
        });

        // 알림이 업데이트되면 클라이언트에게 전송
        new Thread(() -> {
            try {
                List<NotificationVO> notifications = notificationService.getNotificationsByMno(mno);
                for (NotificationVO notification : notifications) {
                    log.info(">>>> 알림 전송: " + notification);
                    sseEmitter.send(notification); // 클라이언트로 알림 전송
                }
                Thread.sleep(6000000); // 10분 대기

            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        }).start();

        // 클라이언트가 연결될 때 SseEmitter 저장
        emitterStorage.put(mno, sseEmitter);

        return sseEmitter;
    }

    // 클라이언트에게 알림 전송
    public void sendNotificationToClient(long mno, NotificationVO notification) {
        SseEmitter emitter = emitterStorage.get(mno);
        if (emitter != null) {
            try {
                emitter.send(notification); // 실시간 알림 전송
            } catch (Exception e) {
                log.error("알림 전송 오류", e);
                emitter.completeWithError(e); // 오류 처리
            }
        }
    }

    // 알림 상태 업데이트 (읽음 처리)
    @GetMapping(value = "/update/{nfno}")
    public String updateNotificationStatus(@PathVariable("nfno") long nfno) {
        int isOk = notificationService.updateNotificationStatus(nfno);
        return isOk>0? "1" : "0";
    }


}
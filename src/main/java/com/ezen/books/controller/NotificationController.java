package com.ezen.books.controller;

import com.ezen.books.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notification/*")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

}
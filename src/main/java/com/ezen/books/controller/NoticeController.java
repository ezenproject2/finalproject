package com.ezen.books.controller;

import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import com.ezen.books.handler.FileHandler;
import com.ezen.books.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/notice/*")
@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final NoticeService noticeService;
    private final FileHandler fileHandler;

    @GetMapping("/list")
    public String list(){
        return "/notice/list";
    }

    @GetMapping("/register")
    public String register(){
        return "/notice/register";
    }

    @ResponseBody
    @PostMapping("/register")
    public String register(@RequestBody NoticeVO noticeVO){

        List<String> fileAddrList = fileHandler.extractUuids(noticeVO.getContent());
        int isOk = noticeService.register(noticeVO, fileAddrList);

        return isOk>0? "1" : "0";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> imageUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        String tempFileAddr = fileHandler.uploadNotice(file);
        log.info(">>>> tempFileAddr > {}", tempFileAddr);

        NoticeTempFileVO noticeTempFileVO = NoticeTempFileVO.builder()
                .fileAddr(tempFileAddr)
                .build();

        int isOk = noticeService.tempSave(noticeTempFileVO);

        if(isOk > 0){
            Map<String, String> response = new HashMap<>();
            //D:\_myproject\_java\_fileUpload\2024\11\28
            String path = "/upload/" + noticeTempFileVO.getFileAddr();
            log.info(">>>> path > {}", path);
            response.put("link", path);
            return response;
        }

        return null;
    }

}

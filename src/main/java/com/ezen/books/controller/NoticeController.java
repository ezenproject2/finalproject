package com.ezen.books.controller;

import com.ezen.books.domain.NoticeTempFileVO;
import com.ezen.books.domain.NoticeVO;
import com.ezen.books.domain.PagingVO;
import com.ezen.books.domain.ProductVO;
import com.ezen.books.handler.FileHandler;
import com.ezen.books.handler.PagingHandler;
import com.ezen.books.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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

    @GetMapping("/register")
    public String register(Model model, @RequestParam("category") String category){
        model.addAttribute("category", category);
        return "/notice/register";
    }

    @ResponseBody
    @PostMapping("/register")
    public String register(@RequestBody NoticeVO noticeVO){
        // 게시글 입력
        log.info(">>>> noticeVO > {}", noticeVO);
        // content 속 img 태그의 주소 경로 찾기
        List<String> fileAddrList = fileHandler.extractUuids(noticeVO.getContent());
        int isOk = noticeService.register(noticeVO, fileAddrList);

        return isOk>0? "1" : "0";
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> imageUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        // 게시글 입력 중 임시 파일 업로드
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

    @ResponseBody
    @GetMapping("/deleteFile")
    public String deleteFile(@RequestParam("uuid") String uuid){
        // 게시글 입력 중 파일 삭제
        int isOk = noticeService.deleteFile(uuid);
        return isOk>0? "1" : "0";
    }

    @GetMapping(value = "/list")
    public String list(Model model, PagingVO pagingVO){
        log.info(">>>> pagingVO > {}", pagingVO);

        int totalCount = noticeService.getTotalCount(pagingVO);
        PagingHandler ph = new PagingHandler(pagingVO, totalCount);
        List<NoticeVO> list = noticeService.getList(pagingVO);

        model.addAttribute("ph", ph);
        model.addAttribute("list", list);
        log.info(">>>> ph > {}", ph);
        log.info(">>>> list > {}", list);

        return "/notice/list";
    }

    @GetMapping("/detail")
    public String detail(Model model, long ntno){
        NoticeVO noticeVO = noticeService.getDetail(ntno);
        model.addAttribute("noticeVO", noticeVO);

        return "/notice/detail";
    }

    @GetMapping("/modify")
    public String modify(Model model, long ntno){
        NoticeVO noticeVO = noticeService.getDetail(ntno);
        model.addAttribute("noticeVO", noticeVO);

        return "/notice/modify";
    }

    @ResponseBody
    @PostMapping("/modify")
    public String modify(@RequestBody NoticeVO noticeVO){
        log.info(">>>> noticeVO > {}", noticeVO);

        List<String> fileAddrList = fileHandler.extractUuids(noticeVO.getContent());
        int isOk = noticeService.update(noticeVO, fileAddrList);

        return isOk>0? "1" : "0";
    }


}

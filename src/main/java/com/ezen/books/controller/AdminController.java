package com.ezen.books.controller;

import com.ezen.books.domain.InquiryVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.service.InquiryService;
import com.ezen.books.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/*")
@EnableScheduling
@Controller
public class AdminController {

    private final MemberService memberService;
    private final InquiryService inquiryService;


    @GetMapping("/userList")
    public void userList(Model model){
        List<MemberVO> userList = memberService.getList();
        model.addAttribute("members", userList);
    }

    @GetMapping("/inquiryList")
    public String inquiryList(Model model,
                              @RequestParam(value = "status", required = false) String status) {

        List<InquiryVO> inquiryVOAllList = inquiryService.getAllInquiries(status);

        model.addAttribute("inquiryList", inquiryVOAllList);
        model.addAttribute("stautus", status);
        return "/admin/inquiryList";
    }

    @PostMapping("/inquiry/answer")
    public String answerInquiry(@RequestParam("ino") long ino,
                                @RequestParam("response") String response,
                                Authentication authentication){

        InquiryVO inquiryVO = inquiryService.getInquiriesByIno(ino);
        inquiryVO.setResponse(response);
        inquiryVO.setStatus("답변 완료");

        int updateResult = inquiryService.updateInquiry(inquiryVO);

        return updateResult > 0 ?
                "redirect:/admin/inquiryList" : "redirect:/admin/inquiryList";

    }



}

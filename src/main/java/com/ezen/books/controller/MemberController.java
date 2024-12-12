package com.ezen.books.controller;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member/*")
@Controller
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/join")
    public void join(){}

    @PostMapping("/join")
    public String join(MemberVO memberVO, Model model){
        // 아이디 중복 체크
        if(memberService.checkLoginIdDuplicate(memberVO.getLoginId())){
            model.addAttribute("errorMessage", "이미 존재하는 ID입니다.");
            return "/member/join";
        }

        // 비밀번호 체크
        if(!memberVO.getPassword().equals(memberVO.getPasswordCheck())){
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
            return "/member/join";
        }
        String pwd = passwordEncoder.encode(memberVO.getPassword());
        String pwdCheck = passwordEncoder.encode(memberVO.getPasswordCheck());
        memberVO.setPassword(pwd);
        memberVO.setPasswordCheck(pwdCheck);

        memberService.insert(memberVO);
        log.info(">>>>> USER Info  {}", memberVO);
        return "/index";
    }

    @GetMapping("/login")
    public void login(){}



}

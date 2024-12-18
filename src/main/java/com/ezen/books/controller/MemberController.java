package com.ezen.books.controller;

import com.ezen.books.domain.MemberVO;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.service.MemberService;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

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
        String pwd = passwordEncoder.encode(memberVO.getPassword());
        String pwdCheck = passwordEncoder.encode(memberVO.getPasswordCheck());
        memberVO.setPassword(pwd);
        memberVO.setPasswordCheck(pwdCheck);

        model.addAttribute("memberVO", memberVO);

        memberService.insert(memberVO);
        log.info(">>>>> Join USER Info  {}", memberVO);

        return "/index";
    }

    @ResponseBody
    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Object>> checkId(@RequestParam("loginId") String loginId) {
        boolean isDuplicate = memberService.checkLoginIdDuplicate(loginId);
        Map<String, Object> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate); // true if duplicate, false if available
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login")
    public String login(Model model){
        log.info("Error message: " + model.asMap().get("error"));

        return "/member/login";
    }

    @PostMapping("/login")
    public String login(MemberVO memberVO, Model model) {

        if (memberVO.getLoginId() == null || memberVO.getLoginId().isEmpty()) {
            model.addAttribute("error", "아이디 또는 이메일을 입력해 주세요.");
            return "/member/login"; // Ensure this matches the actual Thymeleaf template name
        }

        if (memberVO.getPassword() == null || memberVO.getPassword().isEmpty()) {
            model.addAttribute("error", "비밀번호를 입력해 주세요.");
            return "/member/login"; // Ensure this matches the actual Thymeleaf template name
        }

        return "redirect:/";
    }


    @GetMapping("/modify")
    public void modify(){}

    @PostMapping("/modify")
    public String modify(MemberVO memberVO, HttpServletRequest request, HttpServletResponse response,
                         RedirectAttributes re){

        int isOk = memberService.updateMember(memberVO);
        logout(request, response);

        if (isOk > 0) {
            re.addFlashAttribute("mod_msg", "ok");
        } else {
            re.addFlashAttribute("mod_msg", "fail");
        }

        return "redirect:/";
    }


    @GetMapping("/remove")
    public String remove(HttpServletRequest request, HttpServletResponse response,
                         Principal principal, RedirectAttributes re){
        log.info(">>> {}", principal.toString());
        String loginId = principal.getName();
        int isOk = memberService.deleteMember(loginId);
        if(isOk > 0){
            re.addFlashAttribute("del_msg","ok");
        } else {
            re.addFlashAttribute("del_msg", "fail");
        }
        logout(request, response);
        return "redirect:/";
    }

    private void logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);
    }


//    @PutMapping("/udate-grade/{mno}")
//    public ResponseEntity<Void> udateGrade(@PathVariable long mno){
//        memberService.updateMemberGrade(mno);
//        return ResponseEntity.ok().build();
//    }
    @PostMapping("/updateGrade")
    public String updateGrade(@RequestParam("mno") long mno){
        // 회원의 등급을 갱신
        memberService.updateMemberGrade(mno);
        return "redirect:/member/profile?mno=" + mno;
    }



}

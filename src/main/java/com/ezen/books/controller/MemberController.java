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
//        // 아이디 중복 체크
//        if(memberService.checkLoginIdDuplicate(memberVO.getLoginId())){
//            model.addAttribute("errorMessage", "이미 존재하는 ID입니다.");
//            return "/member/join";
//        }
//
//        // 비밀번호 체크
//        if(!memberVO.getPassword().equals(memberVO.getPasswordCheck())){
//            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
//            return "/member/join";
//        }
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
        if (memberVO.getPassword() != null && !memberVO.getPassword().isEmpty()) {
            String pwd = passwordEncoder.encode(memberVO.getPassword());
            memberVO.setPassword(pwd);
        } else {
            //memberVO.setPassword(null);
            String existingPassword = memberService.getExistingPassword(memberVO.getLoginId());
            memberVO.setPassword(existingPassword);  // 기존 비밀번호 유지
        }

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

    // 회원 정보 페이지 (등급 표시)
    @GetMapping("/profile")
    public String getProfile(@RequestParam("mno") long mno, Model model){
        // 회원 정보 및 증급 정보를 가져옴
        MemberVO memberVO = memberService.getMemberByInfo(mno);
        model.addAttribute("memberVO", memberVO);

        return "/member/profile";
    }


//
//    @PostMapping("/order")
//    public ResponseEntity<?> placeOrder(@RequestBody OrdersVO ordersVO){
//        // 주문 정보 처리
//        int totalPrice = ordersVO.getTotalPrice();
//        long mno = ordersVO.getMno();
//        long orno = ordersVO.getOrno();
//
//        // 포인트 적립
//        memberService.addPoints(mno, orno, totalPrice);
//
//        // 쿠폰 지급
//        memberService.issueCoupons(mno, totalPrice);
//
//        return ResponseEntity.ok().build();
//    }









}

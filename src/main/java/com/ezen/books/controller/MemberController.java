package com.ezen.books.controller;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.service.MemberService;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> join(@RequestBody MemberVO memberVO, Model model){
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
        return new ResponseEntity<>("1", HttpStatus.OK);
    }

    // 배송지 입력 용도로 준희가 추가한 메서드.
    @PostMapping("/address")
    public ResponseEntity<String> storeAddressToServer(@RequestBody AddressVO addressVO) {
        // The addressVO from the client: AddressVO(adno=0, mno=0, recName=Test, recPhone=83892928383, addrCode=13536, addr=경기 성남시 분당구 판교역로 4 (백현동), addrDetail=test address detail, addrName=null, isDefault=null)
        log.info("The addressVO from the client: {}", addressVO);

        // 가장 최근에 회원가입한 mno를 address의 mno로 넣음.
        long mno = memberService.getLastMno();
        addressVO.setMno(mno);

        // The address from the client: AddressVO(adno=0, mno=9, recName=Tester, recPhone=83892928383, addrCode=13536, addr=경기 성남시 분당구 판교역로 4 (백현동), addrDetail=test address detail, addrName=null, isDefault=Y)
        log.info("The address from the client: {}", addressVO);
        int isDone = memberService.saveAddressToServer(addressVO);

        return (isDone > 0) ?
                new ResponseEntity<>("1", HttpStatus.OK) :
                new ResponseEntity<>("0", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // join 메서드를 한 후 /index로 가기 위한 메서드.
    @GetMapping("/go-to-index")
    public String goToIndexgoToIndex() {
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
    public void login(){}

    @PostMapping("/login")
    public String login(MemberVO memberVO, Model model) {

        MemberVO member = memberService.getMemberByInfo(memberVO.getLoginId());
        log.info(">>> memberVO {}", memberVO);
        log.info(">>> member {}", member);

        if (member == null || "Y".equals(member.getIsDel())) {
            model.addAttribute("error_msg", "아이디 또는 비밀번호가 잘못 되었습니다. 아이디와 비밀번호를 정확히 입력해 주세요.\n");
            return "/member/login"; // Ensure this matches the actual Thymeleaf template name
        }

        return "redirect:/"; // Redirect to home page after successful login
    }


    @GetMapping("/modify")
    public void modify(){}

    @PostMapping("/modify")
    public String modify(MemberVO memberVO, HttpServletRequest request, HttpServletResponse response,
                         RedirectAttributes re){
        if(memberVO.getPassword() != null && !memberVO.getPassword().isEmpty()){
            String pwd = passwordEncoder.encode(memberVO.getPassword());
            memberVO.setPassword(pwd);
        } else {
            memberVO.setPassword(null);
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



}

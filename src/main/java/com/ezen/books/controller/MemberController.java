package com.ezen.books.controller;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.service.MemberService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member/*")
@EnableScheduling
@Controller
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;


    @GetMapping("/join")
    public String join(Model model, HttpSession session){
        String email = (String) session.getAttribute("authenticatedEmail");
        if (email == null || email.isEmpty()) {
            return "redirect:/member/email_auth";
        }
        model.addAttribute("email", email);

        return "/member/join";
    }

    @PostMapping("/join")
    public String join(MemberVO memberVO, @ModelAttribute AddressVO addressVO, Model model,
                       HttpSession session){

        String pwd = passwordEncoder.encode(memberVO.getPassword());
        String pwdCheck = passwordEncoder.encode(memberVO.getPasswordCheck());
        memberVO.setPassword(pwd);
        memberVO.setPasswordCheck(pwdCheck);

        model.addAttribute("memberVO", memberVO);
        memberService.insert(memberVO);

        // ---- 배송지를 저장하기 위해 pjh가 삽입한 로직 ----
        log.info("The memberVO from the client: {}", memberVO);
        log.info("The addressVO from the client: {}", addressVO);

        String memberLoginId = memberVO.getLoginId();
        long memberMno = memberService.getMno(memberLoginId);

        addressVO.setMno(memberMno);
        addressVO.setRecName(memberVO.getName());
        addressVO.setRecPhone(memberVO.getPhoneNumber());
        addressVO.setAddrName("기본 배송지");
        addressVO.setIsDefault("Y");
        memberService.saveAddressToServer(addressVO);
        // ---- 배송지 입력 로직 끝 ----

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
    public String modify(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        log.info(">>>>>> loginId > {}", loginId);
        log.info(">>>> authentication {}",authentication);

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        model.addAttribute("memberVO", memberVO);

        return "/member/modify";
    }

    @PostMapping("/modify")
    public String modify(MemberVO memberVO, HttpServletRequest request, HttpServletResponse response,
                         RedirectAttributes re){
        if(memberVO.getPassword() != null && !memberVO.getPassword().isEmpty()){
            String pwd = passwordEncoder.encode(memberVO.getPassword());
            memberVO.setPassword(pwd);
        } else {
            memberVO.setPassword(null);
        }


        if(memberVO.getPassword() != null && !memberVO.getPassword().isEmpty()){
            String pwd = passwordEncoder.encode(memberVO.getPassword());
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

    // (cron="59 59 23 * * *") : 매일 23시59분59초에 실행
    @Scheduled(cron="30 20 14 * * *")
    public void updateAllMemberGrades() {
        try {
            memberService.updateAllMemberGrades(); // 모든 회원의 등급 갱신
            System.out.println("모든 회원 등급 갱신 완료");
        } catch (Exception e) {
            System.err.println("회원 등급 갱신 중 오류 발생: " + e.getMessage());
        }
    }


    // 아이디 찾기 페이지 이동
    @GetMapping("/id_find")
    public String idFind() {
        return "/member/id_find";
    }

    // 아이디 찾기 처리
    @PostMapping("/id_find")
    public String idFind(@RequestParam("email") String email,
                         @RequestParam("name") String name,
                         Model model) {
        MemberVO memberVO = memberService.selectMember(email, name);

        if(memberVO != null){
            model.addAttribute("foundId", memberVO.getLoginId());
        } else {
            model.addAttribute("error", "이메일 또는 이름이 일치하는 회원을 찾을 수 없습니다.");
        }
        return "/member/id_find";
    }


    @GetMapping("/pw_find")
    public void pwFind() {}

    // 이메일 인증 및 인증 번호 전송
    @RequestMapping(value = "/pw_auth.me", method = RequestMethod.POST)
    public ModelAndView pwAuth(Model model, HttpSession session, HttpServletRequest request) throws IOException {
        String email = request.getParameter("email");
        String loginId = request.getParameter("loginId");

        // 이메일과 이름 검증
        if (email == null || email.isEmpty() || !email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            return new ModelAndView("redirect:/member/pw_find?error");
        }
        if (loginId == null || loginId.isEmpty()) {
            return new ModelAndView("redirect:/member/pw_find?error");
        }

        MemberVO vo = memberService.selectMemberByEmail(email); // 회원 정보 조회

        if (vo != null) {
            Random r = new Random();
            String numStr = String.format("%06d", r.nextInt(1000000)); // 6자리 인증 번호 생성

            if (vo.getLoginId().equals(loginId)) {
                session.setAttribute("email", vo.getEmail());
                session.setAttribute("verificationCode", numStr); // 세션에 인증 코드 저장
                log.info("Stored verification code: {}", session.getAttribute("verificationCode"));
                log.info("User entered verification code: {}", numStr);

                // 이메일 전송
                String setfrom = "okchipojyh@gmail.com";
                String tomail = email;
                String title = "[이젠문고] 비밀번호변경 인증 이메일입니다";
                String content = "안녕하세요 회원님, 인증번호는 " + numStr + " 입니다.";

                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");

                    messageHelper.setFrom(setfrom);
                    messageHelper.setTo(tomail);
                    messageHelper.setSubject(title);
                    messageHelper.setText(content);

                    mailSender.send(message);
                    log.info("Verification email sent to {}", email);
                } catch (Exception e) {
                    log.error("Email sending failed: {}", e.getMessage());
                    return new ModelAndView("redirect:/member/email_error");
                }

                ModelAndView mv = new ModelAndView();
                mv.setViewName("/member/pw_auth");
                mv.addObject("num", numStr); // 인증 번호를 뷰로 전달
                return mv;
            } else {
                return new ModelAndView("redirect:/member/pw_find?error");
            }
        } else {
            return new ModelAndView("redirect:/member/pw_find?error");
        }
    }

    // 인증 번호 검증
    @RequestMapping(value = "/pw_set.me", method = RequestMethod.POST)
    public String pwSet(@RequestParam(value="email_injeung", required = false) String email_injeung,
                        @RequestParam(value = "num") String num,
                        Model model, HttpSession session) throws IOException {

        // 세션에 저장된 인증 코드와 비교
        String sessionCode = (String) session.getAttribute("verificationCode");
        if (sessionCode != null && sessionCode.equals(num)) {
            return "/member/pw_new"; // 인증 번호 일치
        } else {
            log.info("Authentication failed. Session Code: {} , User Code: {}", sessionCode, num);
            model.addAttribute("error","인증번호가 맞지않습니다. 다시 한번 확인해주세요.");
            return "/member/pw_auth"; // 인증 실패
        }
    }

    // 새로운 비밀번호 설정
    @RequestMapping(value = "/pw_new.me", method = RequestMethod.POST)
    public String pwNew(@ModelAttribute MemberVO memberVO, HttpSession session, Model model) {
        String email = (String) session.getAttribute("email"); // 세션에서 이메일 조회
        if (email == null) {
            log.error("Email not found in session.");
            return "/member/pw_find"; // 세션에 이메일이 없으면 다시 찾기 페이지로
        }

        String pwd = passwordEncoder.encode(memberVO.getPassword());
        String pwdCheck = passwordEncoder.encode(memberVO.getPasswordCheck());

        memberVO.setPassword(pwd);
        memberVO.setPasswordCheck(pwdCheck);
        memberVO.setEmail(email);

        int result = memberService.pwUpdate(memberVO);

        if (result == 1) {
            return "member/login"; // 비밀번호 변경 성공 시 로그인 화면으로 리디렉션
        } else {
            log.error("Password update failed: {}", result);
            return "/member/pw_new"; // 실패 시 다시 비밀번호 변경 페이지로
        }
    }

    @GetMapping("/email_auth")
    public void email_auth() {}

    @RequestMapping(value = "/sendEmailAuth", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> sendEmailAuth(@RequestBody Map<String, String> requestData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String email = requestData.get("email");

        // 이메일 형식 검증
        if (email == null || !email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            response.put("success", false);
            response.put("message", "이메일 형식이 잘못되었습니다.");
            return response;
        }

        // 이메일 중복 체크
        MemberVO memberVO = memberService.selectMemberByEmail(email);
        if (memberVO != null) {
            response.put("success", false);
            response.put("message", "이미 등록된 이메일입니다.");
            return response;
        }

        // 인증 코드 생성
        Random random = new Random();
        String verificationCode = String.format("%06d", random.nextInt(1000000));  // 6자리 인증 코드

        // 인증 코드를 세션에 저장
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("authenticatedEmail", email);  // 변경된 부분
        log.info("Stored verification code: {}", session.getAttribute("verificationCode"));
        log.info("User entered verification code: {}", verificationCode);

        // 이메일로 인증 코드 전송
        String setfrom = "okchipojyh@gmail.com";
        String tomail = email;
        String title = "[이젠문고] 회원가입 이메일 인증";
        String content = "안녕하세요 회원님, 인증번호는 " + verificationCode + " 입니다.";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");
            messageHelper.setFrom(setfrom);
            messageHelper.setTo(tomail);
            messageHelper.setSubject(title);
            messageHelper.setText(content);

            mailSender.send(message);
            log.info("Verification email sent to {}", email);
            response.put("success", true);
            response.put("message", "이메일이 전송되었습니다. 인증 코드를 확인하세요.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "이메일 전송 실패. 다시 시도해주세요.");
        }

        return response;
    }

    @RequestMapping(value = "/verifyEmailAuth", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> verifyEmailAuth(@RequestBody Map<String, String> requestData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        String email = requestData.get("email");
        String enteredCode = requestData.get("code");

        // 세션에서 저장된 인증 코드 가져오기
        String storedCode = (String) session.getAttribute("verificationCode");
        String storedEmail = (String) session.getAttribute("authenticatedEmail");  // 수정된 부분

        if (storedEmail == null || !storedEmail.equals(email)) {
            response.put("success", false);
            response.put("message", "잘못된 이메일입니다.");
            return response;
        }

        if (storedCode == null || !storedCode.equals(enteredCode)) {
            response.put("success", false);
            response.put("message", "인증번호가 일치하지 않습니다.");
            return response;
        }

        // 인증 성공 후 이메일을 세션에 저장
        session.setAttribute("authenticatedEmail", email);
        log.info("authenticatedEmail: {}", email);

        // 인증 성공 후, 회원가입 페이지로 리다이렉트
        response.put("success", true);
        response.put("message", "인증이 완료되었습니다.");
        return response;
    }


}

package com.ezen.books.controller;

import com.ezen.books.domain.UserVO;
import com.ezen.books.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@RequestMapping("/user/*")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService usv;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/join")
    public void signup(){}

    @PostMapping("/join")
    public String signup(UserVO userVO){
        userVO.setPwd(passwordEncoder.encode(userVO.getPwd()));
        int isOk = usv.insert(userVO);
        log.info(">>>UserDTO {}", userVO);
        return "/index";
    }

    @GetMapping("/login")
    public String login(){
        return "/user/login";
    }

    @GetMapping("/modify")
    public void modify(){}

    @PostMapping("/modify")
    public String modify(UserVO uvo, HttpServletRequest request, HttpServletResponse response,
                         RedirectAttributes re){
        log.info(">>> modify uvo > {}", uvo);
        int isOk = 0;
        if(uvo.getPwd().isEmpty()){
            isOk = usv.modifyPwdEmpty(uvo);
        } else{
            uvo.setPwd(passwordEncoder.encode(uvo.getPwd()));
            isOk = usv.modify(uvo);
        }
        logout(request,response);
        if(isOk > 0){
            re.addAttribute("modify_msg", "ok");
        } else {
            re.addAttribute("modify_msg", "fail");
        }
        return "redirect:/";
    }

    @GetMapping("/remove")
    public String remove(HttpServletRequest request, HttpServletResponse response,
                         Principal principal, RedirectAttributes re){
        log.info(principal.toString());
        String email = principal.getName();
        int isOk = usv.remove(email);
        if(isOk > 0){
            re.addFlashAttribute("remove_msg","ok");
        } else {
            re.addFlashAttribute("remove_msg", "fail");
        }
        logout(request, response);
        return "redirect:/";
    }

    private void logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);
    }

}
package com.ezen.books.controller;

import com.ezen.books.domain.*;
import com.ezen.books.handler.FileHandler;
import com.ezen.books.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/*")
@Controller
public class MypageController {

    // 서비스 의존성 주입
    private final PointService pointService;
    private final CouponService couponService;
    private final MemberService memberService;
    private final GradeService gradeService;
    private final FileHandler fileHandler;
    private final InquiryService inquiryService;

    /*-- 마이페이지 --*/
    @GetMapping("/main")
    public String myPageMain(Model model) {
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();  // 로그인한 사용자 이름 (아이디)
        log.info(">>>>>> loginId > {}", loginId);
        log.info(">>>> authentication {}",authentication);

        MemberVO memberVO = memberService.getMemberByInfo(loginId);  // 사용자 정보 조회
        model.addAttribute("memberVO", memberVO);
        long mno = memberVO.getMno();  // 회원 번호
        log.info(">>> MemberInfo > {}", memberVO);

        // 사용자 정보에 맞는 등급, 포인트, 쿠폰 리스트 조회
        GradeVO gradeVO = gradeService.getGradeByGno(memberVO.getGno());    // grade 정보 조회
        log.info(">>> GradeInfo > {}", gradeVO);
        int pointsBalance = pointService.getBalance(mno);
        List<CouponLogVO> couponList = couponService.findMemberCoupons(mno);
        model.addAttribute("coupons", couponList);

        // 모델에 데이터 추가
        model.addAttribute("gradeVO", gradeVO);
        model.addAttribute("pointsBalance", pointsBalance);
        model.addAttribute("coupons", couponList);

        return "/mypage/main";
    }

    @GetMapping("/inquiryList")
    public String inquiryList(Model model, Authentication authentication,
                              @RequestParam(value = "status", required = false) String status){
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();

        List<InquiryVO> inquiryVOList = inquiryService.getInquiriesByMno(mno);
        List<InquiryVO> inquiryVOAllList = inquiryService.getAllInquiries(status);

        model.addAttribute("inquiryList", inquiryVOList);

        model.addAttribute("inquiryList", inquiryVOAllList);
        model.addAttribute("stautus", status);

        return "/mypage/inquiryList";
    }

    @GetMapping("/inquiry")
    public void inquiry(){}

    @PostMapping("/inquiry")
    public String inquiry(InquiryVO inquiryVO,
                          @RequestParam(name="files", required = false)MultipartFile file,
                          Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();  // 로그인한 사용자 이름 (아이디)

        MemberVO memberVO = memberService.getMemberByInfo(loginId);  // 사용자 정보 조회
        model.addAttribute("memberVO", memberVO);
        long mno = memberVO.getMno();  // 회원 번호

        log.info("|| inquiryVO {}", inquiryVO);
        log.info("|| file > {}", file);

        inquiryVO.setMno(mno);

        if(file != null ){
            String files = fileHandler.uploadInquiry(file);
            log.info("|| fileAddr > {}", files);
            inquiryVO.setFileAddr(files);
        }
        int isOk = inquiryService.insert(inquiryVO);

        return isOk > 0 ? "/index" : "/mypage/inquiry";
    }

    @GetMapping("/coupon")
    public String memberCoupons(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();
        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();
        model.addAttribute("mno", mno);

        // 사용자 쿠폰 목록 조회
        List<CouponLogVO> couponList = couponService.findMemberCoupons(mno);
        model.addAttribute("coupons", couponList);

        return "/mypage/coupon";
    }

    @GetMapping("/point")
    public String pointsHistory(Model model, Authentication authentication){
        String loginId = authentication.getName();
        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();

        List<PointsVO> pointsHistory = pointService.getPointsHistory(mno);
        model.addAttribute("points", pointsHistory);

        return "/mypage/point";
    }



}

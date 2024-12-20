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
    @GetMapping(value = "/main")
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
        List<CouponVO> coupons = couponService.getMemberCoupons(mno);

        // 모델에 데이터 추가
        model.addAttribute("gradeVO", gradeVO);
        model.addAttribute("pointsBalance", pointsBalance);
        model.addAttribute("coupons", coupons);

        return "/mypage/main";
    }

    /*-- 포인트 적립 및 사용 --*/
    @PostMapping("/points/earn")
    public String earnPoints(@RequestParam("mno") long mno, @RequestParam("orno") long orno, @RequestParam("earnedPoints") int earnedPoints) {
        pointService.earnPoints(mno, orno, earnedPoints);  // 포인트 적립

        return "redirect:/order/confirmation?orno=" + orno;  // 주문 확인 페이지로 리다이렉트
    }

    @PostMapping("/points/use")
    public String usePoints(@RequestParam("mno") long mno, @RequestParam("orno") long orno, @RequestParam("usePoints") int usePoints) {
        pointService.usePoints(mno, orno, usePoints);  // 포인트 사용

        return "redirect:/order/confirmation?orno=" + orno;  // 주문 확인 페이지로 리다이렉트
    }

    /*-- 쿠폰 적용 --*/
    @PostMapping("/coupons/apply")
    public String applyCoupon(@RequestParam("mno") long mno, @RequestParam("cno") long cno, @RequestParam("orno") long orno) {
        couponService.applyCoupon(mno, cno, orno);  // 쿠폰 적용

        return "redirect:/order/confirmation?orno=" + orno;  // 주문 확인 페이지로 리다이렉트
    }

    /*-- 주문 생성 --*/
    @PostMapping("/order/place")
    public String placeOrder(@ModelAttribute OrdersVO ordersVO, @RequestParam(value = "cno", required = false) Long cno) {
        long mno = ordersVO.getMno();
        long orno = ordersVO.getOrno();

        // 회원 등급 업데이트
        memberService.updateMemberGrade(mno);

        // 포인트 적립 (주문 금액에 따른 포인트 계산)
        int pointsEarned = (int) (ordersVO.getTotalPrice() * getPointRate(mno));
        pointService.addPoints(mno, pointsEarned, orno);

        // 쿠폰 적용
        if (cno != null) {
            couponService.applyCoupon(mno, cno, orno);
        }

        return "redirect:/order/confirmation?orno=" + orno;  // 주문 확인 페이지로 리다이렉트
    }

    /*-- 주문 확인 페이지 --*/
    @GetMapping("order/confirmation")
    public String orderConfirmation(@RequestParam("orno") long orno, Model model) {
        // 주문 정보 조회
        OrdersVO ordersVO = getOrderInfo(orno);
        model.addAttribute("orderVO", ordersVO);

        return "order/confirmation";  // 주문 확인 뷰 반환
    }

    /*-- 헬퍼 메서드 --*/
    private double getPointRate(long mno) {
        // 회원 등급에 맞는 포인트 비율 반환
        return memberService.getPointRateByGrade(mno);
    }

    private OrdersVO getOrderInfo(long orno) {
        // 주문 정보를 가져오는 로직 (현재는 예시로 빈 객체 반환)
        return new OrdersVO();
    }

    /*-- 데이터 조회 --*/

    // 포인트 잔액 조회
    @GetMapping("/points/balance/{mno}")
    public int getBalance(@PathVariable long mno) {
        return pointService.getBalance(mno);  // 포인트 잔액 조회
    }

    // 포인트 내역 조회
    @GetMapping("/points/history/{mno}")
    public List<PointsVO> getPointHistory(@PathVariable long mno) {
        return pointService.getPointHistory(mno);  // 포인트 내역 조회
    }

    // 회원 등급 조회
    @GetMapping("/grade/{mno}")
    public GradeVO getMemberGrade(@PathVariable long mno) {
        return couponService.getMemberGrade(mno);  // 회원 등급 조회
    }

    // 회원 쿠폰 조회
    @GetMapping("/coupons/{mno}")
    public List<CouponVO> getMemberCoupons(@PathVariable long mno) {
        return couponService.getMemberCoupons(mno);  // 회원의 쿠폰 리스트 조회
    }

    // 사용 가능한 쿠폰 조회
    @GetMapping("/coupons/available")
    public List<CouponVO> getAvailableCoupons(@RequestParam long gno, @RequestParam int purchaseAmount) {
        return couponService.getAvailableCoupons(gno, purchaseAmount);  // 사용 가능한 쿠폰 조회
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

        GradeVO gradeVO = gradeService.getGradeByGno(memberVO.getGno());  // grade 정보 조회
        model.addAttribute("gradeVO", gradeVO);

        inquiryVO.setMno(mno);

        if(file != null ){
            String files = fileHandler.uploadInquiry(file);
            log.info("|| fileAddr > {}", files);
            inquiryVO.setFileAddr(files);
        }
        int isOk = inquiryService.insert(inquiryVO);

        return isOk > 0 ? "/index" : "/mypage/inquiry";
    }


}

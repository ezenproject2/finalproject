package com.ezen.books.controller;

import com.ezen.books.domain.CouponVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.OrdersVO;
import com.ezen.books.domain.PointsVO;
import com.ezen.books.service.CouponService;
import com.ezen.books.service.MemberService;
import com.ezen.books.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/*")
@Controller
public class MypageController {

    private final PointService pointService;
    private final CouponService couponService;
    private final MemberService memberService;

    // 포인트 적립
    @PostMapping("/points/earn")
    public String eranPoints(@RequestParam("mno") long mno, @RequestParam("orno") long orno, @RequestParam("earnedPoints") int earnedPoints){
        pointService.earnPoints(mno, orno, earnedPoints);

        // 포인트 적립 후 주문 확인 페이지로 리다이렉트
        return "redirect:/order/confirmation?orno="+orno;
    }

    // 포인트 사용
    @PostMapping("/points/use")
    public String usePoints(@RequestParam("mno") long mno, @RequestParam("orno") long orno, @RequestParam("usePoints") int usePoints){
        pointService.usePoints(mno, orno, usePoints);
        return "redirect:/order/confirmation?orno="+orno;
    }

    // 쿠폰 적용
    @PostMapping("/coupons/apply")
    public String applyCoupon(@RequestParam("mno") long mno, @RequestParam("cno") long cno, @RequestParam("orno") long orno){
        couponService.applyCoupon(mno, cno, orno);
        return "redirect:/order/confirmation?orno="+orno;
    }

    // 주문 생성
    @PostMapping("/order/place")
    public String placeOrder(@ModelAttribute OrdersVO ordersVO,  @RequestParam(value = "cno", required = false) Long cno){
        long mno = ordersVO.getMno();
        long orno = ordersVO.getOrno();

        // 등급 업데이트
        memberService.updateMemberGrade(mno);

        // 포인트 적립
        int pointsEarned = (int)(ordersVO.getTotalPrice() * getPointRate(mno));
        pointService.addPoints(mno, pointsEarned, orno);

        // 쿠폰 적용
        if(cno != null){
            couponService.applyCoupon(mno, cno, orno);
        }

        return "redirect:/order/confirmation?orno="+orno;
    }

    // 주문 확인 페이지
    @GetMapping("order/confirmation")
    public String orderConfirmation(@RequestParam("orno") long orno, Model model){
        // 주문 정보 조회
        OrdersVO ordersVO = getOrderInfo(orno);
        model.addAttribute("orderVO", ordersVO);

        return "order/confirmation";
    }

    private double getPointRate(long mno){
        // 회원 등급에 맞는 point_rate를 반환하는 메서드
        return memberService.getPointRateByGrade(mno);
    }

    private OrdersVO getOrderInfo(long orno){
        // 주문 정보를 가져오는 로직
        return new OrdersVO();
    }

    /*--조회 구역--*/
    // 포인트 잔액 조회
    @GetMapping("/points/balance/{mno}")
    public int getBalance(@PathVariable long mno){
        return pointService.getBalance(mno);
    }

    // 포인트 내역 조회
    @GetMapping("/points/history/{mno}")
    public List<PointsVO> getPointHistory(@PathVariable long mno){
        return pointService.getPointHistory(mno);
    }

    // 회원 등급 조회
    @GetMapping("/grade/{mno}")
    public GradeVO getMemberGrade(@PathVariable long mno){
        return couponService.getMemberGrade(mno);
    }

    // 회원 쿠폰 조회
    @GetMapping("/coupons/{mno}")
    public List<CouponVO> getMemberCoupons(@PathVariable long mno){
        return couponService.getMemberCoupons(mno);
    }

    // 사용 가능한 쿠폰 조회
    @GetMapping("/coupons/available")
    public List<CouponVO> getAvailableCoupons(@RequestParam long gno, @RequestParam int purchaseAmount){
        return couponService.getAvailableCoupons(gno, purchaseAmount);
    }





}

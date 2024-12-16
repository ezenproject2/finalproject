package com.ezen.books.controller;

import com.ezen.books.domain.CouponVO;
import com.ezen.books.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/coupons")
@Controller
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/{mno}")
    public List<CouponVO> getCoupons(@PathVariable long mno){
        return couponService.getAvailableCouponsForMember(mno);
    }

    @PostMapping("/apply/{mno}/{cno}")
    public ResponseEntity<Void> applyCoupon(@PathVariable long mno, @PathVariable long cno){
        couponService.applyCouponToOrder(mno, cno);
        return ResponseEntity.ok().build();
    }



}

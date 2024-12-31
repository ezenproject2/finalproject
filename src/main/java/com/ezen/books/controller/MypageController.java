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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ProductService productService;

    // 준희 담당 마이페이지를 위해 추가한 코드.
    private final AddressListService addressListService;
    private final OrderListService orderListService;

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

        model.addAttribute("gradeVO", gradeVO);
        model.addAttribute("pointsBalance", pointsBalance);
        model.addAttribute("coupons", couponList);

        /*---------서재-----------*/
        List<OrderDetailProductDTO> odpDTO = memberService.getRecentBooks(mno);
        model.addAttribute("odpDTO", odpDTO);

        return "/mypage/main";
    }

    @GetMapping("/inquiryList")
    public String inquiryList(Model model, Authentication authentication,
                              @RequestParam(value = "status", required = false) String status){
        myPageLeft(model, authentication);

        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();

        List<InquiryVO> inquiryVOList = inquiryService.getInquiriesByMno(mno);

        model.addAttribute("inquiryList", inquiryVOList);

        model.addAttribute("stautus", status);

        return "/mypage/inquiryList";
    }

    @GetMapping("/inquiry")
    public void inquiry(Model model, Authentication authentication){
        myPageLeft(model, authentication);
    }

    @PostMapping("/inquiry")
    public String inquiry(InquiryVO inquiryVO,
                          @RequestParam(name="files", required = false)MultipartFile file,
                          Model model, Authentication authentication){
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
    public String memberCoupons(Model model, Authentication authentication){
        myPageLeft(model, authentication);

        String loginId = authentication.getName();
        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();
        model.addAttribute("mno", mno);

        List<CouponLogVO> couponList = couponService.findMemberAllCoupons(mno);
        model.addAttribute("coupons", couponList);

        long availableCoupons = couponList.stream()
                .filter(c -> "사용 가능".equals(c.getStatus()))
                .count();
        model.addAttribute("availableCoupons", availableCoupons);

        List<CouponLogVO> expiringCoupons = couponService.getExpiringCouponsThisMonth(mno);
        model.addAttribute("expiringCoupons", expiringCoupons);

        return "/mypage/coupon";
    }

    @GetMapping("/point")
    public String pointsHistory(Model model, Authentication authentication){
        myPageLeft(model, authentication);

        String loginId = authentication.getName();
        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();

        List<PointsVO> pointsHistory = pointService.getPointsHistory(mno);
        model.addAttribute("points", pointsHistory);

        int expiringPoints = pointService.getExpiringPoints();
        model.addAttribute("expiringPoints", expiringPoints);

        return "/mypage/point";
    }

    private void myPageLeft(Model model, Authentication authentication){
        String loginId = authentication.getName();
        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        GradeVO gradeVO = gradeService.getGradeByGno(memberVO.getGno());
        int pointsBalance = pointService.getBalance(memberVO.getMno());

        model.addAttribute("memberVO", memberVO);
        model.addAttribute("gradeVO", gradeVO);
        model.addAttribute("pointsBalance", pointsBalance);
    }


    // 준희 담당 마이페이지를 위해 추가한 코드.
    @GetMapping("/order-list")
    public String showOrderList(Model model, Authentication authentication) {
        myPageLeft(model, authentication);
        log.info(" >>> MypageController: showOrderList start.");

        // mno를 얻기 위해 myPageMain의 있던 코드를 가져옴
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();

        boolean isOrderEmpty = orderListService.isOrderEmpty(mno);

        // 화면에 띄울 order_detail과 product의 정보를 가져옴.
        List<List<OrderDetailProductDTO>> orderDetailProductGroup = orderListService.getOrderDetailProductList(mno);
//        log.info("The orderDetailProductGroup: {}", orderDetailProductGroup);
        log.info("orderDetailProductGroup is empty or not :{}", orderDetailProductGroup.isEmpty());

        // 화면에 띄울 orders의 주문 날짜들을 가져옴.
        List<LocalDateTime> orderDateList = new ArrayList<>();
        for(List<OrderDetailProductDTO> groupOfOneOrder : orderDetailProductGroup) {
            String orno = groupOfOneOrder.get(0).getOrderDetailVO().getOrno();
            LocalDateTime orderDate = orderListService.getOrderDate(orno);
            orderDateList.add(orderDate);
        }
        log.info("The orderDateList: {}", orderDateList);

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = orderListService.getMember(mno);
        GradeVO memberGrade = orderListService.getMemberGrade(memberInfo.getGno());

        model.addAttribute("mno", mno);
        model.addAttribute("isOrderEmpty", isOrderEmpty);
        model.addAttribute("orderDetailProductGroup", orderDetailProductGroup);
        model.addAttribute("orderDateList", orderDateList);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("memberGrade", memberGrade);
        return "/mypage/order_list";
    }

    @GetMapping("/address-list")
    public String showAddressList(Model model, Authentication authentication) {
        myPageLeft(model, authentication);
        log.info(" >>> MypageController: showAddressList start.");

        // mno를 얻기 위해 myPageMain의 있던 코드를 가져옴
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();
        log.info("mno: {}", mno);

        // 화면에 띄울 List<AddressVO>를 가져옴.
        List<AddressVO> addrList =  addressListService.getAllAddr(mno);
        log.info("addrList: {}", addrList);

        boolean isAddrEmpty = addrList.isEmpty();

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = addressListService.getMember(mno);
        GradeVO memberGrade = addressListService.getMemberGrade(memberInfo.getGno());
        log.info("memberInfo: {}", memberInfo);
        log.info("memberGrade: {}", memberGrade);

        model.addAttribute("mno", mno);
        model.addAttribute("addrList", addrList);
        model.addAttribute("isAddrEmpty", isAddrEmpty);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("memberGrade", memberGrade);
        return "/mypage/address_list";
    }

}

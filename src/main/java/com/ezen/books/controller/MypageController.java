package com.ezen.books.controller;

import com.ezen.books.domain.*;
import com.ezen.books.handler.FileHandler;
import com.ezen.books.handler.PagingHandler;
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

    // 준희 담당 마이페이지를 위해 추가한 코드.
    private final AddressListService addressListService;
    private final OrderListService orderListService;
    private final PayoutService payoutService;

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



    // 준희 담당 마이페이지를 위해 추가한 코드.
    @GetMapping("/order-list")
    public String showOrderList(PagingVO pagingVO, Model model) {
        log.info(" >>> MypageController: showOrderList start.");

        // mno를 얻기 위해 myPageMain의 있던 코드를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();

        boolean isOrderEmpty = orderListService.isOrderEmpty(mno);

        // 화면에 띄울 order_detail과 product의 정보를 가져옴.
        List<List<OrderDetailProductDTO>> orderDetailProductGroup = orderListService.getOrderDetailProductList(mno);
        // log.info("The orderDetailProductGroup: {}", orderDetailProductGroup);
        log.info("orderDetailProductGroup is empty or not :{}", orderDetailProductGroup.isEmpty());

        // 화면에 띄울 orders의 정보를 가져옴.
        List<OrdersVO> orderList = new ArrayList<>();
        for(List<OrderDetailProductDTO> groupOfOneOrder : orderDetailProductGroup) {
            String orno = groupOfOneOrder.get(0).getOrderDetailVO().getOrno();
            OrdersVO order = orderListService.getOrder(orno);
            orderList.add(order);
        }

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = orderListService.getMember(mno);
        GradeVO memberGrade = orderListService.getMemberGrade(memberInfo.getGno());

        // 페이지네이션
        int totalCount = payoutService.getTotalCount(pagingVO, mno);
        PagingHandler ph = new PagingHandler(pagingVO, totalCount);
        log.info("The ph: {}", ph);

        model.addAttribute("mno", mno);
        model.addAttribute("isOrderEmpty", isOrderEmpty);
        model.addAttribute("orderDetailProductGroup", orderDetailProductGroup);
        model.addAttribute("orderList", orderList);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("memberGrade", memberGrade);
        model.addAttribute("ph", ph);
        return "/mypage/order_list";
    }

    @GetMapping("/address-list")
    public String showAddressList(Model model) {
        log.info(" >>> MypageController: showAddressList start.");

        // mno를 얻기 위해 myPageMain의 있던 코드를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

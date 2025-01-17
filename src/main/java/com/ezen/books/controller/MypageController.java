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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.core.joran.JoranConstants.NULL;

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
    private final MypageAddressListService mypageAddressListService;
    private final MypageOrderListService mypageOrderListService;
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

        List<InquiryVO> inquiryVOList;
        if (status != null && !status.isEmpty()) {
            inquiryVOList = inquiryService.getInquiriesByMnoAndStats(mno, status);
        } else {
            inquiryVOList = inquiryService.getInquiriesByMno(mno);
        }

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

        if (file != null && !file.isEmpty()) {
            String fileAddr = fileHandler.uploadInquiry(file);
            log.info("|| fileAddr > {}", fileAddr);
            inquiryVO.setFileAddr(fileAddr);
        } else {
            inquiryVO.setFileAddr(null);
        }
        int isOk = inquiryService.insert(inquiryVO);

        return isOk > 0 ? "redirect:/mypage/inquiryList" : "redirect:/mypage/inquiry";
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
    public String showOrderList(Model model, Authentication authentication, PagingVO pagingVO) {
        myPageLeft(model, authentication);
        log.info(" >>> MypageController: showOrderList start.");

        // 로그인 한 사용자의 mno 얻기
        long mno = fetchUserMno();

        boolean isOrderEmpty = mypageOrderListService.isOrderEmpty(mno);


        List<List<OrderDetailProductDTO>> orderDetailProductGroup = new ArrayList<>();
        List<OrdersVO> orderList = new ArrayList<>();
        if (isOrderEmpty) {
            //
        } else {
            // 화면에 띄울 order_detail과 product의 정보를 가져옴.
            orderDetailProductGroup = mypageOrderListService.getOrderDetailProductGroup(mno);
            // log.info("The orderDetailProductGroup: {}", orderDetailProductGroup);
            log.info("orderDetailProductGroup is empty or not :{}", orderDetailProductGroup.isEmpty());

            // 화면에 띄울 orders의 정보를 가져옴.
            for(List<OrderDetailProductDTO> groupOfOneOrder : orderDetailProductGroup) {
                String orno = groupOfOneOrder.get(0).getOrderDetailVO().getOrno();
                OrdersVO order = mypageOrderListService.getOrder(orno);
                orderList.add(order);
            }
        }

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = mypageOrderListService.getMember(mno);
        GradeVO memberGrade = mypageOrderListService.getMemberGrade(memberInfo.getGno());

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
    public String showAddressList(Model model, Authentication authentication) {
        myPageLeft(model, authentication);
        log.info(" >>> MypageController: showAddressList start.");

        // mno를 얻기 위해 myPageMain의 있던 코드를 가져옴
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();
        log.info("mno: {}", mno);

        // 화면에 띄울 List<AddressVO>를 가져옴.
        List<AddressVO> addrList =  mypageAddressListService.getAllAddr(mno);
        log.info("addrList: {}", addrList);

        boolean isAddrEmpty = addrList.isEmpty();

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = mypageAddressListService.getMember(mno);
        GradeVO memberGrade = mypageAddressListService.getMemberGrade(memberInfo.getGno());
        log.info("memberInfo: {}", memberInfo);
        log.info("memberGrade: {}", memberGrade);

        model.addAttribute("mno", mno);
        model.addAttribute("addrList", addrList);
        model.addAttribute("isAddrEmpty", isAddrEmpty);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("memberGrade", memberGrade);
        return "/mypage/address_list";
    }

    @GetMapping("order-detail")
    public String showOrderDetail(@RequestParam("orno") String orno, Model model, Authentication authentication) {
        myPageLeft(model, authentication);
        log.info(" >>> MypageController: showOrderDetail start.");
        log.info("The orno: {}", orno);
        Map<String, Object> modelAttrs = new HashMap<>();

        long mno = fetchUserMno();
        log.info("The mno: {}", mno);
//        modelAttrs.put("mno", mno);

        OrdersVO order = mypageOrderListService.getOrder(orno);
        log.info("The order: {}", order);
        modelAttrs.put("order", order);
        modelAttrs.put("order isPickup", order.getIsPickup());

        List<OrderDetailProductDTO> detailProductList = mypageOrderListService.getOrderDetailProductList(orno);
        log.info("The detailProductList: {}", detailProductList);
        modelAttrs.put("detailProductList", detailProductList);

        if(order.getIsPickup().equals("N")) {
            DeliveryVO delivery = mypageOrderListService.getDelivery(orno);
            log.info("The delivery: {}", delivery);
            modelAttrs.put("delivery", delivery);
            modelAttrs.put("pickUp", "none");
            modelAttrs.put("offlineStore", "none");
        } else if (order.getIsPickup().equals("Y")) {
            PickUpVO pickUp = mypageOrderListService.getPickUp(orno);
            OfflineStoreVO offlineStore = mypageOrderListService.getOfflineStore(pickUp.getOsno());
            modelAttrs.put("delivery", "none");
            log.info("The pickUp: {}", pickUp);
            log.info("The offlineStore: {}", offlineStore);
            modelAttrs.put("pickUp", pickUp);
            modelAttrs.put("offlineStore", offlineStore);
        }

        boolean isPickUpNone = (modelAttrs.get("pickUp").equals("none")) ? true : false;
        modelAttrs.put("isPickUpNone", isPickUpNone);

        PaymentVO payment = mypageOrderListService.getPayment(orno);
        modelAttrs.put("payment", payment);

        model.addAllAttributes(modelAttrs);

        /* yh-------------- */
        int usedCouponAmount = couponService.getCouponAmount(orno);
        int usedPointAmount = pointService.getPointAmount(orno);
        int totalAmount = usedCouponAmount + usedPointAmount;
        model.addAttribute("coupon", usedCouponAmount);
        model.addAttribute("point", usedPointAmount);
        model.addAttribute("total", totalAmount);
        /* ---------------- */

        return "mypage/order_detail";
    }

    private long fetchUserMno() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        MemberVO memberVO = memberService.getMemberByInfo(loginId);
        long mno = memberVO.getMno();
        log.info("mno: {}", mno);
        return mno;
    }

}

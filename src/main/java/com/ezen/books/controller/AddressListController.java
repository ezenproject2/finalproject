package com.ezen.books.controller;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.domain.GradeVO;
import com.ezen.books.domain.MemberVO;
import com.ezen.books.domain.OrderDetailProductDTO;
import com.ezen.books.service.AddressListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/address-list")
@Controller
public class AddressListController {

    private final AddressListService addressListService;

//    @GetMapping("/intro")
//    public String showAddressList(@RequestParam("mno") long mno, Model model) {
//        log.info(" >>> AddressListController: showAddressList start.");
//
//        // 화면에 띄울 List<AddressVO>를 가져옴.
//        List<AddressVO> addrList =  addressListService.getAllAddr(mno);
//
//        boolean isAddrEmpty = addrList.isEmpty();
//
//        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
//        MemberVO memberInfo = addressListService.getMember(mno);
//        GradeVO memberGrade = addressListService.getMemberGrade(memberInfo.getGno());
//
//        model.addAttribute("mno", mno);
//        model.addAttribute("addrList", addrList);
//        model.addAttribute("isAddrEmpty", isAddrEmpty);
//        model.addAttribute("memberInfo", memberInfo);
//        model.addAttribute("memberGrade", memberGrade);
//        return "/mypage/address_list";
//    }

    @PostMapping("/register")
    public String registerAddr(AddressVO addressData,
                               @RequestParam String mnoVal) {
        log.info(" >>> AddressListController: registerAddr start.");
        // AddressVO(adno=0, mno=0, recName=유한, recPhone=01073738282, addrCode=13536, addr=경기 성남시 분당구 판교역로 4 (백현동), addrDetail=콘트라베이스 12층, addrName=회사, isDefault=Y)
        log.info("The addressData from the client: {}", addressData);
        log.info("The mnoVal: {}", mnoVal);

        // AddressVO의 mno는 long인데 들어오는 mno는 String이라서 따로 가져온 후 넣어줌.
        addressData.setMno(Long.parseLong(mnoVal));
        String isDone = addressListService.registerAddr(addressData);

        if(isDone == "succeeded") {
            log.info("register addr succeeded.");
        } else {
            log.info("register addr failed.");
        }

        return "redirect:/mypage/address-list";
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteAddr(@RequestBody long adnoData) {
        log.info(" >>> AddressListController: deleteAddr start.");
        log.info("The adnoData from the client: {}", adnoData);

        int isDone = addressListService.deleteAddr(adnoData);
        if(isDone == 1) {
            log.info("delete addr successfully.");
        }

        return (isDone == 1) ? "1" : "0";
    }

    @PostMapping("/modify")
    public String modifyAddr(@RequestBody AddressVO addressData) {
        log.info(" >>> AddressListController: modifyAddr start.");
        log.info("The addressData from the client: {}", addressData);

        int isDone = addressListService.modifyAddr(addressData);
        if(isDone == 1) {
            log.info("modify addr successfully.");
        }

        return (isDone == 1) ? "1" : "0";
    }
}

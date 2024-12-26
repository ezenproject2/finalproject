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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/address-list")
@Controller
public class AddressListController {

    private final AddressListService addressListService;

    @GetMapping("/intro")
    public String showOrderList(@RequestParam("mno") long mno, Model model) {
        log.info(" >>> OrderListController: showOrderList start.");

        // 화면에 띄울 List<AddressVO>를 가져옴.
        List<AddressVO> addrList =  addressListService.getAllAddr(mno);

        boolean isAddrEmpty = addrList.isEmpty();

        // 화면에 띄울 사용자 정보와 등급 정보를 가져옴.
        MemberVO memberInfo = addressListService.getMember(mno);
        GradeVO memberGrade = addressListService.getMemberGrade(memberInfo.getGno());

        model.addAttribute("mno", mno);
        model.addAttribute("addrList", addrList);
        model.addAttribute("isAddrEmpty", isAddrEmpty);
        model.addAttribute("memberInfo", memberInfo);
        model.addAttribute("memberGrade", memberGrade);
        return "/mypage/address_list";
    }
}

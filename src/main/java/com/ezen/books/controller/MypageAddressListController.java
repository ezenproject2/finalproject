package com.ezen.books.controller;

import com.ezen.books.domain.AddressVO;
import com.ezen.books.service.MypageAddressListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mypage/address-list")
@Controller
public class MypageAddressListController {

    private final MypageAddressListService mypageAddressListService;

    @PostMapping("/register")
    public String registerAddr(AddressVO addressData,
                               @RequestParam String mnoVal) {
        log.info(" >>> AddressListController: registerAddr start.");
        // AddressVO(adno=0, mno=0, recName=유한, recPhone=01073738282, addrCode=13536, addr=경기 성남시 분당구 판교역로 4 (백현동), addrDetail=콘트라베이스 12층, addrName=회사, isDefault=Y)
        log.info("The addressData from the client: {}", addressData);
        log.info("The mnoVal: {}", mnoVal);

        // AddressVO의 mno는 long인데 들어오는 mno는 String이라서 따로 가져온 후 넣어줌.
        addressData.setMno(Long.parseLong(mnoVal));

        log.info("The isDefault is: {}", addressData.getIsDefault());

        String isDone = mypageAddressListService.registerAddr(addressData);
        log.info("Is Done: {}", isDone);

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

        int isDone = mypageAddressListService.deleteAddr(adnoData);
        if(isDone == 1) {
            log.info("delete addr successfully.");
        } else {
            log.info("delete addr failed.");
        }

        return (isDone == 1) ? "1" : "0";
    }

    @PostMapping("/modify")
    @ResponseBody
    public String modifyAddr(@RequestBody AddressVO addressData) {
        log.info(" >>> AddressListController: modifyAddr start.");
        log.info("The addressData from the client: {}", addressData);

        int isDone = mypageAddressListService.modifyAddr(addressData);
        if(isDone == 1) {
            log.info("modify addr successfully.");
        } else {
            log.info("modify addr failed.");
        }

        return (isDone == 1) ? "1" : "0";
    }
}

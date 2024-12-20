package com.ezen.books.controller;

import com.ezen.books.domain.OfflineBookVO;
import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.service.OfflineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/offline/*")
@RestController
public class OfflineController {

    private final OfflineService offlineService;

    @ResponseBody
    @GetMapping(value = "/getDetail/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OfflineStoreVO getDetail(@PathVariable("name") String name){
        OfflineStoreVO offlineStoreVO = offlineService.getDetail(name);

        return offlineStoreVO;
    }

    @ResponseBody
    @GetMapping(value = "/getStock/{prno}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OfflineBookVO> getStock(@PathVariable("prno") long prno){
        List<OfflineBookVO> list = offlineService.getStock(prno);
//        log.info(">>>> 오프라인 매장 재고 > {}", list);
        return list;
    }

}

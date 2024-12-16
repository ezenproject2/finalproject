package com.ezen.books.controller;

import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/map/*")
@RestController
public class MapController {

    private final MapService mapService;

    @ResponseBody
    @GetMapping(value = "/getDetail/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public OfflineStoreVO getDetail(@PathVariable("name") String name){
        OfflineStoreVO offlineStoreVO = mapService.getDetail(name);

        return offlineStoreVO;
    }


}

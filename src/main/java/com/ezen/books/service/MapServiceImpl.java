package com.ezen.books.service;

import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.repository.MapMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MapServiceImpl implements MapService {

    private final MapMapper mapMapper;

    @Override
    public OfflineStoreVO getDetail(String name) {
        return mapMapper.getDetail(name);
    }
}

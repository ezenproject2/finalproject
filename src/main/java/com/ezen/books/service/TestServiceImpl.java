package com.ezen.books.service;

import com.ezen.books.repository.TestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class TestServiceImpl implements TestService{
    private final TestMapper testMapper;
}

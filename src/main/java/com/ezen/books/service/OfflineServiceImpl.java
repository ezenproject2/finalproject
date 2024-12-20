package com.ezen.books.service;

import com.ezen.books.domain.OfflineBookVO;
import com.ezen.books.domain.OfflineStoreVO;
import com.ezen.books.repository.OfflineMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class OfflineServiceImpl implements OfflineService {

    private final OfflineMapper offlineMapper;

    @Override
    public OfflineStoreVO getDetail(String name) {
        return offlineMapper.getDetail(name);
    }

    @Override
    public int testStockInsert(long prno) {
        long offlineQty = offlineMapper.getOfflineQty();
        int isOk = 1;
        for(long i = 1; i<=offlineQty; i++){
            // 이미 있는지 확인
            int isValid = offlineMapper.isValid(i, prno);
            if(isValid>0){
                // 이미 있다면 update 구문 사용
                isOk *= offlineMapper.testStockUpdate(i, prno);
            }else{
                // 없다면 insert 구문 사용
                isOk *= offlineMapper.testStockInsert(i, prno);
            }
        }
        return isOk;
    }

    @Override
    public List<OfflineBookVO> getStock(long prno) {
        List<OfflineBookVO> list = new ArrayList<>();
        long offlineQty = offlineMapper.getOfflineQty();
        for(long i = 1; i<=offlineQty; i++){
            int isValid = offlineMapper.isValid(i, prno);
            if(isValid>0){
                // 이미 있다면 그대로 정보를 가져옴
                OfflineBookVO offlineBookVO1 = offlineMapper.getOfflineBookVO(i, prno);
                list.add(offlineBookVO1);
            }else{
                // 없다면 재고가 없는 것으로 판단. 0개로 임의로 데이터 제공
                OfflineBookVO offlineBookVO2 = new OfflineBookVO(i, prno, 0);
                list.add(offlineBookVO2);
            }
        }
        return list;
    }
}

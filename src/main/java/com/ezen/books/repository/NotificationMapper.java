package com.ezen.books.repository;

import com.ezen.books.domain.NotificationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<NotificationVO> getNotificationsByMno(long mno);

    void updateNotificationStatus(long nfno);

    void insertNotification(NotificationVO notification);
}
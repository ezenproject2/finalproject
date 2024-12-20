package com.ezen.books.repository;

import com.ezen.books.domain.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {

    List<Notification> findNotificationsByMno(long mno);

}

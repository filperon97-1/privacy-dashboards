package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByReceiver(User user);
    List<Notification> findAllByReceiverAndIsRead(User user, Boolean isRead);
}

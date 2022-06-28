package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RightRequestRepository extends JpaRepository<RightRequest, UUID> {
    List<RightRequest> findAllByReceiver(User user);
    List<RightRequest> findAllBySenderAndHandled(User user, Boolean handled);
}

package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RightRequestRepository extends JpaRepository<RightRequest, UUID> {
    List<RightRequest> findAllByReceiverId(UUID id);
    RightRequest findByReceiverId(UUID id);
}

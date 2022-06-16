package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository  extends JpaRepository<Message, UUID> {
}

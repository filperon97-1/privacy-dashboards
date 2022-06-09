package com.privacydashboard.application.data.service;



import com.privacydashboard.application.data.entity.IoTApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IoTAppRepository extends JpaRepository<IoTApp, UUID> {
    IoTApp findByName(String name);
}

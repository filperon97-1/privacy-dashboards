package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.entity.UserAppRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserAppRelationRepository extends JpaRepository<UserAppRelation, UUID> {

    @Query("SELECT i from IoTApp i WHERE i.id in (SELECT idIOTApp from UserAppRelation WHERE idUser=:userId) ")
    List<IoTApp> getIoTAppFromUserID(@Param("userId") UUID userId);

    @Query("SELECT u FROM UserAppRelation u WHERE u.idUser=:userId AND u.idIOTApp=:appId")
    UserAppRelation getUserAppRelationFromUserIdAndAppId(@Param("userId") UUID userId, @Param("appId") UUID appId);

    @Query("SELECT u FROM User u WHERE u.id in (SELECT idUser FROM UserAppRelation WHERE idIOTApp=:appId)")
    List<User> getUsersFromAppId(@Param("appId") UUID appId);
}
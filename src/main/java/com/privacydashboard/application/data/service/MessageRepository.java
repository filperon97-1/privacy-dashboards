package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository  extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE (senderId=:user1Id AND receiverId=:user2Id) OR (senderId=:user2Id AND receiverId=:user1Id)" +
            "ORDER BY time")
    List<Message> getConversationFromUsersId(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    @Query("SELECT u FROM User u WHERE (u.id in (SELECT senderId FROM Message WHERE receiverId=:userId) ) OR " +
            "(u.id in (SELECT receiverId FROM Message WHERE senderId=:userId))")
    List<User> getUserConversationFromUserId(@Param("userId") UUID userId);
}

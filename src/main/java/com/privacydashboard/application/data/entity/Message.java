package com.privacydashboard.application.data.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="message")
public class Message extends AbstractEntity{
    @Type(type = "uuid-char")
    private UUID senderId;
    @Type(type = "uuid-char")
    private UUID receiverId;
    private String message;
    private Boolean read;
    private Boolean handled;
    private LocalDateTime time;

    public UUID getSenderId() {
        return senderId;
    }
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    public UUID getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Boolean getRead() {
        return read;
    }
    public void setRead(Boolean read) {
        this.read = read;
    }
    public Boolean getHandled() {
        return handled;
    }
    public void setHandled(Boolean handled) {
        this.handled = handled;

    }
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}

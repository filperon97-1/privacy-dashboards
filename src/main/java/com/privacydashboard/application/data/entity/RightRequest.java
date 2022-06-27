package com.privacydashboard.application.data.entity;

import com.privacydashboard.application.data.RightType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="right_request")
public class RightRequest extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;
    @Type(type = "uuid-char")
    private UUID senderId;
    @Type(type = "uuid-char")
    private UUID receiverId;
    private LocalDateTime time;
    private String other;
    private Boolean handled;
    @Enumerated(EnumType.STRING)
    private RightType rightType;

    public User getSender(){ return sender;}
    public void setSender(User sender){ this.sender=sender;}
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
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    public RightType getRightType() {
        return rightType;
    }
    public void setRightType(RightType rightType) {
        this.rightType = rightType;
    }
    public String getOther() {
        return other;
    }
    public void setOther(String other) {
        this.other = other;
    }
    public Boolean getHandled(){ return handled;}
    public void setHandled(Boolean handled){ this.handled=handled;}
}

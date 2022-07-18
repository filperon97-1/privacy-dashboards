package com.privacydashboard.application.data.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "receiverId")
    private User receiver;
    @ManyToOne
    @JoinColumn(name = "senderId")
    private User sender;
    private String description;
    @OneToOne
    @JoinColumn(name= "messageId")
    private Message message;
    @ManyToOne
    @JoinColumn(name= "requestId")
    private RightRequest request;
    private LocalDateTime time;
    private Boolean isRead;

    public User getReceiver() {
        return receiver;
    }
    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
    public User getSender() {
        return sender;
    }
    public void setSender(User sender) {
        this.sender = sender;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }
    public RightRequest getRequest() {
        return request;
    }
    public void setRequest(RightRequest request) {
        this.request = request;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    public Boolean getRead() {
        return isRead;
    }
    public void setRead(Boolean read) {
        isRead = read;
    }
}

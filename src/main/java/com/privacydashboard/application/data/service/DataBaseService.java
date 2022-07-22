package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataBaseService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final IoTAppRepository ioTAppRepository;
    private final UserAppRelationRepository userAppRelationRepository;
    private final RightRequestRepository rightRequestRepository;
    private final NotificationRepository notificationRepository;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public DataBaseService(PasswordEncoder passwordEncoder, UserRepository userRepository , MessageRepository messageRepository,
                           IoTAppRepository ioTAppRepository, UserAppRelationRepository userAppRelationRepository,
                           RightRequestRepository rightRequestRepository, NotificationRepository notificationRepository) {
        this.passwordEncoder=passwordEncoder;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.ioTAppRepository = ioTAppRepository;
        this.userAppRelationRepository = userAppRelationRepository;
        this.rightRequestRepository = rightRequestRepository;
        this.notificationRepository= notificationRepository;
    }

    // USER REPOSITORY

    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    public User getUserByName(String name){ return userRepository.findByUsername(name);}

    public void addUser(User user){
        userRepository.save(user);
    }

    public void hashPassAndAddUser(User user){
        user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
        addUser(user);
    }

    // MESSAGE REPOSITORY

    public List<Message> getConversationFromUsers(User user1, User user2){
        return messageRepository.getConversationFromUsers(user1, user2);
    }

    public List<User> getUserConversationFromUser(User user){
        return messageRepository.getUserConversationFromUser(user);
    }

    public void addNowMessage(Message message){
        message.setTime(LocalDateTime.now());
        messageRepository.save(message);
        addNewMessageNotification(message);
    }

    // IOTAPP REPOSITORY

    public Optional<IoTApp> getApp(UUID id) {
        return ioTAppRepository.findById(id);
    }

    // USERAPPRELATION REPOSITORY

    public List<IoTApp> getUserApps(User user){
        return userAppRelationRepository.getIoTAppsFromUser(user);
    }

    public List<IoTApp> getUserAppsByName(User user, String name){
        return userAppRelationRepository.getIoTAppsFromUserFilterByName(user, name);
    }

    public List<String> getConsensesFromUserAndApp(User user, IoTApp app){
        return userAppRelationRepository.findByUserAndApp(user, app).getConsenses();
    }

    public List<User> getUsersFromApp(IoTApp app){
        return userAppRelationRepository.getUsersFromApp(app);
    }

    public List<User> getAllContactsFromUser(User user){
        if(user.getRole()==Role.SUBJECT){
            return userAppRelationRepository.getAllContactsFilterBy2Roles(user, Role.CONTROLLER, Role.DPO);
        }
        else{
            return userAppRelationRepository.getAllDPOOrControllerContacts(user);
        }
    }

    public List<IoTApp> getAppsFrom2Users(User user1, User user2){
        return userAppRelationRepository.getAppsFrom2Users(user1, user2);
    }

    public List<User> getControllersFromApp(IoTApp app){
        return userAppRelationRepository.getUsersFromAppFilterByRole(app, Role.CONTROLLER);
    }

    public List<User> getDPOsFromApp(IoTApp app){
        return userAppRelationRepository.getUsersFromAppFilterByRole(app, Role.DPO);
    }

    public List<User> getSubjectsFromApp(IoTApp app){
        return userAppRelationRepository.getUsersFromAppFilterByRole(app, Role.SUBJECT);
    }

    public void addUserApp(User user, IoTApp app){
        UserAppRelation userAppRelation=new UserAppRelation();
        userAppRelation.setApp(app);
        userAppRelation.setUser(user);
        userAppRelationRepository.save(userAppRelation);
    }

    public boolean userHasApp(User user, IoTApp app){
        if(userAppRelationRepository.findByUserAndApp(user, app)==null){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean deleteAppFromUser(User user, IoTApp app){
        UserAppRelation userAppRelation=userAppRelationRepository.findByUserAndApp(user, app);
        if(userAppRelation==null){
            return false;
        }
        else{
            userAppRelationRepository.delete(userAppRelation);
            return true;
        }
    }

    // RIGHT REQUEST REPOSITORY

    public List<RightRequest> getAllRequestsFromReceiver(User user){
        return rightRequestRepository.findAllByReceiver(user);
    }

    public List<RightRequest> getPendingRequestsFromSender(User user){
        return rightRequestRepository.findAllBySenderAndHandled(user, false);
    }

    public List<RightRequest> getHandledRequestsFromSender(User user){
        return rightRequestRepository.findAllBySenderAndHandled(user, true);
    }

    public void addNowRequest(RightRequest request){
        request.setTime(LocalDateTime.now());
        rightRequestRepository.save(request);
        addNewRequestNotification(request);
    }

    public void updateRequest(RightRequest request){
        rightRequestRepository.deleteById(request.getId());
        rightRequestRepository.save(request);
        addUpdatedRequestNotification(request);
    }

    // NOTIFICATION REPOSITORY

    public void addNowNotification(Notification notification){
        notification.setTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsFromUser(User user){
        return notificationRepository.findAllByReceiver(user);
    }

    public List<Notification> getNewNotificationsFromUser(User user){
        return notificationRepository.findAllByReceiverAndIsRead(user, false);
    }

    public void changeIsReadNotification(Notification notification, boolean isRead){
        notificationRepository.changeIsReadNotificationById(notification.getId(), isRead);
    }

    private void addNewMessageNotification(Message message){
       Notification notification=new Notification();
       notification.setReceiver(message.getReceiver());
       notification.setSender(message.getSender());
       notification.setDescription(message.getSender().getName() + " sent you a message");
       notification.setRead(false);
       notification.setMessage(message);
       notification.setRequest(null);
       addNowNotification(notification);
    }

    private void addNewRequestNotification(RightRequest request){
        Notification notification=new Notification();
        notification.setReceiver(request.getReceiver());
        notification.setSender(request.getSender());
        notification.setDescription(request.getSender().getName() + " sent you a right request");
        notification.setRead(false);
        notification.setMessage(null);
        notification.setRequest(request);
        addNowNotification(notification);
    }

    private void addUpdatedRequestNotification(RightRequest request){
        Notification notification=new Notification();
        notification.setReceiver(request.getSender());
        notification.setSender(request.getReceiver());
        notification.setDescription(request.getReceiver().getName() + " changed the status of a request");
        notification.setRead(false);
        notification.setMessage(null);
        notification.setRequest(request);
        addNowNotification(notification);
    }

    // REMOVE EVERYTHING

    public void removeEverythingFromUser(User user){
        List<IoTApp> apps=getUserApps(user);
        for(IoTApp app : apps){
            RightRequest request=new RightRequest();
            request.setHandled(false);
            request.setRightType(RightType.DELTEEVERYTHING);
            request.setReceiver(getControllersFromApp(app).get(0));
            request.setSender(user);
            request.setApp(app);
            addNowRequest(request);
        }
    }
}

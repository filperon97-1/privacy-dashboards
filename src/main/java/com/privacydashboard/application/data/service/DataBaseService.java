package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataBaseService {
    private final PasswordEncoder passwordEncoder;
    private final UserAppRelationRepository userAppRelationRepository;
    private final UserRepository userRepository;
    private final IoTAppRepository ioTAppRepository;
    private final MessageRepository messageRepository;
    private final RightRequestRepository rightRequestRepository;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public DataBaseService(PasswordEncoder passwordEncoder, UserAppRelationRepository userAppRelationRepository, UserRepository userRepository, IoTAppRepository ioTAppRepository, MessageRepository messageRepository, RightRequestRepository rightRequestRepository) {
        this.passwordEncoder=passwordEncoder;
        this.userAppRelationRepository = userAppRelationRepository;
        this.userRepository = userRepository;
        this.ioTAppRepository = ioTAppRepository;
        this.messageRepository = messageRepository;
        this.rightRequestRepository = rightRequestRepository;
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
    }

    public void updateRequest(RightRequest request){
        rightRequestRepository.deleteById(request.getId());
        rightRequestRepository.save(request);
    }
}

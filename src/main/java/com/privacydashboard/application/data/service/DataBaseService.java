package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataBaseService {
    private final UserAppRelationRepository userAppRelationRepository;
    private final UserRepository userRepository;
    private final IoTAppRepository ioTAppRepository;
    private final MessageRepository messageRepository;
    private final RightRequestRepository rightRequestRepository;

    @Autowired
    public DataBaseService(UserAppRelationRepository userAppRelationRepository, UserRepository userRepository, IoTAppRepository ioTAppRepository, MessageRepository messageRepository, RightRequestRepository rightRequestRepository) {
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

    // MESSAGE REPOSITORY

    public List<Message> getConversationFromUsers(User user1, User user2){
        return messageRepository.getConversationFromUsersId(user1.getId(), user2.getId());
    }

    public List<User> getUserConversationFromUser(User user){
        return messageRepository.getUserConversationFromUserId(user.getId());
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
        return userAppRelationRepository.getIoTAppFromUserID(user.getId());
    }

    public List<IoTApp> getUserAppsByName(User user, String name){
        return userAppRelationRepository.getIoTAppFromUserIDFilterByName(user.getId(), name);
    }

    public List<String> getConsensesFromUserAndApp(User user, IoTApp app){
        return userAppRelationRepository.getUserAppRelationFromUserIdAndAppId(user.getId(), app.getId()).getConsenses();
    }

    public List<User> getUsersFromApp(IoTApp app){
        return userAppRelationRepository.getUsersFromAppId(app.getId());
    }

    //PER ORA E' IMPLEMENTATO PENSANDO CHE LA TABELLA IOTAPP NON ABBIA UN CAMPO CON IL SET DI CONTROLLER ASSOCIATI
    public List<User> getAllContactsFromUser(User user){
        List<User> userList= new LinkedList<>();
        List<IoTApp> appList=getUserApps(user);
        for(IoTApp app : appList){
            List<User> partialUserList=getUsersFromApp(app);
            for(User u : partialUserList){
                //if u is the user or if u is already in the list, jump to next iteration
                if(u.getId().equals(user.getId()) || userList.contains(u)){
                    continue;
                }
                //Data Subject cannot have access to other Data Subjects.
                if(u.getRole().equals(Role.SUBJECT) && user.getRole().equals(Role.SUBJECT)){
                    continue;
                }
                userList.add(u);
            }
        }
        return userList;
    }

    public List<IoTApp> getAppsFrom2Users(User user1, User user2){
        List<IoTApp> list1=getUserApps(user1);
        List<IoTApp> list2=getUserApps(user2);
        List<IoTApp> appList=new LinkedList<>();
        for(IoTApp app : list1){
            if(list2.contains(app)){
                appList.add(app);
            }
        }
        return appList;
    }

    public List<User> getControllersFromApp(IoTApp app){
        List<User> controllers=new LinkedList<>();
        List<User> userList = getUsersFromApp(app);
        for(User u : userList){
            if(u.getRole().equals(Role.CONTROLLER)){
                controllers.add(u);
            }
        }
        return controllers;
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
}

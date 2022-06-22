package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.DataRole;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.Message;
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

    @Autowired
    public DataBaseService(UserAppRelationRepository userAppRelationRepository, UserRepository userRepository, IoTAppRepository ioTAppRepository, MessageRepository messageRepository) {
        this.userAppRelationRepository = userAppRelationRepository;
        this.userRepository = userRepository;
        this.ioTAppRepository = ioTAppRepository;
        this.messageRepository = messageRepository;
    }

    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    public List<IoTApp> getUserApps(User user){
        return userAppRelationRepository.getIoTAppFromUserID(user.getId());
    }

    public Set<String> getConsensesFromUserAndApp(User user, IoTApp app){
        return userAppRelationRepository.getUserAppRelationFromUserIdAndAppId(user.getId(), app.getId()).getConsenses();
    }

    public List<User> getUsersFromApp(IoTApp app){
        return userAppRelationRepository.getUsersFromAppId(app.getId());
    }

    public List<Message> getConversationFromUsers(User user1, User user2){
        return messageRepository.getConversationFromUsersId(user1.getId(), user2.getId());
    }


    public List<User> getUserConversationFromUser(User user){
        return messageRepository.getUserConversationFromUserId(user.getId());
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
                if(u.getDataRole().equals(DataRole.SUBJECT) && user.getDataRole().equals(DataRole.SUBJECT)){
                    continue;
                }
                userList.add(u);
            }
        }
        return userList;
    }

    public void addNowMessage(Message message){
        message.setTime(LocalDateTime.now());
        messageRepository.save(message);
    }
}

package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class DataBaseService {
    private final UserAppRelationRepository userAppRelationRepository;
    private final UserRepository userRepository;
    private final IoTAppRepository ioTAppRepository;

    @Autowired
    public DataBaseService(UserAppRelationRepository userAppRelationRepository, UserRepository userRepository, IoTAppRepository ioTAppRepository) {
        this.userAppRelationRepository = userAppRelationRepository;
        this.userRepository = userRepository;
        this.ioTAppRepository = ioTAppRepository;
    }

    public List<IoTApp> getUserApps(User user){
        return userAppRelationRepository.getIoTAppFromUserID(user.getId());
    }

    public Set<String> getConsensesFromUserAndApp(User user, IoTApp app){
        return userAppRelationRepository.getUserAppRelationFromUserIdAndAppId(user.getId(), app.getId()).getConsenses();
    }
}

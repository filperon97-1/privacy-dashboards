package com.privacydashboard.application.data.generator;

import com.privacydashboard.application.data.DataRole;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.entity.UserAppRelation;
import com.privacydashboard.application.data.service.IoTAppRepository;
import com.privacydashboard.application.data.service.MessageRepository;
import com.privacydashboard.application.data.service.UserAppRelationRepository;
import com.privacydashboard.application.data.service.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository, IoTAppRepository ioTAppRepository, UserAppRelationRepository userAppRelationRepository, MessageRepository messageRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            user.setDataRole(DataRole.SUBJECT);
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            admin.setDataRole(DataRole.CONTROLLER);
            userRepository.save(admin);

            //AGGIUNTA
            //creazione admin2
            User admin2 = new User();
            admin2.setName("Admin2");
            admin2.setUsername("admin2");
            admin2.setHashedPassword(passwordEncoder.encode("admin2"));
            admin2.setRoles(Set.of(Role.USER, Role.ADMIN));
            admin2.setDataRole(DataRole.CONTROLLER);
            userRepository.save(admin2);

            //creazione 2 iotApp
            IoTApp ioTApp=new IoTApp();
            ioTApp.setDataController(admin);
            ioTApp.setName("iot app 1");
            ioTApp.setDescription("descrizione 1");
            ioTAppRepository.save(ioTApp);

            IoTApp ioTApp2=new IoTApp();
            ioTApp2.setDataController(admin2);
            ioTApp2.setName("iot app 2");
            ioTApp2.setDescription("descrizione 2");
            ioTAppRepository.save(ioTApp2);

            //creazione 2 userAppRelation
            UserAppRelation userAppRelation1=new UserAppRelation();
            userAppRelation1.setIdUser(user.getId());
            userAppRelation1.setIdIOTApp(ioTApp.getId());
            userAppRelation1.setConsenses(Set.of("consenso1", "consenso2"));
            userAppRelationRepository.save(userAppRelation1);

            UserAppRelation userAppRelation2=new UserAppRelation();
            userAppRelation2.setIdUser(user.getId());
            userAppRelation2.setIdIOTApp(ioTApp2.getId());
            userAppRelation2.setConsenses(Set.of("consenso1", "consenso2"));
            userAppRelationRepository.save(userAppRelation2);

            //AGGIUNTA SUBJECTS, CONTROLLER, DPO, APP
            User[] subjects, controller, DPO;
            IoTApp[] apps;
            subjects= new User[50];
            controller= new User[50];
            DPO=new User[50];
            apps=new IoTApp[50];
            for(int i=0; i<50;i++){
                subjects[i]= new User();
                subjects[i].setUsername("subject" + String.valueOf(i));
                subjects[i].setName("subject" + String.valueOf(i));
                subjects[i].setHashedPassword(passwordEncoder.encode("subject" + String.valueOf(i)));
                subjects[i].setRoles(Collections.singleton(Role.USER));
                subjects[i].setDataRole(DataRole.SUBJECT);
                userRepository.save(subjects[i]);

                controller[i]= new User();
                controller[i].setUsername("controller" + String.valueOf(i));
                controller[i].setName("controller" + String.valueOf(i));
                controller[i].setHashedPassword(passwordEncoder.encode("controller" + String.valueOf(i)));
                controller[i].setRoles(Collections.singleton(Role.USER));
                controller[i].setDataRole(DataRole.CONTROLLER);
                userRepository.save(controller[i]);

                DPO[i]= new User();
                DPO[i].setUsername("DPO" + String.valueOf(i));
                DPO[i].setName("DPO" + String.valueOf(i));
                DPO[i].setHashedPassword(passwordEncoder.encode("DPO" + String.valueOf(i)));
                DPO[i].setRoles(Collections.singleton(Role.USER));
                DPO[i].setDataRole(DataRole.DPO);
                userRepository.save(DPO[i]);

                apps[i]= new IoTApp();
                apps[i].setDescription("description" + String.valueOf(i));
                apps[i].setName("appppp" + String.valueOf(i));
                apps[i].setDataController(controller[i]);
                ioTAppRepository.save(apps[i]);

            }

            //AGGIUNTA USERAPPRELATION

            for(int i=0;i<50;i++){
                //SUBJECT I CON LE APP [I-5, I]
                for(int j=i-5;j<=i;j++){
                    if(j>=0){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(Set.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setIdUser(subjects[i].getId());
                        userAppRelation.setIdIOTApp(apps[j].getId());
                        userAppRelationRepository.save(userAppRelation);
                    }
                }

                //CONTROLLER I CON LE APP [I, I+5]
                for(int j=i;j<=i+5;j++){
                    if(j<50){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(Set.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setIdUser(controller[i].getId());
                        userAppRelation.setIdIOTApp(apps[j].getId());
                        userAppRelationRepository.save(userAppRelation);
                    }
                }

                //DPO I CON LE APP [I,I+5]
                for(int j=i;j<=i+5;j++){
                    if(j<50 && j>=0){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(Set.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setIdUser(DPO[i].getId());
                        userAppRelation.setIdIOTApp(apps[j].getId());
                        userAppRelationRepository.save(userAppRelation);
                    }
                }
            }

            // AGGIUNTA MESSAGGI, PER ORA SOLO ID USERS, MESSAGGIO E DATA, SOLO PER SUBJECT0
            for(int i=0;i<10;i++){
                for(int j=0;j<10;j++){
                    Message message=new Message();
                    if(j%2==0){
                        message.setSenderId(subjects[0].getId());
                        message.setReceiverId(subjects[i+10].getId());
                    }else{
                        message.setReceiverId(subjects[0].getId());
                        message.setSenderId(subjects[i+10].getId());
                    }
                    message.setMessage("questo è il " + String.valueOf(10-j) + " messaggio");
                    message.setTime(LocalDateTime.of(2022, 4, 10, 22, 11-j, 30));
                    messageRepository.save(message);
                }
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime notNow=LocalDateTime.of(2020, 4, 13, 22, 11, 30);
            logger.info(dtf.format(notNow));


            //FINE AGGIUNTA

            logger.info("Generated demo data");
        };
    }

}
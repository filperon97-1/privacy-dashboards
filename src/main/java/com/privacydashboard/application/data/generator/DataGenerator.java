package com.privacydashboard.application.data.generator;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.*;
import com.privacydashboard.application.data.service.*;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository, IoTAppRepository ioTAppRepository, UserAppRelationRepository userAppRelationRepository, MessageRepository messageRepository, RightRequestRepository rightRequestRepository, NotificationRepository notificationRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

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
                subjects[i].setRole(Role.SUBJECT);
                userRepository.save(subjects[i]);

                controller[i]= new User();
                controller[i].setUsername("controller" + String.valueOf(i));
                controller[i].setName("controller" + String.valueOf(i));
                controller[i].setHashedPassword(passwordEncoder.encode("controller" + String.valueOf(i)));
                controller[i].setRole(Role.CONTROLLER);
                userRepository.save(controller[i]);

                DPO[i]= new User();
                DPO[i].setUsername("DPO" + String.valueOf(i));
                DPO[i].setName("DPO" + String.valueOf(i));
                DPO[i].setHashedPassword(passwordEncoder.encode("DPO" + String.valueOf(i)));
                DPO[i].setRole(Role.DPO);
                userRepository.save(DPO[i]);

                apps[i]= new IoTApp();
                apps[i].setDescription("description" + String.valueOf(i));
                apps[i].setName("appppp" + String.valueOf(i));
                //apps[i].setDataController(controller[i]);
                ioTAppRepository.save(apps[i]);

            }

            //AGGIUNTA USERAPPRELATION

            for(int i=0;i<50;i++){
                //SUBJECT I CON LE APP [I-5, I+5]
                for(int j=i-5;j<=i+5;j++){
                    if(j>=0 && j<50){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(List.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setUser(subjects[i]);
                        userAppRelation.setApp(apps[j]);
                        userAppRelationRepository.save(userAppRelation);
                    }
                }

                //CONTROLLER I CON LE APP [I, I+5]
                for(int j=i;j<=i+5;j++){
                    if(j<50){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(List.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setUser(controller[i]);
                        userAppRelation.setApp(apps[j]);
                        userAppRelationRepository.save(userAppRelation);
                    }
                }

                //DPO I CON LE APP [I-3,I+5]
                for(int j=i-3;j<=i+5;j++){
                    if(j<50 && j>=0){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(List.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setUser(DPO[i]);
                        userAppRelation.setApp(apps[j]);
                        userAppRelationRepository.save(userAppRelation);
                    }
                }
            }

            //AGGIUNTA MESSAGGI
            for(int i=0;i<50;i++){
                //SUBJECT I INVIA 2 MESSAGGI A CONTROLLER I, 4 A I+2, e 6 A I+4
                for(int j=0;j<3;j++){
                    int k=i+j*2;        // Controller k
                    if(k>=50){
                        continue;
                    }
                    for(int z=0;z<j*2+2;z++){
                        Message message=new Message();
                        message.setMessage("questo è il " + String.valueOf(z) +" (non in ordine) messaggio da Subject " + String.valueOf(i) + " verso Controller " +String.valueOf(k));
                        message.setTime(LocalDateTime.of(2022, 6, 26-z, 22, 11, 30));
                        message.setSender(subjects[i]);
                        message.setReceiver(controller[k]);
                        messageRepository.save(message);
                    }
                    // CONTROLLER RISPONDE CON 1 SOLO MESSAGGIO
                    Message message=new Message();
                    message.setMessage(" Sono il Controller " + String.valueOf(k));
                    message.setTime(LocalDateTime.of(2022, 6, 26-j, 12, 10, 55));
                    message.setSender(controller[k]);
                    message.setReceiver(subjects[i]);
                    messageRepository.save(message);
                }
            }

            //AGGINUTA REQUEST. SUBJECT I INVIA 2 RICHIESTE A CONTROLLER I, UNA HANDLED UNA NON HANDLED
            for(int i=0;i<50;i++){
                for(int j=0;j<2;j++){
                    RightRequest request=new RightRequest();
                    if(j==0){
                        request.setHandled(false);
                    }
                    else{
                        request.setHandled(true);
                    }
                    request.setSender(subjects[i]);
                    request.setReceiver(controller[i]);
                    request.setApp(apps[i]);
                    request.setTime(LocalDateTime.of(2022, 6-j, 30-(i%20), 12-j, 10, 30));
                    request.setDetails("varie informazioni che potrebbero essere utili");
                    int k=i*2+j;
                    if(k%3==0){
                        request.setRightType(RightType.WITHDRAWCONSENT);
                        request.setOther("consenso1");
                    }
                    else if(k%2==0){
                        request.setRightType(RightType.ERASURE);
                    }
                    else{
                        request.setRightType(RightType.COMPLAIN);
                    }
                    rightRequestRepository.save(request);
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
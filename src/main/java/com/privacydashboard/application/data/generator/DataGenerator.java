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
import java.util.List;
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
                //SUBJECT I CON LE APP [I-5, I+5]
                for(int j=i-5;j<=i+5;j++){
                    if(j>=0 && j<50){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(List.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setIdUser(subjects[i].getId());
                        userAppRelation.setIdIOTApp(apps[j].getId());
                        userAppRelationRepository.save(userAppRelation);
                    }
                }

                //CONTROLLER I CON LE APP [I, I+5]
                for(int j=i;j<=i+5;j++){
                    if(j<50){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(List.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setIdUser(controller[i].getId());
                        userAppRelation.setIdIOTApp(apps[j].getId());
                        userAppRelationRepository.save(userAppRelation);
                    }
                }

                //DPO I CON LE APP [I-3,I+5]
                for(int j=i-3;j<=i+5;j++){
                    if(j<50 && j>=0){
                        UserAppRelation userAppRelation=new UserAppRelation();
                        userAppRelation.setConsenses(List.of("consenso1", "consenso2", "consenso3"));
                        userAppRelation.setIdUser(DPO[i].getId());
                        userAppRelation.setIdIOTApp(apps[j].getId());
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
                        message.setSenderId(subjects[i].getId());
                        message.setReceiverId(controller[k].getId());
                        messageRepository.save(message);
                    }
                    // CONTROLLER RISPONDE CON 1 SOLO MESSAGGIO
                    Message message=new Message();
                    message.setMessage(" Sono il Controller " + String.valueOf(k));
                    message.setTime(LocalDateTime.of(2022, 6, 26-j, 12, 10, 55));
                    message.setSenderId(controller[k].getId());
                    message.setReceiverId(subjects[i].getId());
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
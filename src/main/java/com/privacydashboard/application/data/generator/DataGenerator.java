package com.privacydashboard.application.data.generator;

import com.privacydashboard.application.data.DataRole;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.entity.UserAppRelation;
import com.privacydashboard.application.data.service.IoTAppRepository;
import com.privacydashboard.application.data.service.UserAppRelationRepository;
import com.privacydashboard.application.data.service.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
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
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository, IoTAppRepository ioTAppRepository, UserAppRelationRepository userAppRelationRepository) {
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


            //FINE AGGIUNTA

            logger.info("Generated demo data");
        };
    }

}
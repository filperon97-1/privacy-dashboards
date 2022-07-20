package com.privacydashboard.application.data.apiController;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class AppController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataBaseService dataBaseService;
    @PostMapping
    @RequestMapping("api/app/add")
    public ResponseEntity<?> add(@RequestParam() String userId, @RequestParam() String appId){
        logger.info("add");
        Optional<User> maybeUser;
        Optional<IoTApp> maybeApp;
        try{
            maybeUser=dataBaseService.getUser(UUID.fromString(userId));
            maybeApp=dataBaseService.getApp(UUID.fromString(appId));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("app or user don't exist");
        }
        if(maybeApp.isEmpty()){
            return ResponseEntity.badRequest().body("app does not exist");
        }
        if(maybeUser.isEmpty()){
            return ResponseEntity.badRequest().body("user does not exist");
        }
        if(dataBaseService.userHasApp(maybeUser.get(), maybeApp.get())){
            return ResponseEntity.badRequest().body("user already has this app");
        }
        dataBaseService.addUserApp(maybeUser.get(), maybeApp.get());
        return ResponseEntity.ok("app added successfully");
    }

    @PostMapping
    @RequestMapping("api/app/delete")
    public ResponseEntity<?> delete(@RequestParam() String userId, @RequestParam() String appId){
        logger.info("delete");
        Optional<User> maybeUser;
        Optional<IoTApp> maybeApp;
        try{
            maybeUser=dataBaseService.getUser(UUID.fromString(userId));
            maybeApp=dataBaseService.getApp(UUID.fromString(appId));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("app or user don't exist");
        }
        if(maybeApp.isEmpty()){
            return ResponseEntity.badRequest().body("app does not exist");
        }
        if(maybeUser.isEmpty()){
            return ResponseEntity.badRequest().body("user does not exist");
        }
        if(dataBaseService.deleteAppFromUser(maybeUser.get(), maybeApp.get())){
            return ResponseEntity.ok("app removed successfully");
        }
        else{
            return ResponseEntity.badRequest().body("user doesn't have this app");
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(ex.getParameterName() + " parameter is missing");
    }
}

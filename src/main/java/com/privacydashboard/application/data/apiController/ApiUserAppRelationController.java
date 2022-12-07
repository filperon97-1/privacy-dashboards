package com.privacydashboard.application.data.apiController;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiUserAppRelationController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataBaseService dataBaseService;
    @Autowired
    private AuthenticatedUser authenticatedUser;
    @Autowired
    private ApiGeneralController apiGeneralController;

    /**
     * Associate a user with an app
     * RESTRICTIONS: userId must belong to the one calling this function
     * @param userId ID of the User
     * @param appId ID of the app
     * @return OK if relation added successfully. Bad request if IDs invalid, user or app does not exist, user already has app, user is not the one calling this function
     */
    @PostMapping
    @RequestMapping("api/userapprelation/add")
    public ResponseEntity<?> add(@RequestParam() String userId, @RequestParam() String appId){
        try{
            User user=apiGeneralController.getUserFromId(userId);
            IoTApp app=apiGeneralController.getAppFromId(appId);
            if(!apiGeneralController.isAuthenticatedUserId(userId)){
                return ResponseEntity.badRequest().body("user does not exist");
            }
            if(!apiGeneralController.userHasApp(user,app)){
                dataBaseService.addUserApp(user, app);
                return ResponseEntity.ok("app added successfully");
            }
            else{
                return ResponseEntity.badRequest().body("user already has this app");
            }
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getStackTrace()[0].toString());
        }
    }

    /**
     * Dissociate a user with an app
     * RESTRICTIONS: userId must belong to the one calling this function
     * @param userId ID of the User
     * @param appId ID of the App
     * @return OK if relation deleted successfully. Bad request if IDs invalid, user or app does not exist, user does not have app, user is not the one calling this function
     */
    @DeleteMapping
    @RequestMapping("api/userapprelation/delete")
    public ResponseEntity<?> delete(@RequestParam() String userId, @RequestParam() String appId){
        try{
            User user=apiGeneralController.getUserFromId(userId);
            IoTApp app=apiGeneralController.getAppFromId(appId);
            if(!apiGeneralController.isAuthenticatedUserId(userId)){
                return ResponseEntity.badRequest().body("user does not exist");
            }

            if(apiGeneralController.userHasApp(user,app) && dataBaseService.deleteAppFromUser(user, app)){
                return ResponseEntity.ok("app removed successfully");
            }
            else{
                return ResponseEntity.badRequest().body("user doesn't have this app");
            }
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getStackTrace()[0].toString());
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(ex.getParameterName() + " parameter is missing");
    }
}

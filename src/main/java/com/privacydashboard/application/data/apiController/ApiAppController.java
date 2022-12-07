package com.privacydashboard.application.data.apiController;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;


@RestController
@AnonymousAllowed
public class ApiAppController {

    Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private DataBaseService dataBaseService;
    @Autowired
    private AuthenticatedUser authenticatedUser;
    @Autowired
    private ApiGeneralController apiGeneralController;

    /**
     * Get information about an app
     * RESTRICTIONS: NONE
     * @param appId ID of the app to get information from
     * @return Json with information. Bad Request if ID invalid or app does not exist
     */
    @GetMapping
    @RequestMapping("api/app/get")
    public ResponseEntity<?> get(@RequestParam() String appId){
        try {
            IoTApp app = apiGeneralController.getAppFromId(appId);
            ObjectNode appJson = apiGeneralController.createJsonFromApp(app);
            return ResponseEntity.ok(appJson);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getStackTrace()[0].toString());
        }
    }

    /**
     * Create a new app. The user that calls this function is gonna be associated with the app
     * RESTRICTIONS: ONLY CONTROLLER OR DPO ALLOWED
     * @param body a JSON with the information about the app. It MUST include the NAME
     * @return OK If app successfully added. Bad Request if no authentication or a Data Subject, Json is invalid, ID is invalid, there is already an app with that ID
     */
    @PostMapping
    @RequestMapping("api/app/add")
    public ResponseEntity<?> add(@RequestBody String body){
        if(!apiGeneralController.isControllerOrDpo(true, null)){
            return ResponseEntity.badRequest().body("user is not DPO or Controller");
        }

        try{
            IoTApp app=apiGeneralController.getAppFromJsonString(true, body);

            // if there is already app with that ID
            if(app.getId()!=null && dataBaseService.getApp(app.getId()).isPresent()){
                return ResponseEntity.badRequest().body("cannot create this app");
            }

            dataBaseService.addApp(app);
            dataBaseService.addUserApp(apiGeneralController.getAuthenicatedUser(),app);
            return ResponseEntity.ok("app created successfully");
        }
        catch (IOException | IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getStackTrace()[0].toString());
        }
    }


    /**
     * Delete an app
     * RESTRICTIONS: The one calling the function MUST BE CONTROLLER or DPO of the APP
     * @param appId Id of the app to be removed
     * @return OK id app removed successfully. Bad Request if app does not exist or user is not controller/DPO of the app
     */
    @DeleteMapping
    @RequestMapping("api/app/delete")
    public ResponseEntity<?> delete(@RequestParam() String appId){
        try {
            IoTApp app = apiGeneralController.getAppFromId(appId);
            if(apiGeneralController.userHasApp(apiGeneralController.getAuthenicatedUser(), app)){
                if(apiGeneralController.isControllerOrDpo(true, null)){
                    dataBaseService.deleteApp(app.getId());
                    return ResponseEntity.ok("app deleted successfully");
                }
                else{
                    return ResponseEntity.badRequest().body("user is not a controller or DPO");
                }
            }
            else{
                return ResponseEntity.badRequest().body("user not associated with the app");
            }
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getStackTrace()[0].toString());
        }
    }

    /**
     * Update some values of the app identified by the appID
     * RESTRICTIONS: The one calling the function MUST BE CONTROLLER or DPO of the APP
     * @param appId Id of the app to be updated
     * @param body JSON specifying what values must be changed of the app
     * @return Ok if correctly updated. Bad Request if app does not exist, user is not controller/DPO of the app, JSON is invalid
     */
    @PostMapping
    @RequestMapping("api/app/update")
    public ResponseEntity<?> update(@RequestParam String appId, @RequestBody String body){
        try{
            IoTApp app1 = apiGeneralController.getAppFromId(appId);
            if(!apiGeneralController.userHasApp(apiGeneralController.getAuthenicatedUser(), app1)) {
                return ResponseEntity.badRequest().body("user not associated with the app");
            }
            if(!apiGeneralController.isControllerOrDpo(true, null)) {
                return ResponseEntity.badRequest().body("user is not a controller or DPO");
            }
            IoTApp app2 =apiGeneralController.getAppFromJsonString(false, body);
            updateApp(app1, app2);
            return ResponseEntity.ok("app updated successfully");
        }
        catch (IOException | IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getStackTrace()[0].toString());
        }
    }

    private void updateApp(IoTApp oldApp, IoTApp newApp){
        if(newApp.getName()!=null || newApp.getDescription()!=null){
            dataBaseService.changeNameAndDescriptionForApp(oldApp, newApp.getName(), newApp.getDescription());
        }

        if(newApp.getQuestionnaireVote()!=null || newApp.getDetailVote()!=null || newApp.getOptionalAnswers()!=null){
            dataBaseService.updateQuestionnaireForApp(oldApp, oldApp.getQuestionnaireVote(), oldApp.getDetailVote(), oldApp.getOptionalAnswers());
        }
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(ex.getParameterName() + " parameter is missing");
    }

}

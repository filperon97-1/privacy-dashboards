package com.privacydashboard.application.data.apiController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.privacydashboard.application.data.QuestionnaireVote;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiGeneralController {
    @Autowired
    private AuthenticatedUser authenticatedUser;
    @Autowired
    private DataBaseService dataBaseService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final int nQuestions= 30;

    // BOOLEAN CONTROLS

    public boolean isAuthenticatedUserId(String uuid){
        Optional<User> maybeUser=dataBaseService.getUser(UUID.fromString(uuid));
        Optional<User> maybeAuthenticate=authenticatedUser.get();
        if(maybeUser.isPresent() && maybeAuthenticate.isPresent()){
            return maybeUser.get().equals(maybeAuthenticate.get());
        }
        return false;
    }

    public boolean isControllerOrDpo(boolean considerAuthenticatedUser, String uuid){
        Optional<User> maybeUser;
        if(considerAuthenticatedUser){
            maybeUser=authenticatedUser.get();
        }
        else{
            maybeUser=dataBaseService.getUser(UUID.fromString(uuid));
        }
        return maybeUser.filter(user -> (user.getRole().equals(Role.CONTROLLER) || user.getRole().equals(Role.DPO))).isPresent();
    }

    public boolean userHasApp(User user, IoTApp app){
        return dataBaseService.getUserApps(user).contains(app);
    }

    // GET OBJECTS

    /**
     * @return the authenticate user
     * @throws IllegalArgumentException if there is no authenticated user
     */
    public User getAuthenicatedUser() throws IllegalArgumentException{
        Optional<User> maybeUser= authenticatedUser.get();
        if(maybeUser.isPresent()){
            return maybeUser.get();
        }
        else{
            throw new IllegalArgumentException("No authenticated user");
        }
    }

    /**
     * @param uuid the ID of the User
     * @return the User with that ID
     * @throws IllegalArgumentException If ID is an invalid UUID
     * @throws IllegalArgumentException If user with that ID does not exist
     */
    public User getUserFromId(String uuid) throws IllegalArgumentException{
        Optional<User> maybeUser;
        maybeUser=dataBaseService.getUser(UUID.fromString(uuid));
        if(maybeUser.isPresent()){
            return maybeUser.get();
        }
        else{
            throw new IllegalArgumentException("user does not exist");
        }
    }

    /**
     * @param uuid the ID of the app
     * @return the app with that ID
     * @throws IllegalArgumentException If ID is an invalid UUID
     * @throws IllegalArgumentException If app with that ID does not exist
     */
    public IoTApp getAppFromId(String uuid) throws IllegalArgumentException{
        Optional<IoTApp> maybeApp;
        maybeApp=dataBaseService.getApp(UUID.fromString(uuid));
        if(maybeApp.isPresent()){
            return maybeApp.get();
        }
        else{
            throw new IllegalArgumentException("app does not exist");
        }
    }

    // MANAGE JSON OBJECTS

    /**
     * @param app the app to be represented as a JSON. It MUST contain the ID and the NAME
     * @return the JSON object representing the app
     * @throws IllegalArgumentException If IoTApp object is null
     * @throws IllegalArgumentException If IoTApp object has not an ID
     * @throws IllegalArgumentException If IoTApp object has not a name
     */
    public ObjectNode createJsonFromApp(IoTApp app) throws IllegalArgumentException{
        ObjectNode appJson= mapper.createObjectNode();

        if(app==null || app.getId()==null || app.getName()==null){
            throw new IllegalArgumentException();
        }
        appJson.put("id", app.getId().toString());
        appJson.put("name", app.getName());

        if(app.getDescription()!=null){
            appJson.put("description", app.getDescription());
        }

        if(app.getQuestionnaireVote()!=null){
            appJson.put("questionnaireVote", app.getQuestionnaireVote().toString());
        }

        if(app.getDetailVote()!=null){
            ArrayNode detailVoteArray = mapper.createArrayNode();
            for(String answer : app.getDetailVote()){
                detailVoteArray.add(answer);
            }
            appJson.set("detailVote", detailVoteArray);
        }

        if(app.getOptionalAnswers()!=null){
            ArrayNode optionalAnswersArray = mapper.createArrayNode();
            for(int i=0; i<nQuestions; i++){
                optionalAnswersArray.add(app.getOptionalAnswers().get(i));

            }
            appJson.set("optionalAnswers", optionalAnswersArray);
        }
        return appJson;
    }

    /**
     * @param nameMandatory specify if the name MUST be included (true) or not (false)
     * @param body the JSON body from the POST request
     * @return the app object represented by the JSON
     * @throws IOException If body is not a valid JSON
     * @throws IllegalArgumentException If the JSON does not contain name
     * @throws IllegalArgumentException If the indicated ID is an invalid UUID
     */
    public IoTApp getAppFromJsonString(boolean nameMandatory, String body) throws IOException, IllegalArgumentException {
        JsonNode rootNode = mapper.readTree(new StringReader(body));
        return getAppFromJsonNode(nameMandatory, rootNode);
    }

    /**
     * @param nameMandatory specify if the name MUST be included (true) or not (false)
     * @param node the JSON object to be turned into an app object.
     * @return the app object represented by the JSON
     * @throws IllegalArgumentException If the JSON does not contain name
     * @throws IllegalArgumentException If the indicated ID is an invalid UUID
     */
    public IoTApp getAppFromJsonNode(boolean nameMandatory, JsonNode node) throws IllegalArgumentException{
        IoTApp app= new IoTApp();

        if(node.has("name")){
            app.setName(node.get("name").asText());
        }
        else if(nameMandatory){
            throw new IllegalArgumentException();
        }


        if(node.has("id")){
            app.setId(UUID.fromString(node.get("id").asText()));
        }

        if(node.has("description")){
            app.setId(UUID.fromString(node.get("description").asText()));
        }

        if(node.has("questionnaireVote")){
            switch (node.get("questionnaireVote").asText()){
                case "RED":
                    app.setQuestionnaireVote(QuestionnaireVote.RED); break;
                case "ORANGE":
                    app.setQuestionnaireVote(QuestionnaireVote.ORANGE); break;
                case "GREEN":
                    app.setQuestionnaireVote(QuestionnaireVote.GREEN); break;

            }
        }

        if(node.has("detailVote")){
            JsonNode detailVote=node.get("detailVote");
            if(detailVote.isArray()){
                String[] detailVoteArray= new String[nQuestions];
                int i=0;
                for(JsonNode singleAnswer : detailVote){
                    detailVoteArray[i]=singleAnswer.asText();
                    i++;
                    if(i>=nQuestions){
                        break;
                    }
                }
                if(i<nQuestions){
                    for(int j=i; j<nQuestions; j++){
                        detailVoteArray[j]=null;
                    }
                }
                app.setDetailVote(detailVoteArray);
            }
        }

        if(node.has("optionalAnswers")){
            JsonNode optionalAnswers=node.get("optionalAnswers");
            if(optionalAnswers.isArray()){
                Hashtable<Integer, String> optionalAnswersHash= new Hashtable<>();
                int i=0;
                for(JsonNode singleAnswer : optionalAnswers){
                    optionalAnswersHash.put(i, singleAnswer.asText());
                    i++;
                    if(i>=nQuestions){
                        break;
                    }
                }
                if(i<nQuestions){
                    for(int j=i; j<nQuestions; j++){
                        optionalAnswersHash.put(i, null);
                    }
                }
                app.setOptionalAnswers(optionalAnswersHash);
            }
        }
        return app;
    }
}

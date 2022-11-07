package com.privacydashboard.application.views.questionnaireDeveloper;

import com.privacydashboard.application.data.QuestionnaireVote;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("Questionnaire")
@Route(value = "questionnaire", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class Questionnaire extends Div implements AfterNavigationObserver{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final TextField searchText=new TextField();
    private final Grid<IoTApp> grid= new Grid<>();

    public Questionnaire(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        this.communicationService = communicationService;
        addClassName("grid-view");
        initializeSearchText();
        initializeGrid();
        add(searchText, grid);
    }

    private void initializeSearchText(){
        searchText.setPlaceholder("Search...");
        searchText.setValueChangeMode(ValueChangeMode.LAZY);
        searchText.addValueChangeListener(e-> updateGrid());
        searchText.addClassName("search");
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::createQuestionnaireApp);
    }

    private HorizontalLayout createQuestionnaireApp(IoTApp app){
        Avatar avatar = new Avatar(app.getName());
        Span name= new Span(app.getName());
        name.addClassName("name");
        HorizontalLayout card = new HorizontalLayout(avatar, name);
        card.addClassName("card");
        card.addClickListener(e-> goToSingleQuestionnaire(app));
        if(app.getQuestionnaireVote()==null){
            name.addClassName("link");
        }
        else{
            name.addClassName("linkQuestionnaire");
            if(app.getQuestionnaireVote().equals(QuestionnaireVote.GREEN)){
                name.addClassName("greenName");
            }
            else if(app.getQuestionnaireVote().equals(QuestionnaireVote.ORANGE)){
                card.addClassName("orangeName");
            }
            else{
                card.addClassName("redName");
            }
        }
        return card;
    }

    private void goToSingleQuestionnaire(IoTApp app){
        communicationService.setApp(app);
        UI.getCurrent().navigate("questionnaire_developer");
    }

    private void updateGrid(){
        List<IoTApp> ioTAppList;
        if(searchText.getValue()==null || searchText.getValue().length()==0){
            ioTAppList=dataBaseService.getUserApps(authenticatedUser.getUser());
        }
        else{
            ioTAppList=dataBaseService.getUserAppsByName(authenticatedUser.getUser(), searchText.getValue());
        }
        grid.setItems(ioTAppList);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateGrid();
    }
}

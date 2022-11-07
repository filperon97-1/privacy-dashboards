package com.privacydashboard.application.views.questionnaireDeveloper;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.Collections;
import java.util.List;

@PageTitle("Questionnaire")
@Route(value = "questionnaire", layout = MainLayout.class)
@PermitAll
public class Questionnaire extends Div implements AfterNavigationObserver, BeforeEnterObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final TextField searchText=new TextField();
    private final Grid<IoTApp> grid= new Grid<>();

    private IoTApp priorityApp;

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        priorityApp=communicationService.getApp();
    }

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
        grid.addComponentColumn(app->new Button(app.getName(), e->goToSingleQuestionnaire(app)));
    }

    private void goToSingleQuestionnaire(IoTApp app){
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
        // se esiste l'app selezionata nei parametri, mettilo al primo posto
        if(priorityApp!=null){
            if(ioTAppList.contains(priorityApp)){
                Collections.swap(ioTAppList, 0, ioTAppList.indexOf(priorityApp));
            }
        }
        grid.setItems(ioTAppList);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateGrid();
    }
}

package com.privacydashboard.application.views.privacyNotice;

import com.privacydashboard.application.data.entity.PrivacyNotice;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.List;

@PageTitle("PrivacyNotice")
@Route(value="subject_privacyNotice", layout = MainLayout.class)
@RolesAllowed("SUBJECT")
public class SubjectPrivacyNoticeView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final TextField searchText=new TextField();
    private final Grid<PrivacyNotice> grid= new Grid<>();
    private final MyDialog privacyNoticeDialog= new MyDialog();

    private PrivacyNotice priorityNotice;

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        // get from notification
        priorityNotice= communicationService.getPrivacyNoticeFromNotification();
        if(priorityNotice!=null){
            showPrivacyNotice(priorityNotice);
            return;
        }

        // get from link of another View
        priorityNotice= communicationService.getPrivacyNotice();
        if(priorityNotice!=null){
            showPrivacyNotice(priorityNotice);
        }
    }

    public SubjectPrivacyNoticeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

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
        grid.addComponentColumn(privacyNotice -> new Button(privacyNotice.getApp().getName(), e->showPrivacyNotice(privacyNotice)));
    }

    private void showPrivacyNotice(PrivacyNotice privacyNotice){
        privacyNoticeDialog.setTitle("Privacy Notice " + privacyNotice.getApp().getName());
        privacyNoticeDialog.setContent(new VerticalLayout(convertText(privacyNotice.getText())));
        privacyNoticeDialog.setWithoutFooter(true);
        privacyNoticeDialog.setWidth("100%");
        privacyNoticeDialog.open();
    }

    private void updateGrid(){
        List<PrivacyNotice> privacyNoticeList;
        if(searchText.getValue()==null || searchText.getValue().length()==0){
            privacyNoticeList=dataBaseService.getAllPrivacyNoticeFromUser(authenticatedUser.getUser());
        }
        else{
            privacyNoticeList=dataBaseService.getUserPrivacyNoticeByAppName(authenticatedUser.getUser(), searchText.getValue());
        }

        if(priorityNotice!=null){
            if(privacyNoticeList.contains(priorityNotice)){
                Collections.swap(privacyNoticeList, 0, privacyNoticeList.indexOf(priorityNotice));
            }
        }
        grid.setItems(privacyNoticeList);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event){
        updateGrid();
    }

    /*
    DA CAPIRE COME VISUALIZZARE IL TESTO.
    PER ORA LO SALVO COME HTML IN CASO NON DIA ERRORI ALTRIMENTI COME TEXTAREA
    MA IN CASO SI VOGLIA CARICARE DIRETTAMENTE IL FILE BISOGNA CAPIRE COME FARE
     */
    private Component convertText(String text){
        try{
            return new Html(text);
        } catch (Exception e){
            e.printStackTrace();
            TextArea textArea= new TextArea();
            textArea.setValue(text);
            textArea.setWidthFull();
            textArea.setReadOnly(true);
            return textArea;
        }
    }

}

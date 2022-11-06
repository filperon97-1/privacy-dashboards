package com.privacydashboard.application.views.apps;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.contacts.ContactsView;
import com.privacydashboard.application.views.privacyNotice.ControllerDPOPrivacyNoticeView;
import com.privacydashboard.application.views.privacyNotice.SinglePrivacyNoticeView;
import com.privacydashboard.application.views.privacyNotice.SubjectPrivacyNoticeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.Collections;
import java.util.List;

@PageTitle("Apps")
@Route(value = "apps-view", layout = MainLayout.class)
@PermitAll
public class AppsView extends Div implements AfterNavigationObserver, BeforeEnterObserver {
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

    public AppsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService=dataBaseService;
        this.authenticatedUser=authenticatedUser;
        this.communicationService=communicationService;
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
        grid.addComponentColumn(this::createApp);
    }

    private VerticalLayout createApp(IoTApp app){
        Avatar avatar = new Avatar(app.getName());
        Span name = new Span(app.getName());
        name.addClassName("name");
        Details details = new Details("More", initializeApp(app));
        VerticalLayout card = new VerticalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.add(new HorizontalLayout(avatar , name) , details);

        // se c'è un priorityUser apri details
        if(app.equals(priorityApp)){
            details.setOpened(true);
        }
        return card;
    }

    private VerticalLayout initializeApp(IoTApp i){
        Span description= new Span(i.getDescription());
        Span privacyNotice= new Span("Privacy Notice");
        privacyNotice.addClassName("link");
        privacyNotice.addClickListener(e-> goToPrivacyNotice(i));
        Details controllerDetails= new Details("Data Controllers: " , getUsers(i, Role.CONTROLLER));
        Details DPODetails= new Details("Data Protection Officer: ", getUsers(i, Role.DPO));

        VerticalLayout content=new VerticalLayout(description, privacyNotice, controllerDetails, DPODetails);
        if(authenticatedUser.getUser().getRole().equals(Role.SUBJECT)){
            content.add(new Details("Consenses: " , getConsenses(i)));
        }
        else{
            content.add(new Details("Data Subjects: " , getUsers(i, Role.SUBJECT)));
        }
        return content;
    }

    private void goToPrivacyNotice(IoTApp i){
        communicationService.setPrivacyNotice(dataBaseService.getPrivacyNoticeFromApp(i));
        if(authenticatedUser.getUser().getRole().equals(Role.SUBJECT)){
            UI.getCurrent().navigate(SubjectPrivacyNoticeView.class);
        }
        else{
            UI.getCurrent().navigate(SinglePrivacyNoticeView.class);
        }
    }

    private VerticalLayout getUsers(IoTApp i, Role role){
        VerticalLayout layout=new VerticalLayout();
        List<User> users;
        switch(role) {
            case SUBJECT:
                users = dataBaseService.getSubjectsFromApp(i);
                break;
            case CONTROLLER:
                users = dataBaseService.getControllersFromApp(i);
                break;
            case DPO:
                users = dataBaseService.getDPOsFromApp(i);
                break;
            default:
                return null;
        }

        for(User u : users){
            Span contactLink=new Span(u.getName());
            contactLink.addClassName("link");
            contactLink.addClickListener(e->communicationService.setContact(u));
            contactLink.addClickListener(e-> UI.getCurrent().navigate(ContactsView.class));
            layout.add(contactLink);
        }
        return layout;
    }

    private VerticalLayout getConsenses(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<String> consenses=dataBaseService.getConsensesFromUserAndApp(authenticatedUser.getUser(), i);
        for(String consens :  consenses){
            Span consensSpan=new Span(consens);
            Button button=new Button("Withdraw consent", e -> withdrawConsent(i, consens));
            HorizontalLayout l=new HorizontalLayout(consensSpan, button);
            l.setAlignItems(FlexComponent.Alignment.CENTER);
            l.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            layout.add(l);
        }
        return layout;
    }

    private void withdrawConsent(IoTApp i, String consent){
        RightRequest request=new RightRequest();
        request.setSender(authenticatedUser.getUser());
        request.setRightType(RightType.WITHDRAWCONSENT);
        request.setApp(i);
        request.setOther(consent);
        request.setReceiver(dataBaseService.getControllersFromApp(i).get(0));
        request.setHandled(false);
        communicationService.setRightRequest(request);
        UI.getCurrent().navigate("rights");
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

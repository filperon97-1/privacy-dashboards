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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.Collections;
import java.util.List;

@PageTitle("Apps")
@Route(value = "apps-view", layout = MainLayout.class)
@PermitAll
public class AppsView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;
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
        initializeGrid();
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(app -> initializeApp(app));
        add(grid);
    }

    private Details initializeApp(IoTApp i){
        VerticalLayout content;
        Span description= new Span("description: " + i.getDescription());
        Details controllerDetails= new Details("Data Controllers: " , getUsers(i, Role.CONTROLLER));
        Details DPODetails= new Details("Data Protection Officer: ", getUsers(i, Role.DPO));

        if(authenticatedUser.getUser().getRole().equals(Role.SUBJECT)){
            Details consensesDetails= new Details("Consenses: " , getConsenses(i));
            content= new VerticalLayout(description, controllerDetails, DPODetails, consensesDetails);
        }
        else{
            Details subjectDetails=new Details("Data Subjects: " , getUsers(i, Role.SUBJECT));
            content= new VerticalLayout(description, controllerDetails, DPODetails, subjectDetails);
        }
        Details details=new Details(i.getName(), content);

        // SECONDO METODO (ACCORDION)
        /*
        Accordion accordion= new Accordion();
        accordion.add("Data Controllers: ", getUsers(i, Role.CONTROLLER));
        accordion.add("Data Protection Officer: ", getUsers(i, Role.DPO));
        if(getUser().getRole().equals(Role.SUBJECT)) {
            accordion.add("Consenses: ", getConsenses(i));
        }
        else{
            accordion.add("Data Subjects: " , getUsers(i, Role.SUBJECT));
        }
        Details details= new Details(i.getName(), new VerticalLayout(description, accordion));
         */

        // SE E L'APP CERCATA NEI PARAMETRI APRI DETAILS
        if(i.equals(priorityApp)){
            details.setOpened(true);
        }
        return details;
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
            HorizontalLayout l=new HorizontalLayout(new Span(consens), new Button("Withdraw consent", e -> withdrawConsent(i, consens)));
            l.setAlignItems(Alignment.CENTER);
            l.setVerticalComponentAlignment(Alignment.CENTER);
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

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        List<IoTApp> ioTAppList=dataBaseService.getUserApps(authenticatedUser.getUser());
        // se esiste l'app selezionata nei parametri, mettilo al primo posto
        if(priorityApp!=null){
            if(ioTAppList.contains(priorityApp)){
                Collections.swap(ioTAppList, 0, ioTAppList.indexOf(priorityApp));
            }
        }
        grid.setItems(ioTAppList);
    }
}

package com.privacydashboard.application.views.apps;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.contacts.ContactsView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
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
import java.util.Optional;
import java.util.UUID;

@PageTitle("Apps")
@Route(value = "apps-view/:appID?", layout = MainLayout.class)
@PermitAll
public class AppsView extends VerticalLayout implements AfterNavigationObserver, BeforeEnterObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final Grid<IoTApp> grid= new Grid<>();
    private List<IoTApp> ioTAppList;
    private UUID priorityAppID;

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        Optional<String> appID=event.getRouteParameters().get("appID");
        if(appID.isEmpty()){
            priorityAppID=null;
            return;
        }
        try{
            priorityAppID= UUID.fromString(appID.get());
        }catch (Exception e ){
            priorityAppID=null;
        }
    }

    public AppsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser){
        this.dataBaseService=dataBaseService;
        this.authenticatedUser=authenticatedUser;
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

        if(getUser().getRole().equals(Role.SUBJECT)){
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
        if(i.getId().equals(priorityAppID)){
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
            layout.add(new RouterLink(u.getName() , ContactsView.class,  new RouteParameters("contactID", u.getId().toString())));
        }
        return layout;
    }

    private VerticalLayout getConsenses(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<String> consenses=dataBaseService.getConsensesFromUserAndApp(getUser(), i);
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
        request.setSender(getUser());
        request.setRightType(RightType.WITHDRAWCONSENT);
        request.setApp(i);
        request.setOther(consent);
        request.setReceiver(dataBaseService.getControllersFromApp(i).get(0));
        request.setHandled(false);
        ComponentUtil.setData(UI.getCurrent(), "RightRequest", request);
        UI.getCurrent().navigate("rights");
    }

    private User getUser(){
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            return null;
        }
        return maybeUser.get();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        ioTAppList=dataBaseService.getUserApps(getUser());
        // se esiste l'app selezionata nei parametri, mettilo al primo posto
        if(priorityAppID!=null){
            Optional<IoTApp> maybeApp=dataBaseService.getApp(priorityAppID);
            if(maybeApp.isPresent() && ioTAppList.contains(maybeApp.get())){
                Collections.swap(ioTAppList, 0, ioTAppList.indexOf(maybeApp.get()));
            }
            else{
                priorityAppID=null;
            }
        }
        grid.setItems(ioTAppList);
    }
}

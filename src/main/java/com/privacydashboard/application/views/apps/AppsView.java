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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
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
    private List<IoTApp> ioTAppList;
    private User user;
    private UUID priorityAppID;
    private Div appDiv=new Div();

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
        user=getUser();
        add(appDiv);
    }

    private User getUser(){
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            return null;
        }
        return maybeUser.get();
    }

    private Details initializeApp(IoTApp i){
        Span description= new Span("description: " + i.getDescription());
        Details appUsers;
        VerticalLayout content;
        if(user.getRole().equals(Role.SUBJECT)){
            appUsers= new Details("Data Controllers: " , getDataControllers(i));
            Details consenses= new Details("Consenses: " , getConsenses(i));
            content=new VerticalLayout(description, appUsers, consenses);
        }
        else{
            appUsers=new Details("Data Subjects: " , getDataSubjects(i));
            content=new VerticalLayout(description, appUsers);
        }
        Details details=new Details(i.getName(), content);
        // SE E L'APP CERCATA NEI PARAMETRI APRI DETAILS
        if(i.getId().equals(priorityAppID)){
            details.setOpened(true);
        }
        return details;
    }

    private VerticalLayout getDataControllers(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<User> controllers = dataBaseService.getControllersFromApp(i);
        for(User u : controllers){
            layout.add(new RouterLink(u.getName() , ContactsView.class,  new RouteParameters("contactID", u.getId().toString())));
        }
        return layout;
    }

    private VerticalLayout getDataSubjects(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<User> subjects = dataBaseService.getSubjectsFromApp(i);
        for(User u : subjects){
            layout.add(new RouterLink(u.getName() , ContactsView.class,  new RouteParameters("contactID", u.getId().toString())));
        }
        return layout;
    }

    private VerticalLayout getConsenses(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<String> consenses=dataBaseService.getConsensesFromUserAndApp(user, i);
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
        request.setSender(user);
        request.setRightType(RightType.WITHDRAWCONSENT);
        request.setApp(i);
        request.setOther(consent);
        request.setReceiver(dataBaseService.getControllersFromApp(i).get(0));
        request.setHandled(false);
        ComponentUtil.setData(UI.getCurrent(), "RightRequest", request);
        UI.getCurrent().navigate("rights");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        appDiv.removeAll();
        ioTAppList=dataBaseService.getUserApps(user);
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
        for(IoTApp i : ioTAppList){
            appDiv.add(initializeApp(i));
        }
    }
}

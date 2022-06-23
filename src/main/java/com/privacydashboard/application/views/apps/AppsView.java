package com.privacydashboard.application.views.apps;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.contacts.ContactsView;
import com.vaadin.flow.component.charts.model.Navigator;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;

@PageTitle("Apps")
@Route(value = "apps-view", layout = MainLayout.class)
@PermitAll
public class AppsView extends VerticalLayout {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private List<IoTApp> ioTAppList;
    private List<User> contacts;
    private User user;
    private Navigator navigator;
    public AppsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser){
        this.dataBaseService=dataBaseService;
        this.authenticatedUser=authenticatedUser;
        navigator=new Navigator();
        user=getUser();
        contacts=dataBaseService.getAllContactsFromUser(user);
        ioTAppList=dataBaseService.getUserApps(user);
        for(IoTApp i : ioTAppList){
            add(initializeApp(i));
        }
    }

    private User getUser(){
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            return null;
        }
        return maybeUser.get();
    }

    private Details initializeApp(IoTApp i){
        Span description= new Span("description: " + i.getDescription());
        Details dataControllers= new Details("Data Controllers: " , getDataControllers(i));
        Details consenses= new Details("Consenses: " , getConsenses(i));
        VerticalLayout content=new VerticalLayout(description, dataControllers, consenses);
        return new Details(i.getName(), content);
    }

    private VerticalLayout getDataControllers(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<User> controllers = dataBaseService.getControllersFromApp(i);
        for(User u : controllers){
            layout.add(new RouterLink(u.getName() , ContactsView.class,  new RouteParameters("contactID", u.getId().toString())));
        }
        return layout;
    }

    private VerticalLayout getConsenses(IoTApp i){
        VerticalLayout layout=new VerticalLayout();
        List<String> consenses=dataBaseService.getConsensesFromUserAndApp(user, i);
        for(String consens :  consenses){
            layout.add(new Span(consens));
        }
        return layout;
    }

}

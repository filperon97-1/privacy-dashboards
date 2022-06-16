package com.privacydashboard.application.views.apps;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jdk.jfr.Name;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@PageTitle("Apps")
@Route(value = "apps-view", layout = MainLayout.class)
@PermitAll
public class AppsView extends VerticalLayout {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    public AppsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser){
        this.dataBaseService=dataBaseService;
        this.authenticatedUser=authenticatedUser;
        List<IoTApp> ioTAppList=getIotApps();
        for(IoTApp i : ioTAppList){
            add(new H1(i.getName()));
            add(new H2("descrizione: " + i.getDescription()));
            add(new H2("Data Controller: " + i.getDataController().getName()));
            add(new H2("consensi dati: " ));
            Set<String> consenses=dataBaseService.getConsensesFromUserAndApp(getUser(), i);
            for(String s : consenses){
                add(new H3(s));
            }
        }
    }

    private User getUser(){
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            return null;
        }
        return maybeUser.get();
    }

    private List<IoTApp> getIotApps(){
        return dataBaseService.getUserApps(getUser());
    }

}

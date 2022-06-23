package com.privacydashboard.application.views.apps;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.Navigation;
import com.vaadin.flow.component.charts.model.Navigator;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
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
    private List<IoTApp> ioTAppList;
    private User user;
    private Navigator navigator;
    public AppsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser){
        this.dataBaseService=dataBaseService;
        this.authenticatedUser=authenticatedUser;
        navigator=new Navigator();
        user=getUser();
        ioTAppList=dataBaseService.getUserApps(user);

        // DA ELIMINARE
        for(int i=0; i<100; i++){
            Span span=new Span("span numero" + String.valueOf(i));
            span.setId(String.valueOf(i));
            add(span);
        }
        for(IoTApp i : ioTAppList){
            Span description= new Span("description: " + i.getDescription());
            Span dataController= new Span("Data Controller: ");
            Span consenses=new Span("Consenses");
            VerticalLayout content=new VerticalLayout(description, dataController, consenses);
            Details app = new Details(i.getName(), content);
            add(app);
        }
        Button button=new Button("clicca", e -> UI.getCurrent().navigate("contacts/3"));
        add(button);
    }

    private User getUser(){
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            return null;
        }
        return maybeUser.get();
    }

}

package com.privacydashboard.application.views.home;

import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.UserService;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import javax.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {
    //DA ELIMINARE
    private List<User> userList;
    private UserService userService;

    public HomeView(UserService userService) {
        //DA ELIMINARE
        this.userService=userService;
        userList=userService.findAll();
        for(User user: userList){
            add(new H2(user.getUsername() + " " + user.getHashedPassword() + " " + user.getDataRole()));
        }
        //FINE ELIMINAZIONE
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        add(new H2("This place intentionally left empty"));
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}

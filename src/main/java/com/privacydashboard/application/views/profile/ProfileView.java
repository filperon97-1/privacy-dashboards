package com.privacydashboard.application.views.profile;

import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.model.HorizontalAlign;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ProfileView extends VerticalLayout {
    private final AuthenticatedUser authenticatedUser;
    private final DataBaseService dataBaseService;
    private final User user;

    public ProfileView(User user, AuthenticatedUser authenticatedUser, DataBaseService dataBaseService){
        this.user=user;
        this.authenticatedUser=authenticatedUser;
        this.dataBaseService=dataBaseService;
        initializeInformation();
        initializeRemoveEverything();
        initializeLogOut();
        setAlignItems(Alignment.CENTER);
    }

    private void initializeInformation(){
        Avatar avatar=new Avatar(user.getName(), user.getProfilePictureUrl());
        Span image=new Span(avatar);
        image.addClickListener(e-> changeImage());

        Span name=new Span("Name: " + user.getName());
        Span mail=new Span("Mail: " + user.getMail());
        Span role=new Span("Role: " + user.getRole());
        Button changePassword= new Button("Change password");
        add(image, name, mail, role, changePassword);
    }

    private void changeImage(){

    }

    private void initializeRemoveEverything(){
        Button removeEverythingButton=new Button("Remove everything", e-> confirmDialog());
        add(removeEverythingButton);
    }

    private void confirmDialog(){
        Dialog dialog=new Dialog();
        Span text=new Span("Are you sure you want to remove all your personal information from every app you have?");
        Button confirm=new Button("Confirm", e-> {dataBaseService.removeEverythingFromUser(user);
                                                        dialog.close();});
        Button cancel=new Button("Cancel", e-> dialog.close());

        VerticalLayout layout= new VerticalLayout(text, new HorizontalLayout(confirm, cancel));
        layout.setAlignItems(Alignment.CENTER);
        dialog.add(layout);
        dialog.setOpened(true);
    }


    private void initializeLogOut(){
        Button logOutButton= new Button("LogOut", e-> authenticatedUser.logout());
        add(logOutButton);
    }

}

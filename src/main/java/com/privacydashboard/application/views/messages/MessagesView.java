package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.Optional;

@PageTitle("Messages")
@Route(value="messages", layout = MainLayout.class)
@PermitAll
public class MessagesView extends VerticalLayout implements AfterNavigationObserver{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;
    private final Grid<User> grid=new Grid<>();
    private final Dialog newMessageDialog=new Dialog();
    private final Button newMessageButton=new Button("New Message");

    public MessagesView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        this.communicationService=communicationService;
        addClassName("contacts-view");
        initializeButton();
        initializeGrid();
        initializeNewDialog();
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(contact -> showContact(contact));
        add(grid);
    }

    private void initializeButton(){
        newMessageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newMessageButton.addClickListener(e-> newMessageDialog.open());
        add(newMessageButton);
    }

    private void initializeNewDialog(){
        H1 titleText= new H1("Select Contact");

        ComboBox<User> contactComboBox= new ComboBox<>("Contacts");
        contactComboBox.setItems(dataBaseService.getAllContactsFromUser(getUser()));
        contactComboBox.setItemLabelGenerator(User::getName);

        Button newMessage=new Button("Continue", e->{if(contactComboBox.getValue()!=null){
                                                            newMessageDialog.close();
                                                            goToConversation(contactComboBox.getValue());
                                                            }});
        Button cancel=new Button("Cancel", e-> newMessageDialog.close());
        HorizontalLayout buttonLayout= new HorizontalLayout(newMessage, cancel);

        VerticalLayout layout=new VerticalLayout(titleText, contactComboBox, buttonLayout);
        newMessageDialog.add(layout);
    }

    private HorizontalLayout showContact(User contact){
        Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
        Span name= new Span(contact.getName());
        name.addClassName("name");
        name.addClassName("link");
        HorizontalLayout card = new HorizontalLayout(avatar, name);
        card.addClassName("card");
        card.addClickListener(e-> goToConversation(contact));
        return card;
    }

    private void goToConversation(User contact){
        communicationService.setContact(contact);
        UI.getCurrent().navigate(SingleConversationView.class);
    }

    private void updateGrid(){
        grid.setItems(dataBaseService.getUserConversationFromUser(getUser()));
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
        updateGrid();
    }

}

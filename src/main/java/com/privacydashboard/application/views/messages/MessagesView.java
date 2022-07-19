package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.User;
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
    private final Grid<User> grid=new Grid<>();
    private final Dialog newMessageDialog=new Dialog();
    private final Button newMessageButton=new Button("New Message");

    public MessagesView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
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
                                                            UI.getCurrent().navigate(SingleConversationView.class,
                                                                    new RouteParameters("contactID", contactComboBox.getValue().getId().toString()));
                                                            }
        });
        Button cancel=new Button("Cancel", e-> newMessageDialog.close());
        HorizontalLayout buttonLayout= new HorizontalLayout(newMessage, cancel);

        VerticalLayout layout=new VerticalLayout(titleText, contactComboBox, buttonLayout);
        newMessageDialog.add(layout);
    }

    private RouterLink showContact(User contact){
        Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
        Span name= new Span(contact.getName());
        name.addClassName("name");
        HorizontalLayout card = new HorizontalLayout(avatar, name);
        card.addClassName("card");
        RouterLink link=new RouterLink();
        link.setRoute(SingleConversationView.class, new RouteParameters("contactID", contact.getId().toString()));
        link.add(card);
        return link;
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

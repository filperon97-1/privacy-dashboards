package com.privacydashboard.application.views.contacts;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.apps.AppsView;
import com.privacydashboard.application.views.messages.SingleConversationView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;

import java.util.*;
import javax.annotation.security.PermitAll;

@PageTitle("Contacts")
@Route(value = "contacts", layout = MainLayout.class)
@PermitAll
public class ContactsView extends Div implements AfterNavigationObserver, BeforeEnterObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;
    private User priorityUser;

    private final Grid<User> grid = new Grid<>();
    private final TextField searchText=new TextField();

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        priorityUser=communicationService.getContact();
    }

    public ContactsView(AuthenticatedUser authenticatedUser, DataBaseService dataBaseService, CommunicationService communicationService) {
        this.authenticatedUser=authenticatedUser;
        this.dataBaseService=dataBaseService;
        this.communicationService=communicationService;
        addClassName("contacts-view");
        initializeSearchText();
        initializeGrid();
        add(searchText, grid);
    }

    private void initializeSearchText(){
        searchText.setPlaceholder("Search...");
        searchText.setValueChangeMode(ValueChangeMode.LAZY);
        searchText.addValueChangeListener(e-> updateContacts());
        searchText.addClassName("search");
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(contact -> createContact(contact));
    }

    private VerticalLayout createContact(User contact){
        Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
        Span name = new Span(contact.getName());
        name.addClassName("name");
        Details details = new Details("More", generateContactInformations(contact));
        VerticalLayout card = new VerticalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.add(new HorizontalLayout(avatar , name) , details);

        // se c'è un priorityUser apri details
        if(contact.equals(priorityUser)){
            details.setOpened(true);
        }
        return card;
    }

    private VerticalLayout generateContactInformations(User contact){
        HorizontalLayout messageLink=new HorizontalLayout(new Span("send message"), VaadinIcon.COMMENT.create());
        messageLink.addClassName("link");
        messageLink.addClickListener(e->communicationService.setContact(contact));
        messageLink.addClickListener(e->UI.getCurrent().navigate(SingleConversationView.class));
        return new VerticalLayout(new Span("Name: " + contact.getName()),
                                  new Span("Role: Data " +contact.getRole()),
                                  new Span("Mail: "+ (contact.getMail()==null ? "" : contact.getMail())),
                                  messageLink,
                                  new Details("Apps:" , getApps(contact)));
    }

    private VerticalLayout getApps(User contact){
        VerticalLayout layout=new VerticalLayout();
        List<IoTApp> appList=dataBaseService.getAppsFrom2Users(authenticatedUser.getUser(), contact);
        for(IoTApp i : appList) {
            Span appSpan= new Span(i.getName());
            appSpan.addClassName("link");
            appSpan.addClickListener(e-> communicationService.setApp(i));
            appSpan.addClickListener(e-> UI.getCurrent().navigate(AppsView.class));
            layout.add(appSpan);
        }
        return layout;
    }

    private void updateContacts(){
        List<User> contacts;
        if(searchText.getValue()==null || searchText.getValue().length()==0){
            contacts=dataBaseService.getAllContactsFromUser(authenticatedUser.getUser());
        }
        else{
            contacts=dataBaseService.getAllContactsFromUserFilterByName(authenticatedUser.getUser(), searchText.getValue());
        }

        if(priorityUser!=null){
            if(contacts.contains(priorityUser)){
                Collections.swap(contacts, 0 , contacts.indexOf(priorityUser));
            }
        }
        grid.setItems(contacts);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateContacts();
    }

}

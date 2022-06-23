package com.privacydashboard.application.views.contacts;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.apps.AppsView;
import com.privacydashboard.application.views.messages.SingleConversationView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.*;
import javax.annotation.security.PermitAll;

@PageTitle("Contacts")
@Route(value = "contacts/:contactID?", layout = MainLayout.class)
@PermitAll
public class ContactsView extends Div implements AfterNavigationObserver, BeforeEnterObserver {
    Grid<User> grid = new Grid<>();
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private User user;
    private List<User> contacts;
    private UUID priorityUserID;    //FATTO COSI E' UN PO' UNA MERDA, TROVARE SOLUZIONE MIGLIORE

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        Optional<String> contactID=event.getRouteParameters().get("contactID");
        if(!contactID.isPresent()){
            priorityUserID=null;
            return;
        }
        try{
            priorityUserID=UUID.fromString(contactID.get());
        }catch (Exception e ){
            priorityUserID=null;
            return;
        }

    }

    public ContactsView(AuthenticatedUser authenticatedUser, DataBaseService dataBaseService) {
        this.authenticatedUser=authenticatedUser;
        this.dataBaseService=dataBaseService;
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            add(new H2("user not logged in"));
            return;
        }
        user= maybeUser.get();
        contacts=dataBaseService.getAllContactsFromUser(user);
        addClassName("grid-views");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(contact -> createContact(contact));
        add(grid);
    }

    private VerticalLayout createContact(User contact){
        VerticalLayout card = new VerticalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
        avatar.addClassNames("me-xs");

        Span name = new Span(contact.getName());
        name.addClassName("name");

        HorizontalLayout profile=new HorizontalLayout(avatar , name);
        profile.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        VerticalLayout content=generateContactInformations(contact);
        Details details = new Details("More", content);

        card.add(profile , details);
        card.setAlignItems(FlexComponent.Alignment.START);
        card.setHorizontalComponentAlignment(FlexComponent.Alignment.START);
        // se c'è un priorityUser apri details
        if(contact.getId().equals(priorityUserID)){
            details.setOpened(true);
        }
        return card;
    }

    private VerticalLayout generateContactInformations(User contact){
        Span name = new Span("Name: " + contact.getName());
        Span role = new Span("Role: Data " +contact.getDataRole());
        Span phone = new Span("(501) 555-9128");
        VerticalLayout apps=getApps(contact);
        Details details= new Details("Apps:" , apps);
        RouterLink routerLink=new RouterLink();
        routerLink.setRoute( SingleConversationView.class, new RouteParameters("contactID", contact.getId().toString()));
        routerLink.add(new HorizontalLayout(new Span("send message"), VaadinIcon.COMMENT.create()));
        return new VerticalLayout(name, role, phone, routerLink, details);
    }

    private VerticalLayout getApps(User contact){
        VerticalLayout layout=new VerticalLayout();
        List<IoTApp> appList=dataBaseService.getAppsFrom2Users(user, contact);
        for(IoTApp i : appList) {
            layout.add(new RouterLink(i.getName(), AppsView.class , new RouteParameters("appID", i.getId().toString())));
        }
        return layout;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // se esiste il contatto selezionato nei parametri, mettilo al primo posto
        if(priorityUserID!=null ){
            Optional<User> maybeU=dataBaseService.getUser(priorityUserID);
            if(maybeU.isPresent() && contacts.contains(maybeU.get())){
                Collections.swap(contacts, 0 , contacts.indexOf(maybeU.get()));
            }
            else{
                priorityUserID=null;
            }
        }
        grid.setItems(contacts);
    }

}

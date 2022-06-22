package com.privacydashboard.application.views.contacts;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.messages.SingleConversationView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.*;
import javax.annotation.security.PermitAll;

@PageTitle("Contacts")
@Route(value = "contacts", layout = MainLayout.class)
@PermitAll
public class ContactsView extends Div implements AfterNavigationObserver {
    Grid<User> grid = new Grid<>();

    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    public ContactsView(AuthenticatedUser authenticatedUser, DataBaseService dataBaseService) {
        this.authenticatedUser=authenticatedUser;
        this.dataBaseService=dataBaseService;
        addClassName("grid-views");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(user -> createContact(user));
        add(grid);
    }

    private VerticalLayout createContact(User user){
        VerticalLayout card = new VerticalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Avatar avatar = new Avatar(user.getName(), user.getProfilePictureUrl());
        avatar.addClassNames("me-xs");

        Span name = new Span(user.getName());
        name.addClassName("name");

        HorizontalLayout profile=new HorizontalLayout(avatar , name);
        profile.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        VerticalLayout content=generateContactInformations(user);
        Details details = new Details("More", content);

        card.add(profile , details);
        //card.expand(name);
        card.setAlignItems(FlexComponent.Alignment.START);
        card.setHorizontalComponentAlignment(FlexComponent.Alignment.START);
        return card;
    }

    private VerticalLayout generateContactInformations(User user){
        Span name = new Span("Name: " + user.getName());
        Span role = new Span("Role: Data " +user.getDataRole());
        Span phone = new Span("(501) 555-9128");

        VerticalLayout apps=new VerticalLayout(new Span("app1") , new Span("app2") , new Span("app3") , new Span("app4"));
        Details details= new Details("Apps:" , apps);
        RouterLink routerLink= new RouterLink("send message",
                SingleConversationView.class, new RouteParameters("contactID", user.getId().toString()));
        Icon commentIcon = VaadinIcon.COMMENT.create();
        return new VerticalLayout(name, role, phone, new HorizontalLayout(routerLink, commentIcon), details);

    }

    /*private HorizontalLayout createCard(Person person) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Image image = new Image();
        image.setSrc(person.getImage());
        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name = new Span(person.getName());
        name.addClassName("name");
        Span date = new Span(person.getDate());
        date.addClassName("date");
        header.add(name, date);

        Span post = new Span(person.getPost());
        post.addClassName("post");

        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");

        Icon likeIcon = VaadinIcon.HEART.create();
        likeIcon.addClassName("icon");
        Span likes = new Span(person.getLikes());
        likes.addClassName("likes");
        Icon commentIcon = VaadinIcon.COMMENT.create();
        commentIcon.addClassName("icon");
        Span comments = new Span(person.getComments());
        comments.addClassName("comments");
        Icon shareIcon = VaadinIcon.CONNECT.create();
        shareIcon.addClassName("icon");
        Span shares = new Span(person.getShares());
        shares.addClassName("shares");

        actions.add(likeIcon, likes, commentIcon, comments, shareIcon, shares);

        description.add(header, post, actions);
        card.add(image, description);
        return card;
    }*/

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        List<User> contacts;
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            return;
        }
        User user= maybeUser.get();
        contacts=dataBaseService.getAllContactsFromUser(user);
        grid.setItems(contacts);
    }

}

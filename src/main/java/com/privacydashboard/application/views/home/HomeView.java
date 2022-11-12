package com.privacydashboard.application.views.home;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.data.service.UserService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import javax.annotation.security.PermitAll;
import java.util.Arrays;
import java.util.List;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class HomeView extends VerticalLayout {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final int nSection=6;
    private final int nRows=2;
    private final VerticalLayout[] layouts= new VerticalLayout[nSection];
    private final Span[] titles= new Span[nSection];
    private final Div[] icons= new Div[nSection];

    public HomeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService) {
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        addClassName("home-view");

        initializeLayout();

        createSections();





        /*add(new HorizontalLayout(layouts[0], layouts[1], layouts[2]));
        add(new HorizontalLayout(layouts[3], layouts[4], layouts[5]));*/
        for(int i=0;i<nRows;i++){
            add(new HorizontalLayout(Arrays.copyOfRange(layouts, i*nSection/nRows, (i+1)*nSection/nRows)));
        }


        // DIVIDERE IN VARIE SEZIONI: CONTACTS, MESSAGES, APPS, RIGHTS, PRIVACY NOTICE, QUESTIONNAIRE (per Controllers),

    }

    private void initializeLayout(){
        for(int i=0; i<nSection; i++){
            layouts[i]= new VerticalLayout();
            layouts[i].addClassNames("section", "pointer");
            titles[i]= new Span();
            titles[i].addClassName("title");
            icons[i]= new Div();
            icons[i].addClassNames("las la-10x");
            layouts[i].add(titles[i], icons[i]);
            layouts[i].setAlignItems(Alignment.CENTER);
        }
    }

    private void createSections(){
        createSingleSection(0, "Contacts", "contacts", "la-address-book");
        createSingleSection(1, "Messages", "messages", "la-comments");
        createSingleSection(3, "Apps", "apps-view", "la-list");
        if(authenticatedUser.getUser().getRole().equals(Role.CONTROLLER) || authenticatedUser.getUser().getRole().equals(Role.DPO)){
            createSingleSection(2, "Rights", "rights_controller", "la-school");
            createSingleSection(4, "Privacy Notice", "controller_privacyNotice", "la-file");
            createSingleSection(5, "Questionnaire", "questionnaire","la-archive");
        }
        else{
            createSingleSection(2, "Rights", "rights", "la-school");
            createSingleSection(4, "Privacy Notice", "subject_privacyNotice", "la-file");
        }

    }

    private void createSingleSection(int sectionNumber, String title, String navigationPage, String className){
        titles[sectionNumber].setText(title);
        layouts[sectionNumber].addClickListener(e-> UI.getCurrent().navigate(navigationPage));
        icons[sectionNumber].addClassName(className);
    }

}

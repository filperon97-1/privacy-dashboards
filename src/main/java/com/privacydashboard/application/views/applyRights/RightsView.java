package com.privacydashboard.application.views.applyRights;

import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import java.awt.*;
import java.lang.reflect.Method;

interface RightAction{
    void startAction();
}

@PageTitle("Rights")
@Route(value="rights", layout = MainLayout.class)
@PermitAll
public class RightsView extends VerticalLayout {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private Dialog dialog=new Dialog();

    public RightsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        createDialog();
        generateAllRightsDetails();
        /*VerticalLayout content=new VerticalLayout(new Span("you have the right to withdraw consent at any time [GDPR, article 13 2(C)]"),
                new Button("Withdraw a consent" , e-> startWithdrawConsent()));
        Details consenses = new Details("Consenses", content);
        add(consenses);*/
    }

    private void generateAllRightsDetails(){
        RightAction withdrawAction= () -> startWithdrawConsent();
        add(generateRightDetail("Consenses", "you have the right to withdraw consent at any time [GDPR, article 13 2(C)]",
                "Withdraw a consent", withdrawAction));

        RightAction askInformation=() -> startWithdrawConsent();
        add(generateRightDetail("Ask information", "you have the right to know some information:\n" +
                        "the period for which the personal data will be stored\n" +
                        "the purposes of the processing for which the personal data are intended\n" +
                        "the recipients or categories of recipients of the personal data",
                "Ask information", askInformation));

        RightAction complain=() -> startWithdrawConsent();
        add(generateRightDetail("Complain", "compile a complain to the supervisory authority",
                "Compile a complain", complain));

        RightAction erasure=() -> startWithdrawConsent();
        add(generateRightDetail("Right to erasure", "ask to erase some personal data",
                "Ask to erase", erasure));

    }

    private Details generateRightDetail(String title, String description, String buttonString , RightAction action){
        VerticalLayout content=new VerticalLayout(new Span(description),
                new Button(buttonString , e-> action.startAction()));
        Details consenses = new Details(title, content);
        return consenses;
    }

    private void createDialog(){
        H1 titleText= new H1("Select Contact");
        dialog.add(titleText);
        add(dialog);
    }

    private void startWithdrawConsent(){
        dialog.open();
    }
}

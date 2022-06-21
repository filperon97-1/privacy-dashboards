package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("NoContact")
@Route(value="no-contact")
@PermitAll
public class NoContactView extends Div {
    public NoContactView(){
        setText("Contact does not exist");
    }
}

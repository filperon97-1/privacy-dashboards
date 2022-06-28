package com.privacydashboard.application.views.applyRights;

import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.security.RolesAllowed;

@PageTitle("Single Right")
@Route(value="single_right", layout = MainLayout.class)
@RolesAllowed("SUBJECT")
public class SingleRightView extends VerticalLayout {
    Logger logger = LoggerFactory.getLogger(getClass());
    public SingleRightView(){
        Object object=ComponentUtil.getData(UI.getCurrent(), "RightRequest");
        RightRequest request=(RightRequest) object; // CATTURARE LE POSSIBILI ECCEZZIONI
        add(new H1(request.getApp().getName() + "  " + request.getSender().getName()));
        add(new H1("hola"));
    }


}

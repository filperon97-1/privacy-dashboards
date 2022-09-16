package com.privacydashboard.application.views.privacyNotice;

import com.privacydashboard.application.data.entity.PrivacyNotice;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("PrivacyNotice")
@Route(value="subject_privacyNotice", layout = MainLayout.class)
@RolesAllowed("SUBJECT")
public class SubjectPrivacyNoticeView extends VerticalLayout implements AfterNavigationObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final Grid<PrivacyNotice> grid= new Grid<>();
    private final MyDialog privacyNoticeDialog= new MyDialog();

    public SubjectPrivacyNoticeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        initializeGrid();
        add(grid);
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(privacyNotice -> new Button(privacyNotice.getApp().getName(), e->showPrivacyNotice(privacyNotice)));
    }

    private void showPrivacyNotice(PrivacyNotice privacyNotice){
        TextArea textArea=new TextArea();
        textArea.setValue(privacyNotice.getText());
        textArea.setReadOnly(true);
        textArea.setWidthFull();
        privacyNoticeDialog.setTitle("Privacy Notice " + privacyNotice.getApp().getName());
        privacyNoticeDialog.setContent(new VerticalLayout(textArea));
        privacyNoticeDialog.setWithoutFooter(true);
        privacyNoticeDialog.setWidth("100%");
        privacyNoticeDialog.open();
    }

    private void updateGrid(){
        List<PrivacyNotice> privacyNoticeList=dataBaseService.getAllPrivacyNoticeFromUser(authenticatedUser.getUser());
        grid.setItems(privacyNoticeList);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event){
        updateGrid();
    }

}

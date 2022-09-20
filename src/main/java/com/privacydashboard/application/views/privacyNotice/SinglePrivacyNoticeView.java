package com.privacydashboard.application.views.privacyNotice;

import com.privacydashboard.application.data.entity.PrivacyNotice;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;

import javax.annotation.security.RolesAllowed;

@PageTitle("Compile privacy notice")
@Route(value="single_privacy_notice", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class SinglePrivacyNoticeView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private FormPrivacyNotice form;
    private final Tab customizedTab= new Tab("Create from empty");
    private final Tab standardTab= new Tab("Use template");
    private final Tab uploadTab= new Tab(new Icon("lumo", "upload"));
    private final Tabs tabs=new Tabs(customizedTab, standardTab, uploadTab);
    private final Div content= new Div();
    private final TextArea textArea= new TextArea();
    private final Button saveButton= new Button("Save", e->savePrivacyNotice());

    private PrivacyNotice privacyNotice;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        privacyNotice=communicationService.getPrivacyNotice();
        if(privacyNotice==null || privacyNotice.getApp()==null || !dataBaseService.getUserApps(authenticatedUser.getUser()).contains(privacyNotice.getApp())){
            event.rerouteTo(ControllerDPOPrivacyNoticeView.class);
        }
        form=new FormPrivacyNotice(privacyNotice, dataBaseService);
    }

    public SinglePrivacyNoticeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        content.setSizeFull();
        textArea.setWidthFull();

        initializeTabs();
        add(tabs, content);
    }

    private void initializeTabs(){
        customizedTab.addClassName("pointer");
        standardTab.addClassName("pointer");
        uploadTab.addClassName("pointer");
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(
            e->{
                if(e.getSelectedTab().equals(e.getPreviousTab())){
                    return;
                }
                if(e.getSelectedTab().equals(standardTab)){
                    content.removeAll();
                    content.add(form);
                }
                else if(e.getSelectedTab().equals(customizedTab)){
                    content.removeAll();
                    content.add(new VerticalLayout(textArea, saveButton));
                }
                else{
                    content.removeAll();
                    content.add(new Span("Da implementare"));
                }
            }

        );
    }

    private void savePrivacyNotice(){
        if(textArea.getValue()==null){
            return;
        }
        if(dataBaseService.getPrivacyNoticeFromApp(privacyNotice.getApp())==null){
            confirmNewPrivacyNotice();
        }
        else{
            confirmOverwritePrivacyNotice();
        }
    }

    private void confirmNewPrivacyNotice(){
        MyDialog dialog=new MyDialog();
        dialog.setTitle("Confirm");
        dialog.setContent(new HorizontalLayout(new Span("Do you want to upload this Privacy Notice for the app: " + privacyNotice.getApp().getName() + "?")));
        dialog.setContinueButton(new Button("confirm", e-> {
            dataBaseService.addPrivacyNoticeForApp(privacyNotice.getApp(), textArea.getValue());
            Notification notification = Notification.show("Privacy Notice uploaded correctly");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(ControllerDPOPrivacyNoticeView.class);
            dialog.close();
        }));
        dialog.open();
    }

    private void confirmOverwritePrivacyNotice(){
        MyDialog dialog=new MyDialog();
        dialog.setTitle("Confirm");
        dialog.setContent(new HorizontalLayout(new Span("There is already a Privacy Notice for the app " + privacyNotice.getApp().getName() + ", do you want to overwrite it with this one?")));
        dialog.setContinueButton(new Button("confirm", e-> {
            dataBaseService.changePrivacyNoticeForApp(privacyNotice.getApp(), textArea.getValue());
            Notification notification = Notification.show("Privacy Notice overwritten correctly");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(ControllerDPOPrivacyNoticeView.class);
            dialog.close();
        }));
        dialog.open();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event){
        if(privacyNotice.getText()!=null){
            textArea.setValue(privacyNotice.getText());
            tabs.setSelectedTab(customizedTab);
            content.add(new VerticalLayout(textArea, saveButton));
        }
        else{
            tabs.setSelectedTab(standardTab);
            content.add(form);
        }
    }
}

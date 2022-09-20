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
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.io.InputStream;

@PageTitle("Compile privacy notice")
@Route(value="single_privacy_notice", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class SinglePrivacyNoticeView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final Tabs tabs=new Tabs();
    private final Tab customizedTab= new Tab("Create from empty");
    private final Tab standardTab= new Tab("Use template");
    private final Tab uploadTab= new Tab(new Icon("lumo", "upload"));
    private final Div content= new Div();

    private final VerticalLayout customizedLayout= new VerticalLayout();
    private FormPrivacyNotice standardLayout;
    private final VerticalLayout uploadLayout= new VerticalLayout();
    private final TextArea textAreaCustomized= new TextArea();

    private PrivacyNotice privacyNotice;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        privacyNotice=communicationService.getPrivacyNotice();
        if(privacyNotice==null || privacyNotice.getApp()==null || !dataBaseService.getUserApps(authenticatedUser.getUser()).contains(privacyNotice.getApp())){
            event.rerouteTo(ControllerDPOPrivacyNoticeView.class);
        }
    }

    /*
    This view is formed of three different views which are gonna be visible on content element and are navigable from the tabs at the top of the page.
    These views are called customizedLayout, standardLayout and uploadLayout
     */
    public SinglePrivacyNoticeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        initializeCustomizedLayout();
        initializeStandardLayout();
        initializeUploadLayout();
        initializeTabs();
        add(tabs, content);
    }

    private void initializeCustomizedLayout(){
        customizedTab.addClassName("pointer");
        textAreaCustomized.setWidthFull();
        Button saveButton= new Button("Save", e->savePrivacyNotice(textAreaCustomized));
        customizedLayout.add(textAreaCustomized, saveButton);
    }

    private void initializeStandardLayout(){
        standardTab.addClassName("pointer");
        standardLayout=new FormPrivacyNotice(privacyNotice, dataBaseService);
    }

    private void initializeUploadLayout(){
        uploadTab.addClassName("pointer");
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(e-> processFile(buffer, e));

        Button saveButton= new Button("Save", e-> logger.info(buffer.getFileName()));
        uploadLayout.add(upload, saveButton);
    }

    private void initializeTabs(){
        content.setSizeFull();
        tabs.setWidthFull();
        tabs.add(customizedTab, standardTab, uploadTab);
        tabs.addSelectedChangeListener(
            e->{
                if(e.getSelectedTab().equals(e.getPreviousTab())){
                    return;
                }
                content.removeAll();
                if(customizedTab.equals(e.getSelectedTab())){
                    content.add(customizedLayout);
                }
                else if(standardTab.equals(e.getSelectedTab())){
                    content.add(standardLayout);
                }
                else if(uploadTab.equals(e.getSelectedTab())){
                    content.add(uploadLayout);
                }
            });
    }

    private void processFile(MemoryBuffer buffer, SucceededEvent event){
        String fileName=event.getFileName();
        logger.info(event + "\nAAAA\n");
        logger.info(fileName + "\nAAAA\n");
        InputStream inputStream = buffer.getInputStream();
        try{
            logger.info(inputStream.readAllBytes().toString());
        } catch (Exception e){

        }

        //logger.info(event.);
    }

    private void savePrivacyNotice(TextArea textArea){
        if(textArea.getValue()==null){
            return;
        }
        if(dataBaseService.getPrivacyNoticeFromApp(privacyNotice.getApp())==null){
            confirmNewPrivacyNotice(textArea.getValue());
        }
        else{
            confirmOverwritePrivacyNotice(textArea.getValue());
        }
    }

    private void confirmNewPrivacyNotice(String text){
        MyDialog dialog=new MyDialog();
        dialog.setTitle("Confirm");
        dialog.setContent(new HorizontalLayout(new Span("Do you want to upload this Privacy Notice for the app: " + privacyNotice.getApp().getName() + "?")));
        dialog.setContinueButton(new Button("confirm", e-> {
            dataBaseService.addPrivacyNoticeForApp(privacyNotice.getApp(), text);
            Notification notification = Notification.show("Privacy Notice uploaded correctly");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate(ControllerDPOPrivacyNoticeView.class);
            dialog.close();
        }));
        dialog.open();
    }

    private void confirmOverwritePrivacyNotice(String text){
        MyDialog dialog=new MyDialog();
        dialog.setTitle("Confirm");
        dialog.setContent(new HorizontalLayout(new Span("There is already a Privacy Notice for the app " + privacyNotice.getApp().getName() + ", do you want to overwrite it with this one?")));
        dialog.setContinueButton(new Button("confirm", e-> {
            dataBaseService.changePrivacyNoticeForApp(privacyNotice.getApp(), text);
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
            textAreaCustomized.setValue(privacyNotice.getText());
            tabs.setSelectedTab(customizedTab);
            content.add(customizedLayout);
        }
        else{
            tabs.setSelectedTab(standardTab);
            content.add(standardLayout);
        }
    }
}

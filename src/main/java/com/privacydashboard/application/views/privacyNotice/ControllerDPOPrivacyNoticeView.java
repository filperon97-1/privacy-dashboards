package com.privacydashboard.application.views.privacyNotice;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.PrivacyNotice;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("PrivacyNotice")
@Route(value="controller_privacyNotice", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class ControllerDPOPrivacyNoticeView extends VerticalLayout implements AfterNavigationObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final Grid<PrivacyNotice> grid=new Grid<>();
    private final MyDialog newPrivacyNoticeDialog=new MyDialog();
    private final Button newPrivacyNoticeButton= new Button("Compile new Privacy Notice", e-> newPrivacyNoticeDialog.open());
    private final ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");

    Logger logger = LoggerFactory.getLogger(getClass());

    public ControllerDPOPrivacyNoticeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        addClassName("privacy_notice-view");
        initializeGrid();
        initializeNewPrivacyNoticeDialog();
        add(newPrivacyNoticeButton);
        add(grid);
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(privacyNotice -> new Button(privacyNotice.getApp().getName(), e->showPrivacyNotice(privacyNotice)));
    }

    private void initializeNewPrivacyNoticeDialog(){
        appComboBox.setItems(dataBaseService.getUserApps(authenticatedUser.getUser()));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        Button continueButton= new Button("Continue", e-> confirmNewPrivacyNotice());

        newPrivacyNoticeDialog.setTitle("Select app");
        newPrivacyNoticeDialog.setContent(new VerticalLayout(appComboBox));
        newPrivacyNoticeDialog.setContinueButton(continueButton);
    }

    private void confirmNewPrivacyNotice(){
        if(appComboBox.getValue()==null){
            return;
        }
        IoTApp app=appComboBox.getValue();
        if(dataBaseService.getPrivacyNoticeFromApp(app)!=null){
            MyDialog dialog=new MyDialog();
            dialog.setTitle("Confirm");
            dialog.setContent(new HorizontalLayout(new Span("There is already a Privacy Notice for this app, do you want to create a new one?" +
                    " The current one will be lost")));
            dialog.setContinueButton(new Button("confirm", e-> {
                                                                    createNewPrivacyNotice(app);
                                                                    newPrivacyNoticeDialog.close();
                                                                    dialog.close();
            }));
            dialog.open();
        }
        else{
            createNewPrivacyNotice(app);
            newPrivacyNoticeDialog.close();
        }
    }

    private void showPrivacyNotice(PrivacyNotice privacyNotice){
        add(new Span(privacyNotice.getText()));
    }

    private void createNewPrivacyNotice(IoTApp app){
        Icon uploadIcon = new Icon("lumo", "upload");
        logger.info("todo bien");
    }

    private void updateGrid(){
        List<PrivacyNotice> privacyNoticeList=dataBaseService.getAllPrivacyNoticeFromUser(authenticatedUser.getUser());
        grid.setItems(privacyNoticeList);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateGrid();
    }

}

package com.privacydashboard.application.views.privacyNotice;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.PrivacyNotice;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.apps.AppsView;
import com.privacydashboard.application.views.contacts.ContactsView;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("PrivacyNotice")
@Route(value="controller_privacyNotice", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class ControllerDPOPrivacyNoticeView extends VerticalLayout implements AfterNavigationObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;

    private final TextField searchText=new TextField();
    private final Grid<PrivacyNotice> grid=new Grid<>();
    private final MyDialog newPrivacyNoticeDialog=new MyDialog();
    private final Button newPrivacyNoticeButton= new Button("Compile new Privacy Notice", e-> newPrivacyNoticeDialog.open());
    private final ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");

    public ControllerDPOPrivacyNoticeView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService){
        this.dataBaseService= dataBaseService;
        this.authenticatedUser= authenticatedUser;
        this.communicationService= communicationService;

        addClassName("grid-view");
        initializeSearchText();
        initializeGrid();
        initializeNewPrivacyNoticeDialog();

        newPrivacyNoticeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout headerLayout= new HorizontalLayout(searchText, newPrivacyNoticeButton);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(headerLayout, grid);
    }

    private void initializeSearchText(){
        searchText.setPlaceholder("Search...");
        searchText.setValueChangeMode(ValueChangeMode.LAZY);
        searchText.addValueChangeListener(e-> updateGrid());
        searchText.addClassName("search");
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(this::showPrivacyNotice);
    }

    private VerticalLayout showPrivacyNotice(PrivacyNotice privacyNotice){
        Avatar avatar = new Avatar(privacyNotice.getApp().getName());
        Span name= new Span(privacyNotice.getApp().getName());
        name.addClassName("name");
        name.addClassName("link");
        name.addClickListener(e-> goToSinglePrivacyNoticeView(privacyNotice));
        Details details = new Details("More", detailsLayout(privacyNotice));
        VerticalLayout card = new VerticalLayout(new HorizontalLayout(avatar , name) , details);
        card.addClassName("card");
        return card;
    }

    private VerticalLayout detailsLayout(PrivacyNotice privacyNotice){
        Span goToApp=new Span("Go to the app");
        goToApp.addClassName("link");
        goToApp.addClickListener(e-> communicationService.setApp(privacyNotice.getApp()));
        goToApp.addClickListener(e-> UI.getCurrent().navigate(AppsView.class));

        List<User> controllers= dataBaseService.getControllersFromApp(privacyNotice.getApp());
        VerticalLayout controllersLayout= new VerticalLayout();
        for(User u : controllers){
            Span contactLink=new Span(u.getName());
            contactLink.addClassName("link");
            contactLink.addClickListener(e->communicationService.setContact(u));
            contactLink.addClickListener(e-> UI.getCurrent().navigate(ContactsView.class));
            controllersLayout.add(contactLink);
        }
        Details controllersDetails= new Details("Data Controllers:", controllersLayout);

        List<User> dpos= dataBaseService.getDPOsFromApp(privacyNotice.getApp());
        VerticalLayout dposLayout= new VerticalLayout();
        for(User u : dpos){
            Span contactLink=new Span(u.getName());
            contactLink.addClassName("link");
            contactLink.addClickListener(e->communicationService.setContact(u));
            contactLink.addClickListener(e-> UI.getCurrent().navigate(ContactsView.class));
            dposLayout.add(contactLink);
        }
        Details dposDetails= new Details("Data Protection Officers:", dposLayout);

        List<User> subjects= dataBaseService.getSubjectsFromApp(privacyNotice.getApp());
        VerticalLayout subjectsLayout= new VerticalLayout();
        for(User u : subjects){
            Span contactLink=new Span(u.getName());
            contactLink.addClassName("link");
            contactLink.addClickListener(e->communicationService.setContact(u));
            contactLink.addClickListener(e-> UI.getCurrent().navigate(ContactsView.class));
            subjectsLayout.add(contactLink);
        }
        Details subjectsDetails= new Details("Data Subjects:", subjectsLayout);

        return new VerticalLayout(goToApp, controllersDetails, dposDetails, subjectsDetails);
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
            Button confirmButton=new Button("Confirm",
                    e->{
                        createNewPrivacyNotice(app);
                        newPrivacyNoticeDialog.close();
                        dialog.close();
                    });

            dialog.setTitle("Confirm");
            dialog.setContent(new HorizontalLayout(new Span("There is already a Privacy Notice for this app, do you want to create a new one?" +
                    " The current one will be lost")));
            dialog.setContinueButton(confirmButton);
            dialog.open();
        }
        else{
            createNewPrivacyNotice(app);
            newPrivacyNoticeDialog.close();
        }
    }

    private void createNewPrivacyNotice(IoTApp app){
        PrivacyNotice privacyNotice= new PrivacyNotice();
        privacyNotice.setText(null);
        privacyNotice.setApp(app);
        goToSinglePrivacyNoticeView(privacyNotice);
    }

    private void goToSinglePrivacyNoticeView(PrivacyNotice privacyNotice){
        communicationService.setPrivacyNotice(privacyNotice);
        UI.getCurrent().navigate(SinglePrivacyNoticeView.class);
    }
    
    private void updateGrid(){
        List<PrivacyNotice> privacyNoticeList;
        if(searchText.getValue()==null || searchText.getValue().length()==0){
            privacyNoticeList=dataBaseService.getAllPrivacyNoticeFromUser(authenticatedUser.getUser());
        }
        else{
            privacyNoticeList=dataBaseService.getUserPrivacyNoticeByAppName(authenticatedUser.getUser(), searchText.getValue());
        }
        grid.setItems(privacyNoticeList);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateGrid();
    }

}

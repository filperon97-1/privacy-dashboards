package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialogRight{
    private final User user;
    private final DataBaseService dataBaseService;
    private RightRequest request;

    private final Dialog requestDialog= new Dialog();
    private final H1 title=new H1();
    private final HorizontalLayout content=new HorizontalLayout();
    private final Button cancelButton=new Button("Cancel", e-> requestDialog.close());
    private final Button continueButton=new Button("Continue");

    Logger logger = LoggerFactory.getLogger(getClass());

    public DialogRight(RightRequest request, User user, DataBaseService dataBaseService){
        this.request=request;
        this.user=user;
        this.dataBaseService=dataBaseService;
    }

    public DialogRight(RightType rightType, User user, DataBaseService dataBaseService) {
        this.user = user;
        this.dataBaseService = dataBaseService;
        initializeRequest(rightType);
    }

    public void showRequestDialog(){
        createLayout();
        if(request.getRightType().equals(RightType.WITHDRAWCONSENT)){
            withdrawConsent();
        }
        if(request.getRightType().equals(RightType.ERASURE)){
            erasure();
        }
        requestDialog.open();
    }

    private void initializeRequest(RightType rightType){
        request= new RightRequest();
        request.setSender(user);
        request.setRightType(rightType);
        request.setHandled(false);
    }

    private void createLayout(){
        HorizontalLayout buttonLayout= new HorizontalLayout(continueButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        VerticalLayout layout=new VerticalLayout(title, content, buttonLayout);
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        requestDialog.add(layout);
    }

    private void withdrawConsent(){
        title.setText("Select App");

        ComboBox<String> consensComboBox= new ComboBox<>("Consens");
        consensComboBox.setPlaceholder("Filter by name...");

        ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");
        appComboBox.setItems(dataBaseService.getUserApps(user));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        appComboBox.setPlaceholder("Filter by name...");
        appComboBox.addValueChangeListener(e-> consensComboBox.setItems(dataBaseService.getConsensesFromUserAndApp(user,appComboBox.getValue())));

        continueButton.addClickListener( e->{
            if(appComboBox.getValue()!=null && consensComboBox.getValue()!=null){
                request.setApp(appComboBox.getValue());
                request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                request.setOther(consensComboBox.getValue());
                requestDialog.close();
                showConfirmRequest();
            }});
        content.add(appComboBox, consensComboBox);
    }

    private void erasure(){
        title.setText("Select App");

        ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");
        appComboBox.setItems(dataBaseService.getUserApps(user));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        appComboBox.setPlaceholder("Filter by name...");

        TextArea textArea=new TextArea("What to erase");
        textArea.setPlaceholder("Descrive what you want to erase");

        continueButton.addClickListener(e->{
            if(appComboBox.getValue()!=null && textArea.getValue()!=null){
                request.setApp(appComboBox.getValue());
                request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                request.setOther(textArea.getValue());
                requestDialog.close();
                showConfirmRequest();
            }
        });
        content.add(appComboBox, textArea);
    }

    public void showConfirmRequest(){
        if(request==null){
            return;
        }
        if(request.getRightType()==null || request.getReceiver()==null || request.getSender()==null || request.getApp()==null){
            return;
        }
        Dialog confirmDialog=new Dialog();
        confirmDialog.setWidth("50%");

        HorizontalLayout appName=new HorizontalLayout(new H1("APP:  "), new H2(request.getApp().getName()));
        appName.setAlignItems(FlexComponent.Alignment.CENTER);
        appName.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        HorizontalLayout right=new HorizontalLayout(new H1("RIGHT:  "), new H2(request.getRightType().toString()));
        right.setAlignItems(FlexComponent.Alignment.CENTER);
        right.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        TextArea premadeMessage=new TextArea();
        premadeMessage.setValue(getPremadeText());
        premadeMessage.setWidthFull();
        premadeMessage.setReadOnly(true);
        TextArea details=new TextArea();
        details.setPlaceholder("Add additional information");
        details.setWidthFull();

        Button confirm=new Button("Confirm", e->{request.setDetails(details.getValue());
            dataBaseService.addNowRequest(request);
            confirmDialog.close();});
        Button cancel=new Button("Cancel", e-> confirmDialog.close());

        confirmDialog.add(new VerticalLayout(appName, right, premadeMessage, details, new HorizontalLayout(confirm, cancel)));
        confirmDialog.open();
    }

    private String getPremadeText(){
        if(request.getRightType().equals(RightType.WITHDRAWCONSENT)){
            return("Dear " + request.getReceiver().getName() + ", \n" +
                    "I would like to withdraw the consent: " +request.getOther() + " from the app " + request.getApp().getName() + ",\n" +
                    "Best regards, \n" +
                    request.getSender().getName());
        }
        if(request.getRightType().equals(RightType.ERASURE)){
            return("Dear " + request.getReceiver().getName() + ", \n" +
                    "I would like to erase the following information: " +request.getOther() + " ,from the app " + request.getApp().getName() + ",\n" +
                    "Best regards, \n" +
                    request.getSender().getName());
        }
        return null;
    }
}

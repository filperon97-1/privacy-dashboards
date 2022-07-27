package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.LinkedList;
import java.util.Optional;

public class DialogRight{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;

    private final Dialog requestDialog= new Dialog();
    private final Header title=new Header();
    private final HorizontalLayout content=new HorizontalLayout();
    private final Button cancelButton=new Button("Cancel", e-> requestDialog.close());
    private final Button continueButton=new Button("Continue");

    public DialogRight(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser){
        this.dataBaseService=dataBaseService;
        this.authenticatedUser=authenticatedUser;
    }

    public void showDialogRequest(RightType rightType){
        User user=getUser();
        if(user==null){
            return;
        }
        RightRequest request=initializeRequest(user, rightType);
        initializeDialogLayout();
        if(request.getRightType().equals(RightType.WITHDRAWCONSENT)){
            withdrawConsent(user, request);
        }
        if(request.getRightType().equals(RightType.ERASURE)){
            erasure(user, request);
        }
        if(request.getRightType().equals(RightType.INFO)){
            info(user, request);
        }
        if(request.getRightType().equals(RightType.COMPLAIN)){
            complain(user, request);
        }
        requestDialog.open();
    }

    private void initializeDialogLayout(){
        HorizontalLayout buttonLayout= new HorizontalLayout(continueButton, cancelButton);
        VerticalLayout layout=new VerticalLayout(title, content, buttonLayout);
        requestDialog.add(layout);
        //requestDialog.setWidth("70%");
    }

    private RightRequest initializeRequest(User user, RightType rightType){
        RightRequest request= new RightRequest();
        request.setSender(user);
        request.setRightType(rightType);
        request.setHandled(false);
        return request;
    }

    private void withdrawConsent(User user, RightRequest request){
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
                showDialogConfirm(request);
            }});
        content.add(appComboBox, consensComboBox);
    }

    private void erasure(User user, RightRequest request){
        title.setText("Select App");

        ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");
        appComboBox.setItems(dataBaseService.getUserApps(user));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        appComboBox.setPlaceholder("Filter by name...");

        TextArea textArea=new TextArea("What to erase");
        textArea.setPlaceholder("Descrive what you want to erase");
        textArea.setWidthFull();

        continueButton.addClickListener(e->{
            if(appComboBox.getValue()!=null && textArea.getValue()!=null){
                request.setApp(appComboBox.getValue());
                request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                request.setOther(textArea.getValue());
                requestDialog.close();
                showDialogConfirm(request);
            }
        });
        content.add(appComboBox, textArea);
    }

    private void info(User user, RightRequest request){
        title.setText("Select App");

        ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");
        appComboBox.setItems(dataBaseService.getUserApps(user));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        appComboBox.setPlaceholder("Filter by name...");

        ComboBox<String> infoComboBox= new ComboBox<>("info");
        LinkedList<String> infos= new LinkedList<>();
        infos.add("Periodo di mantenimento dei dati");
        infos.add("Contatto del Data Protection Officer");
        infos.add("Conoscere lo scopo del processamento dei dati e le sue basi legali");
        infos.add("Conoscere gli interessi legittimi del Controller o delle terze parti");
        infos.add("Conoscere i destinatari dei dati");
        infos.add("Sapere se i dati vanno a paesi terzi");
        infos.add("Altro");
        infoComboBox.setItems(infos);

        continueButton.addClickListener(e->{
            if(appComboBox.getValue()!=null && infoComboBox.getValue()!=null){
                request.setApp(appComboBox.getValue());
                request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                request.setOther(infoComboBox.getValue());
                requestDialog.close();
                showDialogConfirm(request);
            }
        });
        content.add(appComboBox, infoComboBox);
    }

    private void complain(User user, RightRequest request){
        title.setText("Select App");

        ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");
        appComboBox.setItems(dataBaseService.getUserApps(user));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        appComboBox.setPlaceholder("Filter by name...");

        TextArea textArea=new TextArea("Complain");
        textArea.setPlaceholder("Write your complain");
        textArea.setWidthFull();

        continueButton.addClickListener(e->{
            if(appComboBox.getValue()!=null && textArea.getValue()!=null){
                request.setApp(appComboBox.getValue());
                request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                request.setOther(textArea.getValue());
                requestDialog.close();
                showDialogConfirm(request);
            }
        });
        content.add(new VerticalLayout(appComboBox, textArea));
    }

    public void showDialogConfirm(RightRequest request){
        if(request==null){
            return;
        }
        if(request.getRightType()==null || request.getReceiver()==null || request.getSender()==null || request.getApp()==null){
            return;
        }
        // Sender of the request and authenticated user must be the same person
        if(!request.getSender().equals(getUser())){
            return;
        }
        Dialog confirmDialog=new Dialog();
        confirmDialog.setWidth("50%");

        HorizontalLayout appName=new HorizontalLayout(new H1("APP:  "), new H2(request.getApp().getName()));
        appName.setAlignItems(FlexComponent.Alignment.CENTER);
        HorizontalLayout right=new HorizontalLayout(new H1("RIGHT:  "), new H2(request.getRightType().toString()));
        right.setAlignItems(FlexComponent.Alignment.CENTER);

        TextArea premadeMessage=new TextArea();
        premadeMessage.setValue(getPremadeText(request));
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

    private String getPremadeText(RightRequest request){
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
        if(request.getRightType().equals(RightType.INFO)){
            return("Dear " + request.getReceiver().getName() + ", \n" +
                    "I would like to know the following information: " +request.getOther() + " regarding the app " + request.getApp().getName() + ",\n" +
                    "Best regards, \n" +
                    request.getSender().getName());
        }
        if(request.getRightType().equals(RightType.COMPLAIN)){
            return("Dear " + request.getReceiver().getName() + ", \n" +
                    "I would like to know the complain with the supervision authority: " +request.getOther() + " about the app " + request.getApp().getName() + ",\n" +
                    "Best regards, \n" +
                    request.getSender().getName());
        }
        return null;
    }

    private User getUser(){
        Optional<User> maybeUser=this.authenticatedUser.get();
        if(maybeUser.isEmpty()){
            return null;
        }
        return maybeUser.get();
    }
}
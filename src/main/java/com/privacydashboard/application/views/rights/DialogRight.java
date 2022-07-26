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
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.Optional;

public class DialogRight {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;

    public DialogRight(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
    }

    public void showDialogRequest(RightType rightType) {
        User user = getUser();
        if (user == null || dataBaseService == null) {
            return;
        }
        DialogRequest dialogRequest = new DialogRequest(rightType, user, dataBaseService);
        dialogRequest.open();
    }

    public void showDialogConfirm(RightRequest request) {
        User user = getUser();
        if (user == null || dataBaseService == null) {
            return;
        }
        // Sender user must be the authenticated one
        if (!request.getSender().equals(user)) {
            return;
        }
        DialogConfirm dialogConfirm = new DialogConfirm(request, dataBaseService);
        dialogConfirm.open();
    }

    private User getUser() {
        Optional<User> maybeUser = this.authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            return null;
        }
        return maybeUser.get();
    }


    private class DialogRequest extends Dialog {
        private RightRequest request;
        private final User user;
        private final DataBaseService dataBaseService;
        private final H1 title = new H1();
        private final HorizontalLayout content = new HorizontalLayout();
        private final Button cancelButton = new Button("Cancel", e -> this.close());
        private final Button continueButton = new Button("Continue");

        private DialogRequest(RightType rightType, User user, DataBaseService dataBaseService) {
            super();
            this.user = user;
            this.dataBaseService = dataBaseService;
            initializeRequest(rightType);
            createLayout();
            if (request.getRightType().equals(RightType.WITHDRAWCONSENT)) {
                withdrawConsent();
            }
            if (request.getRightType().equals(RightType.ERASURE)) {
                erasure();
            }
        }

        private void initializeRequest(RightType rightType) {
            request = new RightRequest();
            request.setSender(user);
            request.setRightType(rightType);
            request.setHandled(false);
        }

        private void createLayout() {
            HorizontalLayout buttonLayout = new HorizontalLayout(continueButton, cancelButton);
            VerticalLayout layout = new VerticalLayout(title, content, buttonLayout);
            add(layout);
        }

        private void withdrawConsent() {
            title.setText("Select App");

            ComboBox<String> consensComboBox = new ComboBox<>("Consens");
            consensComboBox.setPlaceholder("Filter by name...");

            ComboBox<IoTApp> appComboBox = new ComboBox<>("Apps");
            appComboBox.setItems(dataBaseService.getUserApps(user));
            appComboBox.setItemLabelGenerator(IoTApp::getName);
            appComboBox.setPlaceholder("Filter by name...");
            appComboBox.addValueChangeListener(e -> consensComboBox.setItems(dataBaseService.getConsensesFromUserAndApp(user, appComboBox.getValue())));

            continueButton.addClickListener(e -> {
                if (appComboBox.getValue() != null && consensComboBox.getValue() != null) {
                    request.setApp(appComboBox.getValue());
                    request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                    request.setOther(consensComboBox.getValue());
                    this.close();
                    DialogConfirm dialogConfirm = new DialogConfirm(request, dataBaseService);
                    dialogConfirm.open();
                }
            });
            content.add(appComboBox, consensComboBox);
        }

        private void erasure() {
            title.setText("Select App");

            ComboBox<IoTApp> appComboBox = new ComboBox<>("Apps");
            appComboBox.setItems(dataBaseService.getUserApps(user));
            appComboBox.setItemLabelGenerator(IoTApp::getName);
            appComboBox.setPlaceholder("Filter by name...");

            TextArea textArea = new TextArea("What to erase");
            textArea.setPlaceholder("Descrive what you want to erase");

            continueButton.addClickListener(e -> {
                if (appComboBox.getValue() != null && textArea.getValue() != null) {
                    request.setApp(appComboBox.getValue());
                    request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                    request.setOther(textArea.getValue());
                    this.close();
                    DialogConfirm dialogConfirm = new DialogConfirm(request, dataBaseService);
                    dialogConfirm.open();
                }
            });
            content.add(appComboBox, textArea);
        }
    }

    private class DialogConfirm extends Dialog {
        private final DataBaseService dataBaseService;

        private DialogConfirm(RightRequest request, DataBaseService dataBaseService) {
            super();
            this.dataBaseService = dataBaseService;
            if (request == null) {
                return;
            }
            if (request.getRightType() == null || request.getReceiver() == null || request.getSender() == null || request.getApp() == null) {
                return;
            }

            HorizontalLayout appName = new HorizontalLayout(new H1("APP:  "), new H2(request.getApp().getName()));
            appName.setAlignItems(FlexComponent.Alignment.CENTER);
            appName.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            HorizontalLayout right = new HorizontalLayout(new H1("RIGHT:  "), new H2(request.getRightType().toString()));
            right.setAlignItems(FlexComponent.Alignment.CENTER);
            right.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

            TextArea premadeMessage = new TextArea();
            premadeMessage.setValue(getPremadeText(request));
            premadeMessage.setWidthFull();
            premadeMessage.setReadOnly(true);
            TextArea details = new TextArea();
            details.setPlaceholder("Add additional information");
            details.setWidthFull();

            Button confirm = new Button("Confirm", e -> {
                request.setDetails(details.getValue());
                dataBaseService.addNowRequest(request);
                this.close();
            });
            Button cancel = new Button("Cancel", e -> this.close());

            add(new VerticalLayout(appName, right, premadeMessage, details, new HorizontalLayout(confirm, cancel)));
        }

        private String getPremadeText(RightRequest request) {
            if (request.getRightType().equals(RightType.WITHDRAWCONSENT)) {
                return ("Dear " + request.getReceiver().getName() + ", \n" +
                        "I would like to withdraw the consent: " + request.getOther() + " from the app " + request.getApp().getName() + ",\n" +
                        "Best regards, \n" +
                        request.getSender().getName());
            }
            if (request.getRightType().equals(RightType.ERASURE)) {
                return ("Dear " + request.getReceiver().getName() + ", \n" +
                        "I would like to erase the following information: " + request.getOther() + " ,from the app " + request.getApp().getName() + ",\n" +
                        "Best regards, \n" +
                        request.getSender().getName());
            }
            return null;
        }
    }
}
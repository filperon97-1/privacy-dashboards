package com.privacydashboard.application.views.mainLayout;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.security.UserDetailsServiceImpl;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;

public class ProfileView extends VerticalLayout {
    private final User user;
    private final AuthenticatedUser authenticatedUser;
    private final DataBaseService dataBaseService;
    private final UserDetailsServiceImpl userDetailsService;

    private Span image;
    private Span name;
    private Span role;
    private Span mail;
    private Span newMail;
    private Details changePasswordDetails;
    private PasswordField actualPassword;
    private PasswordField newPassword;
    private PasswordField confirmPassword;

    public ProfileView(User user, AuthenticatedUser authenticatedUser, DataBaseService dataBaseService, UserDetailsServiceImpl userDetailsService){
        this.user=user;
        this.authenticatedUser=authenticatedUser;
        this.dataBaseService=dataBaseService;
        this.userDetailsService=userDetailsService;
        if(!user.equals(authenticatedUser.getUser())){
            return;
        }
        initializeInformation();
        initializeRemoveEverything();
        initializeLogOut();
        setAlignItems(Alignment.CENTER);
    }

    private void initializeInformation(){
        Avatar avatar=new Avatar(user.getName(), user.getProfilePictureUrl());
        image=new Span(avatar);
        image.addClickListener(e-> changeImage());

        name=new Span("Name: " + user.getName());
        role=new Span("Role: " + user.getRole());
        Icon icon=new Icon(VaadinIcon.PENCIL);
        icon.setSize("12px");
        icon.addClickListener(e-> changeMail());
        mail=new Span("Mail: " + (user.getMail()==null ? "   " : user.getMail()));
        mail.add(icon);
        newMail= new Span("New mail: ");
        newMail.add(new TextArea());
        newMail.setVisible(false);
        changePasswordDetails= new Details("Change password", changePassword());
        add(image, name, role, mail, newMail, changePasswordDetails);
    }

    private void changeImage(){

    }

    private void changeMail(){
        newMail.setVisible(true);
    }

    private VerticalLayout changePassword(){
        actualPassword= new PasswordField("Actual");
        actualPassword.setErrorMessage("password must be the same as old one");
        actualPassword.setMinLength(8);
        actualPassword.addValueChangeListener(e-> checkActualPassword());

        newPassword= new PasswordField("New");
        newPassword.setMinLength(8);
        newPassword.setErrorMessage("the password must be at least 8 characters");

        confirmPassword= new PasswordField("Confirm");
        confirmPassword.setErrorMessage("the two passwords must be equal");
        confirmPassword.addValueChangeListener(e-> checkConfirmPassword());

        Button save= new Button("Save", e->savePassword());
        VerticalLayout layout=new VerticalLayout(actualPassword, newPassword, confirmPassword, save);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    private void checkActualPassword() {
        if (userDetailsService.isSamePassword(actualPassword.getValue(), user.getHashedPassword())) {
            actualPassword.setInvalid(false);
        } else {
            actualPassword.setInvalid(true);
        }
    }

    private void checkConfirmPassword(){
        if(confirmPassword.getValue().equals(newPassword.getValue())) {
            confirmPassword.setInvalid(false);
        }
        else {
            confirmPassword.setInvalid(true);
        }
    }

    private void savePassword(){
        checkActualPassword();
        checkConfirmPassword();
        if(newPassword.getValue().length()<8){
            newPassword.setInvalid(true);
        }
        if(actualPassword.isInvalid() || newPassword.isInvalid() || confirmPassword.isInvalid()) {
            return;
        }
        userDetailsService.changeUserPassword(user, newPassword.getValue());
        actualPassword.setValue("");
        newPassword.setValue("");
        confirmPassword.setValue("");
        changePasswordDetails.setOpened(false);
        Notification notification = Notification.show("Password correctly changed!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void initializeRemoveEverything(){
        if(user.getRole().equals(Role.SUBJECT)){
            Button removeEverythingButton=new Button("Remove everything", e-> confirmDialog());
            add(removeEverythingButton);
        }
    }

    private void confirmDialog(){
        Dialog dialog=new Dialog();
        Span text=new Span("Are you sure you want to remove all your personal information from every app you have?");
        Button confirm=new Button("Confirm", e-> {dataBaseService.removeEverythingFromUser(user);
                                                        Notification notification = Notification.show("The request has been sent to the Data Controllers!");
                                                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                                        dialog.close();});
        Button cancel=new Button("Cancel", e-> dialog.close());

        VerticalLayout layout= new VerticalLayout(text, new HorizontalLayout(confirm, cancel));
        layout.setAlignItems(Alignment.CENTER);
        dialog.add(layout);
        dialog.setOpened(true);
    }


    private void initializeLogOut(){
        Button logOutButton= new Button("LogOut", e-> authenticatedUser.logout());
        add(logOutButton);
    }

}

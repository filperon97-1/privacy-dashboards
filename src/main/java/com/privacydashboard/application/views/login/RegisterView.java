package com.privacydashboard.application.views.login;

import com.privacydashboard.application.data.DataRole;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.UserService;
import com.privacydashboard.application.security.SecurityConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedList;
import java.util.List;


/*
 TO DO:
 - Check della password fatto anche sul primo PasswordField e non solo sul secondo
 - Quando il browser chiede di salvare le credenziali c'è la HashPassword
 */
@Route("registration")
@PageTitle("Registration")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    private TextField username=new TextField("username");
    private PasswordField password=new PasswordField("password");
    private PasswordField confirmPassword=new PasswordField("confirm password");
    private ComboBox<DataRole> dataRole= new ComboBox<>("Role");
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private Binder<User> binder= new Binder<>(User.class);

    public RegisterView(UserService userService, PasswordEncoder passwordEncoder){
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;
        addClassName("registration");
        setAlignItems(Alignment.CENTER);
        implementBinder();
        Button registerButton= new Button("Register", e-> confirm());
        add(new H1("Registration"), username, password, confirmPassword, dataRole, registerButton);
    }

    private void implementBinder(){
        //implements dataRole ComboBox
        List<DataRole> dataRoleList=new LinkedList<>();
        dataRoleList.add(DataRole.CONTROLLER);
        dataRoleList.add(DataRole.SUBJECT);
        dataRole.setItems(dataRoleList);

        binder.forField(username).withValidator(name -> name.length()>5, "name lenght must be at least 5")
                .withValidator(name-> name.endsWith("hola"), "name must end with 'hola'")
                .bind(User::getUsername, User::setUsername);
        binder.forField(confirmPassword).withValidator(pass-> pass.length()>8, "pass lenght must be at least 8")
                .withValidator(pass -> pass.equals(password.getValue()), "it must be equal to the password")
                .withConverter(this::encodePassword, this::encodePassword)  //encode password
                .bind(User::getHashedPassword, User::setHashedPassword);
        binder.bind(dataRole, User::getDataRole, User::setDataRole);
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    private void confirm(){
        User user=new User();
        try{
            binder.writeBean(user);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        userService.addUser(user);
        UI.getCurrent().getPage().setLocation("/"); // reinderizza alla pagina login
    }
}

package com.privacydashboard.application.views.login;

import com.privacydashboard.application.data.DataRole;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;


/*
 TO DO:
 - CONTROLLO DELLA REGISTRAZIONE(FARLO QUI O IN UN METODO PIU INTERNO??)
 - MESSAGGI D'ERRORE SIMIL LOGIN
 - COMBOBOX HA ERRORI
 - AZIONI DOPO LA FINE DELLA REGISTRAZIONE
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

    public RegisterView(UserService userService, PasswordEncoder passwordEncoder){
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;

        addClassName("registration");
        setAlignItems(Alignment.CENTER);
        Button registerButton= new Button("Register", e-> confirm(username.getValue(),
                                                                        password.getValue(),
                                                                        confirmPassword.getValue(),
                                                                        dataRole.getValue()));
        add(new H1("Registration"), username, password, confirmPassword, dataRole, registerButton);
    }

    // DA COMPLETARE LA VERIFICA
    private void confirm(String username, String password, String confirmPassword, DataRole dataRole){
        if (username.isEmpty()){
            Notification.show("enter a valid username");
            return;
        }
        if(password.isEmpty() || confirmPassword.isEmpty()){

        }
        User user=new User();
        user.setUsername(username);
        user.setHashedPassword(passwordEncoder.encode(password));
        user.setDataRole(dataRole);
        userService.addUser(user);
        // reinderizza alla pagina Home facendo il login o alla pagina login
    }
}

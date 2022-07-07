package com.privacydashboard.application.views.login;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.LinkedList;
import java.util.List;

@Route("registration")
@PageTitle("Registration")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {
    private TextField username=new TextField("USERNAME");
    private PasswordField password=new PasswordField("PASSWORD");
    private PasswordField confirmPassword=new PasswordField("CONFIRM PASSWORD");
    private ComboBox<Role> role= new ComboBox<>("ROLE");
    private final DataBaseService dataBaseService;
    private Binder<User> binder= new Binder<>(User.class);

    public RegisterView(DataBaseService dataBaseService){
        this.dataBaseService=dataBaseService;
        addClassName("registration");
        setAlignItems(Alignment.CENTER);
        implementBinder();
        Button registerButton= new Button("Register", e-> confirm());
        add(new H1("Registration"), username, password, confirmPassword, role, registerButton);
    }

    private void implementBinder(){
        // implements dataRole ComboBox
        List<Role> roleList=new LinkedList<>();
        roleList.add(Role.CONTROLLER);
        roleList.add(Role.SUBJECT);
        roleList.add(Role.DPO);
        role.setItems(roleList);

        // bind User and form
        password.setMinLength(8);
        password.setErrorMessage("the password must be at least 8 characters");
        binder.forField(username).withValidator(name -> name.length()>=5, "name must be at least 5 characters")
                .withValidator(name-> isUniqueName(name), "username already in use, please use another one")
                .bind(User::getUsername, User::setUsername);
        binder.forField(confirmPassword).withValidator(pass-> pass.length()>=8, "the password must be at least 8 characters")
                .withValidator(pass -> pass.equals(password.getValue()), "the two passwords must be equals")
                .bind(User::getHashedPassword, User::setHashedPassword);
        binder.forField(role).withValidator(value -> value!=null, "please select a role").bind(User::getRole, User::setRole);
        //binder.bind(role, User::getRole, User::setRole);
    }

    private boolean isUniqueName(String name){
        if(dataBaseService.getUserByName(name)==null){
            return true;
        }
        else{
            return false;
        }
    }

    private void confirm(){
        User user=new User();
        try{
            binder.writeBean(user);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        dataBaseService.hashPassAndAddUser(user);   // create user and hash the password (better do the hash in a different layer(not the one visible to the user)??)
        UI.getCurrent().getPage().setLocation("/"); // reinderizza alla pagina login
    }
}

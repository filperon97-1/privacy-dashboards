package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.Optional;

@PageTitle("Messages")
@Route(value="messages", layout = MainLayout.class)
@PermitAll
public class MessagesView extends VerticalLayout implements AfterNavigationObserver{
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private User user;
    private List<User> contacts;
    private Grid<User> grid=new Grid<>();
    private Dialog newMessageDialog=new Dialog();
    private Button newMessageButton=new Button("New Message");

    public MessagesView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        addClassName("grid-views");
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            add(new H2("user not logged in"));
            return;
        }
        user= maybeUser.get();
        contacts=dataBaseService.getAllContactsFromUser(user);
        initializeNewDialog();

        newMessageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newMessageButton.addClickListener(e-> newMessageDialog.open());
        add(newMessageButton);

        grid.setHeight("100%");
        grid.setWidth("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(contact -> showContact(contact));
        add(grid);
    }

    private void initializeNewDialog(){
        H1 titleText= new H1("Select Contact");

        ComboBox<User> contactComboBox= new ComboBox<>("Contacts");
        contactComboBox.setItems(contacts);
        contactComboBox.setItemLabelGenerator(User::getName);

        Button newMessage=new Button("Continue", e->{if(contactComboBox.getValue()!=null){
                                                            newMessageDialog.close();
                                                            UI.getCurrent().navigate("conversation/"+ contactComboBox.getValue().getId().toString());
                                                            }
        });
        Button cancel=new Button("Cancel", e-> newMessageDialog.close());
        HorizontalLayout buttonLayout= new HorizontalLayout(newMessage, cancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout=new VerticalLayout(titleText, contactComboBox, buttonLayout);
        layout.setHorizontalComponentAlignment(Alignment.CENTER);
        newMessageDialog.add(layout);
    }

    private RouterLink showContact(User contact){
        RouterLink link=new RouterLink();
        link.setRoute(SingleConversationView.class, new RouteParameters("contactID", contact.getId().toString()));
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");
        Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
        avatar.addClassNames("me-xs");
        card.add(avatar, new Span(contact.getName()));
        link.add(card);
        return link;
    }

    private void updateGrid(){
        List <User> contacts=dataBaseService.getUserConversationFromUser(user);
        grid.setItems(contacts);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        updateGrid();
    }

}

package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.time.ZoneOffset;
import java.util.LinkedList;
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

        /*TextField searchConversationText=new TextField();
        searchConversationText.setPlaceholder("search conversation");
        add(searchConversationText);*/

        newMessageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newMessageButton.setDisableOnClick(true);   //impedisce di fare la stessa cosa se si preme più volte consecutive ravvicinate
        newMessageButton.addClickListener(e-> newMessageDialog.open());
        add(newMessageButton);

        grid.setHeight("100%");
        grid.setWidth("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(contact -> showContact(contact));
        add(grid);


        /*DIVIDERE IN PARTI:
        - TUTTI I MESSAGGI
        - RICHIESTE NON ANCORA PRESE IN CARICO (es: Subject ha fatto richiesta di rimozione, non è ancora stata processata)
        - RICHIESTE ARCHIVIATE(es: Subject ha fatto richiesta di rimozione e il Controller ha rimosso)
         */
    }

    private void initializeNewDialog(){
        H1 titleText= new H1("Select Contact");

        ComboBox<User> contactComboBox= new ComboBox<>("Contacts");
        contactComboBox.setItems(contacts);
        contactComboBox.setItemLabelGenerator(User::getName);

        TextField filterText= new TextField();
        filterText.setPlaceholder("Send to...");
        filterText.setValueChangeMode(ValueChangeMode.LAZY);    //considera testo cambiato solo quando smette di scrivere
        filterText.addValueChangeListener(e-> contactComboBox.setItems(filterContactsByName(filterText.getValue())));

        TextArea messageText=new TextArea();
        messageText.setPlaceholder("Text...");
        messageText.setWidthFull();


        HorizontalLayout filterLayout=new HorizontalLayout(filterText, contactComboBox);
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        filterLayout.setAlignItems(Alignment.CENTER);

        Button newMessage=new Button("Send Message", e->{
                                                            if(contactComboBox.getValue()!=null && messageText.getValue()!=null){
                                                                sendMessage(contactComboBox.getValue(), messageText.getValue());
                                                                newMessageButton.setEnabled(true);
                                                                newMessageDialog.close();
                                                                updateGrid();
                                                            }
        });
        Button cancel=new Button("Cancel", e-> {
                                                newMessageButton.setEnabled(true);
                                                newMessageDialog.close();});
        HorizontalLayout buttonLayout= new HorizontalLayout(newMessage, cancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout=new VerticalLayout(titleText, filterLayout, messageText, buttonLayout);
        layout.setHorizontalComponentAlignment(Alignment.CENTER);
        newMessageDialog.add(layout);
    }

    private List<User> filterContactsByName(String text){
        List<User> users=new LinkedList<>();
        for(User u : contacts){
            if(u.getName().toLowerCase().contains(text.toLowerCase()) || u.getUsername().toLowerCase().contains(text.toLowerCase())){
                users.add(u);
            }
        }
        return users;
    }

    private void sendMessage(User receiver, String text){
        Message message=new Message();
        message.setMessage(text);
        message.setReceiverId(receiver.getId());
        message.setSenderId(user.getId());
        dataBaseService.addNowMessage(message);
    }

    private HorizontalLayout showContact(User contact){
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");
        Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
        avatar.addClassNames("me-xs");

        card.add(avatar, new RouterLink(contact.getName(),
                SingleConversationView.class, new RouteParameters("contactID", contact.getId().toString())));
        return card;
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

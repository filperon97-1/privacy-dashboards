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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

import javax.annotation.security.PermitAll;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@PageTitle("Messages")
@Route(value="messages", layout = MainLayout.class)
@PermitAll
public class MessagesView extends VerticalLayout {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private User user;
    private Grid<User> grid=new Grid<>();
    private List<List<Message>> conversations;
    private List<User> contacts;
    private Dialog newMessageDialog=new Dialog();
    private Button newMessageButton=new Button("New Message");

    public MessagesView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        addClassName("notifications-view");
        Optional<User> maybeUser = authenticatedUser.get();
        if (!maybeUser.isPresent()) {
            add(new H2("user not logged in"));
            return;
        }
        user= maybeUser.get();
        contacts=dataBaseService.getAllContactsFromUser(user);
        initializeNewDialog();

        TextField searchConversationText=new TextField();
        searchConversationText.setPlaceholder("search conversation");
        add(searchConversationText);

        newMessageButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newMessageButton.setDisableOnClick(true);   //impedisce di fare la stessa cosa se si preme più volte consecutive ravvicinate
        newMessageButton.addClickListener(e-> newMessageDialog.open());
        add(newMessageButton);

        displayConversations();


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

    private void displayConversations(){
        conversations= getConversations();
        for(List<Message> conversation : conversations){
            /*
            Message m= conversation.get(0);
            User contact;
            if (m.getReceiverId().equals(user.getId())){
                contact=dataBaseService.getUser(m.getSenderId()).get();
            }
            else{
                contact=dataBaseService.getUser(m.getReceiverId()).get();
            }
            List<MessageListItem> messageListItems=new LinkedList<>();
            for(Message message : conversation){
                User u=message.getSenderId().equals(user.getId()) ? user : contact;
                MessageListItem messageItem=new MessageListItem(message.getMessage(),message.getTime().toInstant(ZoneOffset.UTC), u.getName());
                messageListItems.add(messageItem);
            }
            MessageList messageList=new MessageList();
            messageList.setItems(messageListItems);
            add(new H1(contact.getName()));
            add(messageList);*/

            //PROVA
            Message m= conversation.get(0);
            User contact;
            if (m.getReceiverId().equals(user.getId())){
                contact=dataBaseService.getUser(m.getSenderId()).get();
            }
            else{
                contact=dataBaseService.getUser(m.getReceiverId()).get();
            }

            HorizontalLayout card = new HorizontalLayout();
            card.addClassName("card");
            card.setSpacing(false);
            card.getThemeList().add("spacing-s");
            Avatar avatar = new Avatar(contact.getName(), contact.getProfilePictureUrl());
            avatar.addClassNames("me-xs");

            card.add(avatar, new RouterLink(contact.getName(),
                    SingleConversationView.class, new RouteParameters("contactID", contact.getId().toString())));
            Div div=new Div();
            /*Div menu = new Div();
            menu.add(new RouterLink(contact.getName(),
                    SingleConversationView.class, new RouteParameters("contactID", contact.getId().toString())));*/
            add(card);
            //FINE PROVA
        }
    }

    private List<List<Message>> getConversations(){
        List <User> contacts=dataBaseService.getUserConversationFromUser(user);
        List<List<Message>> conversations=new LinkedList<>();
        for(User u : contacts){
            conversations.add(dataBaseService.getConversationFromUsers(user, u));
        }
        return conversations;
    }

}

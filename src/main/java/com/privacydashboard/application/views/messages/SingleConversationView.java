package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import java.time.ZoneOffset;
import java.util.*;

@PageTitle("Conversation")
@Route(value="conversation/:contactID", layout = MainLayout.class)
@PermitAll
public class SingleConversationView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver{// implements HasUrlParameter<UUID> {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private User contact;
    private final Span title=new Span();
    private final MessageList messageList= new MessageList();
    Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        Optional<User> maybeOtherUser;
        Optional<String> contactID=event.getRouteParameters().get("contactID");
        if(!contactID.isPresent()){
            event.rerouteTo(NoContactView.class);
        }
        try {
            maybeOtherUser = dataBaseService.getUser(UUID.fromString(contactID.get()));
            if (!maybeOtherUser.isPresent()) {
                event.rerouteTo(NoContactView.class);
            }
            contact = maybeOtherUser.get();
            if(!dataBaseService.getAllContactsFromUser(getUser()).contains(contact)){
                event.rerouteTo(NoContactView.class);
            }
            title.removeAll();
            title.add(new H2(contact.getName()));
        }catch(IllegalArgumentException e){
            event.rerouteTo(NoContactView.class);
        }
    }

    public SingleConversationView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        add(title);
        add(messageList);
        initializeSendMessageLayout();
    }

    private void initializeSendMessageLayout(){
        TextArea messageText=new TextArea();
        messageText.setPlaceholder("Text...");
        messageText.setWidth("700px");

        Button sendMessageButton=new Button("Send Message", e->{
            if(messageText.getValue()!=null){
                sendMessage(messageText.getValue());
                messageText.setValue("");
                updateConversation();
            }
        });
        add(new HorizontalLayout(messageText , sendMessageButton));
    }

    //DA SISTEMARE LA TIME ZONE
    private List<MessageListItem> getMessages(){
        List<Message> conversation=dataBaseService.getConversationFromUsers(getUser(), contact);
        List<MessageListItem> messageListItems=new LinkedList<>();
        for(Message message : conversation){
            User u=message.getSender();
            MessageListItem messageItem=new MessageListItem(message.getMessage(),message.getTime().toInstant(ZoneOffset.UTC), u.getName());
            messageListItems.add(messageItem);
        }
        return messageListItems;
    }

    private void sendMessage(String text){
        Message message=new Message();
        message.setMessage(text);
        message.setReceiver(contact);
        message.setSender(getUser());
        dataBaseService.addNowMessage(message);
    }

    private void updateConversation(){
        messageList.setItems(getMessages());
    }

    private User getUser(){
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            return null;
        }
        return maybeUser.get();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event){
        updateConversation();
    }
}

package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@PageTitle("Conversation")
@Route(value="conversation/:contactID", layout = MainLayout.class)
@PermitAll
public class SingleConversationView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver{// implements HasUrlParameter<UUID> {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private User user;
    private User contact;
    private MessageList messageList= new MessageList();
    private Page page=new Page(UI.getCurrent());

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
            if(!dataBaseService.getAllContactsFromUser(user).contains(contact)){
                event.rerouteTo(NoContactView.class);
            }
            page.setTitle(contact.getName());
        }catch(IllegalArgumentException e){
            event.rerouteTo(NoContactView.class);
        }
    }

    public SingleConversationView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        addClassNames("single-conversation-view");
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        Optional<User> maybeUser= authenticatedUser.get();
        if(!maybeUser.isPresent()){
            add(new H1("NOT LOGGED IN"));
            return;
        }
        user=maybeUser.get();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event){
        add(new H1(contact.getName()));
        displayConversation();
    }

    //DA SISTEMARE LA TIME ZONE
    private void displayConversation(){
        messageList.setItems(getMessages());
        add(messageList);

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

    private List<MessageListItem> getMessages(){
        List<Message> conversation=dataBaseService.getConversationFromUsers(user, contact);
        List<MessageListItem> messageListItems=new LinkedList<>();
        for(Message message : conversation){
            User u=message.getSenderId().equals(user.getId()) ? user : contact;
            MessageListItem messageItem=new MessageListItem(message.getMessage(),message.getTime().toInstant(ZoneOffset.UTC), u.getName());
            messageListItems.add(messageItem);
        }
        return messageListItems;
    }

    private void sendMessage(String text){
        Message message=new Message();
        message.setMessage(text);
        message.setReceiverId(contact.getId());
        message.setSenderId(user.getId());
        dataBaseService.addNowMessage(message);
    }

    private void updateConversation(){
        messageList.setItems(getMessages());
    }
}

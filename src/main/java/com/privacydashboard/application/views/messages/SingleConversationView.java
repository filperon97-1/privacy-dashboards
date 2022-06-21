package com.privacydashboard.application.views.messages;

import com.privacydashboard.application.data.entity.Message;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;

import javax.annotation.security.PermitAll;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@PageTitle("Conversation")
@Route(value="conversation/:contactID", layout = MainLayout.class)
@PermitAll
public class SingleConversationView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver{// implements HasUrlParameter<UUID> {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private User user;
    private User contact;

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
        List<Message> conversation=dataBaseService.getConversationFromUsers(user, contact);
        List<MessageListItem> messageListItems=new LinkedList<>();
        for(Message message : conversation){
            User u=message.getSenderId().equals(user.getId()) ? user : contact;
            MessageListItem messageItem=new MessageListItem(message.getMessage(),message.getTime().toInstant(ZoneOffset.UTC), u.getName());
            messageListItems.add(messageItem);
        }
        MessageList messageList=new MessageList();
        messageList.setItems(messageListItems);
        add(messageList);

        TextArea messageText=new TextArea();
        messageText.setPlaceholder("Text...");
        messageText.setWidth("700px");
        add(messageText);
    }
}

package com.privacydashboard.application.views.mainLayout;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.views.applyRights.RightsView;
import com.privacydashboard.application.views.messages.SingleConversationView;
import com.privacydashboard.application.views.rightRequest.RightRequestsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.ContextMenu;

import java.util.List;

public class NotificationView {
    private final User user;
    private final DataBaseService dataBaseService;
    private final CommunicationService communicationService;
    private final ContextMenu menuNotifications=new ContextMenu();

    public NotificationView(User user, DataBaseService dataBaseService, CommunicationService communicationService){
        this.dataBaseService=dataBaseService;
        this.user=user;
        this.communicationService=communicationService;
    }

    public ContextMenu getContextMenu(){
        menuNotifications.setOpenOnClick(true);
        updateNotifications();
        return menuNotifications;
    }

    private void updateNotifications(){
        menuNotifications.removeAll();
        List<Notification> notifications=dataBaseService.getNewNotificationsFromUser(user);
        for(Notification notification  : notifications){
            menuNotifications.addItem(notification.getDescription(), e-> goToNotification(notification));
        }
    }

    private void goToNotification(Notification notification){
        // Message notification
        if(notification.getMessage()!=null && notification.getRequest()==null){
            dataBaseService.changeIsReadNotification(notification, true);
            updateNotifications();
            communicationService.setContact(notification.getSender());
            UI.getCurrent().navigate(SingleConversationView.class);
        }
        if(notification.getRequest()!=null && notification.getMessage()==null){
            dataBaseService.changeIsReadNotification(notification, true);
            updateNotifications();
            communicationService.setRightNotification(notification);
            if(user.getRole().equals(Role.SUBJECT)){
                UI.getCurrent().navigate(RightsView.class);
            }
            else{
                UI.getCurrent().navigate(RightRequestsView.class);
            }
        }
    }
}
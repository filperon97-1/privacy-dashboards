package com.privacydashboard.application.views.mainLayout;

import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.views.applyRights.RightsView;
import com.privacydashboard.application.views.messages.SingleConversationView;
import com.privacydashboard.application.views.rightRequest.RightRequestsView;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;

public class NotificationView {
    private final User user;
    private final DataBaseService dataBaseService;
    private ContextMenu menuNotifications=new ContextMenu();
    public NotificationView(User user, DataBaseService dataBaseService){
        this.dataBaseService=dataBaseService;
        this.user=user;
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
            UI.getCurrent().navigate(SingleConversationView.class, new RouteParameters("contactID", notification.getSender().getId().toString()));
        }
        if(notification.getRequest()!=null && notification.getMessage()==null){
            dataBaseService.changeIsReadNotification(notification, true);
            updateNotifications();
            ComponentUtil.setData(UI.getCurrent(), "RightNotification", notification);
            if(notification.getReceiver().getRole().equals(Role.SUBJECT)){
                UI.getCurrent().navigate(RightsView.class);
            }
            else{
                UI.getCurrent().navigate(RightRequestsView.class);
            }
        }
    }
}
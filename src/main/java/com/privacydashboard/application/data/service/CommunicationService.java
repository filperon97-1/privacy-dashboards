package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;

// classe usata per passare vari oggetti tra le Views senza dover usare parametri nell'URL
@Service
public class CommunicationService {
    public CommunicationService(){
    }

    public Notification getRightNotification() {
        Notification notification = null;
        Object object = ComponentUtil.getData(UI.getCurrent(), "RightNotification");
        try {
            if (object != null) {
                notification = (Notification) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ComponentUtil.setData(UI.getCurrent(), "RightNotification", null);
        }
        return notification;
    }

    public void setRightNotification(Notification notification){
        ComponentUtil.setData(UI.getCurrent(), "RightNotification", notification);
    }

    public RightRequest getRightRequest(){
        RightRequest request=null;
        Object object=ComponentUtil.getData(UI.getCurrent(), "RightRequest");
        try {
            if (object != null) {
                request = (RightRequest) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ComponentUtil.setData(UI.getCurrent(), "RightRequest", null);
        }
        return request;
    }

    public void setRightRequest(RightRequest request){
        ComponentUtil.setData(UI.getCurrent(), "RightRequest", request);
    }

    public IoTApp getApp(){
        IoTApp app=null;
        Object object=ComponentUtil.getData(UI.getCurrent(), "App");
        try {
            if (object != null) {
                app = (IoTApp) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ComponentUtil.setData(UI.getCurrent(), "App", null);
        }
        return app;
    }

    public void setApp(IoTApp app){
        ComponentUtil.setData(UI.getCurrent(), "App", app);
    }

    public User getContact(){
        User contact=null;
        Object object=ComponentUtil.getData(UI.getCurrent(), "Contact");
        try {
            if (object != null) {
                contact = (User) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ComponentUtil.setData(UI.getCurrent(), "Contact", null);
        }
        return contact;
    }

    public void setContact(User contact){
        ComponentUtil.setData(UI.getCurrent(), "Contact", contact);
    }
}

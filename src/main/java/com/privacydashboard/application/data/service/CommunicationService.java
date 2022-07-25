package com.privacydashboard.application.data.service;

import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.RightRequest;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
}

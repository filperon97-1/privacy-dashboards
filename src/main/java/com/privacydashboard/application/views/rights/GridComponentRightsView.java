package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.GlobalVariables.RightType;
import com.privacydashboard.application.data.Role;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.views.usefulComponents.ToggleButton;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

public class GridComponentRightsView extends Dialog {
    private final Role role;

    public GridComponentRightsView(Role role){
        this.role=role;
    }

    public HorizontalLayout getHeaderLayout(){
        return getHeaderLayout(null);
    }

    public HorizontalLayout getHeaderLayout(ToggleButton toggleButton){
        Span[] spans= new Span[5];
        if(role.equals(Role.SUBJECT)){
            spans[0]= new Span("RECEIVER");
            spans[4]= new Span("HANDLED");
        }
        else{
            spans[0]= new Span("SENDER");
            spans[4] = new Span(toggleButton);
        }
        spans[1]= new Span("RIGHT TYPE");
        spans[2]= new Span("APP");
        spans[3]= new Span("TIME");
        for(Span span : spans){
            span.addClassName("name");
        }
        HorizontalLayout headerLayout= new HorizontalLayout(spans);
        headerLayout.addClassName("headerLayout");
        return headerLayout;
    }

    public HorizontalLayout getCard(RightRequest request){
        Span[] spans= new Span[5];
        if(role.equals(Role.SUBJECT)){
            spans[0] = new Span(request.getReceiver().getName());
        }
        else {
            spans[0] = new Span(request.getSender().getName());
        }
        spans[1]= new Span(request.getRightType().toString());
        spans[2]= new Span(request.getApp().getName());
        spans[3]= new Span(DateTimeFormatter.ofPattern("dd/MM/yyy").format(request.getTime()));
        spans[4]= new Span(request.getHandled().toString());
        for(Span span : spans){
            span.addClassName("name");
        }

        HorizontalLayout card= new HorizontalLayout(spans);
        card.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        card.addClassName("card");
        card.addClassName("canOpen");
        return card;
    }

    public VerticalLayout getContent(RightRequest request){
        User contact;
        Span descriptionSpan= new Span();
        if(role.equals(Role.SUBJECT)){
            descriptionSpan.setText("Receiver User:   ");
            contact= request.getReceiver();
        }
        else{
            descriptionSpan.setText("Sender User:   ");
            contact= request.getSender();
        }
        Span userSpan= new Span(contact.getName());
        userSpan.addClassName("link");
        userSpan.addClickListener(e-> fireEvent(new ContactEvent(this, contact)));
        HorizontalLayout user= new HorizontalLayout(descriptionSpan, userSpan);
        Span rightType=new Span("Right:   " + request.getRightType().toString());

        Span app= new Span("App:   ");
        Span appSpan= new Span(request.getApp().getName());
        appSpan.addClickListener(e-> fireEvent(new AppEvent(this, request.getApp())));
        appSpan.addClassName("link");
        HorizontalLayout appLayout= new HorizontalLayout(app, appSpan);

        Span time=new Span("Time:   " + DateTimeFormatter.ofPattern("dd/MM/yyy").format(request.getTime()));
        Span details=new Span("Details:   " + request.getDetails());
        String otherString="";
        if(request.getRightType().equals(RightType.WITHDRAWCONSENT)){
            otherString="Consent to withdraw:   ";
        }
        if(request.getRightType().equals(RightType.COMPLAIN)){
            otherString="Complain:   ";
        }
        if(request.getRightType().equals(RightType.INFO)){
            otherString="Info:   ";
        }
        if(request.getRightType().equals(RightType.ERASURE)){
            otherString="What to erase:   ";
        }
        Span other=new Span(otherString + (request.getOther()==null ? "" : request.getOther()));
        TextArea textArea;
        Checkbox checkbox= new Checkbox();
        if(role.equals(Role.SUBJECT)){
            textArea= new TextArea("Controller response");
            textArea.setReadOnly(true);
            checkbox.setReadOnly(true);
        }
        else{
            textArea=new TextArea("Your response");
            textArea.setPlaceholder("Write your response...");
        }
        textArea.setValue(request.getResponse()==null ? "" : request.getResponse());
        checkbox.setValue(request.getHandled());
        checkbox.setLabel("Handled");

        return new VerticalLayout(user, rightType, appLayout, time, details, other, textArea, checkbox/*, new Span("ID: " + request.getId().toString())*/);
    }

    public static class ContactEvent extends ComponentEvent<GridComponentRightsView> {
        Logger logger = LoggerFactory.getLogger(getClass());
        private final User contact;
        ContactEvent(GridComponentRightsView source, User contact){
            super(source, false);
            logger.info("contact event");
            this.contact= contact;
        }

        public User getContact(){
            return contact;
        }
    }

    public static class AppEvent extends ComponentEvent<GridComponentRightsView> {
        private final IoTApp app;
        AppEvent(GridComponentRightsView source, IoTApp app){
            super(source, false);
            this.app= app;
        }

        public IoTApp getApp(){
            return app;
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@PageTitle("Rights")
@Route(value="rights_controller", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class ControllerDPORightsView extends VerticalLayout implements BeforeEnterObserver, AfterNavigationObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;
    private final Grid<RightRequest> grid= new Grid<>();
    private RightRequest priorityRight=null;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        Notification notification=communicationService.getRightNotification();
        if(notification!=null){
            priorityRight=notification.getRequest();
        }
    }

    public ControllerDPORightsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        this.communicationService= communicationService;
        initializeGrid();
    }

    private void initializeGrid(){
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumn(request -> request.getSender().getName()).setHeader("NAME");
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE");
        grid.addColumn(request -> request.getApp().getName()).setHeader("APP");
        grid.addColumn(request -> dtf.format(request.getTime())).setHeader("TIME");
        grid.addColumn(RightRequest::getDetails).setHeader("DETAILS");
        grid.addColumn(RightRequest::getHandled).setHeader("HANDLED");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> showRequest(event.getValue()));
        add(grid);
    }

    private void showRequest(RightRequest request){
        if(request==null){
            return;
        }
        Dialog requestDialog= new Dialog();
        HorizontalLayout sender=new HorizontalLayout(new Span("Sender User: "), new Span(request.getSender().getName()));
        HorizontalLayout rightType=new HorizontalLayout(new Span("Right: "), new Span(request.getRightType().toString()));
        HorizontalLayout app=new HorizontalLayout(new Span("App: "), new Span(request.getApp().getName()));
        HorizontalLayout time=new HorizontalLayout(new Span("Time: "), new Span(dtf.format(request.getTime())));
        HorizontalLayout details=new HorizontalLayout(new Span("Details: "), new Span(request.getDetails()));
        String otherString="";
        if(request.getRightType().equals(RightType.WITHDRAWCONSENT)){
            otherString="Consent to withdraw: ";
        }
        if(request.getRightType().equals(RightType.COMPLAIN)){
            otherString="Complain: ";
        }
        if(request.getRightType().equals(RightType.INFO)){
            otherString="Info: ";
        }
        if(request.getRightType().equals(RightType.ERASURE)){
            otherString="What to erase: ";
        }
        HorizontalLayout other=new HorizontalLayout(new Span(otherString), new Span(request.getOther()));
        Checkbox checkbox=new Checkbox();
        checkbox.setValue(request.getHandled());
        checkbox.setLabel("Handled");

        Button save=new Button("Save" , e->{changeStatusRequest(request, checkbox.getValue());
                                                requestDialog.close();});
        save.addClassNames("button");
        Button cancel=new Button("Cancel", e->requestDialog.close());
        cancel.addClassNames("button");
        HorizontalLayout buttonLayout=new HorizontalLayout(save,cancel);
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setVerticalComponentAlignment(Alignment.CENTER);

        VerticalLayout layout= new VerticalLayout(sender, rightType, app, time, details, other, checkbox, buttonLayout);
        requestDialog.add(layout);
        requestDialog.open();
    }

    private void changeStatusRequest(RightRequest request, Boolean newHandled){
        if(newHandled!=request.getHandled()){
            dataBaseService.changeHandledRequest(request, newHandled);
            updateGrid();
        }
    }

    private void updateGrid(){
        List<RightRequest> rightRequests=dataBaseService.getAllRequestsFromReceiver(authenticatedUser.getUser());
        if(priorityRight!=null && rightRequests.contains(priorityRight)){
            Collections.swap(rightRequests, 0 , rightRequests.indexOf(priorityRight));
        }
        grid.setItems(rightRequests);
        grid.select(priorityRight);
        priorityRight=null;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
       updateGrid();
    }
}

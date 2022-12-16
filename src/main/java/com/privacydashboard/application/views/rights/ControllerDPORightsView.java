package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.privacydashboard.application.views.usefulComponents.ToggleButton;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
    private final ToggleButton toggleButton=new ToggleButton("HANDLED", false);
    private final MyDialog requestDialog= new MyDialog();
    private RightRequest priorityRight=null;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public void beforeEnter(BeforeEnterEvent event){
        RightRequest request=communicationService.getRightFromNotification();
        if(request!=null){
            priorityRight=request;
        }
    }

    public ControllerDPORightsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        this.communicationService= communicationService;
        initializeGrid();
    }

    private void initializeGrid(){
        toggleButton.addClickListener(e-> updateGrid(toggleButton.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(request -> request.getSender().getName()).setHeader("NAME").setSortable(true);
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE").setSortable(true);
        grid.addColumn(request -> request.getApp().getName()).setHeader("APP").setSortable(true);
        grid.addColumn(request -> dtf.format(request.getTime())).setHeader("TIME").setSortable(true);
        grid.addColumn(RightRequest::getDetails).setHeader("DETAILS").setSortable(true);
        grid.addColumn(RightRequest::getHandled).setHeader(toggleButton);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> showRequest(event.getValue()));

        add(grid);
    }

    private void showRequest(RightRequest request){
        if(request==null){
            return;
        }
        Span sender=new Span("Sender User:   "+ request.getSender().getName());
        Span rightType=new Span("Right:   " + request.getRightType().toString());
        Span app=new Span("App:   " + request.getApp().getName());
        Span time=new Span("Time:   " + dtf.format(request.getTime()));
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
        TextArea textArea=new TextArea("Your response");
        textArea.setPlaceholder("Write your response...");
        textArea.setValue(request.getResponse()==null ? "" : request.getResponse());
        Checkbox checkbox=new Checkbox();
        checkbox.setValue(request.getHandled());
        checkbox.setLabel("Handled");

        Button save=new Button("Save" , e->changeRequest(request, checkbox.getValue(), textArea.getValue()));
        requestDialog.setTitle("Right Request");
        requestDialog.setContinueButton(save);
        requestDialog.setContent(new VerticalLayout(sender, rightType, app, time, details, other, textArea, checkbox));
        requestDialog.open();
    }

    private void changeRequest(RightRequest request, Boolean handled, String response){
        if((response.equals(request.getResponse()) || response.equals(""))  && handled==request.getHandled()){
            return;
        }
        request.setResponse(response);
        request.setHandled(handled);
        dataBaseService.changeRightRequest(request);
        updateGrid(toggleButton.getValue());
        requestDialog.close();
    }

    private void updateGrid(boolean handled){
        List<RightRequest> rightRequests;
        if(handled){
            rightRequests=dataBaseService.getHandledRequestsFromReceiver(authenticatedUser.getUser());
        }
        else{
            rightRequests=dataBaseService.getPendingRequestsFromReceiver(authenticatedUser.getUser());
        }

        if(priorityRight!=null && rightRequests.contains(priorityRight)){
            Collections.swap(rightRequests, 0 , rightRequests.indexOf(priorityRight));
        }
        grid.setItems(rightRequests);
        grid.select(priorityRight);
        priorityRight=null;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
       updateGrid(false);
    }
}

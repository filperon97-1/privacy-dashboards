package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.Notification;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@PageTitle("Rights")
@Route(value="rights", layout = MainLayout.class)
@RolesAllowed("SUBJECT")
public class SubjectRightsView extends VerticalLayout implements BeforeEnterObserver{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final CommunicationService communicationService;
    private final Grid<RightRequest> grid= new Grid<>();
    private RightRequest priorityRight=null;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    // Uso ComponentUtil per passare le informazioni invece dei parametri dell'url. Dopo bisogna resettarlo
    @Override
    public void beforeEnter(BeforeEnterEvent event){
        // apply right
        RightRequest request= communicationService.getRightRequest();
        if(request!=null){
            DialogRight dialogRight=new DialogRight(dataBaseService, authenticatedUser);
            dialogRight.showDialogConfirm(request);
            return;
        }
        // show notification
        Notification notification=communicationService.getRightNotification();
        if(notification!=null){
            priorityRight=notification.getRequest();
            if(priorityRight!=null){
                showRequests(priorityRight.getHandled());
            }
        }
    }

    public SubjectRightsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser, CommunicationService communicationService) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        this.communicationService=communicationService;
        initializeGrid();
        createButtons();
        generateAllRightsDetails();
    }

    private void initializeGrid(){
        grid.addColumn(request -> request.getReceiver().getName()).setHeader("NAME");
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE");
        grid.addColumn(request -> request.getApp().getName()).setHeader("APP");
        grid.addColumn(request -> dtf.format(request.getTime())).setHeader("TIME");
        grid.addColumn(RightRequest::getDetails).setHeader("DETAILS");
        grid.addColumn(RightRequest::getHandled).setHeader("HANDLED");
        //grid.asSingleSelect().addValueChangeListener(event -> showRequest(event.getValue()));
    }

    private void createButtons(){
        Button pendingRequests=new Button("Pending requests", event -> showRequests(false));
        Button handledRequests=new Button("Handled requests", event -> showRequests(true));
        add(new HorizontalLayout(pendingRequests, handledRequests));
    }

    private void showRequests(Boolean handled){
        Dialog rightList=new Dialog();
        List<RightRequest> rightRequests;
        if(handled) {
            rightRequests = dataBaseService.getHandledRequestsFromSender(authenticatedUser.getUser());
        }
        else{
            rightRequests = dataBaseService.getPendingRequestsFromSender(authenticatedUser.getUser());
        }
        if(priorityRight!=null && rightRequests.contains(priorityRight)){
            Collections.swap(rightRequests, 0 , rightRequests.indexOf(priorityRight));
        }
        grid.setItems(rightRequests);
        grid.select(priorityRight);
        rightList.add(grid);
        rightList.setWidthFull();
        rightList.open();
    }

    private void generateAllRightsDetails(){
        add(generateRightDetail("Consenses", "you have the right to withdraw consent at any time [GDPR, article 13 2(C)]",
                "Withdraw a consent", RightType.WITHDRAWCONSENT));

        add(generateRightDetail("Ask information", "you have the right to know some information:\n" +
                        "the period for which the personal data will be stored,\n" +
                        "the purposes of the processing for which the personal data are intended,\n" +
                        "the recipients or categories of recipients of the personal data",
                "Ask information", RightType.INFO));

        add(generateRightDetail("Complain", "compile a complain to the supervisory authority",
                "Compile a complain", RightType.COMPLAIN));

        add(generateRightDetail("Right to erasure", "ask to erase some personal data",
                "Ask to erase", RightType.ERASURE));

    }

    private Details generateRightDetail(String title, String description, String buttonString , RightType rightType){
        return new Details(title, new VerticalLayout(new Span(description),
                                                    new Button(buttonString, e-> startRequest(rightType))));
    }

    private void startRequest(RightType rightType){
        DialogRight dialogRight=new DialogRight(dataBaseService, authenticatedUser);
        dialogRight.showDialogRequest(rightType);
    }
}

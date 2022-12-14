package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.privacydashboard.application.views.usefulComponents.MyDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
        request= communicationService.getRightFromNotification();
        if(request!= null){
            priorityRight=request;
            showRequestList(priorityRight.getHandled());
            return;
        }
        // show Pending Requests (action available from Home)
        Boolean open= communicationService.getOpenPendingRequests();
        if(open!=null && open){
            showRequestList(false);
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
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn(request -> request.getReceiver().getName()).setHeader("NAME").setSortable(true);
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE").setSortable(true);
        grid.addColumn(request -> request.getApp().getName()).setHeader("APP").setSortable(true);
        grid.addColumn(request -> dtf.format(request.getTime())).setHeader("TIME").setSortable(true);
        grid.addColumn(RightRequest::getDetails).setHeader("DETAILS").setSortable(true);
        grid.addColumn(RightRequest::getHandled).setHeader("HANDLED");
        grid.asSingleSelect().addValueChangeListener(event -> showRequest(event.getValue()));
    }

    private void createButtons(){
        Button pendingRequests=new Button("Pending requests", event -> showRequestList(false));
        Button handledRequests=new Button("Handled requests", event -> showRequestList(true));
        pendingRequests.addClassName("buuutton");
        handledRequests.addClassName("buuutton");
        add(new HorizontalLayout(pendingRequests, handledRequests));
    }

    private void showRequestList(Boolean handled){
        MyDialog rightList=new MyDialog();
        List<RightRequest> rightRequests;
        if(handled) {
            rightRequests = dataBaseService.getHandledRequestsFromSender(authenticatedUser.getUser());
            rightList.setTitle("Handled requests");
        }
        else{
            rightRequests = dataBaseService.getPendingRequestsFromSender(authenticatedUser.getUser());
            rightList.setTitle("Pending requests");
        }
        if(priorityRight!=null && rightRequests.contains(priorityRight)){
            Collections.swap(rightRequests, 0 , rightRequests.indexOf(priorityRight));
        }
        grid.setItems(rightRequests);
        grid.select(priorityRight);
        rightList.setWithoutFooter(true);
        rightList.setContent(new HorizontalLayout(grid));
        rightList.setWidthFull();
        rightList.open();
        priorityRight=null;
    }

    private void showRequest(RightRequest request){
        MyDialog requestDialog=new MyDialog();
        if(request==null){
            return;
        }
        Span sender=new Span("Receiving User:   "+ request.getReceiver().getName());
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
        TextArea textArea=new TextArea("Controller response");
        textArea.setValue(request.getResponse()==null ? "" : request.getResponse());
        textArea.setReadOnly(true);
        Checkbox checkbox=new Checkbox();
        checkbox.setValue(request.getHandled());
        checkbox.setLabel("Handled");
        checkbox.setReadOnly(true);
        requestDialog.setWithoutFooter(true);
        requestDialog.setTitle("Right request");
        requestDialog.setContent(new VerticalLayout(sender, rightType, app, time, details, other, textArea, checkbox));
        requestDialog.open();
    }

    private void generateAllRightsDetails(){
        add(generateRightDetail("Data portability", "you have the right to receive your personal data [GDPR, article 20]",
                "Access data", RightType.PORTABILITY));

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
        Button button=new Button(buttonString, e-> startRequest(rightType));
        button.addClassName("buuutton");
        return new Details(title, new VerticalLayout(new Span(description), button));
    }

    private void startRequest(RightType rightType){
        DialogRight dialogRight=new DialogRight(dataBaseService, authenticatedUser);
        dialogRight.showDialogRequest(rightType);
    }
}

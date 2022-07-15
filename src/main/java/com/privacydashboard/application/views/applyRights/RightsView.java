package com.privacydashboard.application.views.applyRights;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.IoTApp;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
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
import java.util.Optional;

interface RightAction{
    void startAction();
}

@PageTitle("Rights")
@Route(value="rights", layout = MainLayout.class)
@RolesAllowed("SUBJECT")
public class RightsView extends VerticalLayout implements BeforeEnterObserver{
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final Dialog dialog=new Dialog();
    private final Dialog confirmDialog=new Dialog();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    // Uso ComponentUtil per passare le informazioni invece dei parametri dell'url. Dopo bisogna resettarlo
    @Override
    public void beforeEnter(BeforeEnterEvent event){
        Object object=ComponentUtil.getData(UI.getCurrent(), "RightRequest");
        if(object==null){
            return;
        }
        try{
            RightRequest request=(RightRequest) object;
            ComponentUtil.setData(UI.getCurrent(), "RightRequest", null);
            confirmRequest(request);
        }catch(Exception e){
            ComponentUtil.setData(UI.getCurrent(), "RightRequest", null);
            return;
        }
    }

    public RightsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        createButtons();
        generateAllRightsDetails();
    }

    private void generateAllRightsDetails(){
        RightAction withdrawAction= () -> startWithdrawConsent();
        add(generateRightDetail("Consenses", "you have the right to withdraw consent at any time [GDPR, article 13 2(C)]",
                "Withdraw a consent", withdrawAction));

        RightAction askInformation=() -> startWithdrawConsent();
        add(generateRightDetail("Ask information", "you have the right to know some information:\n" +
                        "the period for which the personal data will be stored,\n" +
                        "the purposes of the processing for which the personal data are intended,\n" +
                        "the recipients or categories of recipients of the personal data",
                "Ask information", askInformation));

        RightAction complain=() -> startWithdrawConsent();
        add(generateRightDetail("Complain", "compile a complain to the supervisory authority",
                "Compile a complain", complain));

        RightAction erasure=() -> startWithdrawConsent();
        add(generateRightDetail("Right to erasure", "ask to erase some personal data",
                "Ask to erase", erasure));

    }

    private Details generateRightDetail(String title, String description, String buttonString , RightAction action){
        VerticalLayout content=new VerticalLayout(new Span(description),
                new Button(buttonString , e-> action.startAction()));
        Details rightDetail = new Details(title, content);
        return rightDetail;
    }

    private void createButtons(){
        Button pendingRequests=new Button("Pending requests", event -> showRequests(false));
        Button handledRequests=new Button("Handled requests", event -> showRequests(true));
        add(new HorizontalLayout(pendingRequests, handledRequests));
    }

    private void showRequests(Boolean handled){
        dialog.removeAll();
        Grid<RightRequest> grid=new Grid();
        grid.addColumn(request -> request.getReceiver().getName()).setHeader("NAME");
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE");
        grid.addColumn(request -> request.getApp().getName()).setHeader("APP");
        grid.addColumn(request -> dtf.format(request.getTime())).setHeader("TIME");
        grid.addColumn(RightRequest::getDetails).setHeader("DETAILS");
        grid.addColumn(RightRequest::getHandled).setHeader("HANDLED");
        if(handled){
            grid.setItems(dataBaseService.getHandledRequestsFromSender(getUser()));
        }
        else{
            grid.setItems(dataBaseService.getPendingRequestsFromSender(getUser()));
        }
        dialog.add(grid);
        dialog.setWidthFull();
        dialog.open();
    }

    private void startWithdrawConsent(){
        dialog.removeAll();
        dialog.setWidth("50%");
        H1 titleText= new H1("Select App");

        ComboBox<String> consensComboBox= new ComboBox<>("Consens");
        consensComboBox.setPlaceholder("Filter by name...");

        ComboBox<IoTApp> appComboBox= new ComboBox<>("Apps");
        appComboBox.setItems(dataBaseService.getUserApps(getUser()));
        appComboBox.setItemLabelGenerator(IoTApp::getName);
        appComboBox.setPlaceholder("Filter by name...");
        appComboBox.addValueChangeListener(e-> consensComboBox.setItems(dataBaseService.getConsensesFromUserAndApp(getUser(),appComboBox.getValue())));

        Button newMessage=new Button("Continue", e->{
            if(appComboBox.getValue()!=null && consensComboBox.getValue()!=null){
                RightRequest request=new RightRequest();
                request.setApp(appComboBox.getValue());
                request.setReceiver(dataBaseService.getControllersFromApp(appComboBox.getValue()).get(0));
                request.setSender(getUser());
                request.setOther(consensComboBox.getValue());
                request.setRightType(RightType.WITHDRAWCONSENT);
                request.setHandled(false);
                dialog.close();
                confirmRequest(request);
            }
        });
        Button cancel=new Button("Cancel", e-> dialog.close());
        HorizontalLayout buttonLayout= new HorizontalLayout(newMessage, cancel);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout layout=new VerticalLayout(titleText, new HorizontalLayout(appComboBox, consensComboBox), buttonLayout);
        layout.setHorizontalComponentAlignment(Alignment.CENTER);
        dialog.add(layout);
        dialog.open();
    }

    private void confirmRequest(RightRequest request){
        confirmDialog.removeAll();
        confirmDialog.setWidth("50%");

        HorizontalLayout appName=new HorizontalLayout(new H1("APP:  "), new H2(request.getApp().getName()));
        appName.setAlignItems(Alignment.CENTER);
        appName.setVerticalComponentAlignment(Alignment.CENTER);
        HorizontalLayout right=new HorizontalLayout(new H1("RIGHT:  "), new H2(request.getRightType().toString()));
        right.setAlignItems(Alignment.CENTER);
        right.setVerticalComponentAlignment(Alignment.CENTER);

        TextArea premadeMessage=new TextArea();
        premadeMessage.setValue("Dear " + request.getReceiver().getName() + ", \n" +
                "I would like to withdraw the consent: " +request.getOther() + " from the app " + request.getApp().getName() + ",\n" +
                "Best regards, \n" +
                request.getSender().getName());
        premadeMessage.setWidthFull();
        premadeMessage.setReadOnly(true);
        TextArea details=new TextArea();
        details.setPlaceholder("Add additional information");
        details.setWidthFull();

        Button confirm=new Button("Confirm", e->{request.setDetails(details.getValue());
                                                        dataBaseService.addNowRequest(request);
                                                        confirmDialog.close();});
        Button cancel=new Button("Cancel", e-> confirmDialog.close());

        confirmDialog.add(new VerticalLayout(appName, right, premadeMessage, details, new HorizontalLayout(confirm, cancel)));
        confirmDialog.open();
    }

    private User getUser(){
        Optional<User> maybeUser=this.authenticatedUser.get();
        if(maybeUser.isEmpty()){
            return null;
        }
        return maybeUser.get();
    }
}

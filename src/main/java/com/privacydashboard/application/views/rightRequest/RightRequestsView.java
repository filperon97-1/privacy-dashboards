package com.privacydashboard.application.views.rightRequest;

import com.privacydashboard.application.data.RightType;
import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
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
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@PageTitle("Rights")
@Route(value="rights_controller", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class RightRequestsView extends VerticalLayout implements AfterNavigationObserver {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final Grid<RightRequest> grid= new Grid<>();
    private final Dialog requestDialog=new Dialog();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public RightRequestsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        addClassName("grid-views");
        initializeGrid();
    }

    private void initializeGrid(){
        grid.setSizeFull();
        grid.setHeight("100%");
        grid.setWidth("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addColumn(request -> request.getSender().getName()).setHeader("NAME");
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE");
        grid.addColumn(request -> request.getApp().getName()).setHeader("APP");
        grid.addColumn(request -> dtf.format(request.getTime())).setHeader("TIME");
        grid.addColumn(RightRequest::getDetails).setHeader("DETAILS");
        grid.addColumn(RightRequest::getHandled).setHeader("HANDLED");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        add(grid);
        // quando si seleziona una riga, si apre il dialog
        grid.asSingleSelect().addValueChangeListener(event -> showRequest(event.getValue()));
    }

    private void showRequest(RightRequest request){
        if(request==null){
            return;
        }
        requestDialog.removeAll();
        HorizontalLayout sender=new HorizontalLayout(new Span("Sender User: "), new Span(request.getSender().getName()));
        HorizontalLayout rightType=new HorizontalLayout(new Span("Right: "), new Span(request.getRightType().toString()));
        HorizontalLayout app=new HorizontalLayout(new Span("App: "), new Span(request.getApp().getName()));
        HorizontalLayout time=new HorizontalLayout(new Span("Time: "), new Span(dtf.format(request.getTime())));
        HorizontalLayout details=new HorizontalLayout(new Span("Details: "), new Span(request.getDetails()));
        String otherString="";
        if(request.getRightType().equals(RightType.WITHDRAWCONSENT)){
            otherString="Consent to withdraw: ";
        }
        HorizontalLayout other=new HorizontalLayout(new Span(otherString), new Span(request.getOther()));
        Checkbox checkbox=new Checkbox();
        checkbox.setValue(request.getHandled());
        checkbox.setLabel("Handled");

        Button save=new Button("Save" , e->{request.setHandled(checkbox.getValue());
                                                dataBaseService.updateRequest(request);
                                                requestDialog.close();
                                                updateGrid();});
        Button cancel=new Button("Cancel", e->requestDialog.close());
        HorizontalLayout buttonLayout=new HorizontalLayout(save,cancel);
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.setVerticalComponentAlignment(Alignment.CENTER);

        VerticalLayout layout= new VerticalLayout(sender, rightType, app, time, details, other, checkbox, buttonLayout);
        requestDialog.add(layout);
        requestDialog.open();
    }

    private void updateGrid(){
        Optional<User> maybeUser=this.authenticatedUser.get();
        if(maybeUser.isEmpty()){
            return;
        }
        grid.setItems(dataBaseService.getAllRequestsFromReceiver(maybeUser.get()));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
       updateGrid();
    }
}

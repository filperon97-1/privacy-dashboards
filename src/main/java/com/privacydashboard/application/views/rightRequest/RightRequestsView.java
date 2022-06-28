package com.privacydashboard.application.views.rightRequest;

import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@PageTitle("Rights")
@Route(value="rights_controller", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class RightRequestsView extends VerticalLayout {
    private final DataBaseService dataBaseService;
    private final AuthenticatedUser authenticatedUser;
    private final Grid<RightRequest> grid= new Grid<>();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public RightRequestsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        addClassName("grid-views");
        initializeGrid();
        updateGrid();
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
        // quando si seleziona una riga, chiama editContact
        //grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));
    }

    private void updateGrid(){
        Optional<User> maybeUser=this.authenticatedUser.get();
        if(maybeUser.isEmpty()){
            return;
        }
        grid.setItems(dataBaseService.getAllRequestsFromReceiver(maybeUser.get()));
    }
}

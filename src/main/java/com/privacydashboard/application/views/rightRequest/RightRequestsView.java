package com.privacydashboard.application.views.rightRequest;

import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.DataBaseService;
import com.privacydashboard.application.security.AuthenticatedUser;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@PageTitle("Rights")
@Route(value="rights_controller", layout = MainLayout.class)
@RolesAllowed({"CONTROLLER", "DPO"})
public class RightRequestsView extends VerticalLayout {
    private DataBaseService dataBaseService;
    private AuthenticatedUser authenticatedUser;
    private Grid<RightRequest> grid= new Grid<>();

    Logger logger = LoggerFactory.getLogger(getClass());
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public RightRequestsView(DataBaseService dataBaseService, AuthenticatedUser authenticatedUser) {
        this.dataBaseService = dataBaseService;
        this.authenticatedUser = authenticatedUser;
        addClassName("grid-views");
        initializeGrid();
        Optional<User> maybeUser=authenticatedUser.get();
        if(!maybeUser.isPresent()){
            return;
        }
        updateGrid(maybeUser.get());
    }

    private void initializeGrid(){
        grid.setSizeFull();
        grid.setHeight("100%");
        grid.setWidth("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addColumn(request -> request.getSender().getName()).setHeader("NAME");
        grid.addColumn(request -> request.getRightType().toString()).setHeader("RIGHT TYPE");
        grid.addColumn(request -> dtf.format(request.getTime()).toString()).setHeader("TIME");
        grid.addColumn(request -> request.getOther()).setHeader("DETAILS");
        grid.addColumn(request -> request.getHandled()).setHeader("HANDLED");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        add(grid);
        // quando si seleziona una riga, chiama editContact
        //grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));
    }

    private void updateGrid(User user){
        grid.setItems(dataBaseService.getAllRequestsFromReceiver(user));
    }
}

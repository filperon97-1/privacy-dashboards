package com.privacydashboard.application.views.rights;

import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.CommunicationService;
import com.privacydashboard.application.data.service.DataBaseService;
import com.vaadin.flow.component.grid.Grid;

public class RightsList{
    private final User user;
    private final DataBaseService dataBaseService;
    private final CommunicationService communicationService;
    public RightsList(User user, DataBaseService dataBaseService, CommunicationService communicationService){
        this.user=user;
        this.dataBaseService=dataBaseService;
        this.communicationService=communicationService;
    }

    public Grid getGrid(Boolean handled){
        return null;
    }
}

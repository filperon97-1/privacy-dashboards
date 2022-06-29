package com.privacydashboard.application.views.applyRights;

import com.privacydashboard.application.data.entity.RightRequest;
import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.views.MainLayout;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


import javax.annotation.security.RolesAllowed;

@PageTitle("Single Right")
@Route(value="single_right", layout = MainLayout.class)
@RolesAllowed("SUBJECT")
public class SingleRightView extends VerticalLayout {
    private ComboBox<User> controllers=new ComboBox<>();
    private TextArea textArea=new TextArea();

    public SingleRightView(){
        textArea.setValue("\n" +
                "Stop ai motori endotermici dal 2035, ok da CdM Ambiente Ue\n" +
                "Motori.\n" +
                "Il Consiglio dei ministri Ue dell'Ambiente ha annunciato nella notte di aver raggiunto l'intesa sul pacchetto di misure green 'Fit for 55'. Timmermans: 'Il futuro è elettrico, i carburanti sintetici non sembrano una soluzione realistica'");
        textArea.setReadOnly(true);
        /*controllers.
        initializeComboBox();
        try{
            Object object
        }
        initi*/
        add(textArea);
        Object object=ComponentUtil.getData(UI.getCurrent(), "RightRequest");
        RightRequest request=(RightRequest) object; // CATTURARE LE POSSIBILI ECCEZZIONI
        add(new H1(request.getApp().getName() + "  " + request.getSender().getName()));
        add(new H1("hola"));
    }


}

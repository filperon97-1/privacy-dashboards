package com.privacydashboard.application.views.usefulComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MyDialog extends Dialog {
    private String title="";
    private Header header=new Header();

    private HorizontalLayout content=new HorizontalLayout();

    private Button continueButton=new Button("Continue");
    private Button cancelButton=new Button("Cancel");
    private HorizontalLayout buttonLayout=new HorizontalLayout();

    public MyDialog(){
        initializeButtons();
        initializeHeader();
    }

    public MyDialog(String title){
        this.title=title;
        initializeHeader();
        add(content);
        initializeButtons();
    }

    private void initializeButtons(){
        continueButton.addClassNames("button");
        continueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancelButton.addClassNames("button");
        cancelButton.addClickListener(e->close());
        buttonLayout.add(continueButton, cancelButton);
        add(buttonLayout);
    }

    private void initializeHeader(){
        header.add(title);
        Button closeButton=new Button(VaadinIcon.CLOSE_SMALL.create());
        closeButton.addClickListener(e->close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        header.add(closeButton);
        add(header);
    }

    public void setContinueButton(Button button){
        this.continueButton=button;
    }

    public void setContent(HorizontalLayout content){
        this.content=content;
    }

    public void setTitle(String title){
        this.title=title;
    }
}

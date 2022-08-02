package com.privacydashboard.application.views.usefulComponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.Lumo;

public class MyDialog extends Dialog {
    private Span title=new Span();
    Button closeButton=new Button(VaadinIcon.CLOSE_SMALL.create(), e->close());
    private Header header=new Header();

    private Span content=new Span();

    private Button continueButton=new Button("Continue");
    private Button cancelButton=new Button("Cancel", e->close());
    private Footer footer= new Footer();

    public MyDialog(){
        getElement().getThemeList().add("my-dialog");
        initializeHeader();
        initializeContent();
        initializeButtons();
    }

    private void initializeHeader(){
        //getElement().setAttribute("aria-labelledby", "dialog-title");
        title.addClassName("dialog-title");
        closeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY);
        header.add(title);
        header.add(closeButton);
        add(header);
    }

    private void initializeContent(){
        content.addClassName("dialog-content");
        add(content);
    }

    private void initializeButtons(){
        continueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        footer.add(continueButton, cancelButton);
        add(footer);
    }

    public void setContinueButton(Button button){
        this.continueButton=button;
        continueButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        footer.removeAll();
        footer.add(continueButton, cancelButton);
    }

    public void setContent(HorizontalLayout content){
        this.content.removeAll();
        this.content.add(content);
    }

    public void setContent(VerticalLayout content){
        this.content.removeAll();
        this.content.add(content);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }
}

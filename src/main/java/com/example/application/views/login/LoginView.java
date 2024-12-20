package com.example.application.views.login;


import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login") 
@PageTitle("Daily-MOB Login")
@AnonymousAllowed 

public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm loginForm;

    public LoginView() {
        loginForm = new LoginForm();  
        setSizeFull();
        setAlignItems(Alignment.CENTER); 
        setJustifyContentMode(JustifyContentMode.CENTER); 

        loginForm.setAction("login");  
        loginForm.addClassName("login-form");
//        loginForm.getStyle()
//        .set("background-color", "#f4f4f9")
//        .set("padding", "20px")
//        .set("border-radius", "10px")
//        .set("border", "1px solid #ddd");
        Anchor registerLink = new Anchor("register", "New user? Get registered here.");
        registerLink.getStyle().set("margin-top", "10px"); // Add some spacing
        add(new H1("Daily MOB"), new Div("You can log in as 'alice', 'bob' or 'admin'. The password for all of them is 'password'."), loginForm,registerLink);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            loginForm.setError(true); 
        }
    }    
} 
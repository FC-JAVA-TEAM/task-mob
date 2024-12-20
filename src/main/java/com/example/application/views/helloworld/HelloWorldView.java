package com.example.application.views.helloworld;

import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Hello World")
@Route("hello-world")
@Menu(order = 1, icon = LineAwesomeIconUrl.GLOBE_SOLID)
//@RolesAllowed({ "ADMIN" })
@PermitAll
public class HelloWorldView extends HorizontalLayout{

	private TextField name;
	private Button sayHello;

	public HelloWorldView() {
		name = new TextField("Your name");
		sayHello = new Button("Say hello");
		sayHello.addClickListener(e -> {
			Notification.show("Hello " + name.getValue());
		});
		sayHello.addClickShortcut(Key.ENTER);

		setMargin(true);
		setVerticalComponentAlignment(Alignment.END, name, sayHello);

		add(name, sayHello);
	}
	
}

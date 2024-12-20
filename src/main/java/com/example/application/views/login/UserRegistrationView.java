package com.example.application.views.login;

import java.util.HashSet;
import java.util.Set;

import com.example.application.service.MobUserUserDetailsService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("register") // The route for the registration page
@PageTitle("User Registration")
@AnonymousAllowed
public class UserRegistrationView extends VerticalLayout {

	
	private final MobUserUserDetailsService service;
    // Fields for the form
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private Set<String> roles;

    public UserRegistrationView(MobUserUserDetailsService service) {
        this.service = service;
		// Initialize fields
        usernameField = new TextField("Username");
        passwordField = new PasswordField("Password");
        roleComboBox = new ComboBox<>("Roles");

        // Define available roles (this could be dynamic or fetched from a database)
        roleComboBox.setItems("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR","ROLE_GUEST");
        
        // Set default role to "User"
        roleComboBox.setValue("User");

        // Initialize the roles set
        roles = new HashSet<>();

        // Button for form submission
        Button submitButton = new Button("Register", event -> registerUser());

        // Add components to the layout
        add(new H1("Register New User"),
            new Text("Please provide your details below:"),
            createFormLayout(),
            submitButton
        );
    }

    // Create a form layout with the necessary fields
    private FormLayout createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, passwordField, roleComboBox);
        return formLayout;
    }

    // Handle user registration
    private void registerUser() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String selectedRole = roleComboBox.getValue();
        Set<String> role = new HashSet<>();
        role.add(selectedRole);
        // Add selected role to roles set
        roles.add(selectedRole);
        
        service.registerUser(username, password, role);

        // Print out the registration data (You can add logic to save this data to the database)
        System.out.println("User Registered with Username: " + username);
        System.out.println("Password: " + password); // Ensure password is stored securely
        System.out.println("Roles: " + roles);
        UI.getCurrent().navigate("login");
    }
}

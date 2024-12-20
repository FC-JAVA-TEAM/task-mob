package com.example.application.views;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@Layout
@CssImport("./styles/styles.css")
//@PermitAll
@AnonymousAllowed

public class MainLayout extends AppLayout  implements BeforeEnterObserver{

	private H1 viewTitle;
	private Avatar userAvatar;
	private HorizontalLayout headerLayout;

	@Autowired
	AuthenticationContext authenticationContext;

	 @Override
	    public void beforeEnter(BeforeEnterEvent event) {
	        // Check if the user is authenticated before allowing access to the page
	        if (SecurityContextHolder.getContext().getAuthentication() == null ||
	            !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
	            event.rerouteTo("login"); // Redirect to login if the user is not authenticated
	        }
	    }
	public MainLayout() {
		setPrimarySection(Section.DRAWER);
		addDrawerContent();
		addHeaderContent();
	}
	 private void setTheme(boolean dark) {
	        var js = "document.documentElement.setAttribute('theme', $0)";

	        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
	    }
	private void addHeaderContent() {
		
		
		
		
		
		DrawerToggle toggle = new DrawerToggle();
		toggle.setAriaLabel("Menu toggle");

		viewTitle = new H1();
		viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

		userAvatar = createUserAvatar();

		headerLayout = new HorizontalLayout(toggle, viewTitle);
		headerLayout.setAlignItems(Alignment.CENTER);
		headerLayout.setWidthFull();
		headerLayout.expand(viewTitle);

		if (SecurityContextHolder.getContext().getAuthentication() != null
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			// Add avatar to the header

			
			if (!username.equals("anonymousUser")) {
				VerticalLayout l = new VerticalLayout();
				  l.setWidth("5%");
				
				l.add(userAvatar);
				l.add(new Span(username));
				headerLayout.add(l);
				addToNavbar(true, headerLayout);
			}

		}

	}

	private Avatar createUserAvatar() {

		// Create the avatar with a default name (you can dynamically set this from
		// logged-in user)
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Avatar avatar = new Avatar("U");
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			avatar.setName(username);
		}

		avatar.addClassNames("avatar");
		avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);

		// avatar.setSize("40px");

		// Create ContextMenu for avatar
		ContextMenu contextMenu = new ContextMenu(avatar);
		contextMenu.setOpenOnClick(true);

		// Add menu items: Profile, Update Password, Logout
		MenuItem profileItem = contextMenu.addItem("Update Profile", event -> showProfileDialog());
		MenuItem passwordItem = contextMenu.addItem("Update Password", event -> showChangePasswordDialog());
		// MenuItem logoutItem = contextMenu.addItem("Logout", event -> logout());

		logout(contextMenu);

		return avatar;
	}

	private void logout(ContextMenu contextMenu) {
		MenuItem logoutItem = contextMenu.addItem("Logout", event -> {
			authenticationContext.logout();
			
		//	headerLayout.remove(userAvatar);
			// VaadinSession.getCurrent().getSession().invalidate();

			// VaadinSession vaadinSession = VaadinSession.getCurrent();
			if (VaadinSession.getCurrent() != null && VaadinSession.getCurrent().getSession() != null) {
				VaadinSession.getCurrent().getSession().invalidate(); // Invalidate the session
			}
			getUI().ifPresent(ui -> ui.navigate("login")); // Navigate to the login view
		});
	}

	private void showProfileDialog() {
		// You can show a dialog to update the profile here
		Dialog profileDialog = new Dialog();
		profileDialog.add("Profile Update Form"); // Add your profile update form here
		profileDialog.open();
	}

	private void showChangePasswordDialog() {
		// Show a dialog with password change form
		Dialog passwordDialog = new Dialog();

		FormLayout formLayout = new FormLayout();
		PasswordField oldPassword = new PasswordField("Old Password");
		PasswordField newPassword = new PasswordField("New Password");
		PasswordField confirmPassword = new PasswordField("Confirm Password");

		Button updateButton = new Button("Update Password", e -> {
			// Add your password update logic here
			passwordDialog.close();
		});

		formLayout.add(oldPassword, newPassword, confirmPassword, updateButton);
		passwordDialog.add(formLayout);
		passwordDialog.open();
	}

	private void addDrawerContent() {
		 var themeToggle = new Checkbox("Dark theme");
	        themeToggle.addValueChangeListener(e -> {
	            setTheme(e.getValue());
	        });

		Span appName = new Span("Daily-MOB");
		appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
		Header header = new Header(appName);

		Scroller scroller = new Scroller(createNavigation());

		addToDrawer(header, scroller, createFooter(),themeToggle);
	}

	private SideNav createNavigation() {
		SideNav nav = new SideNav();

		List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
		menuEntries.forEach(entry -> {
			if (entry.icon() != null) {
				nav.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
			} else {
				nav.addItem(new SideNavItem(entry.title(), entry.path()));
			}
		});

		return nav;
	}

	private Footer createFooter() {
		Footer layout = new Footer();

		return layout;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		viewTitle.setText(getCurrentPageTitle());
	}

	private String getCurrentPageTitle() {
		return MenuConfiguration.getPageHeader(getContent()).orElse("");
	}
}

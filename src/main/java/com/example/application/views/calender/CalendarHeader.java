package com.example.application.views.calender;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import jakarta.annotation.security.PermitAll;

@CssImport("./styles/styles.css")
@PermitAll
public class CalendarHeader extends VerticalLayout {
	
	
	private Span monthYearLabel;
	public CalendarHeader(Runnable onPrevMonth, Runnable onNextMonth, Runnable onAddActivity, Runnable onViewActivities,
			Runnable onAddDailyNote, Runnable onViewDailyNotes) {
		monthYearLabel = new Span();
		getStyle().set("padding", "0");
		// Navigation Buttons
		Button prevMonthButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
		prevMonthButton.setTooltipText("Previous Month");
		prevMonthButton.addClickListener(e -> onPrevMonth.run());

		Button nextMonthButton = new Button(new Icon(VaadinIcon.ARROW_RIGHT));
		nextMonthButton.setTooltipText("Next Month");
		nextMonthButton.addClickListener(e -> onNextMonth.run());

		// Create MenuBar
		MenuBar menuBar = new MenuBar();
		menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

		// Activities Menu
		MenuItem activitiesMenu = createIconItem(menuBar, VaadinIcon.LIST, "Action", null);
		SubMenu activitiesSubMenu = activitiesMenu.getSubMenu();

		MenuItem activityItem = activitiesSubMenu.addItem("Activity");

		SubMenu activitySubMenu = activityItem.getSubMenu();

		createIconItem(activitySubMenu, VaadinIcon.PLUS_CIRCLE, "Add Activity", e -> {
			onAddActivity.run();
			// Notification.show("Adding Activity...", 3000, Notification.Position.MIDDLE);
		});
//        
		createIconItem(activitySubMenu, VaadinIcon.TRASH, "Delete Activities", e -> {
			onViewActivities.run();
			// Notification.show("Viewing Activities...", 3000,
			// Notification.Position.MIDDLE);
		});

		MenuItem mayDay = activitiesSubMenu.addItem("MayDay");

		SubMenu maySubMenu = mayDay.getSubMenu();

//        createIconItem(maySubMenu, VaadinIcon.DATABASE, "May Day", e -> {
//        	onMayDaySummary.run();
//           // Notification.show("Adding Activity...", 3000, Notification.Position.MIDDLE);
//        });
//        
//        createIconItem(maySubMenu, VaadinIcon.CHART_GRID, "Namaz", e -> {
//            onViewActivities.run();
//          //  Notification.show("Viewing Activities...", 3000, Notification.Position.MIDDLE);
//        });

		MenuItem dailyNote = activitiesSubMenu.addItem("Daily Note");

		SubMenu dailyNoteSubMenu = dailyNote.getSubMenu();

		createIconItem(dailyNoteSubMenu, VaadinIcon.OPEN_BOOK, "Add Daily Note", e -> {
			onAddDailyNote.run();
//           // Notification.show("Adding Activity...", 3000, Notification.Position.MIDDLE);
		});
//        
		createIconItem(dailyNoteSubMenu, VaadinIcon.TRASH, "View Daily Note", e -> {
			onViewDailyNotes.run();
//          //  Notification.show("Viewing Activities...", 3000, Notification.Position.MIDDLE);
		});
//        createIconItem(dailyNoteSubMenu, VaadinIcon.TRASH, "Delete Daily Note", e -> {
//            onViewActivities.run();
//          //  Notification.show("Viewing Activities...", 3000, Notification.Position.MIDDLE);
//        });

		// Header Layout
		HorizontalLayout headerLayout = new HorizontalLayout(prevMonthButton, monthYearLabel, nextMonthButton);
		headerLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		headerLayout.setWidthFull();
		headerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		headerLayout.getStyle().set("margin-bottom", "10px");

		// Combine Header and MenuBar
		HorizontalLayout combinedLayout = new HorizontalLayout(headerLayout, menuBar);
		combinedLayout.setWidthFull();
		combinedLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

		add(combinedLayout); // Add the combined layout to the header
	}

	// Helper method to create menu items with icons
	private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label,
			ComponentEventListener<ClickEvent<MenuItem>> listener) {
		Icon icon = new Icon(iconName);
		MenuItem item = menu.addItem(icon, listener);
		if (label != null) {
			item.add(new Text(label));
		}
		return item;
	}

	// Overloaded method for SubMenu without listener
	private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, Object unused) {
		return createIconItem(menu, iconName, label, null);
	}

	public void updateMonthYearLabel(LocalDate currentMonth) {
		String monthYear = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " "
				+ currentMonth.getYear();
		monthYearLabel.setText(monthYear);
	}
}

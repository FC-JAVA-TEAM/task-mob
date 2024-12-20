package com.example.application.views.calender;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.application.entity.DailyNote;
import com.example.application.entity.MobUser;
import com.example.application.service.DailyNoteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;

import jakarta.annotation.security.PermitAll;

@PermitAll
public class DailyNoteForm {

	private final DailyNoteService dailyNoteService;

	private final Runnable onActivityUpdate;

	public DailyNoteForm(DailyNoteService dailyNoteService, Runnable on) {
		this.dailyNoteService = dailyNoteService;
		this.onActivityUpdate = on;

	}

	public DailyNoteForm(DailyNoteService dailyNoteService) {
		this.dailyNoteService = dailyNoteService;
		this.onActivityUpdate = null;

	}

	// Method to open the dialog for adding a daily note
	public void openAddDailyNoteDialog(LocalDate date) {
		Dialog dialog = new Dialog();

		dialog.getElement().getStyle().set("animation", "fadeIn 0.3s ease-out").set("border-radius", "10px")
				.set("box-shadow", "0px 4px 20px rgba(0, 0, 0, 0.1)").set("background-color", "#f9f9f9");

// Inject CSS for fadeIn animation
		dialog.getElement().getNode().runWhenAttached(ui -> {
			ui.getPage().executeJs("const style = document.createElement('style');"
					+ "style.innerHTML = '@keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); }}';"
					+ "document.head.appendChild(style);");
		});

		HorizontalLayout titleLayout = new HorizontalLayout();
		Icon addNoteIcon = VaadinIcon.FILE_TEXT_O.create();
		addNoteIcon.getStyle().set("color", "#FF9800"); // Orange color for the icon
		Span titleLabel = new Span("Add Daily Note for: " + date);
		titleLabel.getStyle().set("font-size", "24px").set("font-weight", "bold").set("color", "white");

		titleLayout.add(addNoteIcon, titleLabel);
		titleLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		titleLayout.setWidthFull();
		titleLayout.setSpacing(false);
		titleLayout.getStyle().set("background", "linear-gradient(90deg, #1e3c72, #2a5298)").set("padding", "15px")
				.set("border-top-left-radius", "10px").set("border-top-right-radius", "10px");

		FormLayout formLayout = new FormLayout();
		formLayout.setWidthFull();
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

		// TextArea for Daily Note Content
		TextArea noteContentArea = new TextArea("Today's Experience");
		noteContentArea.setPlaceholder("Share your full-day experiences, reflections, or observations...");
		noteContentArea.setWidthFull();
		noteContentArea.setHeight("300px");
		noteContentArea.getStyle().set("font-size", "16px").set("color", "#333");
		Button saveButton = new Button("Save", event -> {
			String content = noteContentArea.getValue();
			if (content == null || content.trim().isEmpty()) {
				Notification.show("Daily note cannot be empty", 3000, Notification.Position.MIDDLE);
			} else {
				try {
					DailyNote dailyNote = new DailyNote();
					dailyNote.setDate(date);
					dailyNote.setContent(content);
					dailyNote.setDone(false); // Default to not done

					Optional<MobUser> checkUser = dailyNoteService
							.checkUser(dailyNoteService.getAuthenticatedUsername().getUserId());
					// UserEntity userEntity = userRepository.findById(checkUser.get()).get();

					dailyNote.setUser(checkUser.get());
					dailyNoteService.save(dailyNote);
					dialog.close();
					// displayMonthCalendar(date);
					Notification.show("Daily note added successfully!", 3000, Notification.Position.TOP_CENTER);
				} catch (Exception e) {
					Notification.show("An error occurred while saving the daily note", 3000,
							Notification.Position.MIDDLE);
					e.printStackTrace();
				}
			}
		});
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.getStyle().set("background-color", "#4CAF50") // Green for success
				.set("color", "white").set("border-radius", "8px").set("width", "100%").set("margin-top", "20px");

		// Cancel Button with styling
		Button cancelButton = new Button("Cancel", event -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		cancelButton.getStyle().set("background-color", "#F44336") // Red for cancel
				.set("color", "white").set("border-radius", "8px").set("width", "100%");

		// Buttons Layout
		VerticalLayout buttonsLayout = new VerticalLayout(saveButton, cancelButton);
		buttonsLayout.setSpacing(true);
		buttonsLayout.setPadding(false);
		buttonsLayout.setWidthFull();
		buttonsLayout.setAlignItems(Alignment.STRETCH); // Align buttons to stretch the full width

		// Dialog Layout
		VerticalLayout dialogLayout = new VerticalLayout(titleLayout, formLayout, buttonsLayout);
		dialogLayout.setPadding(true);
		dialogLayout.setSpacing(true);
		dialogLayout.setAlignItems(Alignment.STRETCH); // Align content to stretch

		// Add components to dialog
		formLayout.add(noteContentArea);
		dialog.add(dialogLayout);
		dialog.open();

	}

	public void openViewDailyNotesDialog(LocalDate date) {
		Dialog activitiesDialog = new Dialog();
		activitiesDialog.setWidth("700px");
		activitiesDialog.setHeight("550px");
		activitiesDialog.setCloseOnEsc(true);
		activitiesDialog.setCloseOnOutsideClick(true);

		HorizontalLayout titleLayout = new HorizontalLayout();
		Icon activitiesIcon = VaadinIcon.CALENDAR.create();
		Span titleLabel = new Span("Review My Day for: " + date);
		titleLabel.getStyle().set("font-size", "24px").set("font-weight", "bold").set("color", "white");

		titleLayout.add(activitiesIcon, titleLabel);
		titleLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		titleLayout.setWidthFull();
		titleLayout.setSpacing(true);
		titleLayout.getStyle().set("background", "linear-gradient(90deg, #1e3c72, #2a5298)") // Gradient background
				.set("padding", "15px").set("border-top-left-radius", "10px").set("border-top-right-radius", "10px");

		// List<DailyNote> activities = dailyNoteService.findByDate(date);
		List<DailyNote> activities = dailyNoteService.findByDateAndUserId(date,
				dailyNoteService.getAuthenticatedUsername().getUserId());
		// List<DailyNoteXX> activities = dailyNoteService.findByMonth(date);
		VerticalLayout activitiesLayout = new VerticalLayout();
		activitiesLayout.setPadding(false);
		activitiesLayout.setSpacing(true);
		activitiesLayout.setWidthFull();

		if (activities != null && !activities.isEmpty()) {
			for (DailyNote activity : activities) {
				HorizontalLayout activityItemLayout = new HorizontalLayout();
				activityItemLayout.setWidthFull();
				activityItemLayout.setPadding(true);
				activityItemLayout.getStyle().set("border-bottom", "1px solid #ddd").set("background-color", "#ffffff")
						.set("border-radius", "8px").set("margin-bottom", "10px")
						.set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

				Span activityLabel = new Span(activity.getContent());
				activityLabel.getStyle().set("font-size", "16px").set("color", "#333");
				activityLabel.setWidthFull();

				Button editButton = new Button(new Icon(VaadinIcon.EDIT));
				editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				editButton.getStyle().set("color", "#1976D2");
				editButton.addClickListener(event -> {
					activitiesDialog.close();
					openEditDailyNoteDialog(activity, null);
				});

				Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
				deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
				deleteButton.getStyle().set("color", "#1976D2");
				deleteButton.addClickListener(event -> {
					dailyNoteService.delete(activity);
					activities.remove(activity); // Remove the deleted activity from the list
					activitiesLayout.remove(activityItemLayout); // Remove the UI component
					Notification.show("Daily activity deleted", 3000, Notification.Position.BOTTOM_START);
				});

				activityItemLayout.add(activityLabel, editButton, deleteButton);
				activitiesLayout.add(activityItemLayout);
			}
		} else {
			Span noActivitiesLabel = new Span("No activities for this date.");
			noActivitiesLabel.getStyle().set("font-size", "16px").set("color", "#777");
			activitiesLayout.add(noActivitiesLabel);
		}

		Button backButton = new Button("Back", event -> activitiesDialog.close());
		backButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		backButton.getStyle().set("margin-top", "20px").set("width", "100%").set("background-color", "#4CAF50")
				.set("color", "white").set("border-radius", "8px");

		VerticalLayout dialogLayout = new VerticalLayout(titleLayout, activitiesLayout, backButton);
		dialogLayout.setPadding(true);
		dialogLayout.setSpacing(true);
		dialogLayout.setAlignItems(Alignment.STRETCH);
		dialogLayout.getStyle().set("padding", "20px");

		activitiesDialog.add(dialogLayout);
		activitiesDialog.open();
	}

	public void openEditDailyNoteDialog(DailyNote dailyNote, Runnable onUpdate) {
		Dialog dailyNoteDialog = new Dialog();

		// Adjust dialog dimensions
		dailyNoteDialog.setWidth("700px");
		dailyNoteDialog.setHeight("550px"); // Reduced height for a more compact look

		// Dialog animation (fade-in effect)
		dailyNoteDialog.getElement().getStyle().set("animation", "fadeIn 0.3s ease-out").set("border-radius", "10px")
				.set("box-shadow", "0px 4px 20px rgba(0, 0, 0, 0.1)").set("background-color", "#f9f9f9");

		// Inject CSS for fadeIn animation
		dailyNoteDialog.getElement().getNode().runWhenAttached(ui -> {
			ui.getPage().executeJs("const style = document.createElement('style');"
					+ "style.innerHTML = '@keyframes fadeIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); }}';"
					+ "document.head.appendChild(style);");
		});

		// Header with gradient background and adjusted alignment
		Span titleLabel = new Span("Edit Review May Day");
		titleLabel.getStyle().set("font-size", "10px").set("font-weight", "bold").set("color", "white")
				.set("padding", "15px").set("background", "linear-gradient(90deg, #1e3c72, #2a5298)")
				.set("border-top-left-radius", "10px").set("border-top-right-radius", "10px")
				.set("text-align", "center").set("margin-left", "21px").set("width", "100%");

		// Align header and editor
		int charLimit = 2000;
		FormLayout noteFormLayout = new FormLayout();
		TextArea noteDescription = new TextArea("Today's Experience");
		noteDescription.setPlaceholder("Share your full-day experiences, reflections, or observations...");
		noteDescription.setWidthFull();
		noteDescription.setMaxLength(charLimit);

		noteDescription.setValueChangeMode(ValueChangeMode.EAGER);
		noteDescription.addValueChangeListener(e -> {

			e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
		});
		noteDescription.setHeight("300px"); // Adjusted height
		noteDescription.getStyle().set("font-size", "16px").set("color", "#333").set("border-radius", "8px")
				.set("border", "1px solid #ccc").set("padding", "10px");

		noteFormLayout.add(noteDescription);
		noteFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
		noteDescription.setValue(dailyNote.getContent());

		// Save button with icon and new styling
		Button saveNote = new Button("Save", new Icon(VaadinIcon.CHECK), event -> {
			if (noteDescription.isEmpty() || noteDescription.getValue().trim().isEmpty()) {
				Notification.show("Activity description cannot be empty");
			} else {
				try {
					// activity.setDescription(activityDescription);
					dailyNote.setContent(noteDescription.getValue());
					Optional<MobUser> checkUser = dailyNoteService
							.checkUser(dailyNoteService.getAuthenticatedUsername().getUserId());
					// UserEntity userEntity = userRepository.findById(checkUser.get()).get();

					dailyNote.setUser(checkUser.get());
					dailyNoteService.save(dailyNote);

					// activity.setUser(userEntity);
					// dailyNoteService.save(activity);
					dailyNoteDialog.close();
					if (Objects.nonNull(onUpdate)) {
						onUpdate.run();
					}
					Notification.show("Daily activity updated");
				} catch (Exception e) {
					Notification.show("An error occurred while updating the daily activity");
					e.printStackTrace();
				}
			}
		});
		saveNote.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveNote.getStyle().set("background-color", "#4CAF50").set("color", "white").set("border-radius", "8px");

		// Delete button with icon and new styling
		Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH), event -> {
			try {
				dailyNoteService.delete(dailyNote);
				dailyNoteDialog.close();
				onUpdate.run();
				Notification.show("Daily activity deleted");
			} catch (Exception e) {
				Notification.show("An error occurred while deleting the daily activity");
				e.printStackTrace();
			}
		});
		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		deleteButton.getStyle().set("background-color", "#F44336").set("color", "white").set("border-radius", "8px");

		// Cancel button with icon and new styling
		Button cancelButton = new Button("Cancel", new Icon(VaadinIcon.CLOSE), e -> dailyNoteDialog.close());
		cancelButton.getStyle().set("background-color", "#808080").set("color", "white").set("border-radius", "8px");

		// Align buttons in the center
		HorizontalLayout buttonsLayout = new HorizontalLayout(saveNote, deleteButton, cancelButton);
		buttonsLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Center the buttons
		buttonsLayout.setWidthFull();
		buttonsLayout.getStyle().set("padding-top", "20px").set("padding-bottom", "10px");

		// Main layout with better spacing and alignment
		VerticalLayout dialogLayout = new VerticalLayout(titleLabel, noteFormLayout, buttonsLayout);
		dialogLayout.setPadding(true);
		dialogLayout.setSpacing(true);
		dialogLayout.setAlignItems(Alignment.CENTER); // Center-align content
		dialogLayout.getStyle().set("padding", "20px");

		dailyNoteDialog.add(dialogLayout);
		dailyNoteDialog.open();
	}

}

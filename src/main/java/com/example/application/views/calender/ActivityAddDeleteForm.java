package com.example.application.views.calender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.application.entity.DailyActivity;
import com.example.application.entity.MobUser;
import com.example.application.service.DailyActivityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import jakarta.annotation.security.PermitAll;

@PermitAll
public class ActivityAddDeleteForm {

	private final Runnable onActivityUpdate;
	private final DailyActivityService dailyActivityService;

	public ActivityAddDeleteForm(Runnable onActivityUpdate, DailyActivityService dailyActivityService) {
		this.onActivityUpdate = onActivityUpdate;
		this.dailyActivityService = dailyActivityService;

	}

	public void openDeleteDailyActivityDialog() {
		Dialog dialog = new Dialog();
		dialog.setWidth("400px");
		dialog.setWidth("400px");
		FormLayout formLayout = new FormLayout();
		// Date Pickers for selecting the start and end dates
		DatePicker startDatePicker = new DatePicker("Start May Activity From");
		DatePicker endDatePicker = new DatePicker("End May Activity To");
		startDatePicker.setHelperText("Don't Forget to complet!!");

		startDatePicker.setMin(LocalDate.now()); // Start from today
		endDatePicker.setMax(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

		// Layout for existing activities
		VerticalLayout activityCheckboxLayout = new VerticalLayout();
		List<DailyActivity> existingActivities = dailyActivityService
				.findByUserId(dailyActivityService.getAuthenticatedUsername().getUserId());

		// Set to keep track of unique activity descriptions
		Set<String> uniqueActivities = new HashSet<>();

		// Add checkboxes for each unique existing activity
		for (DailyActivity activity : existingActivities) {
			if (uniqueActivities.add(activity.getDescription().toLowerCase())) {
				Checkbox activityCheckbox = new Checkbox(activity.getDescription());
				activityCheckbox.setValue(false); // Initially unchecked
				activityCheckbox.setLabel(activity.getDescription());
				activityCheckboxLayout.add(activityCheckbox);
			}
		}

		// Button for deleting selected activities
		Button deleteButton = new Button("Delete Selected", event -> {
			LocalDate startDate = startDatePicker.getValue();
			LocalDate endDate = endDatePicker.getValue();
			boolean activityDeleted = false;

			if (startDate != null && endDate != null) {
				for (Checkbox checkbox : activityCheckboxLayout.getChildren().map(Checkbox.class::cast).toList()) {
					if (checkbox.getValue()) {
						String description = checkbox.getLabel();
						// Delete the activities for the selected date range
						dailyActivityService.deleteActivitiesInRange(description, startDate, endDate);
						activityDeleted = true;
					}
				}
			} else {
				Notification.show("Please select both start and end dates", 3000, Notification.Position.MIDDLE);
			}

			if (activityDeleted) {
				Notification.show("Selected activities deleted successfully", 3000, Notification.Position.BOTTOM_START);
			} else {
				Notification.show("No activities were deleted", 3000, Notification.Position.BOTTOM_START);
			}

			dialog.close();
			onActivityUpdate.run();
		});

		// Button for cancelling the operation
		Button cancelButton = new Button("Cancel", event -> dialog.close());

		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("600px", 2));
		formLayout.add(startDatePicker, endDatePicker, activityCheckboxLayout, deleteButton, cancelButton);
		dialog.add(formLayout);
		dialog.open();
		dialog.setModal(false);
		dialog.setDraggable(true);
		dialog.setResizable(true);
	}

	public void openAddDailyActivityDialog() {
		// Create the dialog
		Dialog dialog = new Dialog();
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);
		FormLayout formLayout = new FormLayout();
		// Create a form layout for the form inside the dialog

		Set<String> existingActivitySet = new HashSet<>();
		Set<String> uniqueActivities = new HashSet<>();

		// Fetch existing activities from the database
		List<DailyActivity> existingActivities = dailyActivityService
				.findByUserId(dailyActivityService.getAuthenticatedUsername().getUserId());

		// Create checkboxes for unique existing activities
		for (DailyActivity activity : existingActivities) {
			String activityDescription = activity.getDescription().toLowerCase(); // To handle case sensitivity
			if (!uniqueActivities.contains(activityDescription)) {
				uniqueActivities.add(activityDescription); // Add to unique activities set
				Checkbox activityCheckbox = new Checkbox(activity.getDescription());
				activityCheckbox.addValueChangeListener(event -> {
					if (event.getValue()) {
						existingActivitySet.add(activityDescription); // Add to selected set
					} else {
						existingActivitySet.remove(activityDescription); // Remove from selected set
					}
				});
				formLayout.add(activityCheckbox);
			}
		}

		TextField newActivityField = new TextField("Add New Activity (optional)");
		formLayout.add(newActivityField);

		DatePicker startDatePicker = new DatePicker("Start May Activity From");
		DatePicker endDatePicker = new DatePicker("End May Activity To");
		startDatePicker.setHelperText("Don't Forget to complet!!");
		// DatePickers for selecting start and end dates
		startDatePicker.setMin(LocalDate.now()); // Start from today
		formLayout.add(startDatePicker);

		endDatePicker.setMin(LocalDate.now()); // Start from today
		endDatePicker.setMax(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())); // End of current month
		formLayout.add(endDatePicker);

		// Buttons
		Button saveButton = new Button("Save", event -> {
			LocalDate startDate = startDatePicker.getValue();
			LocalDate endDate = endDatePicker.getValue();

			if (startDate == null || endDate == null) {
				Notification.show("Please select both start and end dates", 3000, Notification.Position.MIDDLE);
				return;
			}

			if (endDate.isBefore(startDate)) {
				Notification.show("End date must be after start date", 3000, Notification.Position.MIDDLE);
				return;
			}

			boolean activityAdded = false;
			Optional<MobUser> checkUser = dailyActivityService
					.checkUser(dailyActivityService.getAuthenticatedUsername().getUserId());
			// Save the selected existing activities
			for (String activity : existingActivitySet) {
				boolean isActivityAdded = saveActivityForSelectedDays(startDate, endDate, activity, checkUser);
				if (isActivityAdded) {
					activityAdded = true;
				} else {
					Notification.show("Activity '" + activity + "' already exists for the selected dates.", 3000,
							Notification.Position.MIDDLE);
				}
			}

			// Check and save the new activity if it's not empty and unique
			String newActivity = newActivityField.getValue().trim();
			if (!newActivity.isEmpty()) {
				String newActivityLower = newActivity.toLowerCase(); // Convert to lower case
				if (!uniqueActivities.contains(newActivityLower)) { // Check for uniqueness
					saveActivityForSelectedDays(startDate, endDate, newActivity, checkUser);
					uniqueActivities.add(newActivityLower); // Add to the existing set
					activityAdded = true;
				} else {
					Notification.show("Activity '" + newActivity + "' already exists.", 3000,
							Notification.Position.MIDDLE);
				}
			}

			if (activityAdded) {
				dialog.close();
				Notification.show("Activities added successfully", 3000, Notification.Position.BOTTOM_START);
				onActivityUpdate.run(); // Refresh the calendar
			} else {
				Notification.show("No new activities were added.", 3000, Notification.Position.BOTTOM_START);
			}
		});
		Button cancelButton = new Button("Cancel", event -> dialog.close());
		// Add fields to the form
		formLayout.add(saveButton, cancelButton);

		// Style the dialog
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("600px", 2));

		dialog.open();
		dialog.add(formLayout);
		dialog.setWidth("500px");
		dialog.setHeight("auto");

		// Open the dialog
		dialog.open();
		dialog.setModal(false);
		dialog.setDraggable(true);
		dialog.setResizable(true);
	}

	private boolean saveActivityForSelectedDays(LocalDate startDate, LocalDate endDate, String activityDescription,
			Optional<MobUser> checkUser) {
		boolean isActivityAdded = false;

		// Save the activity for all days from startDate to endDate
		List<DailyActivity> byDateBetweenWithDescription = dailyActivityService
				.findByDateBetweenWithDescription(startDate, endDate, activityDescription);

		Set<LocalDate> existingDates = byDateBetweenWithDescription.stream().map(DailyActivity::getDate)
				.collect(Collectors.toSet());

		List<DailyActivity> newActivities = new ArrayList<>();
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {

			if (!existingDates.contains(date)) {
				DailyActivity activity = new DailyActivity();
				activity.setDate(date);
				activity.setDescription(activityDescription);
				activity.setComplete(false); // Set default to false
				activity.setUser(checkUser.get());
				newActivities.add(activity);
			}
		}
		if (!newActivities.isEmpty()) {
			dailyActivityService.saveAllActivity(newActivities);
			isActivityAdded = true; // At least one activity was added
		}
		return isActivityAdded; // Return true if any activity was added
	}
}

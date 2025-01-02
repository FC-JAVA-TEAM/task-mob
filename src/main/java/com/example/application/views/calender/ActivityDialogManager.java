package com.example.application.views.calender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.application.entity.DailyActivity;
import com.example.application.entity.DailyMoodEntity;
import com.example.application.entity.DailyNote;
import com.example.application.entity.MobUser;
import com.example.application.entity.NamajEntry;
import com.example.application.enums.Mood;
import com.example.application.service.DailyActivityService;
import com.example.application.service.DailyMoodService;
import com.example.application.service.DailyNamajService;
import com.example.application.service.DailyNoteService;
import com.example.application.service.MobUserDetails;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import jakarta.annotation.security.PermitAll;

@PermitAll
public class ActivityDialogManager {

	private final Runnable onActivityUpdate;
	private final DailyMoodService dailyMoodService;
	private final DailyActivityService dailyActivityService;
	private final DailyNoteService dailyNoteService;
	private final DailyNamajService dailyNamajService;

	public ActivityDialogManager(Runnable onActivityUpdate, DailyMoodService dailyMoodService,
			DailyActivityService dailyActivityService, DailyNoteService dailyNoteService,
			DailyNamajService dailyNamajService) {
		this.onActivityUpdate = onActivityUpdate;
		this.dailyMoodService = dailyMoodService;
		this.dailyActivityService = dailyActivityService;
		this.dailyNoteService = dailyNoteService;
		this.dailyNamajService = dailyNamajService;
	}

	// Method to open the dialog for adding daily activities starting from today

	public void openUpdateActivityDialogWindow(LocalDate date) {
		Dialog dialog = new Dialog();
		dialog.setWidth("auto");
		dialog.setHeight("auto");

		LocalDate currentDate = LocalDate.now();

		// Ensure the date is today's date
		if (!date.isEqual(currentDate)) {
			Notification.show("You can only update activities for today.", 3000, Notification.Position.MIDDLE);
			return;
		}

		// Custom header layout
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setWidthFull();
		headerLayout.setPadding(true);
		headerLayout.getStyle().set("background-color", "#f1f3f4") // Light grey background
				.set("padding", "10px").set("font-size", "18px").set("color", "#333") // Dark text for contrast
				.set("border-bottom", "1px solid #ddd");

		// Add header title
		Span headerTitle = new Span("Update Daily Activity, Mood, and Namaj Tracker");
		headerTitle.getStyle().set("font-weight", "bold");

		dialog.setHeaderTitle("Add Daily Activity, Mood, and Diat ");

		// Create TabSheet with tabs for Activity, Mood, and Namaj Tracker
		TabSheet tabSheet = new TabSheet();
		tabSheet.getElement().getStyle().set("border-bottom", "1px solid #ddd").set("padding", "8px 0");

		// Tabs
		Tab activityTab = new Tab(" MY ACTIVITY");
		Tab moodTab = new Tab("MY  MOOD");
		Tab namajTab = new Tab("MY DIAT ");
		Tab dailyViewTab = new Tab("MY DAILY NOTE");

		// Layouts for each tab
		Button saveButton = new Button();
		VerticalLayout activityLayout = createActivityTab(date, dialog);
		VerticalLayout moodLayout = createMoodTab(date, dialog, saveButton);
		VerticalLayout namajLayout = createNamajTab(date, dialog, saveButton);
		VerticalLayout dailyViewLayout = createDailyViewTab(date, dialog);

		// Add the tabs and corresponding layouts to the TabSheet
		tabSheet.add(activityTab, activityLayout);
		tabSheet.add(moodTab, moodLayout);
		 tabSheet.add(namajTab, new Span("TO DO..."));
		//tabSheet.add(namajTab, namajLayout);
		tabSheet.add(dailyViewTab, dailyViewLayout);

		// Add the TabSheet to the dialog
		dialog.add(tabSheet);

		dialog.open();
	}

	private VerticalLayout createActivityTab(LocalDate date, Dialog dialog) {
		VerticalLayout activityLayout = new VerticalLayout();
		FormLayout activityFormLayout = new FormLayout();

		// Fetch existing activities for the selected date
		List<DailyActivity> activities = dailyActivityService.findByDateAndUserId(date,
				dailyActivityService.getAuthenticatedUsername().getUserId());

		// If there are no activities, show a notification
		if (activities.isEmpty()) {
			Notification.show("No activities found for the selected date.", 3000, Notification.Position.MIDDLE);
			return activityLayout;
		}

		for (DailyActivity activity : activities) {
			Checkbox activityCheckbox = new Checkbox(activity.getDescription());
			activityCheckbox.setValue(activity.isComplete()); // Set initial value based on current status
			Span activityLabel = new Span(activity.getDescription());
			updateActivityLabelColor(activityCheckbox, activityLabel);
			activityCheckbox.addValueChangeListener(event -> {
				// Update the activity's status based on the checkbox value
				activity.setComplete(event.getValue());
				// activity.setUser(checkUser.get());
				dailyActivityService.save(activity); // Save the updated activity

				updateActivityLabelColor(activityCheckbox, activityLabel); // Update color based on new status
				Notification.show("Activity status updated successfully.", 3000, Notification.Position.MIDDLE);
			});
			activityFormLayout.add(activityCheckbox);
		}

		Button cancelButton = new Button("Close", event -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

		// Button layout
		HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton);

		buttonLayout.getElement().getStyle()
				// .set("border-bottom", "1px solid #ddd")
				.set("display", "flex").set("width", "100%").set("justify-content", "flex-end");
		activityLayout.add(activityFormLayout, buttonLayout);
		return activityLayout;
	}

	private VerticalLayout createMoodTab(LocalDate date, Dialog dialog, Button saveButton) {

		VerticalLayout moodLayout = new VerticalLayout();
		FormLayout formLayout = new FormLayout();
		formLayout.setWidthFull();
		dialog.getFooter().removeAll();
		// Mood Radio Button Group
		RadioButtonGroup<Mood> moodGroup = new RadioButtonGroup<>();
		moodGroup.setLabel("Select Mood");
		moodGroup.setItems(Mood.values());
		moodGroup.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
		moodGroup.setItemLabelGenerator(Mood::getLabel); // Assuming getLabel returns a user-friendly name
		formLayout.add(moodGroup);
		moodLayout.add(formLayout);
		dialog.getFooter().removeAll();

		Optional<MobUser> checkUser = dailyMoodService
				.checkUser(dailyMoodService.getAuthenticatedUsername().getUserId());

		if (checkUser.isEmpty()) {
			throw new UsernameNotFoundException("User not found");

		}

		DailyMoodEntity existingMood = dailyMoodService.getMoodByDateAndUserId(date,
				dailyMoodService.getAuthenticatedUsername().getUserId());
		if (existingMood != null) {
			moodGroup.setValue(existingMood.getMood()); // Set existing mood as selected
		}

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
		saveButton = new Button("Save", event -> {
			Mood selectedMood = moodGroup.getValue();
			if (selectedMood == null) {
				Notification.show("Please select a mood.", 3000, Notification.Position.MIDDLE);
				return;
			}
			if (existingMood != null) {
//                // Update existing mood
				existingMood.setMood(selectedMood);
				existingMood.setUser(checkUser.get());
				dailyMoodService.update(existingMood); // Update the mood
				Notification.show("Mood updated successfully!", 3000, Notification.Position.BOTTOM_START);
				onActivityUpdate.run();
				dialog.close();
			} else {
//                // Save new mood
				DailyMoodEntity dailyMood = new DailyMoodEntity();
				dailyMood.setDate(date);
				dailyMood.setMood(selectedMood);
				dailyMood.setUser(checkUser.get());
				dailyMoodService.save(dailyMood); // Save the mood
				Notification.show("Mood added successfully!", 3000, Notification.Position.BOTTOM_START);
				onActivityUpdate.run();
				dialog.close();
			}
		});
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

		Button cancelButton = new Button("Cancel", event -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		buttonLayout.getElement().getStyle().set("display", "flex").set("width", "100%").set("justify-content",
				"flex-end");

		buttonLayout.add(saveButton, cancelButton);
		moodLayout.add(buttonLayout);

		return moodLayout;
	}

	public VerticalLayout createDailyViewTab(LocalDate date, Dialog dialog) {

		int charLimit = 1000;

		VerticalLayout moodLayout = new VerticalLayout();

		FormLayout formLayout = new FormLayout();
		// formLayout.setWidthFull();
		dialog.getFooter().removeAll();

		HorizontalLayout buttonLayout = new HorizontalLayout();
		// buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        
		TextArea noteContentArea = new TextArea("Today's Experience");
		noteContentArea.setPlaceholder("Share your full-day experiences, reflections, or observations...");

		noteContentArea.setMaxLength(charLimit);
		noteContentArea.setHeight("250px");
		noteContentArea.getStyle().set("font-size", "16px").set("color", "#444");
		noteContentArea.setMinWidth("100%");

		noteContentArea.setValueChangeMode(ValueChangeMode.EAGER);
		noteContentArea.addValueChangeListener(e -> {
			e.getSource().setHelperText(e.getValue().length() + "/" + charLimit);
		});
		formLayout.add(noteContentArea);
		moodLayout.add(formLayout);
		Button saveButton = new Button("Save", event -> {
			String content = noteContentArea.getValue();
			if (content == null || content.trim().isEmpty()) {
				Notification.show("Daily note cannot be empty", 3000, Notification.Position.MIDDLE);
			} else {
				try {

					Optional<MobUser> checkUser = dailyNoteService
							.checkUser(dailyNoteService.getAuthenticatedUsername().getUserId());
					DailyNote dailyNote = new DailyNote();
					dailyNote.setDate(date);
					dailyNote.setContent(content);
					dailyNote.setDone(false); // Default to not done
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
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

		Button cancelButton = new Button("Cancel", event -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		buttonLayout.getElement().getStyle().set("display", "flex").set("width", "100%").set("justify-content",
				"flex-end");
		// Buttons Layout
		buttonLayout.add(saveButton, cancelButton);
		moodLayout.add(buttonLayout); // Align buttons to stretch the full width
		return moodLayout;
	}

	private VerticalLayout createNamajTab(LocalDate date, Dialog dialog, Button saveButton) {
		VerticalLayout namajLayout = new VerticalLayout();
		namajLayout.setSpacing(true);
		namajLayout.getElement().getStyle().set("padding", "0px 0");

		// Dropdown for selecting Namaz
		ComboBox<String> namazComboBox = new ComboBox<>("Select Namaz");
		namazComboBox.setItems("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Tahajjud");
		namazComboBox.setPlaceholder("Choose a Namaz");

		namazComboBox.getElement().getStyle()
				// .set("border-bottom", "1px solid #ddd")
				.set("padding", "0px 0");

		// Layout for selected Namaz options
		HorizontalLayout optionsLayout = new HorizontalLayout();

		// Radio buttons for performed with Jamaat or without
		RadioButtonGroup<String> jamaatGroup = new RadioButtonGroup<>();
		jamaatGroup.setLabel("Was this performed with Jamaat?");
		jamaatGroup.setItems("With Jamaat", "Without Jamaat");
		jamaatGroup.setValue("Without Jamaat"); // Default to "Without Jamaat"
		optionsLayout.add(jamaatGroup);

		// Radio buttons for missed
		RadioButtonGroup<String> missedGroup = new RadioButtonGroup<>();
		missedGroup.setLabel("Was this missed?");
		missedGroup.setItems("Yes", "No");
		missedGroup.setValue("Yes"); // Default to "No"
		optionsLayout.add(missedGroup);

		// Radio buttons for Kaza
		RadioButtonGroup<String> kazaGroup = new RadioButtonGroup<>();
		kazaGroup.setLabel("Kaza Status");
		kazaGroup.setItems("Kaza Done", "Kaza Not Done");
		kazaGroup.setEnabled(false); // Disable by default
		optionsLayout.add(kazaGroup);

		Optional<MobUser> authenticatedUsername = dailyNamajService
				.checkUser(dailyNamajService.getAuthenticatedUsername().getUserId());

		// Add listener to handle missed selection
		missedGroup.addValueChangeListener(event -> {
			boolean isMissed = event.getValue().equals("Yes");
			jamaatGroup.setEnabled(!isMissed); // Disable Jamaat options if missed
			kazaGroup.setEnabled(isMissed); // Enable Kaza options only if missed
			if (!isMissed) {
				kazaGroup.clear(); // Clear Kaza selection if not missed

			}
		});

		if (missedGroup.getValue().equalsIgnoreCase("YES")) {
			jamaatGroup.setEnabled(false);
			kazaGroup.setEnabled(true);
		}
		// Add the dropdown and options to the layout
		namajLayout.add(namazComboBox, optionsLayout);

		// Save button
		saveButton = new Button("Save/Update", event -> {
			String selectedNamaz = namazComboBox.getValue();
			String jamaatStatus = jamaatGroup.getValue();
			String missed = missedGroup.getValue(); // Use "Yes" or "No"
			String kazaStatus = kazaGroup.getValue(); // Get Kaza selection

			// Validation
			if (selectedNamaz == null || selectedNamaz.isEmpty()) {
				Notification.show("Please select a Namaz.", 3000, Notification.Position.MIDDLE);
				return;
			}

			if (missed.equals("Yes") && !jamaatGroup.isEnabled()) {
				// Jamaat option is irrelevant if missed
				jamaatStatus = "N/A";
			}

			Optional<NamajEntry> existingEntryOpt = dailyNamajService.findByNamazDateAndNamazTypeAndUserId(date,
					selectedNamaz, authenticatedUsername.get().getId());

			NamajEntry entry;
			if (existingEntryOpt.isPresent()) {
				// Update existing entry
				entry = existingEntryOpt.get();
				entry.setJamatType(jamaatStatus);
				entry.setMissed(missed); // Set missed to "Yes" or "No"
				missedGroup.setValue(existingEntryOpt.get().getMissed());

				if (kazaGroup.isEnabled()) {
					entry.setKaza(kazaStatus);
				} else {
					entry.setKaza("KAZA NOT REQUIRED");
				}
				// Set Kaza based on selection
				dailyNamajService.update(entry);
				Notification.show("Namaz Tracker updated successfully!", 3000, Notification.Position.BOTTOM_START);
				onActivityUpdate.run();
			} else {
				// Create new entry
				entry = new NamajEntry();
				entry.setNamazDate(date);
				entry.setNamazType(selectedNamaz);
				entry.setJamatType(jamaatStatus);
				entry.setMissed(missed); // Set missed to "Yes" or "No"
				entry.setUser(authenticatedUsername.get());
				if (kazaGroup.isEnabled()) {
					entry.setKaza(kazaStatus);
				} else {
					entry.setKaza("KAZA NOT REQUIRED");
				} // Set Kaza based on selection
				dailyNamajService.update(entry);
				onActivityUpdate.run();
				Notification.show("Namaz Tracker added successfully!", 3000, Notification.Position.BOTTOM_START);
			}

			dialog.close();
		});

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

		Button cancelButton = new Button("Cancel", event -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

		// Button layout
		HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

		buttonLayout.getElement().getStyle()
				// .set("border-bottom", "1px solid #ddd")
				.set("display", "flex").set("width", "100%").set("justify-content", "flex-end");

		// dialog.getFooter().add(buttonLayout);
		namajLayout.add(buttonLayout);

		// If updating an existing entry, populate the fields
		namazComboBox.addValueChangeListener(event -> {

			String selectedNamaz = event.getValue();
			if (selectedNamaz != null && !selectedNamaz.isEmpty()) {
				Optional<NamajEntry> existingEntryOptInner = dailyNamajService
						.findByNamazDateAndNamazTypeAndUserId(date, selectedNamaz, authenticatedUsername.get().getId());
				if (existingEntryOptInner.isPresent()) {
					NamajEntry existingEntry = existingEntryOptInner.get();
					jamaatGroup.setValue(existingEntry.getJamatType());
					missedGroup.setValue(existingEntry.getMissed()); // Directly set the missed value ("Yes" or "No")
					kazaGroup.setValue(existingEntry.getKaza()); // Set Kaza status
				} else {
					// Reset to default values if no entry exists
					jamaatGroup.setValue("Without Jamaat");
					missedGroup.setValue("No");
					kazaGroup.clear(); // Clear Kaza selection if no entry exists
				}
			}
		});

		return namajLayout;
	}

	private void updateActivityLabelColor(Checkbox checkbox, Span label) {
		if (checkbox.getValue()) {
			label.getStyle().set("color", "green"); // Green for completed
			onActivityUpdate.run();
		} else {
			label.getStyle().set("color", "red"); // Red for not completed
			onActivityUpdate.run();
		}
	}

}
package com.example.application.views.calender;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.application.entity.DailyActivity;
import com.example.application.entity.DailyMoodEntity;
import com.example.application.service.DailyActivityService;
import com.example.application.service.DailyMoodService;
import com.example.application.service.MobUserDetails;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import jakarta.annotation.security.PermitAll;

@CssImport("./styles/styles.css")
//@JsModule("https://unpkg.com/@lottiefiles/lottie-player@latest/dist/lottie-player.js")
//@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0)
@PermitAll
public class CalendarBody extends VerticalLayout {

	private FlexLayout calendarLayout;
	private LocalDate currentMonth;
	private ActivityDialogManager activityDialogManager;
	private final DailyMoodService dailyMoodService;
	private final DailyActivityService activityService;

	public CalendarBody(ActivityDialogManager activityDialogManager, DailyMoodService dailyMoodService, DailyActivityService activityService) {
		this.dailyMoodService = dailyMoodService;
		this.activityDialogManager = activityDialogManager;
		setWidth("100%"); // Main Div width set to dailyActivityServicexx%
		this.currentMonth = LocalDate.now().withDayOfMonth(1);
		this.activityService = activityService;

		// Setup layout to hold the full month calendar in a grid style
		calendarLayout = new FlexLayout();
		calendarLayout.setWidth("100%");
		calendarLayout.getStyle().set("display", "grid");
		// calendarLayout.getStyle().set("grid-template-columns", "repeat(7, 1fr)");
		calendarLayout.getStyle().set("gap", "3px");
		calendarLayout.addClassName("calendar-background");
		calendarLayout.setHeight("100%");

		Div calendarContainer = new Div();
		calendarContainer.setWidthFull();
		calendarContainer.addClassName("calendar-container");
		calendarContainer.add(calendarLayout);
		 this.addClassName("dark");
		 calendarContainer.setHeight("100%");
		setHeight("100%");
		// Add components to the main layout
		add(calendarContainer);

		// Display the initial calendar
		displayMonthCalendar(currentMonth);
	}

	public void displayMonthCalendar(LocalDate month) {
		// Clear the existing components
		calendarLayout.removeAll();

		LocalDate startOfMonth = month.withDayOfMonth(1);
		LocalDate endOfMonth = month.withDayOfMonth(month.lengthOfMonth());

		// Add headers for days of the week
		String[] weekdays = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
		for (String dayName : weekdays) {
			Div headerCell = new Div();
			headerCell.setText(dayName);
			headerCell.addClassName("day-name");

			headerCell.getStyle().set("font-weight", "bold");
			headerCell.getStyle().set("text-align", "center");
			calendarLayout.add(headerCell);
		}

		// Determine the day of the week the month starts on (1=Monday, 7=Sunday)
		int firstDayOfWeek = month.withDayOfMonth(1).getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
		int emptyCells = firstDayOfWeek - 1; // Calculate the number of empty cells

		// Add empty cells for days before the first day
		for (int i = 0; i < emptyCells; i++) {
			Div emptyCell = new Div();
			calendarLayout.add(emptyCell);
		}

		 List<DailyActivity> activitiesForMonth = activityService.findByDateBetween(startOfMonth, endOfMonth,getAuthenticatedUsername().getUserId());

	        // Map to store activities by date
	        Map<LocalDate, List<DailyActivity>> activitiesByDate = activitiesForMonth.stream()
	                .collect(Collectors.groupingBy(DailyActivity::getDate));

		
		List<DailyMoodEntity> moodsForMonth = dailyMoodService.findByDateBetweenAndUserId(startOfMonth, endOfMonth);
		Map<LocalDate, DailyMoodEntity> moodsByDate = moodsForMonth.stream()
				.collect(Collectors.toMap(DailyMoodEntity::getDate, mood -> mood));
		
		List<DailyActivity> dailyActivities = activityService.findByDateBetween(startOfMonth, endOfMonth,getAuthenticatedUsername().getUserId());
		Map<LocalDate, List<DailyActivity>> activitysByDate = dailyActivities.stream()
		        .collect(Collectors.groupingBy(DailyActivity::getDate));

		// Fill in the days of the month
		for (int day = 1; day <= month.lengthOfMonth(); day++) {
			LocalDate dayDate = month.withDayOfMonth(day);
			Div dayCell = createDayCell(dayDate, moodsByDate,activitysByDate);

			calendarLayout.add(dayCell);
		}

		// Add empty cells to fill the last row if necessary
		int totalCells = calendarLayout.getComponentCount();
		int cellsToAdd = 7 - (totalCells % 7);
		if (cellsToAdd < 7 && cellsToAdd != 0) {
			for (int i = 0; i < cellsToAdd; i++) {
				Div emptyCell = new Div();
				calendarLayout.add(emptyCell);
			}
		}
	}

	private String getProgressBarColorClass(double progress) {
		if (progress >= 1.0) {
			return "progress-green";
		} else if (progress >= 0.5) {
			return "progress-orange";
		} else {
			return "progress-red";
		}
	}

	public MobUserDetails getAuthenticatedUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Check if the authentication object is not null and the user is authenticated
		if (authentication != null && authentication.isAuthenticated()) {
			// Further check if the user is not anonymous
			if (!(authentication.getPrincipal() instanceof MobUserDetails)) {
				// If the user is anonymous (for example, the user might be using a 'guest' or
				// 'anonymous' role)
				return null; // or return a default value like "anonymous" or throw an exception
			}

			// If the user is authenticated and not anonymous, return their username
			return (MobUserDetails) authentication.getPrincipal(); // This will return the username of the authenticated
																	// user
		}
		// Return null if the user is not authenticated
		return null;
	}

	private Div createDayCell(LocalDate date, Map<LocalDate, DailyMoodEntity> moodsByDate, Map<LocalDate, List<DailyActivity>> activitysByDate) {
		Div dayCell = new Div();
		dayCell.addClassName("calendar-cell");
		dayCell.getStyle().set("padding", "5px");
		// dayCell.getStyle().set("width", "100%");

		dayCell.getStyle().set("border", "1px solid lightgray");
		dayCell.getStyle().set("display", "flex");
		dayCell.getStyle().set("flex-direction", "column");
		dayCell.getStyle().set("justify-content", "space-around");
		dayCell.getStyle().set("align-items", "center");
		dayCell.getStyle().set("align-content", "space-between");
		// Highlight today's date
		if (date.equals(LocalDate.now())) {
			addTimeBasedGif(dayCell);
			dayCell.addClassName("today");
			dayCell.getStyle().set("background-color", "#e0f7fa");
		}

		Span dayNumber = new Span(String.valueOf(date.getDayOfMonth()));
		dayNumber.getStyle().set("font-weight", "bold").set("font-size", "20px");
		dayCell.add(dayNumber);

		if (moodsByDate.get(date) != null) {
			Icon moodIcon = getMoodIcon(moodsByDate.get(date).getMood().getLabel());
			if (moodIcon != null) {
				moodIcon.getStyle().set("position", "absolute");
				moodIcon.getStyle().set("bottom", "5px"); // Position the icon
				moodIcon.getStyle().set("right", "5px");
				moodIcon.getElement().getStyle().set("color", "red"); 
				dayCell.add(moodIcon); // Add the icon to the cell
			}
		}

		// Create a container for the activities
		VerticalLayout activitiesContainer = new VerticalLayout();
		activitiesContainer.setPadding(false); // Minimize padding
		activitiesContainer.setSpacing(false); // Minimize spacing
		
		List<DailyActivity> newActivity = new ArrayList<>();
		
		 for (Map.Entry<LocalDate, List<DailyActivity>> entry : activitysByDate.entrySet()) {
   		  List<DailyActivity> activities = entry.getValue(); 
   		                   newActivity.addAll(activities);
		 }
		
		
	        if (activitysByDate != null && !activitysByDate.isEmpty()) {
	          //  for (DailyActivity activity : activitysByDate) {
	        	for (DailyActivity activity : newActivity) {
	        		
	        		if(date.equals(activity.getDate())) {
	            		  // The activity for that date
	                Div activityLayout = new Div();
	              //  activityLayout.setSpacing(false); // No spacing between activities
	                activityLayout.setWidthFull(); // Ensure it takes full width
	                
	                // Create a colored dot for the activity
	                Div activityDot = new Div();
	                activityDot.setWidth("12px"); // Circle diameter
	                activityDot.setHeight("12px");
	                activityDot.getStyle().set("border-radius", "50%"); // Make it a circle
	                activityDot.getStyle().set("margin-right", "5px"); // Space between dot and text

	                // Set the color based on the activity status
	                if (activity.isComplete()) {
	                    activityDot.getStyle().set("background-color", "#123d12");
	                } else {
	                    activityDot.getStyle().set("background-color", "#ad3737");
	                }

	                // Create a label for the activity description
	                Span activityLabel = new Span(activity.getDescription());
	                activityLabel.getStyle().set("color", activity.isComplete() ? "#123d12" : "#ad3737"); // Set color based on status
	               // activityLabel.getStyle().set("font-size", "12px"); // Set font size to small
	                activityLabel.getStyle().set("margin-left", "9px");
	                activityLayout.getStyle().set("font-size", "13px");
	                activityLayout.getStyle().set("display", "flex");
	               // activityLayout.getStyle().set("margin-left", "9px");
	                activityLayout.getStyle().set("align-items", "baseline");
	                // Add dot and label to the layout
	                activityLayout.add(activityDot, activityLabel);
	                activitiesContainer.add(activityLayout);
	           
	        		}
	        		}
	        }

	        // Add the activities container and the day number at the bottom
	        dayCell.add(activitiesContainer);
	       // dayCell.add(progressBar);
	       // Label dayNumber = new Label(String.valueOf(date.getDayOfMonth()));
	        dayNumber.getStyle().set("font-weight", "bold").set("font-size", "20px");
	        dayCell.add(dayNumber);

//        // Add click listener to open update activity dialog
		dayCell.addClickListener(event -> activityDialogManager.openUpdateActivityDialogWindow(date));

		return dayCell;
	}

	private Icon getMoodIcon(String mood) {
		switch (mood.toLowerCase()) {
		case "bad":
			return new Icon(VaadinIcon.MEH_O);
		case "good":
			return new Icon(VaadinIcon.STAR);
		case "happy":
			return new Icon(VaadinIcon.SMILEY_O);
		case "ok":
			return new Icon(VaadinIcon.SMILEY_O);
		case "very good":
			return new Icon(VaadinIcon.HEART_O);
		case "average":
			return new Icon(VaadinIcon.THUMBS_UP);
		case "very bad":
			return new Icon(VaadinIcon.FROWN_O);
		default:
			return null; // Return null if no valid mood found
		}
	}

	private void addTimeBasedGif(Div dayCell) {
		LocalTime currentTime = LocalTime.now();
		String gifPath;

		if (currentTime.isAfter(LocalTime.of(5, 0)) && currentTime.isBefore(LocalTime.NOON)) {
//	        // Morning: 5 AM - 12 PM
			gifPath = "images/afternoon.json"; // Replace with your Lottie JSON file path or URL
			// gifPath = "images/moon.json"; // Replace with your Lottie JSON file path or
			// URL
		} else if (currentTime.isAfter(LocalTime.NOON) && currentTime.isBefore(LocalTime.of(18, 0))) {
//	        // Afternoon: 12 PM - 6 PM
			gifPath = "images/afternoon.json"; // Replace with your Lottie JSON file path or URL
		} else {
//	        // Night: 6 PM - 5 AM
			gifPath = "images/moon.json"; // Replace with your Lottie JSON file path or URL
		}

		String lottieAnimation = String.format("<lottie-player src='%s'  speed='1' "
				+ "style='position: absolute; top: 0; left: 0; width: 100%%; height: 100%%; "
				+ " pointer-events: none; opacity: 0.3;' loop autoplay></lottie-player>", gifPath);
		Html lottie = new Html(lottieAnimation);
		dayCell.add(lottie);
	}

}

package com.example.application.views.calender;

import java.time.LocalDate;

import com.example.application.service.DailyActivityService;
import com.example.application.service.DailyMoodService;
import com.example.application.service.DailyNamajService;
import com.example.application.service.DailyNoteService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("calendar")
@PageTitle("Calendar")
@Menu(icon = "line-awesome/svg/file.svg", order = 1)
@CssImport("./styles/styles.css")
@JsModule("https://unpkg.com/@lottiefiles/lottie-player@latest/dist/lottie-player.js")
@PermitAll

public class FullMonthCalendarView extends Composite<VerticalLayout> {
	private LocalDate currentMonth;
	private CalendarHeader calendarHeader;

	private CalendarBody calendarBody;
	 private final DailyActivityService dailyActivityService;

	private ActivityDialogManager activityDialogManager;
	private final DailyNoteForm dailyNoteForm;
	 private final ActivityAddDeleteForm activityAddDeleteForm;

	public FullMonthCalendarView(DailyMoodService dailyMoodService, DailyActivityService dailyActivityService,
			DailyNoteService dailyNoteService,
			DailyNamajService dailyNamajService
			) {
		this.currentMonth = LocalDate.now().withDayOfMonth(1);
		
		dailyNoteForm = new DailyNoteForm(dailyNoteService,this::refreshCalendar);
		activityAddDeleteForm = new ActivityAddDeleteForm(this::refreshCalendar, dailyActivityService);
		
		
		this.calendarHeader = new CalendarHeader(this::navigateToPreviousMonth, this::navigateToNextMonth,
				() -> activityAddDeleteForm.openAddDailyActivityDialog(),
				() -> activityAddDeleteForm.openDeleteDailyActivityDialog(),
				() -> dailyNoteForm.openAddDailyNoteDialog(LocalDate.now()),
				() -> dailyNoteForm.openViewDailyNotesDialog(LocalDate.now())
				);
		this.dailyActivityService = dailyActivityService;
		
		this.activityDialogManager = new ActivityDialogManager(this::refreshCalendar, dailyMoodService, dailyActivityService,dailyNoteService,dailyNamajService);
		this.calendarBody = new CalendarBody(activityDialogManager, dailyMoodService, dailyActivityService);
		//this.activityAddDeleteForm = activityAddDeleteForm;

		// Update header with the current month
		calendarHeader.updateMonthYearLabel(currentMonth);

		// Setup main view layout
		getContent().setSizeFull();
		// getContent().add( calendarBody);
		calendarBody.setHeight("100%");
		getContent().add(calendarHeader, calendarBody);

		// Display the initial calendar for the current month
		refreshCalendar();
	}

	private void navigateToPreviousMonth() {
		currentMonth = currentMonth.minusMonths(1);
		calendarHeader.updateMonthYearLabel(currentMonth);
		refreshCalendar();
	}

	private void navigateToNextMonth() {
		currentMonth = currentMonth.plusMonths(1);
		calendarHeader.updateMonthYearLabel(currentMonth);
		refreshCalendar();
	}

	private void refreshCalendar() {
		calendarBody.displayMonthCalendar(currentMonth);
	}

}

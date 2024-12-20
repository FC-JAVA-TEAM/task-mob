package com.example.application.views.calender;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.application.entity.DailyNote;
import com.example.application.service.DailyNoteService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import jakarta.annotation.security.PermitAll;

@Route("daily-notes")
@PageTitle("Daily Notes")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 6)
@PermitAll
public class DailyNotesGridView extends Div implements RouterLayout {

	private final DailyNoteService dailyNoteService;
	private TreeGrid<DailyNote> treeGrid; // Make treeGrid a class member
	private Set<DailyNote> expandedItems = new HashSet<>(); // Set to keep track of expanded items

	public DailyNotesGridView(DailyNoteService dailyNoteService) {
		this.dailyNoteService = dailyNoteService;
		init();
	}

	private void init() {
		// Header
		HorizontalLayout titleLayout = new HorizontalLayout();
		Span titleLabel = new Span("Daily Notes");
		titleLabel.getStyle().set("font-size", "24px").set("font-weight", "bold");

		titleLayout.add(titleLabel);
		titleLayout.setWidthFull();
		titleLayout.setJustifyContentMode(JustifyContentMode.CENTER);

		// Create TreeGrid for displaying daily notes
		treeGrid = new TreeGrid<>();
		treeGrid.addHierarchyColumn(note -> note.getDate().toString()).setHeader("Date");
		treeGrid.addColumn(DailyNote::getContent).setHeader("Daily Note");

		// Fetch and display daily notes

		// Add click listener to handle item selection
		treeGrid.addItemClickListener(event -> {
			DailyNote selectedNote = event.getItem();
			if (selectedNote != null) {
				 openEditDailyNoteDialog(selectedNote,this::updateDailyNotesGrid);
			}
		});
		updateDailyNotesGrid();
		// Add components to the layout
		VerticalLayout layout = new VerticalLayout(titleLayout, treeGrid);
		layout.setPadding(true);
		layout.setSpacing(true);
		add(layout);
	}

	private void updateDailyNotesGrid() {
		LocalDate currentDate = LocalDate.now(); // Adjust if needed
		List<DailyNote> dailyNotes = dailyNoteService.findByMonthAndUserId(currentDate,
				dailyNoteService.getAuthenticatedUsername().getUserId());
		// Store currently expanded items before the update
		Set<DailyNote> currentExpandedItems = new HashSet<>(expandedItems);
		expandedItems.clear(); // Clear the previous expanded items

		// Structure the data for TreeGrid
		TreeData<DailyNote> treeData = new TreeData<>();
		Map<LocalDate, List<DailyNote>> notesByDate = dailyNotes.stream()
				.collect(Collectors.groupingBy(DailyNote::getDate));

		for (LocalDate localDate : notesByDate.keySet()) {
			List<DailyNote> notes = notesByDate.get(localDate);
			DailyNote parentNote = new DailyNote(); // Create a dummy parent object for the date
			parentNote.setDate(localDate);
			treeData.addItem(null, parentNote); // Add parent item with null as its parent

			// Add each note as a child item
			for (DailyNote note : notes) {
				treeData.addItem(parentNote, note); // Attach child to parent
			}
		}

		// Set the tree data to the grid
		TreeDataProvider<DailyNote> dataProvider = new TreeDataProvider<>(treeData);
		treeGrid.setDataProvider(dataProvider); // Use TreeDataProvider

		// Expand previously expanded items after the data update
		for (DailyNote item : currentExpandedItems) {
			// Check if the item is in the new treeData
			if (treeData.getChildren(item).size() > 0) { // Check if item has children
				treeGrid.expand(item);
				expandedItems.add(item); // Keep track of expanded items
			}
		}
	}

    private void openEditDailyNoteDialog(DailyNote note,Runnable onUpdate) {
       DailyNoteForm dailyNoteForm = new DailyNoteForm(dailyNoteService);
        dailyNoteForm.openEditDailyNoteDialog(note,onUpdate); // Pass the update callback
    }
}

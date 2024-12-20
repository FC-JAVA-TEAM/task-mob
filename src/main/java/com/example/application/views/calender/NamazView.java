package com.example.application.views.calender;


import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.application.entity.NamajEntry;
import com.example.application.service.DailyNamajService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

import jakarta.annotation.security.PermitAll;

@Route("namaz")
@PageTitle("Namaz Tracker")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 1)
@CssImport("./styles/styles.css")
@PermitAll
public class NamazView extends Div implements RouterLayout {

    private final DailyNamajService namajEntryService;
    private TreeGrid<NamajEntry> treeGrid;
    private Map<LocalDate, List<NamajEntry>> sortedEntriesByDate; // Declare it here

    public NamazView(DailyNamajService namajEntryService) {
        this.namajEntryService = namajEntryService;
        init();
    }

    private void init() {
        // Header
        HorizontalLayout titleLayout = new HorizontalLayout();
        Span titleLabel = new Span("Namaz Tracker");
        titleLabel.getStyle().set("font-size", "24px").set("font-weight", "bold");

        titleLayout.add(titleLabel);
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Create TreeGrid for displaying namaz entries
        treeGrid = new TreeGrid<>();

        // Existing columns
        treeGrid.addHierarchyColumn(entry -> entry.getNamazDate().toString()).setHeader("Date");
        treeGrid.addColumn(NamajEntry::getNamazType).setHeader("Namaz Type");
        treeGrid.addColumn(NamajEntry::getMissed).setHeader("PRAY Missed");
        treeGrid.addColumn(NamajEntry::isKaza).setHeader("Kaza");

        treeGrid.addComponentColumn(this::createProgressBarForEntry).setHeader("Progress");


        treeGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        // Fetch and display namaz entries
        updateNamazGrid();

        // Add components to the layout
        VerticalLayout layout = new VerticalLayout(titleLayout, treeGrid);
        layout.setPadding(true);
        layout.setSpacing(true);
        add(layout);
    }

 // Method to create a progress bar for both parent and child entries
    private ProgressBar createProgressBarForEntry(NamajEntry entry) {
        ProgressBar progressBar = new ProgressBar();

        // Parent row progress bar logic
        if (entry.getNamazType() == null) { // Parent entry
            return createParentProgressBar(entry); // Delegate to parent-specific logic
        } 
        
        // Child row progress bar logic
        else { // Child entry
            // Check if the Namaz was missed or not
            if ("No".equalsIgnoreCase(entry.getMissed())) {
                progressBar.setValue(1.0); // Set progress to full (1.0) for performed Namaz
                progressBar.addClassName("green-progress"); // Add green color class for performed Namaz
            } else {
                progressBar.setValue(0.0); // Set progress to empty (0.0) for missed Namaz
                progressBar.addClassName("red-progress"); // Add red color class for missed Namaz
            }

            return progressBar;
        }
    }

    // Method to update the TreeGrid
    private void updateNamazGrid() {
        LocalDate currentDate = LocalDate.now(); // Adjust if needed
      //  List<NamajEntry> namazEntries = namajEntryService.findByMonth(currentDate); // Get entries for the current date
        List<NamajEntry> namazEntries = namajEntryService.findByNamazDateBetweenAndUserId(currentDate,namajEntryService.getAuthenticatedUsername().getUserId()); // Get entries for the current date

        // Structure the data for TreeGrid
        TreeData<NamajEntry> treeData = new TreeData<>();
        Map<LocalDate, List<NamajEntry>> entriesByDate = namazEntries.stream()
            .collect(Collectors.groupingBy(NamajEntry::getNamazDate));

        // Sort the entries in descending order by date and store in the class-level variable
        sortedEntriesByDate = entriesByDate.entrySet().stream()
            .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey())) // Sort in descending order
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                Map.Entry::getValue, 
                (e1, e2) -> e1, 
                LinkedHashMap::new // Use LinkedHashMap to maintain order
            ));

        for (LocalDate localDate : sortedEntriesByDate.keySet()) {
            List<NamajEntry> entries = sortedEntriesByDate.get(localDate);
            NamajEntry parentEntry = new NamajEntry(); // Create a dummy parent object for the date
            parentEntry.setNamazDate(localDate);
            parentEntry.setMissed(null);
            parentEntry.setKaza(null);
            treeData.addItem(null, parentEntry); // Add parent item with null as its parent

            // Add each entry as a child item
            for (NamajEntry entry : entries) {
                treeData.addItem(parentEntry, entry); // Attach child to parent
            }
        }

        // Set the tree data to the grid
        TreeDataProvider<NamajEntry> dataProvider = new TreeDataProvider<>(treeData);
        treeGrid.setDataProvider(dataProvider); // Use TreeDataProvider

        treeGrid.setPartNameGenerator(item -> {
            LocalDate namazDate = item.getNamazDate();
            List<NamajEntry> entriesForDate = sortedEntriesByDate.get(namazDate);

            if (item.getNamazType() == null) { 
                // Parent row logic
                if (entriesForDate != null) {
                    boolean allNamazTypes = checkIfAllNamazTypesPresent(namazDate, entriesForDate);
                    boolean noMissedPrayers = entriesForDate.stream().allMatch(e -> "No".equalsIgnoreCase(e.getMissed()));
                    boolean noKaza = entriesForDate.stream().allMatch(e -> e.getKaza().equalsIgnoreCase("KAZA NOT REQUIRED"));

                    if (allNamazTypes && noMissedPrayers && noKaza) {
                        return "high-rating"; // Apply the green-row CSS class to parent
                    }
                }
            } else { 
                // Child row logic
                if (entriesForDate != null) {
                    boolean allNamazTypes = checkIfAllNamazTypesPresent(namazDate, entriesForDate);
                    boolean noMissedPrayers = entriesForDate.stream().allMatch(e -> "No".equalsIgnoreCase(e.getMissed()));
                    boolean noKaza = entriesForDate.stream().allMatch(e -> e.getKaza().equalsIgnoreCase("KAZA NOT REQUIRED"));

                    if (allNamazTypes && noMissedPrayers && noKaza) {
                        return "high-rating"; // Apply the green-row CSS class to child
                    }
                }
            }

            return "low-rating"; // Default class for other rows
        });
    }

    // Method to create a progress bar for parent entries (dates only)
    private ProgressBar createParentProgressBar(NamajEntry entry) {
        ProgressBar progressBar = new ProgressBar(0, 6, 0); // 6 prayers total

        if (entry.getNamazType() == null) { // Check if this is a parent entry
            LocalDate namazDate = entry.getNamazDate();
            List<NamajEntry> entriesForDate = sortedEntriesByDate.get(namazDate);

            if (entriesForDate != null) {
                long performedNamazCount = entriesForDate.stream()
                    .filter(e -> "No".equalsIgnoreCase(e.getMissed())) // Count performed prayers
                    .count();

                progressBar.setValue(performedNamazCount); // Set progress based on performed prayers

                // Change color based on progress
                String colorClass = getColorClassByProgress(performedNamazCount);
                progressBar.addClassName(colorClass);
                
                progressBar.addClassName(getColorClassByProgress(performedNamazCount));

                return progressBar;
            }
        }

        return progressBar; // Return the progress bar even if it's not applicable (could be hidden with CSS)
    }

    private String getColorClassByProgress(long performedNamazCount) {
        switch ((int) performedNamazCount) {
            case 0: return "red-progress";       // 0 prayers performed
            case 1: case 2: return "orange-progress"; // 1-2 prayers performed
            case 3: case 4: return "yellow-progress"; // 3-4 prayers performed
            case 5: return "lightgreen-progress"; // 5 prayers performed
            case 6: return "green-progress";     // All 6 prayers performed
            default: return "";
        }
    }

    private boolean checkIfAllNamazTypesPresent(LocalDate date, List<NamajEntry> entries) {
        // Assuming the six Namaz types are predefined
        Set<String> requiredNamazTypes = Set.of("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Tahajjud");

        Set<String> presentNamazTypes = entries.stream()
            .map(NamajEntry::getNamazType)
            .collect(Collectors.toSet());

        return presentNamazTypes.containsAll(requiredNamazTypes); // Check if all required types are present
    }
}

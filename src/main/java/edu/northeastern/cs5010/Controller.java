package edu.northeastern.cs5010;

import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Main controller that initializes the application and displays views.
 */
public class Controller {

  /**
   * Main entry point for the calendar application.
   * Restores saved calendars and displays the create and detail views.
   *
   * @param args command line arguments (not used)
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        // Step 1: Restore calendars from previous runs
        List<Calendar> calendars;
        try {
          calendars = CalendarManager.restoreAllCalendars("calendars");
          System.out.println("Loaded " + calendars.size() + " calendars");
        } catch (IOException e) {
          System.out.println("No saved calendars found, creating new calendar");
          calendars = List.of(new Calendar("My Calendar"));
        }

        // Step 2: Select a calendar (just pick the first one)
        Calendar selectedCalendar = calendars.get(0);
        System.out.println("Selected calendar: " + selectedCalendar.getTitle());

        // Step 3: Create and display CreateEventView
        CreateEventView createView = new CreateEventView(selectedCalendar);
        createView.setVisible(true);

        // Step 4: If there are events, show EventDetailView for the first one
        if (!selectedCalendar.getEvents().isEmpty()) {
          Event firstEvent = selectedCalendar.getEvents().get(0);
          EventDetailView detailView = new EventDetailView(firstEvent, selectedCalendar);
          detailView.setVisible(true);
        } else {
          System.out.println("No events in calendar to display");
        }

        // Optional: Save calendars on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
            CalendarManager.saveAllCalendars(calendars, "calendars");
            System.out.println("Calendars saved successfully");
          } catch (IOException e) {
            System.err.println("Error saving calendars: " + e.getMessage());
          }
        }));

      } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error starting application: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    });
  }
}
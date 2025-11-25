package edu.northeastern.cs5010;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CalendarManager import/export functionality.
 */
public class CalendarManagerTest {

  @Test
  public void testSaveAndRestoreAllCalendars() throws IOException {
    // Create multiple calendars with events
    Calendar cal1 = new Calendar("Work");
    Calendar cal2 = new Calendar("Personal");

    Event event1 = new Event.Builder("Meeting",
        LocalDate.of(2025, 11, 15),
        LocalDate.of(2025, 11, 15))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();

    Event event2 = new Event.Builder("Birthday",
        LocalDate.of(2025, 11, 20),
        LocalDate.of(2025, 11, 20))
        .build();

    cal1.addEvent(event1);
    cal2.addEvent(event2);

    List<Calendar> original = new ArrayList<>();
    original.add(cal1);
    original.add(cal2);

    // Save all calendars
    CalendarManager.saveAllCalendars(original, "test_all");

    // Restore all calendars
    List<Calendar> restored = CalendarManager.restoreAllCalendars("test_all");

    // Verify
    assertEquals(2, restored.size());
    assertEquals(1, restored.get(0).getEvents().size());
    assertEquals(1, restored.get(1).getEvents().size());
  }

  @AfterEach
  public void cleanup() {
    new File("test_calendar.csv").delete();
    new File("test_all_0.csv").delete();
    new File("test_all_1.csv").delete();
  }
}
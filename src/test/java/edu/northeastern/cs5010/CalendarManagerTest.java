package edu.northeastern.cs5010;

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

  @AfterEach
  public void cleanup() {
    new File("test_calendar.csv").delete();
  }

  @Test
  public void testExportAndImport() throws IOException {
    Calendar original = new Calendar("Test Calendar");

    Event event1 = new Event.Builder("Meeting",
        LocalDate.of(2025, 11, 15),
        LocalDate.of(2025, 11, 15))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .location("Room 101")
        .build();

    original.addEvent(event1);
    original.exportToCsv("test_calendar.csv");

    Calendar imported = CalendarManager.importFromCsv("test_calendar.csv");

    assertEquals(1, imported.getEvents().size());

    Event importedEvent = imported.getEvents().get(0);
    assertEquals("Meeting", importedEvent.getSubject());
    assertEquals(LocalDate.of(2025, 11, 15), importedEvent.getStartDate());
    assertEquals(LocalTime.of(10, 0), importedEvent.getStartTime());
    assertEquals(LocalTime.of(11, 0), importedEvent.getEndTime());
    assertEquals("Room 101", importedEvent.getLocation());
  }
}
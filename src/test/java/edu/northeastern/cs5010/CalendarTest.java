package edu.northeastern.cs5010;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the Calendar class.
 * Tests generated with Claude AI assistance to achieve full coverage.
 */
class CalendarTest {

  private Calendar calendar;
  private LocalDate testDate;

  @BeforeEach
  void setup() {
    calendar = new Calendar("Test Calendar");
    testDate = LocalDate.of(2025, 11, 15);
  }

  @Test
  void testCalendarCreation() {
    assertEquals("Test Calendar", calendar.getTitle());
    assertEquals(0, calendar.getEvents().size());
  }

  @Test
  void testCalendarWithConflictsAllowed() {
    Calendar flexibleCalendar = new Calendar("Flexible", true);
    assertEquals("Flexible", flexibleCalendar.getTitle());
  }

  @Test
  void testAddSingleAllDayEvent() {
    Event event = new Event.Builder("Birthday", testDate, testDate).build();
    calendar.addEvent(event);

    assertEquals(1, calendar.getEvents().size());
    assertEquals("Birthday", calendar.getEvents().get(0).getSubject());
  }

  @Test
  void testAddSingleTimedEvent() {
    Event event = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(event);
    assertEquals(1, calendar.getEvents().size());
  }

  @Test
  void testRejectDuplicateAllDayEvent() {
    Event event1 = new Event.Builder("Birthday", testDate, testDate).build();
    Event event2 = new Event.Builder("Birthday", testDate, testDate).build();

    calendar.addEvent(event1);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(event2);
    });

    assertTrue(exception.getMessage().contains("same subject, date, and time"));
  }

  @Test
  void testRejectDuplicateTimedEvent() {
    Event event1 = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    Event event2 = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();

    calendar.addEvent(event1);

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(event2);
    });
  }

  @Test
  void testAllowSameSubjectDifferentTimes() {
    Event event1 = new Event.Builder("Exercise", testDate, testDate)
        .startTime(LocalTime.of(6, 0))
        .endTime(LocalTime.of(7, 0))
        .build();
    Event event2 = new Event.Builder("Exercise", testDate, testDate)
        .startTime(LocalTime.of(18, 0))
        .endTime(LocalTime.of(19, 0))
        .build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);

    assertEquals(2, calendar.getEvents().size());
  }

  @Test
  void testRejectOverlappingTimedEvents() {
    Event event1 = new Event.Builder("Meeting 1", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    Event event2 = new Event.Builder("Meeting 2", testDate, testDate)
        .startTime(LocalTime.of(14, 30))
        .endTime(LocalTime.of(15, 30))
        .build();

    calendar.addEvent(event1);

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(event2);
    });
  }

  @Test
  void testAllowOverlappingWhenConflictsEnabled() {
    Calendar flexibleCalendar = new Calendar("Flexible", true);

    Event event1 = new Event.Builder("Meeting 1", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    Event event2 = new Event.Builder("Meeting 2", testDate, testDate)
        .startTime(LocalTime.of(14, 30))
        .endTime(LocalTime.of(15, 30))
        .build();

    flexibleCalendar.addEvent(event1);
    flexibleCalendar.addEvent(event2);

    assertEquals(2, flexibleCalendar.getEvents().size());
  }

  @Test
  void testAllDayEventConflictsWithAllDay() {
    Event event1 = new Event.Builder("Holiday 1", testDate, testDate).build();
    Event event2 = new Event.Builder("Holiday 2", testDate, testDate).build();

    calendar.addEvent(event1);

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(event2);
    });
  }

  @Test
  void testAllDayEventConflictsWithTimedEvent() {
    Event allDay = new Event.Builder("All Day Event", testDate, testDate).build();
    Event timed = new Event.Builder("Timed Event", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(allDay);

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(timed);
    });
  }

  @Test
  void testNonOverlappingTimedEvents() {
    Event morning = new Event.Builder("Morning Meeting", testDate, testDate)
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(10, 0))
        .build();
    Event afternoon = new Event.Builder("Afternoon Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(morning);
    calendar.addEvent(afternoon);

    assertEquals(2, calendar.getEvents().size());
  }

  @Test
  void testGetEventByIdentifier() {
    Event event = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(event);

    Event found = calendar.getEvent("Meeting", testDate, LocalTime.of(14, 0));
    assertNotNull(found);
    assertEquals("Meeting", found.getSubject());
  }

  @Test
  void testGetEventReturnsNullWhenNotFound() {
    Event found = calendar.getEvent("Nonexistent", testDate, null);
    assertNull(found);
  }

  @Test
  void testGetEventsOnDate() {
    Event event1 = new Event.Builder("Morning Event", testDate, testDate)
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(10, 0))
        .build();
    Event event2 = new Event.Builder("Afternoon Event", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    LocalDate otherDate = LocalDate.of(2025, 11, 16);
    Event event3 = new Event.Builder("Event 3", otherDate, otherDate).build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);
    calendar.addEvent(event3);

    List<Event> eventsOn15th = calendar.getEventsOnDate(testDate);
    assertEquals(2, eventsOn15th.size());

    List<Event> eventsOn16th = calendar.getEventsOnDate(otherDate);
    assertEquals(1, eventsOn16th.size());
  }

  @Test
  void testGetEventsOnDateReturnsEmptyListWhenNone() {
    List<Event> events = calendar.getEventsOnDate(testDate);
    assertEquals(0, events.size());
  }

  @Test
  void testIsBusyWithAllDayEvent() {
    Event allDay = new Event.Builder("All Day", testDate, testDate).build();
    calendar.addEvent(allDay);

    assertTrue(calendar.isBusy(testDate, LocalTime.of(10, 0)));
    assertTrue(calendar.isBusy(testDate, LocalTime.of(23, 59)));
    assertFalse(calendar.isBusy(testDate.plusDays(1), LocalTime.of(10, 0)));
  }

  @Test
  void testIsBusyWithTimedEvent() {
    Event event = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(event);

    assertTrue(calendar.isBusy(testDate, LocalTime.of(14, 0)));
    assertTrue(calendar.isBusy(testDate, LocalTime.of(14, 30)));
    assertFalse(calendar.isBusy(testDate, LocalTime.of(15, 0)));
    assertFalse(calendar.isBusy(testDate, LocalTime.of(13, 0)));
  }

  @Test
  void testAddRecurringEventWithOccurrenceLimit() {
    Event template = new Event.Builder("Weekly Meeting", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();

    Set<DayOfWeek> days = Set.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY);
    RecurrencePattern pattern = new RecurrencePattern(days, 6);

    calendar.addRecurringEvent(template, pattern);

    assertEquals(6, calendar.getEvents().size());
  }

  @Test
  void testAddRecurringEventWithEndDate() {
    LocalDate start = LocalDate.of(2025, 11, 3); // Monday
    Event template = new Event.Builder("Standup", start, start)
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(9, 15))
        .build();

    Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    RecurrencePattern pattern = new RecurrencePattern(weekdays,
        LocalDate.of(2025, 11, 14));

    calendar.addRecurringEvent(template, pattern);

    assertTrue(calendar.getEvents().size() >= 10);
  }

  @Test
  void testUpdateSingleEvent() {
    Event original = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(original);

    Event updated = new Event.Builder("Updated Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .location("New Room")
        .build();

    calendar.updateSingleEvent("Meeting", testDate, LocalTime.of(14, 0), updated);

    Event found = calendar.getEvent("Updated Meeting", testDate, LocalTime.of(14, 0));
    assertNotNull(found);
    assertEquals("New Room", found.getLocation());
  }

  @Test
  void testUpdateNonexistentEventThrows() {
    Event updated = new Event.Builder("Ghost Event", testDate, testDate).build();

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.updateSingleEvent("Ghost Event", testDate, null, updated);
    });
  }

  @Test
  void testMultiDayEventInDateRangeQuery() {
    LocalDate start = LocalDate.of(2025, 11, 15);
    LocalDate end = LocalDate.of(2025, 11, 17);
    Event multiDay = new Event.Builder("Conference", start, end).build();

    calendar.addEvent(multiDay);

    assertEquals(1, calendar.getEventsOnDate(LocalDate.of(2025, 11, 15)).size());
    assertEquals(1, calendar.getEventsOnDate(LocalDate.of(2025, 11, 16)).size());
    assertEquals(1, calendar.getEventsOnDate(LocalDate.of(2025, 11, 17)).size());
    assertEquals(0, calendar.getEventsOnDate(LocalDate.of(2025, 11, 18)).size());
  }

  @Test
  void testRecurringEventsAllHaveSameSeriesId() {
    Event template = new Event.Builder("Daily Standup", testDate, testDate)
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(9, 15))
        .build();

    Set<DayOfWeek> days = Set.of(DayOfWeek.MONDAY);
    RecurrencePattern pattern = new RecurrencePattern(days, 3);

    calendar.addRecurringEvent(template, pattern);

    List<Event> events = calendar.getEvents();
    String firstId = events.get(0).getRecurringSeriesId();

    assertNotNull(firstId);
    for (Event e : events) {
      assertEquals(firstId, e.getRecurringSeriesId());
    }
  }

  @Test
  void testUpdateEntireRecurringSeries() {
    Event template = new Event.Builder("Team Sync", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(10, 30))
        .build();

    Set<DayOfWeek> days = Set.of(DayOfWeek.FRIDAY);
    RecurrencePattern pattern = new RecurrencePattern(days, 3);

    calendar.addRecurringEvent(template, pattern);

    String seriesId = calendar.getEvents().get(0).getRecurringSeriesId();

    Event newDetails = new Event.Builder("Updated Team Sync", testDate, testDate)
        .startTime(LocalTime.of(11, 0))
        .endTime(LocalTime.of(11, 30))
        .location("New Room")
        .build();

    calendar.updateRecurringSeries(seriesId, newDetails);

    for (Event e : calendar.getEvents()) {
      assertEquals("Updated Team Sync", e.getSubject());
      assertEquals(LocalTime.of(11, 0), e.getStartTime());
      assertEquals("New Room", e.getLocation());
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 10, 20})
  void testRecurringEventGeneratesCorrectCount(int count) {
    Event template = new Event.Builder("Recurring", testDate, testDate).build();
    Set<DayOfWeek> allDays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    RecurrencePattern pattern = new RecurrencePattern(allDays, count);

    calendar.addRecurringEvent(template, pattern);

    assertEquals(count, calendar.getEvents().size());
  }

  @Test
  void testBackToBackEventsDoNotConflict() {
    Event first = new Event.Builder("First", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    Event second = new Event.Builder("Second", testDate, testDate)
        .startTime(LocalTime.of(11, 0))
        .endTime(LocalTime.of(12, 0))
        .build();

    calendar.addEvent(first);
    calendar.addEvent(second);

    assertEquals(2, calendar.getEvents().size());
  }

  @Test
  void testUpdateRecurringFromDate() {
    Event template = new Event.Builder("Weekly Standup", testDate, testDate)
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(9, 15))
        .build();

    Set<DayOfWeek> days = Set.of(DayOfWeek.MONDAY);
    RecurrencePattern pattern = new RecurrencePattern(days, 5);

    calendar.addRecurringEvent(template, pattern);

    String seriesId = calendar.getEvents().get(0).getRecurringSeriesId();
    LocalDate thirdOccurrence = calendar.getEvents().get(2).getStartDate();

    Event newDetails = new Event.Builder("Updated Standup", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(10, 15))
        .build();

    calendar.updateRecurringFromDate(seriesId, thirdOccurrence, newDetails);

    // First two should still have old name
    assertEquals("Weekly Standup", calendar.getEvents().get(0).getSubject());
    assertEquals("Weekly Standup", calendar.getEvents().get(1).getSubject());

    // Last three should have new name
    assertEquals("Updated Standup", calendar.getEvents().get(2).getSubject());
    assertEquals("Updated Standup", calendar.getEvents().get(3).getSubject());
    assertEquals("Updated Standup", calendar.getEvents().get(4).getSubject());
  }

  @Test
  void testCsvExport() throws IOException {
    Event event = new Event.Builder("Test Event", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .location("Room 101")
        .description("Test description")
        .build();

    calendar.addEvent(event);

    String filename = "test-calendar.csv";
    calendar.exportToCsv(filename);

    // Verify file was created
    java.io.File file = new java.io.File(filename);
    assertTrue(file.exists());

    // Clean up
    file.delete();
  }

  @Test
  void testCsvEscapingWithCommas() throws IOException {
    Event event = new Event.Builder("Meeting, urgent", testDate, testDate)
        .description("Notes, with commas")
        .build();

    calendar.addEvent(event);
    calendar.exportToCsv("test-escape.csv");

    java.io.File file = new java.io.File("test-escape.csv");
    assertTrue(file.exists());
    file.delete();
  }

  @Test
  void testGetAllDayEventByIdentifier() {
    Event allDay = new Event.Builder("All Day Meeting", testDate, testDate).build();
    calendar.addEvent(allDay);

    Event found = calendar.getEvent("All Day Meeting", testDate, null);
    assertNotNull(found);
    assertEquals("All Day Meeting", found.getSubject());
  }

  @Test
  void testUpdateSingleEventFailsAndRestoresOriginal() {
    Event original = new Event.Builder("First", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();

    Event blocker = new Event.Builder("Blocker", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(original);
    calendar.addEvent(blocker);

    Event conflictingUpdate = new Event.Builder("Updated", testDate, testDate)
        .startTime(LocalTime.of(14, 30))
        .endTime(LocalTime.of(15, 30))
        .build();

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.updateSingleEvent("First", testDate, LocalTime.of(10, 0), conflictingUpdate);
    });

    // Original should still exist
    assertNotNull(calendar.getEvent("First", testDate, LocalTime.of(10, 0)));
  }

  @Test
  void testCsvExportWithSpecialCharacters() throws IOException {
    Event event = new Event.Builder("Meeting, \"urgent\"", testDate, testDate)
        .description("Notes with\nNewline and, comma")
        .location("Room \"A\"")
        .build();

    calendar.addEvent(event);
    calendar.exportToCsv("test-special.csv");

    java.io.File file = new java.io.File("test-special.csv");
    assertTrue(file.exists());
    file.delete();
  }

  @Test
  void testCsvExportWithNullFields() throws IOException {
    Event minimal = new Event.Builder("Minimal", testDate, testDate).build();
    calendar.addEvent(minimal);

    calendar.exportToCsv("test-minimal.csv");

    java.io.File file = new java.io.File("test-minimal.csv");
    assertTrue(file.exists());
    file.delete();
  }

  @Test
  void testCsvEscapeQuotesOnly() throws IOException {
    Event event = new Event.Builder("Quote\"Test", testDate, testDate).build();
    calendar.addEvent(event);
    calendar.exportToCsv("test-quotes.csv");
    new java.io.File("test-quotes.csv").delete();
  }

  @Test
  void testCsvEscapeNewlineOnly() throws IOException {
    Event event = new Event.Builder("Line\nBreak", testDate, testDate).build();
    calendar.addEvent(event);
    calendar.exportToCsv("test-newline.csv");
    new java.io.File("test-newline.csv").delete();
  }

  @Test
  void testIsBusyReturnsFalseForDifferentDate() {
    Event event = new Event.Builder("Meeting", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    calendar.addEvent(event);

    assertFalse(calendar.isBusy(testDate.plusDays(5), LocalTime.of(14, 0)));
  }

  @Test
  void testVisibilityPublicInCsvExport() throws IOException {
    Event publicEvent = new Event.Builder("Public Event", testDate, testDate)
        .visibility(Visibility.PUBLIC)
        .build();
    calendar.addEvent(publicEvent);
    calendar.exportToCsv("test-public.csv");
    new java.io.File("test-public.csv").delete();
  }

  @Test
  void testEventsWithExplicitEndTimes() {
    Event event1 = new Event.Builder("First", testDate, testDate)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();
    Event event2 = new Event.Builder("Second", testDate, testDate)
        .startTime(LocalTime.of(11, 30))
        .endTime(LocalTime.of(12, 30))
        .build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);

    assertEquals(2, calendar.getEvents().size());
  }

  @Test
  void testTwoTimedEventsOnDifferentDates() {
    Event event1 = new Event.Builder("Event 1", testDate, testDate)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    Event event2 = new Event.Builder("Event 2", testDate.plusDays(1), testDate.plusDays(1))
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    calendar.addEvent(event1);
    calendar.addEvent(event2);

    assertFalse(calendar.isBusy(testDate.plusDays(2), LocalTime.of(14, 0)));
  }

  @Test
  void testUpdateSeriesWithNullSeriesId() {
    Event standalone = new Event.Builder("Standalone", testDate, testDate).build();
    calendar.addEvent(standalone);

    Event newDetails = new Event.Builder("Updated", testDate, testDate).build();

    // This should not match anything since standalone has null series ID
    calendar.updateRecurringSeries(null, newDetails);

    // Original event should still exist unchanged
    assertEquals("Standalone", calendar.getEvents().get(0).getSubject());
  }
}
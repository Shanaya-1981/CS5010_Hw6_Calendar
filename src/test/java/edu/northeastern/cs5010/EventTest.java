package edu.northeastern.cs5010;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the Event class.
 * Tests generated with Claude AI assistance to achieve full coverage.
 */
class EventTest {

  @Test
  void testCreateBasicAllDayEvent() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Birthday", date, date).build();

    assertEquals("Birthday", event.getSubject());
    assertEquals(date, event.getStartDate());
    assertEquals(date, event.getEndDate());
    assertNull(event.getStartTime());
    assertNull(event.getEndTime());
    assertTrue(event.isAllDay());
    assertEquals(Visibility.PRIVATE, event.getVisibility());
  }

  @Test
  void testCreateTimedEvent() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Meeting", date, date)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();

    assertFalse(event.isAllDay());
    assertEquals(LocalTime.of(14, 0), event.getStartTime());
    assertEquals(LocalTime.of(15, 0), event.getEndTime());
  }

  @Test
  void testMultiDayAllDayEvent() {
    LocalDate start = LocalDate.of(2025, 11, 15);
    LocalDate end = LocalDate.of(2025, 11, 17);
    Event event = new Event.Builder("Conference", start, end).build();

    assertEquals(start, event.getStartDate());
    assertEquals(end, event.getEndDate());
    assertTrue(event.isAllDay());
  }

  @Test
  void testEventWithAllOptionalFields() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Team Meeting", date, date)
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 30))
        .location("Conference Room A")
        .description("Quarterly planning session")
        .visibility(Visibility.PUBLIC)
        .build();

    assertEquals("Conference Room A", event.getLocation());
    assertEquals("Quarterly planning session", event.getDescription());
    assertEquals(Visibility.PUBLIC, event.getVisibility());
  }

  @Test
  void testDefaultVisibilityIsPrivate() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Private Event", date, date).build();

    assertEquals(Visibility.PRIVATE, event.getVisibility());
  }

  @Test
  void testAllDayEventCannotHaveEndTime() {
    LocalDate date = LocalDate.of(2025, 11, 15);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Bad Event", date, date)
          .endTime(LocalTime.of(17, 0))
          .build();
    });

    assertTrue(exception.getMessage().contains("All-day events cannot have end time"));
  }

  @Test
  void testTimedEventMustHaveBothTimes() {
    LocalDate date = LocalDate.of(2025, 11, 15);

    assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Bad Event", date, date)
          .startTime(LocalTime.of(14, 0))
          .build();
    });
  }

  @Test
  void testEndDateCannotBeBeforeStartDate() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Time Traveler",
          LocalDate.of(2025, 11, 15),
          LocalDate.of(2025, 11, 10))
          .build();
    });
  }

  @Test
  void testEndTimeMustBeAfterStartTimeOnSameDay() {
    LocalDate date = LocalDate.of(2025, 11, 15);

    assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Backwards Event", date, date)
          .startTime(LocalTime.of(15, 0))
          .endTime(LocalTime.of(14, 0))
          .build();
    });
  }

  @Test
  void testStartAndEndTimeCanBeEqualThrowsError() {
    LocalDate date = LocalDate.of(2025, 11, 15);

    assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Zero Duration", date, date)
          .startTime(LocalTime.of(14, 0))
          .endTime(LocalTime.of(14, 0))
          .build();
    });
  }

  @ParameterizedTest
  @CsvSource({
      "Lunch, 2025-11-15, 2025-11-15",
      "Workshop, 2025-12-01, 2025-12-03",
      "Vacation, 2025-11-20, 2025-11-27"
  })
  void testCreateVariousAllDayEvents(String subject, String startStr, String endStr) {
    LocalDate start = LocalDate.parse(startStr);
    LocalDate end = LocalDate.parse(endStr);

    Event event = new Event.Builder(subject, start, end).build();

    assertEquals(subject, event.getSubject());
    assertTrue(event.isAllDay());
  }

  @Test
  void testBuilderMethodChaining() {
    LocalDate date = LocalDate.of(2025, 11, 15);

    Event event = new Event.Builder("Chained Event", date, date)
        .startTime(LocalTime.of(9, 0))
        .endTime(LocalTime.of(10, 0))
        .location("Room 101")
        .description("Testing method chaining")
        .visibility(Visibility.PUBLIC)
        .build();

    assertNotNull(event);
    assertEquals("Room 101", event.getLocation());
  }
}
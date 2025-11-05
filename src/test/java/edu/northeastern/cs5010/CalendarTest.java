package edu.northeastern.cs5010;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {

  private Calendar calendar;

  @BeforeEach
  void setup() {
    calendar = new Calendar("Test Calendar");
  }

  @Test
  void testAddEvent() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Meeting", date, date).build();

    calendar.addEvent(event);
    assertEquals(1, calendar.getEvents().size());
  }

  @Test
  void testRejectDuplicateAllDayEvent() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event1 = new Event.Builder("Birthday", date, date).build();
    Event event2 = new Event.Builder("Birthday", date, date).build();

    calendar.addEvent(event1);

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(event2);
    });
  }

  @Test
  void testRejectConflictingEvents() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event1 = new Event.Builder("Meeting 1", date, date)
        .startTime(LocalTime.of(14, 0))
        .endTime(LocalTime.of(15, 0))
        .build();
    Event event2 = new Event.Builder("Meeting 2", date, date)
        .startTime(LocalTime.of(14, 30))
        .endTime(LocalTime.of(15, 30))
        .build();

    calendar.addEvent(event1);

    assertThrows(IllegalArgumentException.class, () -> {
      calendar.addEvent(event2);
    });
  }

  @Test
  void testGetEventsOnDate() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Meeting", date, date).build();

    calendar.addEvent(event);

    assertEquals(1, calendar.getEventsOnDate(date).size());
    assertEquals(0, calendar.getEventsOnDate(LocalDate.of(2025, 11, 16)).size());
  }
}
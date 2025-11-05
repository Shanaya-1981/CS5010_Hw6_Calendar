package edu.northeastern.cs5010;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

  @Test
  void testCreateAllDayEvent() {
    LocalDate date = LocalDate.of(2025, 11, 15);
    Event event = new Event.Builder("Birthday", date, date).build();

    assertEquals("Birthday", event.getSubject());
    assertTrue(event.isAllDay());
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
  }

  @Test
  void testAllDayCannotHaveEndTime() {
    LocalDate date = LocalDate.of(2025, 11, 15);

    assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Bad Event", date, date)
          .endTime(LocalTime.of(17, 0))
          .build();
    });
  }

  @Test
  void testEndDateBeforeStartDateFails() {
    assertThrows(IllegalArgumentException.class, () -> {
      new Event.Builder("Bad Event",
          LocalDate.of(2025, 11, 15),
          LocalDate.of(2025, 11, 10))
          .build();
    });
  }
}
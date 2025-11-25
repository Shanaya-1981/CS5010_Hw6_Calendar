package edu.northeastern.cs5010;

import org.junit.jupiter.api.Test;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RecurrencePatternTest {

  @Test
  void testPatternWithOccurrenceLimit() {
    Set<DayOfWeek> days = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
    RecurrencePattern pattern = new RecurrencePattern(days, 5);

    assertEquals(5, pattern.getOccurrenceLimit());
    assertNull(pattern.getEndDate());
    assertTrue(pattern.getDaysOfWeek().contains(DayOfWeek.MONDAY));
  }

  @Test
  void testPatternWithEndDate() {
    Set<DayOfWeek> days = Set.of(DayOfWeek.TUESDAY);
    LocalDate endDate = LocalDate.of(2025, 12, 15);
    RecurrencePattern pattern = new RecurrencePattern(days, endDate);

    assertNull(pattern.getOccurrenceLimit());
    assertEquals(endDate, pattern.getEndDate());
  }
}
package edu.northeastern.cs5010;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes how an event repeats over time.
 * You specify which days of the week it should happen and when to stop repeating,
 * either after a certain number of times or on a particular date.
 */
public class RecurrencePattern {

  private final Set<DayOfWeek> daysOfWeek;
  private final Integer occurrenceLimit;
  private final LocalDate endDate;

  /**
   * Creates a pattern that repeats for a specific number of times.
   * For example, you could create a pattern for "every Monday and Wednesday, 10 times total."
   *
   * @param daysOfWeek  which days of the week to create events on
   * @param occurrences total number of events to create
   */

  public RecurrencePattern(Set<DayOfWeek> daysOfWeek, int occurrences) {
    this.daysOfWeek = new HashSet<>(daysOfWeek);  // Defensive copy
    this.occurrenceLimit = occurrences;
    this.endDate = null;
  }

  /**
   * Creates a pattern that repeats until a specific date is reached.
   * For example, "every Tuesday until December 15th."
   *
   * @param daysOfWeek which days of the week to create events on
   * @param endDate    the last date to generate events on
   */
  public RecurrencePattern(Set<DayOfWeek> daysOfWeek, LocalDate endDate) {
    this.daysOfWeek = new HashSet<>(daysOfWeek);  // Defensive copy
    this.occurrenceLimit = null;  // ADD THIS LINE - initialize it!
    this.endDate = endDate;
  }

  /**
   * Gets the days of the week when events should be created.
   *
   * @return set of days like MONDAY, WEDNESDAY, FRIDAY
   */
  public Set<DayOfWeek> getDaysOfWeek() {
    return daysOfWeek;
  }

  /**
   * Gets how many total event instances to create.
   *
   * @return the maximum number of events, or null if this pattern uses a date limit instead
   */
  public Integer getOccurrenceLimit() {
    return occurrenceLimit;
  }

  /**
   * Gets the date when we should stop generating events.
   *
   * @return the cutoff date, or null if this pattern uses an occurrence limit instead
   */
  public LocalDate getEndDate() {
    return endDate;
  }
}
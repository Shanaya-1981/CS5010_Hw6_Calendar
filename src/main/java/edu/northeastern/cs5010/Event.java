package edu.northeastern.cs5010;

import java.beans.Visibility;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents a calendar event with required and optional fields.
 */
public class Event {

  private final String subject;
  private final LocalDate startDate;
  private final LocalTime startTime;
  private final LocalDate endDate;
  private final LocalTime endTime;
  private final String location;
  private final String description;
  private final Visibility visibility;

  /**
   * Constructs an Event from a Builder.
   *
   * @param builder the Builder with event configuration
   * @throws IllegalArgumentException if event violates rules
   */
  private Event(Builder builder) {
    this.subject = builder.subject;
    this.startDate = builder.startDate;
    this.startTime = builder.startTime;
    this.endDate = builder.endDate;
    this.endTime = builder.endTime;
    this.location = builder.location;
    this.description = builder.description;
    this.visibility = builder.visibility != null ? builder.visibility : Visibility.PRIVATE;

    validateRules();
  }

  private void validateRules() {
    if (startTime == null && endTime != null) {
      throw new IllegalArgumentException("All-day events cannot have end time");
    }
    if (startTime != null && endDate == null) {
      throw new IllegalArgumentException("Events with start time must have end date");
    }
  }

  public String getSubject() {
    return subject;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public String getLocation() {
    return location;
  }

  public String getDescription() {
    return description;
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public boolean isAllDay() {
    return startTime == null;
  }

  /**
   * Builder for creating Event instances.
   */
  public static class Builder {

    private final String subject;
    private final LocalDate startDate;
    private LocalTime startTime = null;
    private LocalDate endDate = null;
    private LocalTime endTime = null;
    private String location = null;
    private String description = null;
    private Visibility visibility = null;

    public Builder(String subject, LocalDate startDate) {
      this.subject = subject;
      this.startDate = startDate;
    }

    public Builder startTime(LocalTime startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder endDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }

    public Builder endTime(LocalTime endTime) {
      this.endTime = endTime;
      return this;
    }

    public Builder location(String location) {
      this.location = location;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder visibility(Visibility visibility) {
      this.visibility = visibility;
      return this;
    }

    public Event build() {
      return new Event(this);
    }
  }
}
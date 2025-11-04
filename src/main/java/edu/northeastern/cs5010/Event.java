package edu.northeastern.cs5010;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a calendar event with both required and optional information.
 * Every event needs a subject, start date, and end date at minimum.
 * You can optionally add times, location, description, and set visibility.
 */
public class Event {

  private final String subject;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalTime startTime;
  private final LocalTime endTime;
  private final String location;
  private final String description;
  private final Visibility visibility;

  /**
   * Constructs an Event using the provided Builder.
   * This is private to ensure all Events are created through the Builder pattern.
   *
   * @param builder the Builder that contains the event's configuration
   * @throws IllegalArgumentException if the event configuration breaks any rules
   */
  private Event(Builder builder) {
    this.subject = builder.subject;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.location = builder.location;
    this.description = builder.description;
    this.visibility = builder.visibility != null ? builder.visibility : Visibility.PRIVATE;

    validate();
  }

  /**
   * Checks that the event fields follow the calendar's business rules.
   * This includes validating date ranges and ensuring times are consistent.
   *
   * @throws IllegalArgumentException if any validation rule is violated
   */
  private void validate() {
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("End date cannot be before start date");
    }

    if (startTime == null && endTime != null) {
      throw new IllegalArgumentException("All-day events cannot have end time");
    }

    if (startTime != null && endTime == null) {
      throw new IllegalArgumentException("Events with start time must also have end time");
    }

    // For same-day events with times, validate that end time is after start time
    if (startDate.equals(endDate) && startTime != null && endTime != null) {
      if (!startTime.isBefore(endTime)) {
        throw new IllegalArgumentException("End time must be after start time for same-day events");
      }
    }
  }

  /**
   * Gets the subject of this event.
   *
   * @return the event subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Gets the start date of this event.
   *
   * @return the start date
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Gets the end date of this event.
   *
   * @return the end date
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Gets the start time of this event, if it has one.
   *
   * @return the start time, or null for all-day events
   */
  public LocalTime getStartTime() {
    return startTime;
  }

  /**
   * Gets the end time of this event, if it has one.
   *
   * @return the end time, or null for all-day events
   */
  public LocalTime getEndTime() {
    return endTime;
  }

  /**
   * Gets the location of this event.
   *
   * @return the location, or null if not specified
   */
  public String getLocation() {
    return location;
  }

  /**
   * Gets the description of this event.
   *
   * @return the description, or null if not specified
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the visibility level of this event.
   *
   * @return the visibility level
   */
  public Visibility getVisibility() {
    return visibility;
  }

  /**
   * Checks whether this event spans the entire day without specific times.
   *
   * @return true if this is an all-day event
   */
  public boolean isAllDay() {
    return startTime == null;
  }

  /**
   * Builder class for constructing Event instances with a clean, readable syntax.
   * Use this to create events by specifying only the fields you need.
   */
  public static class Builder {

    private final String subject;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private LocalTime startTime = null;
    private LocalTime endTime = null;
    private String location = null;
    private String description = null;
    private Visibility visibility = null;

    /**
     * Creates a new Builder with the required event information.
     *
     * @param subject   the event subject
     * @param startDate when the event starts
     * @param endDate   when the event ends
     */
    public Builder(String subject, LocalDate startDate, LocalDate endDate) {
      this.subject = subject;
      this.startDate = startDate;
      this.endDate = endDate;
    }

    /**
     * Sets the start time for this event.
     *
     * @param startTime the start time
     * @return this Builder to allow method chaining
     */
    public Builder startTime(LocalTime startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time for this event.
     *
     * @param endTime the end time
     * @return this Builder to allow method chaining
     */
    public Builder endTime(LocalTime endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets where this event takes place.
     *
     * @param location the event location
     * @return this Builder to allow method chaining
     */
    public Builder location(String location) {
      this.location = location;
      return this;
    }

    /**
     * Sets additional details about this event.
     *
     * @param description the event description
     * @return this Builder to allow method chaining
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Sets whether this event is public or private.
     *
     * @param visibility the visibility level
     * @return this Builder to allow method chaining
     */
    public Builder visibility(Visibility visibility) {
      this.visibility = visibility;
      return this;
    }

    /**
     * Creates the Event with the configuration you specified.
     *
     * @return a new Event instance
     * @throws IllegalArgumentException if the configuration is invalid
     */
    public Event build() {
      return new Event(this);
    }
  }
}
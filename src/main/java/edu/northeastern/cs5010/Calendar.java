package edu.northeastern.cs5010;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a calendar that holds events and enforces uniqueness rules.
 * Calendars can optionally prevent time conflicts between events.
 */
public class Calendar {

  private final String title;
  private final List<Event> events;
  private final boolean allowConflicts;
  private List<CalendarListener> listeners;
  private final Map<String, Event> eventsByKey;

  /**
   * Creates a new calendar with the given title.
   * By default, this calendar will not allow conflicting events.
   *
   * @param title the name of this calendar
   */
  public Calendar(String title) {
    this(title, false);
  }

  /**
   * Creates a new calendar with the given title and conflict policy.
   *
   * @param title          the name of this calendar
   * @param allowConflicts whether to allow events with overlapping times
   */
  public Calendar(String title, boolean allowConflicts) {
    this.title = title;
    this.allowConflicts = allowConflicts;
    this.events = new ArrayList<>();
    this.listeners = new ArrayList<>();
    this.eventsByKey = new HashMap<>();
  }

  /**
   * Adds an event to this calendar if it passes validation.
   * Checks for duplicate events and optionally checks for time conflicts.
   *
   * @param event the event to add
   * @throws IllegalArgumentException if event is a duplicate
   * @throws IllegalArgumentException if event conflicts and conflicts are disabled
   */
  public void addEvent(Event event) {
    if (hasDuplicate(event)) {
      throw new IllegalArgumentException(
          "Event with same subject, date, and time already exists");
    }

    if (!allowConflicts && hasTimeConflict(event)) {
      throw new IllegalArgumentException(
          "Event conflicts with existing event");
    }

    events.add(event);
    eventsByKey.put(generateEventKey(event), event);
    announceEventAdded(event);
  }

  // Generates unique key for duplicate detection: subject+date+time
  private String generateEventKey(Event event) {
    String timeKey = event.getStartTime() != null ? event.getStartTime().toString() : "ALLDAY";
    return event.getSubject() + "|" + event.getStartDate() + "|" + timeKey;
  }

  // Checks if event with same subject, date, and time already exists
  private boolean hasDuplicate(Event newEvent) {
    return eventsByKey.containsKey(generateEventKey(newEvent));
  }

  // Checks if event has overlapping times with any existing event
  private boolean hasTimeConflict(Event newEvent) {
    for (Event existing : events) {
      if (eventsOverlap(existing, newEvent)) {
        return true;
      }
    }
    return false;
  }

  // Determines if two events have overlapping time ranges
  private boolean eventsOverlap(Event e1, Event e2) {
    boolean datesOverlap = !e1.getEndDate().isBefore(e2.getStartDate())
        &&
        !e2.getEndDate().isBefore(e1.getStartDate());

    if (!datesOverlap) {
      return false;
    }

    if (e1.isAllDay() || e2.isAllDay()) {
      return true;
    }

    LocalTime start1 = e1.getStartTime();
    LocalTime end1 = e1.getEndTime() != null ? e1.getEndTime() : e1.getStartTime();
    LocalTime start2 = e2.getStartTime();
    LocalTime end2 = e2.getEndTime() != null ? e2.getEndTime() : e2.getStartTime();

    return end1.isAfter(start2) && end2.isAfter(start1);
  }

  /**
   * Retrieves a specific event by its identifying information.
   *
   * @param subject   the event subject
   * @param startDate the start date
   * @param startTime the start time, or null for all-day events
   * @return the matching event, or null if not found
   */
  public Event getEvent(String subject, LocalDate startDate, LocalTime startTime) {
    for (Event event : events) {
      if (event.getSubject().equals(subject)
          &&
          event.getStartDate().equals(startDate)) {

        if (startTime == null && event.getStartTime() == null) {
          return event;
        }
        if (startTime != null && startTime.equals(event.getStartTime())) {
          return event;
        }
      }
    }
    return null;
  }

  /**
   * Gets whether this calendar allows conflicting events.
   *
   * @return true if conflicts are allowed, false otherwise
   */
  public boolean getAllowConflicts() {
    return allowConflicts;
  }

  /**
   * Gets all events that occur on the specified date.
   *
   * @param date the date to query
   * @return list of events on that date, possibly empty
   */
  public List<Event> getEventsOnDate(LocalDate date) {
    List<Event> result = new ArrayList<>();
    for (Event event : events) {
      if (!event.getEndDate().isBefore(date) && !event.getStartDate().isAfter(date)) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Checks if the calendar has any events at the specified date and time.
   *
   * @param date the date to check
   * @param time the time to check
   * @return true if there's an event at that moment
   */
  public boolean isBusy(LocalDate date, LocalTime time) {
    for (Event event : events) {
      if (!event.getEndDate().isBefore(date) && !event.getStartDate().isAfter(date)) {
        if (event.isAllDay()) {
          return true;
        }
        if (time != null && !time.isBefore(event.getStartTime())
            &&
            time.isBefore(event.getEndTime())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Gets the title of this calendar.
   *
   * @return the calendar title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets all events in this calendar.
   *
   * @return a copy of the events list
   */
  public List<Event> getEvents() {
    return new ArrayList<>(events);
  }

  /**
   * Exports all events in this calendar to a CSV file in Google Calendar format.
   * The generated file can be imported directly into Google Calendar.
   * Note: This method was generated with assistance from Claude AI as permitted
   * by the assignment instructions for CSV export functionality.
   *
   * @param filename the path where the CSV file should be saved
   * @throws IOException if there's an error writing to the file
   */

  public void exportToCsv(String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
      // Write the header row that Google Calendar expects
      writer.println("Subject,Start Date,Start Time,End Date,End Time,All Day Event,"
          + "Description,Location,Private");

      // Write each event as a row
      for (Event event : events) {
        String subject = escapeCsv(event.getSubject());
        String startDate = event.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String startTime = event.getStartTime() != null
            ? event.getStartTime().format(DateTimeFormatter.ofPattern("hh:mm a")) : "";
        String endDate = event.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String endTime = event.getEndTime() != null
            ? event.getEndTime().format(DateTimeFormatter.ofPattern("hh:mm a")) : "";
        String allDay = event.isAllDay() ? "True" : "False";
        String description =
            event.getDescription() != null ? escapeCsv(event.getDescription()) : "";
        String location = event.getLocation() != null ? escapeCsv(event.getLocation()) : "";
        String isPrivate = event.getVisibility() == Visibility.PRIVATE ? "True" : "False";

        writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
            subject, startDate, startTime, endDate, endTime,
            allDay, description, location, isPrivate);
      }
    }
  }

  /**
   * Adds a recurring event by generating individual instances based on the pattern.
   * Validates all instances before adding any to ensure atomicity. If one instance
   * would create a conflict or duplicate, none are added.
   *
   * @param template an example event that defines the subject, times, and other details
   * @param pattern  specifies which days to repeat on and when to stop
   * @throws IllegalArgumentException if any generated instance would create a conflict or duplicate
   */
  public void addRecurringEvent(Event template, RecurrencePattern pattern) {
    String seriesId = java.util.UUID.randomUUID().toString();
    List<Event> instances = new ArrayList<>();
    LocalDate date = template.getStartDate();
    int count = 0;

    // Phase 1: Generate and validate all instances
    while (shouldContinueRecurrence(count, date, pattern)) {
      if (pattern.getDaysOfWeek().contains(date.getDayOfWeek())) {
        Event instance = new Event.Builder(template.getSubject(), date, date)
            .startTime(template.getStartTime())
            .endTime(template.getEndTime())
            .location(template.getLocation())
            .description(template.getDescription())
            .visibility(template.getVisibility())
            .recurringSeriesId(seriesId)
            .build();

        // Validate without adding yet
        if (hasDuplicate(instance)) {
          throw new IllegalArgumentException(
              "Recurring event would create duplicate at " + date);
        }
        if (!allowConflicts && hasTimeConflict(instance)) {
          throw new IllegalArgumentException(
              "Recurring event would create conflict at " + date);
        }

        instances.add(instance);
        count++;
      }
      date = date.plusDays(1);
    }

    // Phase 2: All validated, now add them
    for (Event instance : instances) {
      events.add(instance);
      announceEventAdded(instance);
    }
  }

  // Checks if we should continue generating recurring instances
  private boolean shouldContinueRecurrence(int count, LocalDate date, RecurrencePattern pattern) {
    if (pattern.getOccurrenceLimit() != null && count >= pattern.getOccurrenceLimit()) {
      return false;
    }
    if (pattern.getEndDate() != null && date.isAfter(pattern.getEndDate())) {
      return false;
    }
    return true;
  }

  /**
   * Updates a single event that's part of a recurring series.
   * Only this one occurrence changes - the rest of the series stays the same.
   *
   * @param subject subject of the event to change
   * @param date    the specific date of the occurrence
   * @param time    the start time, or null if all-day
   * @param updated the new version of this event
   * @throws IllegalArgumentException if event not found or update creates conflict
   */
  public void updateSingleEvent(String subject, LocalDate date, LocalTime time, Event updated) {
    Event original = getEvent(subject, date, time);
    if (original == null) {
      throw new IllegalArgumentException("Cannot find event to update");
    }

    events.remove(original);
    eventsByKey.remove(generateEventKey(original));

    try {
      addEvent(updated);
      announceEventModified(updated);
    } catch (IllegalArgumentException e) {
      events.add(original);
      eventsByKey.put(generateEventKey(original), original);
      throw e;
    }
  }

  /**
   * Updates all events in a recurring series with new details.
   * Finds every event that shares the given series ID and applies the changes.
   *
   * @param seriesId   the ID linking the recurring events together
   * @param newDetails the updated event information to apply
   */
  public void updateRecurringSeries(String seriesId, Event newDetails) {
    List<Event> toChange = new ArrayList<>();

    for (Event event : events) {
      if (seriesId != null && seriesId.equals(event.getRecurringSeriesId())) {
        toChange.add(event);
      }
    }

    for (Event old : toChange) {
      events.remove(old);

      Event updated = new Event.Builder(newDetails.getSubject(),
          old.getStartDate(), old.getStartDate())
          .startTime(newDetails.getStartTime())
          .endTime(newDetails.getEndTime())
          .location(newDetails.getLocation())
          .description(newDetails.getDescription())
          .visibility(newDetails.getVisibility())
          .recurringSeriesId(seriesId)
          .build();

      events.add(updated);
      announceEventModified(updated);
    }
  }

  /**
   * Updates all future instances in a recurring series starting from a specific date.
   * Events before this date stay unchanged, but this date and all future occurrences
   * get the new details.
   *
   * @param seriesId   the ID of the recurring series
   * @param fromDate   the date to start applying changes (inclusive)
   * @param newDetails the updated event information
   */
  public void updateRecurringFromDate(String seriesId, LocalDate fromDate, Event newDetails) {
    List<Event> toChange = new ArrayList<>();

    for (Event event : events) {
      if (seriesId.equals(event.getRecurringSeriesId())
          &&
          !event.getStartDate().isBefore(fromDate)) {
        toChange.add(event);
      }
    }

    for (Event old : toChange) {
      events.remove(old);

      Event updated = new Event.Builder(newDetails.getSubject(),
          old.getStartDate(), old.getStartDate())
          .startTime(newDetails.getStartTime())
          .endTime(newDetails.getEndTime())
          .location(newDetails.getLocation())
          .description(newDetails.getDescription())
          .visibility(newDetails.getVisibility())
          .recurringSeriesId(seriesId)
          .build();

      events.add(updated);
      announceEventModified(updated);
    }
  }

  /**
   * Registers a listener to receive notifications when events are added or modified.
   * The listener will be called whenever changes occur in this calendar.
   *
   * @param listener the listener to register
   */
  public void addCalendarListener(CalendarListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Removes a previously registered listener so it no longer receives notifications.
   *
   * @param listener the listener to remove
   */
  public void removeCalendarListener(CalendarListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notifies all registered listeners that an event has been added to the calendar.
   *
   * @param event the event that was added
   */
  protected void announceEventAdded(Event event) {
    for (CalendarListener listener : listeners) {
      listener.onEventAdded(event);
    }
  }

  /**
   * Notifies all registered listeners that an event has been modified.
   *
   * @param event the event that was modified
   */
  protected void announceEventModified(Event event) {
    for (CalendarListener listener : listeners) {
      listener.onEventModified(event);
    }
  }

  // Escapes special characters in CSV fields by wrapping in quotes when needed
  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }

    String escaped = value.replace("\"", "\"\"");

    if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
      return "\"" + escaped + "\"";
    }

    return escaped;
  }
}
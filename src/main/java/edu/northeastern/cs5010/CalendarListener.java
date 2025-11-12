package edu.northeastern.cs5010;

/**
 * Interface for objects that want to be notified when calendar events change.
 * Listeners can register with a Calendar to receive notifications when events
 * are added or modified.
 */
public interface CalendarListener {

  /**
   * Called when an event is added to the calendar.
   *
   * @param event the event that was added
   */
  void onEventAdded(Event event);

  /**
   * Called when an event is modified in the calendar.
   *
   * @param event the event that was modified
   */
  void onEventModified(Event event);
}
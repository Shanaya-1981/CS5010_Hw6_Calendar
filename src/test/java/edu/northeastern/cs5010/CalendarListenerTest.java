package edu.northeastern.cs5010;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Observer pattern implementation in Calendar.
 * These tests verify that listeners are properly notified of calendar changes.
 */
public class CalendarListenerTest {

  // Helper class to track notifications
  private static class TestListener implements CalendarListener {

    private List<Event> addedEvents = new ArrayList<>();
    private List<Event> modifiedEvents = new ArrayList<>();

    @Override
    public void onEventAdded(Event event) {
      addedEvents.add(event);
    }

    @Override
    public void onEventModified(Event event) {
      modifiedEvents.add(event);
    }

    public int getAddedCount() {
      return addedEvents.size();
    }

    public int getModifiedCount() {
      return modifiedEvents.size();
    }

    public boolean receivedAdd(Event event) {
      return addedEvents.contains(event);
    }

    public void reset() {
      addedEvents.clear();
      modifiedEvents.clear();
    }
  }

  private Calendar calendar;

  @BeforeEach
  public void setUp() {
    calendar = new Calendar("Test Calendar");
  }

  @Test
  public void testSingleListenerReceivesAddNotification() {
    // TODO: Create a TestListener
    TestListener testListener = new TestListener();

    // TODO: Register it with calendar
    calendar.addCalendarListener(testListener);
    // TODO: Add an event
    Event event = new Event.Builder("Chemistry Class",
        LocalDate.of(2025, 12, 16),
        LocalDate.of(2025, 12, 16))
        .startTime(LocalTime.of(2, 0))
        .endTime(LocalTime.of(4, 0))
        .build();

    // TODO: Assert listener received exactly 1 notification
    calendar.addEvent(event);
    assertEquals(1, testListener.getAddedCount(), "Listener received exactly 1 notification");
  }

  @Test
  public void testMultipleListenersAllNotified() {

    TestListener listener1 = new TestListener();
    TestListener listener2 = new TestListener();
    TestListener listener3 = new TestListener();

    calendar.addCalendarListener(listener1);
    calendar.addCalendarListener(listener2);
    calendar.addCalendarListener(listener3);

    Event event = new Event.Builder("History Class",
        LocalDate.of(2025, 11, 16),
        LocalDate.of(2025, 11, 16))
        .startTime(LocalTime.of(2, 0))
        .endTime(LocalTime.of(3, 0))
        .build();

    calendar.addEvent(event);
    assertEquals(1, listener1.getAddedCount(),
        "Added listener should receive 1 notifications");
    assertEquals(1, listener2.getAddedCount(),
        "Added listener should receive 1 notifications");
    assertEquals(1, listener3.getAddedCount(),
        "Added listener should receive 1 notifications");
  }

  @Test
  public void testRemovedListenerNotNotified() {
    TestListener listener = new TestListener();

    calendar.addCalendarListener(listener);
    calendar.removeCalendarListener(listener);

    Event event = new Event.Builder("Meeting",
        LocalDate.of(2025, 11, 15),
        LocalDate.of(2025, 11, 15))
        .startTime(LocalTime.of(10, 0))
        .endTime(LocalTime.of(11, 0))
        .build();

    calendar.addEvent(event);

    assertEquals(0, listener.getAddedCount(),
        "Removed listener should not receive notifications");
  }

  @Test
  public void testModifyNotification() {
    TestListener listener = new TestListener();
    calendar.addCalendarListener(listener);

    Event originalEvent = new Event.Builder("Chemistry Class",
        LocalDate.of(2025, 12, 16),
        LocalDate.of(2025, 12, 16))
        .startTime(LocalTime.of(2, 0))
        .endTime(LocalTime.of(4, 0))
        .build();

    calendar.addEvent(originalEvent);

    Event updatedEvent = new Event.Builder("Advanced Chemistry",  // NEW title!
        LocalDate.of(2025, 12, 16),
        LocalDate.of(2025, 12, 16))
        .startTime(LocalTime.of(2, 0))
        .endTime(LocalTime.of(4, 0))
        .build();

    calendar.updateSingleEvent(
        "Chemistry Class",
        LocalDate.of(2025, 12, 16),
        LocalTime.of(2, 0),
        updatedEvent
    );

    assertEquals(1, listener.getModifiedCount(),
        "Listener should receive 1 modify notification");
  }
}
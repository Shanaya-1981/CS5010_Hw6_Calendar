package edu.northeastern.cs5010;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saving and loading of calendars to/from files.
 * Handles both single calendar CSV import/export and persistence of multiple calendars.
 */
public class CalendarManager {

  /**
   * Imports a single calendar from a CSV file in Google Calendar format.
   *
   * @param filename the CSV file to read from
   * @return the imported Calendar with all its events
   * @throws IOException if the file cannot be read
   */
  public static Calendar importFromCsv(String filename) throws IOException {
    // Get calendar name from filename (remove .csv)
    String calendarName = filename.replace(".csv", "");
    Calendar calendar = new Calendar(calendarName);

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

      String line = reader.readLine(); // Skip header

      while ((line = reader.readLine()) != null) {
        String[] fields = line.split(
            ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // Split on comma outside quotes

        if (fields.length < 9) {
          continue; // Skip bad lines
        }

        String subject = fields[0].replace("\"", "");
        LocalDate startDate = LocalDate.parse(fields[1], dateFormat);
        LocalDate endDate = LocalDate.parse(fields[3], dateFormat);
        boolean isAllDay = fields[5].equals("True");

        Event.Builder builder = new Event.Builder(subject, startDate, endDate);

        // Add times if not all-day
        if (!isAllDay && !fields[2].isEmpty()) {
          LocalTime startTime = LocalTime.parse(fields[2], timeFormat);
          LocalTime endTime = LocalTime.parse(fields[4], timeFormat);
          builder.startTime(startTime).endTime(endTime);
        }

        // Optional fields
        if (fields.length > 6 && !fields[6].isEmpty()) {
          builder.description(fields[6].replace("\"", ""));
        }
        if (fields.length > 7 && !fields[7].isEmpty()) {
          builder.location(fields[7].replace("\"", ""));
        }
        if (fields.length > 8 && fields[8].equals("True")) {
          builder.visibility(Visibility.PRIVATE);
        }

        calendar.addEvent(builder.build());
      }
    }

    return calendar;
  }

  /**
   * Saves all calendars to separate CSV files.
   * Each calendar is saved as {filename}_0.csv, {filename}_1.csv, etc.
   *
   * @param calendars    the list of calendars to save
   * @param baseFilename the base filename (without extension)
   * @throws IOException if files cannot be written
   */
  public static void saveAllCalendars(List<Calendar> calendars, String baseFilename)
      throws IOException {
    for (int i = 0; i < calendars.size(); i++) {
      String filename = baseFilename + "_" + i + ".csv";
      calendars.get(i).exportToCsv(filename);
    }
  }

  /**
   * Restores all calendars from CSV files.
   * Looks for {baseFilename}_0.csv, {baseFilename}_1.csv, etc.
   *
   * @param baseFilename the base filename (without extension)
   * @return list of restored calendars
   * @throws IOException if files cannot be read
   */
  public static List<Calendar> restoreAllCalendars(String baseFilename) throws IOException {
    List<Calendar> calendars = new ArrayList<>();
    int i = 0;

    while (true) {
      String filename = baseFilename + "_" + i + ".csv";
      File file = new File(filename);

      if (!file.exists()) {
        break;  // No more files
      }

      calendars.add(importFromCsv(filename));
      i++;
    }

    return calendars;
  }
}
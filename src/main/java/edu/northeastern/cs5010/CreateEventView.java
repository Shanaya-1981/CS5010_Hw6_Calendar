package edu.northeastern.cs5010;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A Swing form for creating new events and adding them to a calendar.
 */
public class CreateEventView extends JFrame {

  private final Calendar calendar;

  private JTextField titleField;
  private JTextField startDateField;
  private JTextField startTimeField;
  private JTextField endDateField;
  private JTextField endTimeField;
  private JTextField locationField;
  private JTextArea descriptionArea;
  private JCheckBox allDayCheckBox;
  private JComboBox<String> visibilityComboBox;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a",
      java.util.Locale.US);

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public CreateEventView(Calendar calendar) {
    this.calendar = calendar;
    initializeUI();
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  private void initializeUI() {
    setTitle("Create New Event - " + calendar.getTitle());
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;

    // Title
    addLabel(formPanel, gbc, row, "Event Title:*");
    titleField = new JTextField(30);
    addField(formPanel, gbc, row++, titleField);

    // Start Date
    addLabel(formPanel, gbc, row, "Start Date:*");
    startDateField = new JTextField(15);
    startDateField.setToolTipText("Format: MM/dd/yyyy (e.g., 11/15/2025)");
    addField(formPanel, gbc, row++, startDateField);

    // Start Time
    addLabel(formPanel, gbc, row, "Start Time:");
    startTimeField = new JTextField(15);
    startTimeField.setToolTipText("Format: hh:mm AM/PM (e.g., 02:30 PM)");
    addField(formPanel, gbc, row++, startTimeField);

    // End Date
    addLabel(formPanel, gbc, row, "End Date:*");
    endDateField = new JTextField(15);
    endDateField.setToolTipText("Format: MM/dd/yyyy (e.g., 11/15/2025)");
    addField(formPanel, gbc, row++, endDateField);

    // End Time
    addLabel(formPanel, gbc, row, "End Time:");
    endTimeField = new JTextField(15);
    endTimeField.setToolTipText("Format: hh:mm AM/PM (e.g., 03:30 PM)");
    addField(formPanel, gbc, row++, endTimeField);

    // All Day
    addLabel(formPanel, gbc, row, "All Day Event:");
    allDayCheckBox = new JCheckBox();
    allDayCheckBox.addActionListener(e -> toggleTimeFields());
    addField(formPanel, gbc, row++, allDayCheckBox);

    // Location
    addLabel(formPanel, gbc, row, "Location:");
    locationField = new JTextField(30);
    addField(formPanel, gbc, row++, locationField);

    // Description
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    formPanel.add(new JLabel("Description:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    descriptionArea = new JTextArea(4, 30);
    descriptionArea.setLineWrap(true);
    JScrollPane descScrollPane = new JScrollPane(descriptionArea);
    formPanel.add(descScrollPane, gbc);
    row++;

    gbc.weighty = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;

    // Visibility
    addLabel(formPanel, gbc, row, "Visibility:");
    visibilityComboBox = new JComboBox<>(new String[]{"PUBLIC", "PRIVATE"});
    addField(formPanel, gbc, row++, visibilityComboBox);

    add(formPanel, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);

    JButton createButton = new JButton("Create Event");
    createButton.addActionListener(e -> createEvent());
    buttonPanel.add(createButton);

    add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setMinimumSize(new Dimension(500, 550));
    setLocationRelativeTo(null);
  }

  private void addLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    panel.add(new JLabel(text), gbc);
  }

  private void addField(JPanel panel, GridBagConstraints gbc, int row, Component field) {
    gbc.gridx = 1;
    gbc.gridy = row;
    gbc.weightx = 1;
    panel.add(field, gbc);
  }

  private void toggleTimeFields() {
    boolean isAllDay = allDayCheckBox.isSelected();
    startTimeField.setEnabled(!isAllDay);
    endTimeField.setEnabled(!isAllDay);
    if (isAllDay) {
      startTimeField.setText("");
      endTimeField.setText("");
    }
  }

  private void createEvent() {
    try {
      String title = titleField.getText().trim();
      if (title.isEmpty()) {
        showError("Event title is required.");
        return;
      }

      LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMATTER);
      LocalDate endDate = LocalDate.parse(endDateField.getText().trim(), DATE_FORMATTER);

      // YOUR Event.Builder needs all 3: subject, startDate, endDate
      Event.Builder builder = new Event.Builder(title, startDate, endDate);

      boolean isAllDay = allDayCheckBox.isSelected();

      if (!isAllDay) {
        String startTimeStr = startTimeField.getText().trim();
        String endTimeStr = endTimeField.getText().trim();

        if (!startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
          LocalTime startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
          LocalTime endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
          builder.startTime(startTime).endTime(endTime);
        }
      }

      String location = locationField.getText().trim();
      if (!location.isEmpty()) {
        builder.location(location);
      }

      String description = descriptionArea.getText().trim();
      if (!description.isEmpty()) {
        builder.description(description);
      }

      String visibility = (String) visibilityComboBox.getSelectedItem();
      builder.visibility(Visibility.valueOf(visibility));

      Event event = builder.build();
      calendar.addEvent(event);

      showSuccess("Event \"" + title + "\" created successfully!");
      clearForm();

    } catch (DateTimeParseException e) {
      showError("Invalid date/time format. Check your entries.");
    } catch (Exception e) {
      showError("Error creating event: " + e.getMessage());
    }
  }

  private void clearForm() {
    titleField.setText("");
    startDateField.setText("");
    startTimeField.setText("");
    endDateField.setText("");
    endTimeField.setText("");
    locationField.setText("");
    descriptionArea.setText("");
    allDayCheckBox.setSelected(false);
    visibilityComboBox.setSelectedIndex(0);
    toggleTimeFields();
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private void showSuccess(String message) {
    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
  }
}
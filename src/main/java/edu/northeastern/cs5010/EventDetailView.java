package edu.northeastern.cs5010;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * A view for displaying and modifying event details.
 */
public class EventDetailView extends JFrame {

  private final Event event;
  private final Calendar calendar;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

  public EventDetailView(Event event, Calendar calendar) {
    this.event = event;
    this.calendar = calendar;
    initializeUI();
  }

  private void initializeUI() {
    setTitle("Event Details");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    int row = 0;

    addDetailRow(detailsPanel, gbc, row++, "Title:", event.getSubject());
    addDetailRow(detailsPanel, gbc, row++, "Start Date:",
        event.getStartDate().format(DATE_FORMATTER));
    addDetailRow(detailsPanel, gbc, row++, "End Date:", event.getEndDate().format(DATE_FORMATTER));

    if (event.getStartTime() != null) {
      addDetailRow(detailsPanel, gbc, row++, "Start Time:",
          event.getStartTime().format(TIME_FORMATTER));
    }

    if (event.getEndTime() != null) {
      addDetailRow(detailsPanel, gbc, row++, "End Time:",
          event.getEndTime().format(TIME_FORMATTER));
    }

    if (event.getLocation() != null) {
      addDetailRow(detailsPanel, gbc, row++, "Location:", event.getLocation());
    }

    if (event.getDescription() != null) {
      addDetailRow(detailsPanel, gbc, row++, "Description:", event.getDescription());
    }

    addDetailRow(detailsPanel, gbc, row++, "Visibility:", event.getVisibility().toString());

    add(detailsPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());
    buttonPanel.add(closeButton);

    add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setMinimumSize(new Dimension(400, 300));
    setLocationRelativeTo(null);
  }

  private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label,
      String value) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    JLabel labelComp = new JLabel(label);
    labelComp.setFont(labelComp.getFont().deriveFont(Font.BOLD));
    panel.add(labelComp, gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(new JLabel(value), gbc);
  }
}
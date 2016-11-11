package gui_swing_worker_deserialize_json;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Clara on 11/11/16.
 * Uses a SwingWorker to fetch times in the background.
 */

public class NextBusGUI extends JFrame {

    JTextArea busTimes;
    JButton getTimesButton;
    JLabel statusLabel;
    JPanel mainPanel;

    NextBusGUI() {
        addComponents();
        addListeners();
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(400, 400));
        pack();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }


    void timesFetched(BusDeparture[] times) {

        if (times == null) {
            statusLabel.setText("Error fetching times");

        } else if (times.length == 0) {
            statusLabel.setText("No upcoming departures found");

        } else {

            String displayString = "";

            for (BusDeparture departure : times) {
                String timeReportedFromBus = departure.getActual().equalsIgnoreCase("True") ? " (actual time reported by the bus)" : " (from schedule)";

                String departureString = departure.getDepartureText() + timeReportedFromBus;
                displayString = displayString + departureString + "\n";

            }

            busTimes.setText(displayString);
            busTimes.setCaretPosition(0);     //this scrolls the JScrollPane back to the top, by default it will scroll to the end.

            statusLabel.setText("Fetched times");
        }
    }


    private void addListeners() {

        getTimesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Fetching times...");
                NextBus.getTimes(NextBusGUI.this);
            }
        });

    }


    private void addComponents() {
        busTimes = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(busTimes);
        busTimes.setEditable(false);     //Can't edit, just for displaying times

        getTimesButton = new JButton("Get bus times");
        statusLabel = new JLabel("Click button to get bus times from MCTC to First Avenue");

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(getTimesButton, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

    }


    public static void main(String[] args) {

        NextBusGUI gui = new NextBusGUI();

    }

}
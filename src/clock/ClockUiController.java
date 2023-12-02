/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package clock;

import java.net.URL;
import java.security.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * FXML Controller class
 *
 * @author jcarl
 */
public class ClockUiController implements Initializable {

    @FXML
    private AnchorPane clockPane;
    @FXML
    private Label lblTime;
    @FXML
    private AnchorPane timerPane;
    @FXML
    private AnchorPane stopwatchPane;
    @FXML
    private Label lblTimer;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnClock;
    @FXML
    private Button btnTimer;
    @FXML
    private Button btnStopwatch;
    @FXML
    private Pane bottomNavigation;
    @FXML
    private Label lblTimerTime;
    @FXML
    private Button btnTimerStart;
    @FXML
    private Button btnTimerStop;
    @FXML
    private TextField txtHour;
    @FXML
    private TextField txtMinute;
    @FXML
    private TextField txtSecond;
    @FXML
    private Button btnTimerPause;
    @FXML
    private AnchorPane alarmPane;
    @FXML
    private Button btnAlarm;

    @FXML
    private TextField txtLabel;
    @FXML
    private Button btnSet;
    @FXML
    private ComboBox<String> comboBoxRepeat;
    @FXML
    private TextField txtAMPM;
    @FXML
    private TextField txtTimeAlarm;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        bottomNavigation.setVisible(true);
        clockPane.setVisible(true);
        alarmPane.setVisible(false);
        timerPane.setVisible(false);
        stopwatchPane.setVisible(false);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), this::updateTimer));
        timeline.setCycleCount(Timeline.INDEFINITE);

        comboBoxRepeat.getItems().addAll("Once", "Daily", "Weekdays", "Weekends");

        timeNow();
    }

    private final boolean stop = false;
    private Timeline timeline;
    private boolean running = false;
    private int seconds = 0;
    private Timeline countdownTimeline;
    private int countdownSeconds;
    private int hour;
    private int minute;
    private int second;
    private Thread timerThread;
    private java.sql.Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private void setButtonColor(Button button, boolean isSelected) {
        if (isSelected) {
            button.getStyleClass().add("selected-button");
        } else {
            button.getStyleClass().remove("selected-button");
        }
    }

    private Button lastClickedButton = null;

    @FXML
    public void SwitchForm(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (clickedButton == lastClickedButton) {
            // Ignore the click if the same button was clicked twice in a row
            return;
        }

        // Reset the color of the last clicked button
        if (lastClickedButton != null) {
            setButtonColor(lastClickedButton, false);
        }

        // Update the last clicked button
        lastClickedButton = clickedButton;

        if (clickedButton == btnClock) {
            setButtonColor(btnClock, true);
            setButtonColor(btnAlarm, false);
            setButtonColor(btnTimer, false);
            setButtonColor(btnStopwatch, false);

            bottomNavigation.setVisible(true);
            clockPane.setVisible(true);
            alarmPane.setVisible(false);
            timerPane.setVisible(false);
            stopwatchPane.setVisible(false);

        } else if (clickedButton == btnAlarm) {
            setButtonColor(btnClock, false);
            setButtonColor(btnAlarm, true);
            setButtonColor(btnTimer, false);
            setButtonColor(btnStopwatch, false);

            bottomNavigation.setVisible(true);
            clockPane.setVisible(false);
            alarmPane.setVisible(true);
            timerPane.setVisible(false);
            stopwatchPane.setVisible(false);

        } else if (clickedButton == btnTimer) {
            setButtonColor(btnClock, false);
            setButtonColor(btnAlarm, false);
            setButtonColor(btnTimer, true);
            setButtonColor(btnStopwatch, false);

            bottomNavigation.setVisible(true);
            clockPane.setVisible(false);
            alarmPane.setVisible(false);
            timerPane.setVisible(true);
            stopwatchPane.setVisible(false);

        } else if (clickedButton == btnStopwatch) {
            setButtonColor(btnClock, false);
            setButtonColor(btnAlarm, false);
            setButtonColor(btnTimer, false);
            setButtonColor(btnStopwatch, true);

            bottomNavigation.setVisible(true);
            clockPane.setVisible(false);
            alarmPane.setVisible(false);
            timerPane.setVisible(false);
            stopwatchPane.setVisible(true);
        }
    }

    private void timeNow() {
        Thread thread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("hh : mm : ss");
            while (!stop) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
                final String timeNow = sdf.format(new Date());
                Platform.runLater(() -> {
                    lblTime.setText(timeNow);
                });
            }
        });

        thread.start();
    }

    @FXML
    public void handleStartTimer(ActionEvent event) {
        // Check if the timer thread is already running
        if (timerThread == null || !timerThread.isAlive()) {
            try {
                hour = txtHour.getText().isEmpty() ? 0 : Integer.parseInt(txtHour.getText());
                minute = txtMinute.getText().isEmpty() ? 0 : Integer.parseInt(txtMinute.getText());
                second = txtSecond.getText().isEmpty() ? 0 : Integer.parseInt(txtSecond.getText());

                // Create a new thread for the timer
                timerThread = new Thread(() -> {
                    while (!Thread.interrupted()) {
                        try {
                            Thread.sleep(1000);
                            second--;
                            if (second < 0) {
                                second = 59;
                                minute--;
                                if (minute < 0) {
                                    minute = 59;
                                    hour--;
                                    if (hour < 0) {
                                        // Timer has expired
                                        break;
                                    }
                                }
                            }

                            Platform.runLater(() -> {
                                lblTimerTime.setText(String.format("%02d : %02d : %02d", hour, minute, second));
                            });
                        } catch (InterruptedException e) {
                            // Thread interrupted, stop the timer
                            break;
                        }
                    }

                    // Timer has expired, play an alarm sound
                    System.out.println("Beep!");
                });

                // Start the timer thread
                timerThread.start();
            } catch (NumberFormatException e) {
                // Handle the case where the input is not a valid integer
                e.printStackTrace(); // You might want to log the error or display a message to the user
            }
        }
    }

    @FXML
    public void handlePauseTimer(ActionEvent event) {
        // Interrupt the timer thread to pause it
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    @FXML
    public void handleStopTimer(ActionEvent event) {
        // Interrupt the timer thread to stop it
        if (timerThread != null) {
            timerThread.interrupt();
        }

        // Reset the time to 0
        hour = 0;
        minute = 0;
        second = 0;

        // Update the UI
        Platform.runLater(() -> {
            lblTimerTime.setText(String.format("%02d : %02d : %02d", hour, minute, second));
        });
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        if (!running) {
            timeline.play();
            running = true;
        }
    }

    @FXML
    private void handlePause(ActionEvent event) {
        if (running) {
            timeline.pause();
            running = false;
        }
    }

    @FXML
    private void handleStop(ActionEvent event) {
        timeline.stop();
        running = false;
        seconds = 0;
        updateTimer(null);
    }

    private void updateTimer(ActionEvent event) {
        seconds++;
        lblTimer.setText(formatTime(seconds));
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int remainingSeconds = totalSeconds % 60;

        return String.format("%02d : %02d : %02d", hours, minutes, remainingSeconds);
    }

}

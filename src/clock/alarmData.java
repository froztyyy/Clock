package clock;

import java.sql.Time;

public class alarmData {
    private String time;
    private String repeatOption;
    private String label;

    public alarmData(String time, String repeatOption, String label) {
        this.time = time;
        this.repeatOption = repeatOption;
        this.label = label;
    }

    // Getters and setters (generated automatically or manually)

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRepeatOption() {
        return repeatOption;
    }

    public void setRepeatOption(String repeatOption) {
        this.repeatOption = repeatOption;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

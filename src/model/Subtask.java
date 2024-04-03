package model;

import util.TimeFormatter;

import java.util.Objects;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(int id, String title, String description, StatusOfTasks status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
        this.type = TypeOfTask.SUBTASK;
    }

    public Subtask(int id, String title, String description, StatusOfTasks status, int epicId, long duration, String startTime) {
        super(id, title, description, status, duration, startTime);
        this.epicId = epicId;
        this.type = TypeOfTask.SUBTASK;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String durationString = (duration != null) ? duration.toMinutes() + "" : "-";
        String startTimeString = (startTime != null) ? startTime.format(TimeFormatter.TIME_FORMATTER) : "-";
        String endTimeString = (getEndTime() != null) ? getEndTime().format(TimeFormatter.TIME_FORMATTER) : "-";
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, type, title, status, description, epicId, durationString, startTimeString, endTimeString);
    }
}

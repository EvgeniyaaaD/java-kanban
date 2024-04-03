package model;

import util.TimeFormatter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected TypeOfTask type;
    protected StatusOfTasks status;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Task(int id, String title, String description, StatusOfTasks status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TypeOfTask.TASK;
    }

    public Task(String title, String description, StatusOfTasks status) {

        this.title = title;
        this.description = description;
        this.status = status;
        this.type = TypeOfTask.TASK;
    }

    public Task(int id, String title, String description, StatusOfTasks status, long duration, String startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime, TimeFormatter.TIME_FORMATTER);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusOfTasks getStatus() {
        return status;
    }

    public void setStatus(StatusOfTasks status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (duration != null && startTime != null) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String durationString = (duration != null) ? duration.toMinutes() + "" : "-";
        String startTimeString = (startTime != null) ? startTime.format(TimeFormatter.TIME_FORMATTER) : "-";
        String endTimeString = (getEndTime() != null) ? getEndTime().format(TimeFormatter.TIME_FORMATTER) : "-";
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, type, title, status, description, "-", durationString, startTimeString, endTimeString);
    }
}

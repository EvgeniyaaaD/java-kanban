package model;

import util.TimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> subtaskId = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description, null);
        this.status = StatusOfTasks.NEW;
        this.type = TypeOfTask.EPIC;
    }


    public List<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(List<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    public void addSubtaskId(int subId) {
        subtaskId.add(subId);
    }

    public void removeSubtaskId(int subId) {
        if (subtaskId.contains(subId)) {
            subtaskId.remove(Integer.valueOf(subId));
        }
    }

    public void removeAllSubtasks() {
        subtaskId.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskId, epic.subtaskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    @Override
    public String toString() {
        String durationString = (duration != null) ? duration.toMinutes() + "" : "-";
        String startTimeString = (startTime != null) ? startTime.format(TimeFormatter.TIME_FORMATTER) : "-";
        String endTimeString = (endTime != null) ? endTime.format(TimeFormatter.TIME_FORMATTER) : "-";
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                id, type, title, status, description, "-", durationString, startTimeString, endTimeString);
    }
}

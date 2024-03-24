package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected ArrayList<Integer> subtaskId = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description, null);
        this.status = StatusOfTasks.NEW;
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
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
        return "domain.model.Epic{" +
                "subtaskId=" + subtaskId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManagement implements HistoryManager {
    private final List<Task> history;


    public InMemoryHistoryManagement() {
        this.history = new ArrayList<>();
    }

    public List<Task> getHistory() {
        return history;
    }

    public void addTaskHistory(Task task) {
        if (this.history.size() < 10) {
            this.history.add(task);
        } else {
            this.history.remove(0);
            this.history.add(task);
        }
    }
}

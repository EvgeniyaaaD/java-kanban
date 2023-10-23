package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManagement implements HistoryManager {
    private final List<Task> history;
    private static final int MAX_VAlUE_HISTORY = 10;


    public InMemoryHistoryManagement() {
        this.history = new LinkedList<>();
    }

    public List<Task> getHistory() {
        List<Task> copyHistory = history;
        return copyHistory;
    }

    public void add(Task task) {
        if (this.history.size() >= MAX_VAlUE_HISTORY) {
            this.history.remove(0);
        }
        this.history.add(task);
    }
}

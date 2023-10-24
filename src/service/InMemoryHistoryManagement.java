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
        return new ArrayList<>(history);
    }

    public void add(Task task) {
        if (history.size() >= MAX_VAlUE_HISTORY) {
            history.remove(0);
        }
        history.add(task);
    }
}

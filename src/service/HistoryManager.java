package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    List<Integer> getHistory();

    void addTask(int taskId);

    void remove(int id);
}

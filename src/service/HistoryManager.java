package service;

import java.util.List;

public interface HistoryManager {

    List<Integer> getHistory();

    void addTask(int taskId);

    void remove(int id);
}

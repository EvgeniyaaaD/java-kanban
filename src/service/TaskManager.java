package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {

    void add(Task task);

    List<Integer> getHistoryTasks();

    void add(Subtask subtask);

    void add(Epic epic);

    Map<Integer, Task> getAllTasks();

    Map<Integer, Subtask> getAllSubtasks();

    Map<Integer, Epic> getAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Epic findEpicById(int id);

    Task findTaskById(int id);

    Subtask findSubtaskById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void update(Task task);

    void update(Subtask subtask);

    void update(Epic epic);

    void updateStatus(Epic epic);

}

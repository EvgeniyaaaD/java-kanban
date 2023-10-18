package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    void add(Task task);

    List<Task> getHistoryManager();

    void add(Subtask subtask);

    void add(Epic epic);

    void printAllTasks();

    void printAllSubtasks();

    void printAllEpics();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task findById(int id);

    Subtask findSubtaskById(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void printAllEpicSubtasks(Epic epic);

    void update(Task task);

    void update(Subtask subtask);

    void update(Epic epic);

    void updateStatus(Epic epic);

}

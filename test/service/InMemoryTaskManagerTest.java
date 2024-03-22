package service;

import model.StatusOfTasks;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(Managers.getHistoryManagement());
    TaskManager manager = Managers.getTaskManagement();
    HistoryManager historyManager = Managers.getHistoryManagement();

    @BeforeEach
    void beforeEach() {
        Task task1 = new Task(-1, "Задача 1", "Покормить кота", StatusOfTasks.NEW);
        inMemoryTaskManager.add(task1);
        Task task2 = new Task(-1, "Задача 2", "Повесить картину", StatusOfTasks.NEW);
        inMemoryTaskManager.add(task2);
    }

    @Test
    void deleteAllTasksDeleteBrowsingHistoryOfTheseTasks() {
        manager.deleteAllTasks();
        List<Integer> result = historyManager.getHistory();
        assertEquals(0, result.size());
    }
}
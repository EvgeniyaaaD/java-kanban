package service;

import model.StatusOfTasks;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.ManagerSaveException;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    HistoryManager historyManager = Managers.getHistoryManagement();

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTaskManager(historyManager);

        Task task1 = new Task(-1, "Задача 1", "Покормить кота", StatusOfTasks.NEW);
        manager.add(task1);
        Task task2 = new Task(-1, "Задача 2", "Повесить картину", StatusOfTasks.NEW);
        manager.add(task2);
    }

    @Test
    void deleteAllTasksDeleteBrowsingHistoryOfTheseTasks() {
        manager.deleteAllTasks();
        List<Integer> result = historyManager.getHistory();
        assertEquals(0, result.size());
    }

    @Test
    public void testException() {
        File tmpFile = new File("invalid dir", "file.csv");
        manager = new FileBackedTaskManager(historyManager, tmpFile);
        assertThrows(ManagerSaveException.class, () -> {
            manager.add(new Task(-1, "Задачка", "А вот ее описание", StatusOfTasks.NEW));
        }, "Ошибка создания файла");
    }

}
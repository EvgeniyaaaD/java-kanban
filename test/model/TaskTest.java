package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    Task task1 = new Task(1, "Задача 1", "Покормить кота", StatusOfTasks.NEW);

    @Test
    void distinguishBetweenTaskWithSameId() {
        Task task2 = new Task(2, "Задача 2", "Повесить картину", StatusOfTasks.NEW);
        task1.setId(0);
        task2.setId(0);
        assertEquals(task1, task2);
    }

    @Test
    void createTask() {
        assertEquals(1, task1.getId());
        assertEquals("Задача 1", task1.getTitle());
        assertEquals("Покормить кота", task1.getDescription());
        assertEquals(StatusOfTasks.NEW, task1.getStatus(), "Задача не была создана");
    }
}
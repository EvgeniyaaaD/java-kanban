package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SubtaskTest {

    @Test
    void equalsSubtasksWithSameIdShouldBeSameSubtasks() {
        Epic epic1 = new Epic(1, "Эпик 1", "Купить продукты");
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Купить яйца", StatusOfTasks.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(3, "Подзадача 2", "Купить хлеб", StatusOfTasks.NEW, epic1.getId());
        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Две задачи с одинаковым id для менеджера не выглядят как одна и та же.");
    }

    @Test
    void createSubTaskShouldCreateSubtask() {
        Epic epic1 = new Epic(1, "Эпик 1", "Купить продукты");
        Subtask subtask1 = new Subtask(2, "Подзадача 1", "Купить яйца", StatusOfTasks.NEW, epic1.getId());
        assertEquals(2, subtask1.getId());
        assertEquals("Подзадача 1", subtask1.getTitle());
        assertEquals("Купить яйца", subtask1.getDescription());
        assertEquals(StatusOfTasks.NEW, subtask1.getStatus());
        assertEquals(epic1.getId(), subtask1.getEpicId(), "Подзадача не была создана");
    }
}
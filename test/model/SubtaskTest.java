package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class SubtaskTest {
    Epic epic1 = new Epic(1, "Эпик 1", "Купить продукты", StatusOfTasks.NEW);
    Subtask subtask1 = new Subtask(2, "Подзадача 1", "Купить яйца", StatusOfTasks.NEW, epic1.getId());
    Subtask subtask2 = new Subtask(3, "Подзадача 2", "Купить хлеб", StatusOfTasks.NEW, epic1.getId());

    @Test
    void distinguishBetweenEpicsTasksWithSameId() {

        Epic epic2 = new Epic(4, "Эпик 2", "Цветы для мамы", StatusOfTasks.NEW);
        Subtask subtask3 = new Subtask(5, "1 подзадача", "Заказать доставку цветов", StatusOfTasks.NEW, epic2.getId());

        subtask1.setId(1);
        subtask2.setId(1);

        assertEquals(subtask1, subtask2, "Две задачи с одинаковым id для менеджера не выглядят как одна и та же.");
    }

    @Test
    void createSubTask() {
        assertEquals(2, subtask1.getId());
        assertEquals("Подзадача 1", subtask1.getTitle());
        assertEquals("Купить яйца", subtask1.getDescription());
        assertEquals(StatusOfTasks.NEW, subtask1.getStatus());
        assertEquals(epic1.getId(), subtask1.getEpicId(), "Подзадача не была создана");
    }
}
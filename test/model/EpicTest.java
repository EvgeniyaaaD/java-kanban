package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic1 = new Epic(1, "Эпик 1", "Купить продукты", StatusOfTasks.NEW);
    Epic epic2 = new Epic(4, "Эпик 2", "Цветы для мамы", StatusOfTasks.NEW);

    @Test
    void distinguishBetweenEpicWithSameId() {
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2);
    }

    @Test
    void createEpic() {
        assertEquals(1, epic1.getId());
        assertEquals("Эпик 1", epic1.getTitle());
        assertEquals("Купить продукты", epic1.getDescription());
        assertEquals(StatusOfTasks.NEW, epic1.getStatus());
        assertTrue(epic1.getSubtaskId().isEmpty(), "Эпик не был создан");
    }

    @Test
    void addSubtaskToEpic() {
        epic1.addSubtaskId(1);
        assertEquals(1, epic1.getSubtaskId().size());
        assertTrue(epic1.getSubtaskId().contains(1));
    }

    @Test
    void removeSubtaskFromEpic() {
        epic1.addSubtaskId(1);
        epic1.removeSubtaskId(1);

        assertTrue(epic1.getSubtaskId().isEmpty(), "В эпике присутствует удаленная подзадача");
    }

    @Test
    void removeAllSubtasksFromEpic() {
        epic1.addSubtaskId(1);
        epic1.addSubtaskId(2);
        epic1.removeAllSubtasks();
        assertTrue(epic1.getSubtaskId().isEmpty(), "Подзадачи не удаляются из эпика");
    }
}
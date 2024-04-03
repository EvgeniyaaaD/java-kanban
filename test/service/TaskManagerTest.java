package service;

import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    private static Stream<StatusOfTasks[]> statusOfTasksCombinations() {
        return Stream.of(
                new StatusOfTasks[]{StatusOfTasks.NEW, StatusOfTasks.NEW},
                new StatusOfTasks[]{StatusOfTasks.IN_PROGRESS, StatusOfTasks.IN_PROGRESS},
                new StatusOfTasks[]{StatusOfTasks.DONE, StatusOfTasks.DONE},
                new StatusOfTasks[]{StatusOfTasks.NEW, StatusOfTasks.IN_PROGRESS},
                new StatusOfTasks[]{StatusOfTasks.NEW, StatusOfTasks.DONE},
                new StatusOfTasks[]{StatusOfTasks.IN_PROGRESS, StatusOfTasks.DONE}
        );
    }

    @ParameterizedTest
    @MethodSource("statusOfTasksCombinations")
    void shouldBeCorrectEpicStatusWithDifferentSubtasks(StatusOfTasks subTaskStatus1, StatusOfTasks subTaskStatus2) {
        Epic epic = new Epic(-1, "Погладить кота", "Погладить кота не слишком сильно");
        manager.add(epic);

        Subtask subTask1 = new Subtask(-1, "Почесать коту за ушком", "Не забыть что у кота два уха", subTaskStatus1, epic.getId());
        Subtask subTask2 = new Subtask(-1, "Почесать коту животик", "Не забыть что кот атакует", subTaskStatus2, epic.getId());

        manager.add(subTask1);
        manager.add(subTask2);

        StatusOfTasks expectedEpicStatus = StatusOfTasks.IN_PROGRESS;
        if (subTaskStatus1 == StatusOfTasks.DONE && subTaskStatus2 == StatusOfTasks.DONE) {
            expectedEpicStatus = StatusOfTasks.DONE;
        }
        if (subTaskStatus1 == StatusOfTasks.NEW && subTaskStatus2 == StatusOfTasks.NEW) {
            expectedEpicStatus = StatusOfTasks.NEW;
        }
        assertEquals(expectedEpicStatus, epic.getStatus());
    }

    @Test
    void subtaskShouldKnowEpic() {
        Epic epic = new Epic(-1, "Погладить кота", "Погладить кота не слишком сильно");
        manager.add(epic);
        Subtask subTask1 = new Subtask(-1, "Почесать коту за ушком", "Не забыть что у кота два уха", StatusOfTasks.NEW, epic.getId());
        manager.add(subTask1);
        assertEquals(epic.getId(), subTask1.getEpicId());
    }

    @Test
    void crossingTasksShouldntBeeCreated() {
        Task task1 = new Task(-1, "Пойти в университет", "Наконец то посетить занятия", StatusOfTasks.NEW, 30, "02.04.2024 11:30");
        Task task2 = new Task(-1, "Пойти в университет", "Наконец то посетить занятия", StatusOfTasks.NEW, 30, "02.04.2024 11:40");
        manager.add(task1);
        assertTrue(manager.getAllTasks().containsKey(task1.getId()));
        manager.add(task2);
        assertFalse(manager.getAllTasks().containsKey(task2.getId()));
    }
}

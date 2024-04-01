package service;

import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getHistoryManagement();
    }

    @Test
    void verifySavedAndLoadedDataIsSame() throws IOException {

        File tempDir = Files.createTempDirectory("test_files").toFile();
        File file = Files.createTempFile(tempDir.toPath(), "tasks", ".csv").toFile();

        TaskManager fileManager = new FileBackedTaskManager(historyManager, file);

        Task task1 = new Task(1, "Путешествие на Луну", "Организовать экспедицию для исследования Луны", StatusOfTasks.NEW);
        Epic epic1 = new Epic(2, "Строительство замка", "Построить величественный замок на вершине холма");
        Subtask subTask1 = new Subtask(3, "Изучение местности", "Провести обследование территории для выбора места под замок", StatusOfTasks.IN_PROGRESS, epic1.getId());

        fileManager.add(task1);
        fileManager.add(epic1);
        fileManager.add(subTask1);
        fileManager.findTaskById(task1.getId());
        TaskManager loadedManager = Managers.getDefault();
        FileBackedTaskManager.loadFromFile(file, (FileBackedTaskManager) loadedManager);
        assertEquals(fileManager.findTaskById(1), loadedManager.findTaskById(1), "Задача, созданная из файла, не совпадает с сохраненной");
        assertEquals(fileManager.findSubtaskById(3), loadedManager.findSubtaskById(3), "Подзадача, созданная из файла, не совпадает с сохраненной");
        assertEquals(fileManager.findEpicById(2), loadedManager.findEpicById(2), "Эпик, созданный из файла, не совпадает с сохраненной");
    }

    @Test
    void loadFromClearFileShouldNotCreateAnything() throws IOException {
        File tempDir = Files.createTempDirectory("test_files").toFile();
        File file = Files.createTempFile(tempDir.toPath(), "empty_tasks", ".csv").toFile();
        TaskManager fileManager = Managers.getDefault();
        FileBackedTaskManager.loadFromFile(file, (FileBackedTaskManager) fileManager);
        assertTrue(fileManager.getAllTasks().isEmpty(), "Ничего не должно быть создано из пустого файла");
        assertTrue(fileManager.getAllEpics().isEmpty(), "Ничего не должно быть создано из пустого файла");
        assertTrue(fileManager.getAllSubtasks().isEmpty(), "Ничего не должно быть создано из пустого файла");
        assertTrue(fileManager.getHistoryTasks().isEmpty(), "Ничего не должно быть создано из пустого файла");
    }

    @Test
    void saveEmptyManagerToFileShouldCreateEmptyFile() throws IOException {
        File tempDir = Files.createTempDirectory("test_files").toFile();
        File file = Files.createTempFile(tempDir.toPath(), "empty_file", ".csv").toFile();

        FileBackedTaskManager fileManager = new FileBackedTaskManager(historyManager, file);
        assertTrue(file.exists(), "Сохраненный файл должен существовать");
        assertEquals(0, file.length(), "Сохраненный файл должен быть пустым");
    }

    @Test
    void createTasksFromFileShouldBeCreatedInManager() throws IOException {
        File tempDir = Files.createTempDirectory("test_files").toFile();
        File file = Files.createTempFile(tempDir.toPath(), "tasks", ".csv").toFile();
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            writer.write("1,TASK,Поиск сокровищ,NEW,Отправить команду на поиски затонувших судов в тропических водах,\n");
            writer.write("2,EPIC,Открытие загадочного острова,NEW,Исследовать неизвестный остров в целях науки и приключений,\n");
            writer.write("3,SUBTASK,Создание карты острова,IN_PROGRESS,Проанализировать данные и нарисовать детальную карту острова,2\n");
        }
        TaskManager loadedManager = Managers.getDefault();
        FileBackedTaskManager.loadFromFile(file, (FileBackedTaskManager) loadedManager);


        Task task = loadedManager.findTaskById(1);
        assertNotNull(task, "Задача должна быть создана из файла");
        assertEquals("Поиск сокровищ", task.getTitle(), "Название задачи должно совпадать");
        assertEquals("Отправить команду на поиски затонувших судов в тропических водах", task.getDescription(), "Описание задачи должно совпадать");
        assertEquals(StatusOfTasks.NEW, task.getStatus(), "Статус задачи должен совпадать");

        Epic epic = loadedManager.findEpicById(2);
        assertNotNull(epic, "Эпик должен быть создан из файла");
        assertEquals("Открытие загадочного острова", epic.getTitle(), "Название эпика должно совпадать");
        assertEquals("Исследовать неизвестный остров в целях науки и приключений", epic.getDescription(), "Описание эпика должно совпадать");
        assertNotEquals(StatusOfTasks.NEW, epic.getStatus(), "Статус эпика должен быть изменен");
        assertEquals(StatusOfTasks.IN_PROGRESS, epic.getStatus(), "Статус эпика должен совпадать");

        Subtask subTask = loadedManager.findSubtaskById(3);
        assertNotNull(subTask, "Подзадача должна быть создана из файла");
        assertEquals("Создание карты острова", subTask.getTitle(), "Название подзадачи должно совпадать");
        assertEquals("Проанализировать данные и нарисовать детальную карту острова", subTask.getDescription(), "Описание подзадачи должно совпадать");
        assertEquals(StatusOfTasks.IN_PROGRESS, subTask.getStatus(), "Статус подзадачи должен совпадать");
    }


}

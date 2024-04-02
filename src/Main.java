import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task1 = new Task(-1, "Задача 1", "Покормить кота", StatusOfTasks.NEW);
        manager.add(task1);
        Task task2 = new Task(-1, "Задача 2", "Повесить картину", StatusOfTasks.NEW);
        manager.add(task2);

        Epic epic1 = new Epic(-1, "Эпик 1", "Купить продукты");
        manager.add(epic1);
        Subtask subtask1 = new Subtask(-1, "Подзадача 1", "Купить яйца", StatusOfTasks.NEW, epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask(-1, "Подзадача 2", "Купить хлеб", StatusOfTasks.NEW, epic1.getId());
        manager.add(subtask2);

        manager.getAllTasks();
        manager.getAllSubtasks();
        manager.getAllEpics();

        Epic epic2 = new Epic(-1, "Эпик 2", "Цветы для мамы");
        manager.add(epic2);
        Subtask subtask3 = new Subtask(-1, "1 подзадача", "Заказать доставку цветов", StatusOfTasks.NEW,
                epic2.getId());
        manager.add(subtask3);

        manager.getAllTasks();
        manager.getAllSubtasks();
        manager.getAllEpics();

        Subtask subtask4 = new Subtask(4, "Подзадача 1", "Купить яйца", StatusOfTasks.IN_PROGRESS, epic1.getId());
        manager.update(subtask4);
        Subtask subtask5 = new Subtask(7, "1 подзадача", "Заказать доставку цветов", StatusOfTasks.DONE,
                epic2.getId());
        manager.update(subtask5);

        manager.getAllTasks();
        manager.getAllSubtasks();
        manager.getAllEpics();

        manager.deleteTaskById(1);
        manager.deleteSubtaskById(7);
        manager.deleteEpicById(6);

        System.out.println(manager.findSubtaskById(4));
        System.out.println(manager.findEpicById(2));
        System.out.println(manager.findEpicById(3));
        System.out.println(manager.findTaskById(2));
        System.out.println(manager.findTaskById(2));
        System.out.println(manager.findTaskById(2));
        System.out.println(manager.findSubtaskById(4));
        System.out.println(manager.findTaskById(2));
        System.out.println(manager.findTaskById(2));
        System.out.println(manager.findTaskById(2));
        System.out.println(manager.findSubtaskById(4));
        System.out.println(manager.findEpicById(3));

        System.out.println(manager.getHistoryTasks());

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getAllEpics());
        manager = FileBackedTaskManager.loadFromFile(new File("src/data/data.csv"));
        Task task10 = new Task(-1, "Задача 10", "Покормить кота", StatusOfTasks.NEW);
        manager.add(task10);
        Task task11 = new Task(-1, "Задача 2", "Повесить картину", StatusOfTasks.NEW);
        manager.add(task11);

        System.out.println("Все задачи из файла");
        for (Task task : manager.getAllTasks().values()) {
            System.out.println(task);
        }
        System.out.println("Все эпики из файла");
        for (Epic task : manager.getAllEpics().values()) {
            System.out.println(task);
        }
        System.out.println("Все подзадачи из файла");
        for (Subtask task : manager.getAllSubtasks().values()) {
            System.out.println(task);
        }
        System.out.println("История");
        System.out.println(manager.getHistoryTasks());
        System.out.println("История из файла");
        System.out.println(manager.getHistoryTasks());

    }
}

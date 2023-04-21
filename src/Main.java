import model.Epic;
import model.Subtask;
import model.Task;
import service.Manager;

public class Main {
        public static void main(String[] args) {
            Manager manager = new Manager();
            Task task1 = new Task(-1, "Задача 1", "Покормить кота", "NEW");
            manager.add(task1);
            Task task2 = new Task(-1, "Задача 2", "Повесить картину", "NEW");
            manager.add(task2);

            Epic epic1 = new Epic(-1, "Эпик 1", "Купить продукты", "NEW");
            manager.add(epic1);
            Subtask subtask1 = new Subtask(-1, "Подзадача 1", "Купить яйца", "NEW", epic1.getId());
            manager.add(subtask1);
            Subtask subtask2 = new Subtask(-1, "Подзадача 2", "Купить хлеб", "NEW", epic1.getId());
            manager.add(subtask2);

            manager.printAllTasks();
            manager.printAllSubtasks();
            manager.printAllEpics();
            manager.printAllEpicSubtasks(epic1);

            Epic epic2 = new Epic(-1, "Эпик 2", "Цветы для мамы", "NEW");
            manager.add(epic2);
            Subtask subtask3 = new Subtask(-1, "1 подзадача", "Заказать доставку цветов", "NEW",
                    epic2.getId());
            manager.add(subtask3);

            manager.printAllTasks();
            manager.printAllSubtasks();
            manager.printAllEpics();

            manager.printAllEpicSubtasks(epic1);
            manager.printAllEpicSubtasks(epic2);

            Subtask subtask4 = new Subtask(4, "Подзадача 1", "Купить яйца", "IN_PROGRESS", epic1.getId());
            manager.update(subtask4);
            Subtask subtask5 = new Subtask(7, "1 подзадача", "Заказать доставку цветов", "DONE",
                    epic2.getId());
            manager.update(subtask5);

            manager.printAllTasks();
            manager.printAllSubtasks();
            manager.printAllEpics();

            manager.deleteTaskById(1);
            manager.deleteSubtaskById(7);
            manager.deleteEpicById(6);


            System.out.println(manager.findSubtaskById(4));
            System.out.println(manager.findById(2));


            manager.printAllEpicSubtasks(epic1);

            manager.printAllTasks();
            manager.printAllSubtasks();
            manager.printAllEpics();
        }
}

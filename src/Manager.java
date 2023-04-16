import java.util.HashMap;
import java.util.Objects;

public class Manager {
    private int nextId = 1;
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();

    public void add(Task task) {
        task.setId(nextId++);
        taskList.put(task.getId(), task);
    }

    public void add(Subtask subtask) {
        subtask.setId(nextId++);
        subtaskList.put(subtask.getId(), subtask);
    }

    public void add(Epic epic) {
        epic.setId(nextId++);
        epicList.put(epic.getId(), epic);
    }

    public void printAllTasks() {
        System.out.println(taskList);
    }

    public void printAllSubtasks() {
        System.out.println(subtaskList);
    }

    public void printAllEpics() {
        System.out.println(epicList);
    }

    public void deleteAllTasks() {
        taskList.clear();
        System.out.println(taskList);
    }

    public void deleteAllSubtasks() {
        subtaskList.clear();
        for (Integer id : subtaskList.keySet()) {
            if (subtaskList.get(id) == null) {
                return;
            }
            int idEpic = subtaskList.get(id).getEpicId();
            if (epicList.get(idEpic) == null) {
                return;
            }
            epicList.get(idEpic).getSubtaskId().clear();
            updateStatus(epicList.get(idEpic));
        }
        System.out.println(subtaskList);
    }

    public void deleteAllEpics() {
        epicList.clear();
        for (Integer idEpic : epicList.keySet()) {
            if (epicList.get(idEpic) == null) {
                return;
            }
            for (Integer id : epicList.get(idEpic).getSubtaskId()) {
                epicList.get(idEpic).getSubtaskId().remove(id);
            }
            updateStatus(epicList.get(idEpic));
        }
        System.out.println(epicList);
    }

    public Object findById(int id) {
        if (taskList.containsKey(id)) {
            return taskList.get(id);
        }
        if (subtaskList.containsKey(id)) {
            return subtaskList.get(id);
        }
        if (epicList.containsKey(id)) {
           return epicList.get(id);
        }
        return "Задачи с таким номером нет";
    }




    public void deleteById(int id) {
        if (taskList.containsKey(id)) {
            taskList.remove(id);
        } else if (subtaskList.containsKey(id)) {
            subtaskList.remove(id);
        } else if (epicList.containsKey(id)) {
            epicList.remove(id);
            if (!epicList.get(id).getSubtaskId().isEmpty()) {
                for (Integer subId : epicList.get(id).getSubtaskId()) {
                    subtaskList.remove(subId);
                }
            }
        } else {
            System.out.println("По этому Id задачи не найдено");
        }
    }

    public void printAllEpicSubtasks(Epic epic) {
        if (epicList.containsValue(epic)) {
            for (Integer id : epic.getSubtaskId()) {
                System.out.println(subtaskList.get(id));
            };
        }
    }


    public void update(Task task) {
        taskList.put(task.getId(), task);
    }

    public void update(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);

        int colId = subtask.getEpicId();
        if (!epicList.containsKey(colId)) {
            return;
        }

        Epic epic = epicList.get(colId);
        updateStatus(epic);
    }

    public void update(Epic epic) {
        epicList.put(epic.getId(), epic);

        if (!epicList.containsKey(epic.getId())) {
            epic.setStatus("NEW");
        }
        updateStatus(epic);
    }

    private void updateStatus(Epic epic) {
        String statusEpic = "";
        int statusNew = 0;
        int statusInProgress = 0;
        int statusDone = 0;
        for (Integer subtaskId : epic.getSubtaskId()) {
            switch (subtaskList.get(subtaskId).getStatus()) {
                case "NEW":
                    statusNew++;
                    break;
                case "IN_PROGRESS":
                    statusInProgress++;
                    break;
                case "DONE":
                    statusDone++;
                    break;
            }

            if (statusInProgress == 0 && statusDone == 0) {
                epic.setStatus("NEW");
            } else if (statusNew == 0 && statusInProgress == 0) {
                epic.setStatus("DONE");
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        }
    }
}

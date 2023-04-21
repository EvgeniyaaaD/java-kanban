package service;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.HashMap;

public class Manager {
    private int nextId = 1;
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();

    private void generateId(Task task) {
        task.setId(nextId++);
    }

    public void add(Task task) {
        generateId(task);
        taskList.put(task.getId(), task);
    }

    public void add(Subtask subtask) {
        generateId(subtask);
        subtaskList.put(subtask.getId(), subtask);
        if (epicList.containsKey(subtask.getEpicId())) {
            epicList.get(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
        }
    }

    public void add(Epic epic) {
        generateId(epic);
        epicList.put(epic.getId(), epic);
    }

    public void printAllTasks() {
        System.out.println("");
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
        for (Integer id : subtaskList.keySet()) {
            int idEpic = subtaskList.get(id).getEpicId();
            epicList.get(idEpic).getSubtaskId().clear();
            updateStatus(epicList.get(idEpic));
        }
        subtaskList.clear();
    }

    public void deleteAllEpics() {
        for (Integer idEpic : epicList.keySet()) {
            for (Integer id : epicList.get(idEpic).getSubtaskId()) {
                subtaskList.remove(id);
            }
            updateStatus(epicList.get(idEpic));
        }
        epicList.clear();
    }

    public Task findById(int id) {
        Task task = null;
        if (taskList.containsKey(id)) {
            task = taskList.get(id);
        } else if (subtaskList.containsKey(id)) {
            task = subtaskList.get(id);
        } else if (epicList.containsKey(id)) {
           task = epicList.get(id);
        }
        return task;
    }

    public Subtask findSubtaskById(int id) {
        Subtask subtask = null;
        if (subtaskList.containsKey(id)) {
            subtask = subtaskList.get(id);
        }
        return subtask;
    }

    public void deleteTaskById(int id) {
            taskList.remove(id);
    }

    public void deleteSubtaskById(int id) {
        int epicId = 0;
        epicId = subtaskList.get(id).getEpicId();
        epicList.get(epicId).getSubtaskId().remove((Integer)id);
        updateStatus(epicList.get(epicId));
        subtaskList.remove(id);
    }

    public void deleteEpicById(int id) {
        if (epicList.get(id) == null) {
            return;
        }
        if (!epicList.get(id).getSubtaskId().isEmpty()) {
            for (Integer subId : epicList.get(id).getSubtaskId()) {
                subtaskList.remove(subId);
            }
        }
        epicList.remove(id);
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
        updateStatus(epicList.get(colId));
    }

    public void update(Epic epic) {
        epicList.put(epic.getId(), epic);
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

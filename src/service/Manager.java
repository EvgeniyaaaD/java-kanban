package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextId = 1;
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();

    public void add(Task task) {
        task.setId(nextId++);
        taskList.put(task.getId(), task);
        nextId++;
    }

    public void add(Subtask subtask) {
        subtask.setId(nextId++);
        subtaskList.put(subtask.getId(), subtask);
        for (Integer idEpic : epicList.keySet()) {
            if (idEpic == subtask.getEpicId()) {
                epicList.get(idEpic).getSubtaskId().add(subtask.getId());
                return;
            }
        }
    }

    public void add(Epic epic) {
        epic.setId(nextId++);
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
        subtaskList.clear();
    }

    public void deleteAllEpics() {
        for (Integer idEpic : epicList.keySet()) {
            if (epicList.get(idEpic) == null) {
                return;
            }
            for (Integer id : epicList.get(idEpic).getSubtaskId()) {
                epicList.get(idEpic).getSubtaskId().remove(id);
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
        return subtaskList.get(id);
    }

    public void deleteById(int id) {
        if (taskList.containsKey(id)) {
            taskList.remove(id);
        } else if (subtaskList.containsKey(id)) {
            subtaskList.remove(id);

            for (Integer idEpic : epicList.keySet()) {
                if (epicList.get(idEpic) == null) {
                    return;
                }
                for (Integer idSubtask : epicList.get(idEpic).getSubtaskId()) {
                    epicList.get(idEpic).getSubtaskId().remove(idSubtask);
                }
                updateStatus(epicList.get(idEpic));
            }
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
        updateStatus(epicList.get(colId));
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

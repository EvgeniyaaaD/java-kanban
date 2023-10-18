package service;

import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Subtask> subtaskList;
    private HashMap<Integer, Epic> epicList;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.taskList = new HashMap<>();
        this.subtaskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.historyManager = historyManager;
    }

    private void addHistory(Task task) {
        this.historyManager.addTaskHistory(task);
    }

    public List<Task> getHistoryManager() {
        return this.historyManager.getHistory();
    }

    private void generateId(Task task) {
        task.setId(nextId++);
    }

    @Override
    public void add(Task task) {
        generateId(task);
        taskList.put(task.getId(), task);
    }

    @Override
    public void add(Subtask subtask) {
        generateId(subtask);
        subtaskList.put(subtask.getId(), subtask);
        if (epicList.containsKey(subtask.getEpicId())) {
            epicList.get(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
        }
    }

    @Override
    public void add(Epic epic) {
        generateId(epic);
        epicList.put(epic.getId(), epic);
    }

    @Override
    public void printAllTasks() {
        System.out.println("");
        System.out.println(taskList);
    }

    @Override
    public void printAllSubtasks() {
        System.out.println(subtaskList);
    }

    @Override
    public void printAllEpics() {
        System.out.println(epicList);
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
        System.out.println(taskList);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtaskList.keySet()) {
            int idEpic = subtaskList.get(id).getEpicId();
            epicList.get(idEpic).getSubtaskId().clear();
            updateStatus(epicList.get(idEpic));
        }
        subtaskList.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer idEpic : epicList.keySet()) {
            for (Integer id : epicList.get(idEpic).getSubtaskId()) {
                subtaskList.remove(id);
            }
            updateStatus(epicList.get(idEpic));
        }
        epicList.clear();
    }

    @Override
    public Task findById(int id) {
        Task task = null;
        if (taskList.containsKey(id)) {
            task = taskList.get(id);
        } else if (subtaskList.containsKey(id)) {
            task = subtaskList.get(id);
        } else if (epicList.containsKey(id)) {
            task = epicList.get(id);
        }
        addHistory(task);
        return task;
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = null;
        if (subtaskList.containsKey(id)) {
            subtask = subtaskList.get(id);

        }
        addHistory(subtask);
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        taskList.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        int epicId = 0;
        epicId = subtaskList.get(id).getEpicId();
        epicList.get(epicId).getSubtaskId().remove((Integer)id);
        updateStatus(epicList.get(epicId));
        subtaskList.remove(id);
    }

    @Override
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

    @Override
    public void printAllEpicSubtasks(Epic epic) {
        if (epicList.containsValue(epic)) {
            for (Integer id : epic.getSubtaskId()) {
                System.out.println(subtaskList.get(id));
            };
        }
    }

    @Override
    public void update(Task task) {
        taskList.put(task.getId(), task);
    }

    @Override
    public void update(Subtask subtask) {
        subtaskList.put(subtask.getId(), subtask);
        int colId = subtask.getEpicId();
        if (!epicList.containsKey(colId)) {
            return;
        }
        updateStatus(epicList.get(colId));
    }

    @Override
    public void update(Epic epic) {
        epicList.put(epic.getId(), epic);
        updateStatus(epic);
    }

    @Override
    public void updateStatus(Epic epic) {
        int statusNew = 0;
        int statusInProgress = 0;
        int statusDone = 0;
        for (Integer subtaskId : epic.getSubtaskId()) {
            switch (subtaskList.get(subtaskId).getStatus()) {
                case NEW:
                    statusNew++;
                    break;
                case IN_PROGRESS:
                    statusInProgress++;
                    break;
                case DONE:
                    statusDone++;
                    break;
            }
            if (statusInProgress == 0 && statusDone == 0) {
                epic.setStatus(StatusOfTasks.NEW);
            } else if (statusNew == 0 && statusInProgress == 0) {
                epic.setStatus(StatusOfTasks.DONE);
            } else {
                epic.setStatus(StatusOfTasks.IN_PROGRESS);
            }
        }
    }
}

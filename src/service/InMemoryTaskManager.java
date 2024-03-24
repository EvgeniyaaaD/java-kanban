package service;

import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;

    public Map<Integer, Task> getTasks() {
        return taskMap;
    }

    private Map<Integer, Task> taskMap;
    private Map<Integer, Subtask> subtaskMap;
    private Map<Integer, Epic> epicMap;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.taskMap = new HashMap<>();
        this.subtaskMap = new HashMap<>();
        this.epicMap = new HashMap<>();
        this.historyManager = historyManager;
    }

    private void addHistory(int taskId) {
        this.historyManager.addTask(taskId);
    }

    @Override
    public List<Integer> getHistoryTasks() {
        return this.historyManager.getHistory();
    }

    private void generateId(Task task) {
        task.setId(nextId++);
    }

    @Override
    public void add(Task task) {
        generateId(task);
        taskMap.put(task.getId(), task);
    }

    @Override
    public void add(Subtask subtask) {
        generateId(subtask);
        subtaskMap.put(subtask.getId(), subtask);
        if (epicMap.containsKey(subtask.getEpicId())) {
            epicMap.get(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
        }
    }

    @Override
    public void add(Epic epic) {
        generateId(epic);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public Collection<Task> getAllTasks() {
        return taskMap.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtaskMap.values();
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epicMap.values();
    }

    @Override
    public void deleteAllTasks() {
        while (!taskMap.isEmpty()) {
            for (Integer id : taskMap.keySet()) {
                historyManager.remove(id);
            }
        }
        taskMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        while (!subtaskMap.isEmpty()) {
            for (Integer id : subtaskMap.keySet()) {
                historyManager.remove(id);
                int idEpic = subtaskMap.get(id).getEpicId();
                epicMap.get(idEpic).getSubtaskId().clear();
                updateStatus(epicMap.get(idEpic));
            }
        }
        subtaskMap.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer idEpic : epicMap.keySet()) {
            historyManager.remove(idEpic);
            for (Integer id : epicMap.get(idEpic).getSubtaskId()) {
                subtaskMap.remove(id);
                historyManager.remove(id);
            }
            updateStatus(epicMap.get(idEpic));
        }
        epicMap.clear();
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = null;
        if (subtaskMap.containsKey(id)) {
            subtask = subtaskMap.get(id);
            addHistory(id);
        }
        return subtask;
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = null;
        if (epicMap.containsKey(id)) {
            epic = epicMap.get(id);
            addHistory(id);
        }
        return epic;
    }

    @Override
    public Task findTaskById(int id) {
        Task task = null;
        if (taskMap.containsKey(id)) {
            task = taskMap.get(id);
            addHistory(id);
        }
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        int epicId;
        epicId = subtaskMap.get(id).getEpicId();
        epicMap.get(epicId).getSubtaskId().remove((Integer)id);
        updateStatus(epicMap.get(epicId));
        subtaskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (epicMap.get(id) == null) {
            return;
        }
        if (!epicMap.get(id).getSubtaskId().isEmpty()) {
            for (Integer subId : epicMap.get(id).getSubtaskId()) {
                subtaskMap.remove(subId);
                historyManager.remove(subId);
            }
        }
        epicMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void update(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void update(Subtask subtask) {
        subtaskMap.put(subtask.getId(), subtask);
        int colId = subtask.getEpicId();
        if (!epicMap.containsKey(colId)) {
            return;
        }
        updateStatus(epicMap.get(colId));
    }

    @Override
    public void update(Epic epic) {
        epicMap.put(epic.getId(), epic);
        updateStatus(epic);
    }

    @Override
    public void updateStatus(Epic epic) {
        int statusNew = 0;
        int statusInProgress = 0;
        int statusDone = 0;
        for (Integer subtaskId : epic.getSubtaskId()) {
            switch (subtaskMap.get(subtaskId).getStatus()) {
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

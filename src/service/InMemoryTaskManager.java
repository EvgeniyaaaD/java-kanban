package service;

import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected Set<Task> prioritizedTasks;

    public Map<Integer, Task> getTasks() {
        return taskMap;
    }

    protected Map<Integer, Task> taskMap;
    protected Map<Integer, Subtask> subtaskMap;
    protected Map<Integer, Epic> epicMap;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.taskMap = new HashMap<>();
        this.subtaskMap = new HashMap<>();
        this.epicMap = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    }


    private void addHistory(int taskId) {
        this.historyManager.addTask(taskId);
    }


    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    private void generateId(Task task) {
        task.setId(nextId++);
    }

    public boolean isCrossing(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return getPrioritizedTasks().stream()
                .filter(otherTask -> otherTask.getStartTime() != null && otherTask.getEndTime() != null)
                .anyMatch(otherTask -> !(task.getEndTime().isBefore(otherTask.getStartTime()) ||
                        otherTask.getEndTime().isBefore(task.getStartTime())));
    }

    public Set<Task> getPrioritizedTasks() {

        taskMap.values().forEach(task -> {
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        });
        subtaskMap.values().forEach(subTask -> {
            if (subTask.getStartTime() != null) {
                prioritizedTasks.add(subTask);
            }
        });

        epicMap.values().forEach(epic -> {
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);
            }
        });

        return prioritizedTasks;
    }

    @Override
    public List<Integer> getHistoryTasks() {
        return this.historyManager.getHistory();
    }

    @Override
    public void add(Task task) {
        if (!isCrossing(task)) {
            generateId(task);
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void add(Subtask subtask) {
        if (!isCrossing(subtask)) {
            generateId(subtask);
            subtaskMap.put(subtask.getId(), subtask);
            if (epicMap.containsKey(subtask.getEpicId())) {
                epicMap.get(subtask.getEpicId()).getSubtaskId().add(subtask.getId());
            }
            updateStatus(epicMap.get(subtask.getEpicId()));
        }
    }

    @Override
    public void add(Epic epic) {
        if (!isCrossing(epic)) {
            generateId(epic);
            epicMap.put(epic.getId(), epic);
        }
    }

    @Override
    public Map<Integer, Task> getAllTasks() {
        return taskMap;
    }

    @Override
    public Map<Integer, Subtask> getAllSubtasks() {
        return subtaskMap;
    }

    @Override
    public Map<Integer, Epic> getAllEpics() {
        return epicMap;
    }

    @Override
    public void deleteAllTasks() {
        taskMap.keySet()
                .forEach(historyManager::remove);
        taskMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtaskMap.keySet().stream()
                .forEach(id -> {
                    historyManager.remove(id);
                    int idEpic = subtaskMap.get(id).getEpicId();
                    epicMap.get(idEpic).getSubtaskId().clear();
                    updateStatus(epicMap.get(idEpic));
                });
        subtaskMap.clear();
    }


    @Override
    public void deleteAllEpics() {
        epicMap.keySet().stream().forEach(idEpic -> {
            historyManager.remove(idEpic);
            epicMap.get(idEpic).getSubtaskId().stream().forEach(id -> {
                subtaskMap.remove(id);
                historyManager.remove(id);
            });
            updateStatus(epicMap.get(idEpic));
        });
        epicMap.clear();
    }


    @Override
    public Subtask findSubtaskById(int id) {
        return subtaskMap.entrySet().stream()
                .filter(entry -> entry.getKey() == id)
                .peek(entry -> addHistory(id)) // Добавляем в историю, если найдено
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }


    @Override
    public Epic findEpicById(int id) {
        return epicMap.entrySet().stream()
                .filter(entry -> entry.getKey() == id)
                .peek(entry -> addHistory(id)) // Добавляем в историю, если найдено
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }


    @Override
    public Task findTaskById(int id) {
        return taskMap.entrySet().stream()
                .filter(entry -> entry.getKey() == id)
                .peek(entry -> addHistory(id)) // Добавляем в историю, если найдено
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
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
        epicMap.get(epicId).getSubtaskId().remove((Integer) id);
        subtaskMap.remove(id);
        historyManager.remove(id);
        updateStatus(epicMap.get(epicId));
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
        if (!isCrossing(task)) {
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void update(Subtask subtask) {
        if (!isCrossing(subtask)) {
            subtaskMap.put(subtask.getId(), subtask);
            int colId = subtask.getEpicId();
            if (!epicMap.containsKey(colId)) {
                return;
            }
            updateStatus(epicMap.get(colId));
        }
    }

    @Override
    public void update(Epic epic) {
        if (!isCrossing(epic)) {
            epicMap.put(epic.getId(), epic);
            updateStatus(epic);
        }
    }

    @Override
    public void updateStatus(Epic epic) {
        int statusNew = 0;
        int statusInProgress = 0;
        int statusDone = 0;

        LocalDateTime earliestStartTime = epic.getSubtaskId().stream()
                .map(subtaskId -> subtaskMap.get(subtaskId).getStartTime())
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEndTime = epic.getSubtaskId().stream()
                .map(subtaskId -> subtaskMap.get(subtaskId).getEndTime())
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(earliestStartTime);
        epic.setEndTime(latestEndTime);

        if (earliestStartTime != null && latestEndTime != null) {
            Duration duration = Duration.between(earliestStartTime, latestEndTime);
            epic.setDuration(duration);
        }

        for (Integer subtaskId : epic.getSubtaskId()) {
            Subtask subtask = subtaskMap.get(subtaskId);
            switch (subtask.getStatus()) {
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

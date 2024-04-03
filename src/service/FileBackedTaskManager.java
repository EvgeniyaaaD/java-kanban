package service;

import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;
import model.TypeOfTask;
import service.exception.ManagerCreateException;
import service.exception.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static int maxId = 0;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getHistoryManagement(), file);
        Map<Integer, Task> allTasksFromFile = readTasksFromFile(file, manager);
        manager.createFromFile(allTasksFromFile);
        manager.setNextId(++maxId);
        return manager;
    }


    private static Map<Integer, Task> readTasksFromFile(File file, FileBackedTaskManager fileBackedTasksManager) {
        Map<Integer, Task> allTasksFromFile = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String currentLine = reader.readLine();
            String historyLine = null;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.isEmpty()) {
                    historyLine = reader.readLine();
                    continue;
                }
                String[] parts = currentLine.split(",");
                if (parts.length < 5) {
                    continue;
                }
                int id = Integer.parseInt(parts[0]);
                TypeOfTask type = TypeOfTask.valueOf(parts[1]);
                String name = parts[2];
                StatusOfTasks status = StatusOfTasks.valueOf(parts[3]);
                String description = parts[4];
                maxId = Math.max(id, maxId);


                long duration;
                if (parts.length <= 6 || parts[6].equals("-")) {
                    switch (type) {
                        case TASK -> allTasksFromFile.put(id, new Task(id, name, description, status));
                        case EPIC -> allTasksFromFile.put(id, new Epic(id, name, description));
                        case SUBTASK -> {
                            int epicId = Integer.parseInt(parts[5]);
                            allTasksFromFile.put(id, new Subtask(id, name, description, status, epicId));
                        }
                    }
                } else {
                    if (!parts[6].equals("-")) {
                        duration = Long.parseLong(parts[6]);
                        String startTime = parts[7];
                        switch (type) {
                            case TASK ->
                                    allTasksFromFile.put(id, new Task(id, name, description, status, duration, startTime));
                            case EPIC -> allTasksFromFile.put(id, new Epic(id, name, description));
                            case SUBTASK -> {
                                int epicId = Integer.parseInt(parts[5]);
                                allTasksFromFile.put(id, new Subtask(id, name, description, status, epicId, duration, startTime));
                            }
                        }
                    }
                }
            }
            if (historyLine != null) {
                addTasksToHistoryManager(fileBackedTasksManager, historyLine);
            }
        } catch (IOException e) {
            throw new ManagerCreateException("Ошибка чтения файла: " + e.getMessage());
        }
        return allTasksFromFile;
    }

    private static void addTasksToHistoryManager(FileBackedTaskManager manager, String historyLine) {
        String[] parts = historyLine.split(",");
        for (String stringId : parts) {
            manager.historyManager.addTask(Integer.parseInt(stringId));
        }
    }


    private void saveTasks(Writer writer, Collection<? extends Task> tasks) throws IOException {
        for (Task task : tasks) {
            writer.write(task.toString() + "\n");
        }
    }

    private void createFromFile(Map<Integer, ? extends Task> tasks) {
        tasks.values().stream()
                .forEach(task -> {
                    if (task instanceof Epic) {
                        createEpicFromFile((Epic) task);
                    } else if (task instanceof Subtask) {
                        createSubTaskFromFile((Subtask) task);
                    } else {
                        createTaskFromFile(task);
                    }
                });
    }


    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,duration,startTime,endTime\n");
            saveTasks(writer, super.getAllTasks().values());
            saveTasks(writer, super.getAllSubtasks().values());
            saveTasks(writer, super.getAllEpics().values());
            writer.write("\n");
            writer.write(toString(this.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания файла.");
        }
    }

    private void createTaskFromFile(Task task) {
        task.setId(task.getId());
        taskMap.put(task.getId(), task);
    }


    private void createSubTaskFromFile(Subtask subTask) {
        subTask.setId(subTask.getId());
        subtaskMap.put(subTask.getId(), subTask);
        Epic epic = epicMap.get(subTask.getEpicId());
        epic.addSubtaskId(subTask.getId());
        updateEpicBySubtask(epic);
    }


    private void createEpicFromFile(Epic epic) {
        epic.setId(epic.getId());
        epicMap.put(epic.getId(), epic);
    }

    private String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        manager.getHistory().stream()
                .map(String::valueOf)
                .forEach(id -> {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(id);
                });
        return sb.toString();
    }

    private void saveIfNotNull(Task task) {
        if (task != null) {
            save();
        }
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public void add(Subtask subtask) {
        super.add(subtask);
        save();
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Subtask findSubtaskById(int id) {
        Subtask subtask = super.findSubtaskById(id);
        saveIfNotNull(subtask);
        return subtask;
    }

    @Override
    public Epic findEpicById(int id) {
        Epic epic = super.findEpicById(id);
        saveIfNotNull(epic);
        return epic;
    }

    @Override
    public Task findTaskById(int id) {
        Task task = super.findTaskById(id);
        saveIfNotNull(task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public void updateEpicBySubtask(Epic epic) {
        super.updateEpicBySubtask(epic);
        save();
    }
}

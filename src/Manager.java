import java.util.HashMap;

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
        System.out.println(subtaskList);
    }

    public void deleteAllEpics() {
        epicList.clear();
        System.out.println(epicList);
    }

    public void findById(int id) {
        if (taskList.containsKey(id)) {
            System.out.println(taskList.get(id));
        }
        if (subtaskList.containsKey(id)) {
            System.out.println(subtaskList.get(id));
        }
        if (epicList.containsKey(id)) {
            System.out.println(epicList.get(id));
        }
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
        for (Integer subtaskId : epic.getSubtaskId()) {
            if (epic.getStatus().equals(subtaskList.get(subtaskId).getStatus())) {
                statusEpic = epic.getStatus();
            } else {
                statusEpic = "IN_PROGRESS";
            }
        }
        epic.setStatus(statusEpic);
    }
}

package service;

public class Managers {

    public static HistoryManager getHistoryManagement() {
        return new InMemoryHistoryManagement();
    }

    public static TaskManager getTaskManagement() {
        return new InMemoryTaskManager(getHistoryManagement());
    }
}

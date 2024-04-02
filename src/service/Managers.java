package service;

import java.io.File;

public class Managers {

    public static HistoryManager getHistoryManagement() {
        return new InMemoryHistoryManagement();
    }

    public static TaskManager getTaskManagement() {
        return new InMemoryTaskManager(getHistoryManagement());
    }

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(getHistoryManagement(), new File("src/data/data.csv"));
    }
}

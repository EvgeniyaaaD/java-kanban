package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapter.DurationAdapter;
import http.adapter.TaskAdapter;
import model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new http.adapter.LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Task.class, new TaskAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }
}

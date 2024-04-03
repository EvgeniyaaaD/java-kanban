package http;

import com.sun.net.httpserver.HttpServer;
import http.handler.EpicsHandler;
import http.handler.HistoryHandler;
import http.handler.PrioritizedHandler;
import http.handler.SubtasksHandler;
import http.handler.TasksHandler;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;


    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.manager = taskManager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
    }

    public TaskManager getManager() {
        return manager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());

        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Сервер запущен на порту : " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен на порту :" + PORT);
    }
}
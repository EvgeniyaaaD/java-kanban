package http.handler;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import http.InvalidIdException;
import model.Task;
import service.TaskManager;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class TasksHandler extends Handler {
    private static final String TASKS = "/tasks/";
    private final Type typeTask = new TypeToken<Task>() {
    }.getType();

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Task task;
            String response;
            final String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst(TASKS, "");
                        int id;
                        try {
                            id = parsePathId(pathId);
                        } catch (NumberFormatException | InvalidIdException e) {
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }
                        response = gson.toJson(manager.findTaskById(id));
                        if (response != null) {
                            sendResponse(exchange, response);
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }
                    } else if (Pattern.matches("^/tasks$", path)) {
                        response = gson.toJson(manager.getAllTasks().values().stream().toList());
                        sendResponse(exchange, response);
                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                    break;
                }

                case "DELETE": {
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst(TASKS, "");
                        int id;
                        try {
                            id = parsePathId(pathId);
                        } catch (NumberFormatException | InvalidIdException e) {
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }
                        manager.deleteTaskById(id);
                        exchange.sendResponseHeaders(200, 0);

                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/tasks$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            task = gson.fromJson(body, typeTask);


                            boolean isCrossing = manager.isCrossing(task);
                            if (!isCrossing) {
                                manager.add(task);
                                response = gson.toJson(task);
                                sendResponse(exchange, response);
                            } else {
                                exchange.sendResponseHeaders(406, 0);
                            }
                        } else {
                            exchange.sendResponseHeaders(500, 0);
                        }
                    } else if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            task = gson.fromJson(body, typeTask);
                            manager.update(task);
                            response = gson.toJson(task);
                            sendResponse(exchange, response);
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }

                    } else {
                        exchange.sendResponseHeaders(500, 0);
                    }
                }
                break;
                default: {
                    exchange.sendResponseHeaders(405, 0);
                    break;
                }

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
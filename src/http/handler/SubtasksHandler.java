package http.handler;

import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import http.InvalidIdException;
import model.Subtask;
import service.TaskManager;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

public class SubtasksHandler extends Handler {
    private static final String SUBTASKS = "/subtasks/";

    private final Type typeSubtask = new TypeToken<Subtask>() {
    }.getType();

    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (exchange) {
            Subtask subtask;
            String response;
            final String path = exchange.getRequestURI().getPath();
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst(SUBTASKS, "");
                        int id;
                        try {
                            id = parsePathId(pathId);
                        } catch (NumberFormatException | InvalidIdException e) {
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }

                        response = gson.toJson(manager.findSubtaskById(id));
                        if (response != null) {
                            sendResponse(exchange, response);
                        } else {
                            exchange.sendResponseHeaders(404, 0);
                        }

                    } else if (Pattern.matches("^/subtasks$", path)) {
                        response = gson.toJson(manager.getAllSubtasks().values().stream().toList());
                        sendResponse(exchange, response);
                    } else {
                        exchange.sendResponseHeaders(500, 0);

                    }
                    break;
                }

                case "DELETE": {
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst(SUBTASKS, "");
                        int id;
                        try {
                            id = parsePathId(pathId);
                        } catch (NumberFormatException | InvalidIdException e) {
                            exchange.sendResponseHeaders(404, 0);
                            break;
                        }

                        manager.deleteSubtaskById(id);
                        exchange.sendResponseHeaders(200, 0);

                    } else {
                        exchange.sendResponseHeaders(404, 0);
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/subtasks$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            subtask = gson.fromJson(body, typeSubtask);
                            boolean isCrossing = manager.isCrossing(subtask);
                            if (!isCrossing) {
                                response = gson.toJson(subtask);
                                sendResponse(exchange, response);
                            } else {
                                exchange.sendResponseHeaders(406, 0);
                            }
                        } else {
                            exchange.sendResponseHeaders(500, 0);

                        }
                    } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String body = readResponse(exchange);
                        if (body != null) {
                            subtask = gson.fromJson(body, typeSubtask);
                            manager.update(subtask);
                            response = gson.toJson(subtask);
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
            System.out.print(exception.getMessage());
        }
    }

}
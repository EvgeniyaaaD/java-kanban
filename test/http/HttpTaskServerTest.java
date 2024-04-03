package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private HttpClient client;
    private TaskManager manager;
    private Epic epic;
    private Task task;
    private Subtask subtask;


    private Gson gson;
    private static final int PORT = 8080;
    private static final String HOST = "http://localhost:";
    private static final String HOST_PORT = HOST + PORT;

    @BeforeEach
    void setUp() throws IOException {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
        gson = Managers.getGson();
        client = HttpClient.newHttpClient();
        epic = new Epic(-1, "Epic name", "Epic Description");
        task = new Task(-1, "Task name", "Task Description", StatusOfTasks.NEW, 15, "17.03.2024 03:00");
        subtask = new Subtask(-1, "Subtask Name", "Subtask Description", StatusOfTasks.NEW, epic.getId(), 15, "17.03.2024 03:00");
        taskServer.start();
    }

    @AfterEach
    void stopServer() {
        taskServer.stop();
    }

    private String toJsonString(Object object) {
        return gson.toJson(object);
    }

    private HttpResponse<String> sendGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(URI uri, String requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void assertStatusCode(int expectedStatus, HttpResponse<?> response) {
        assertEquals(expectedStatus, response.statusCode());
    }

    @Nested
    @DisplayName("Тесты метода GET")
    class GetTests {

        @Test
        @DisplayName("GET должен извлекать подзадачу по идентификатору")
        void shouldRetrieveSubTaskById() throws IOException, InterruptedException {
            Type type = new TypeToken<Subtask>() {
            }.getType();
            manager.add(epic);
            subtask.setEpicId(epic.getId());
            manager.add(subtask);
            URI url = URI.create(HOST_PORT + "/subtasks/2");
            HttpResponse<String> responseGetId = sendGetRequest(url);
            assertEquals(200, responseGetId.statusCode());
            final Subtask actualSubtask = gson.fromJson(responseGetId.body(), type);
            assertEquals(manager.findSubtaskById(subtask.getId()), actualSubtask, "Подзадачи разные");
        }

        @Test
        @DisplayName("GET должен извлекать задачу по идентификатору")
        void shouldRetrieveTaskById() throws IOException, InterruptedException {
            Type type = new TypeToken<Task>() {
            }.getType();
            manager.add(task);
            URI url = URI.create(HOST_PORT + "/tasks/1");
            HttpResponse<String> responseGetId = sendGetRequest(url);
            assertEquals(200, responseGetId.statusCode());
            final Task actualTask = gson.fromJson(responseGetId.body(), type);
            assertEquals(manager.findTaskById(task.getId()), actualTask, "Задачи разные");
        }

        @Test
        @DisplayName("GET должен извлекать эпики, историю и приоритизированные задачи")
        void shouldRetrieveEpicsHistoryAndPrioritized() throws IOException, InterruptedException {
            Type epicType = new TypeToken<Epic>() {
            }.getType();
            Type epicsType = new TypeToken<List<Epic>>() {
            }.getType();

            manager.add(epic);

            URI url = URI.create(HOST_PORT + "/epics");
            HttpResponse<String> responseGet = sendGetRequest(url);
            assertStatusCode(200, responseGet);
            final List<Epic> actualEpics = gson.fromJson(responseGet.body(), epicsType);
            assertEquals(manager.getAllEpics().values().stream().toList(), actualEpics, "Списки эпиков не совпадают");

            url = URI.create(HOST_PORT + "/epics/1");
            HttpResponse<String> responseGetId = sendGetRequest(url);
            assertStatusCode(200, responseGetId);
            final Epic actualEpic = gson.fromJson(responseGetId.body(), epicType);
            final List<Epic> epicsFromManager = manager.getAllEpics().values().stream().toList();
            assertEquals(1, epicsFromManager.size(), "Неверное количество эпиков");
            assertEquals(actualEpic.getTitle(), epic.getTitle(), "Неверное название эпика");
            subtask.setEpicId(1);
            manager.add(subtask);

            url = URI.create(HOST_PORT + "/epics/1/subtasks");
            HttpResponse<String> responseGetEpicSb = sendGetRequest(url);
            assertStatusCode(200, responseGetEpicSb);
            final List<Integer> actualEpicSb = gson.fromJson(responseGetEpicSb.body(), new TypeToken<List<Integer>>() {
            }.getType());
            assertEquals(epic.getSubtaskId(), actualEpicSb, "Список подзадач не совпадает");

            url = URI.create(HOST_PORT + "/prioritized");
            HttpResponse<String> responseGetPriority = sendGetRequest(url);
            assertStatusCode(200, responseGetPriority);
            final Set<Task> actualPriority = gson.fromJson(responseGetPriority.body(), new TypeToken<Set<Task>>() {
            }.getType());
            assertEquals(manager.getPrioritizedTasks(), actualPriority, "Списки приоритетных задач не совпадают");
        }
    }

    @Nested
    @DisplayName("Тесты метода POST")
    class PostTests {

        @Test
        @DisplayName("POST должен создавать новый эпик")
        void shouldCreateNewEpic() throws IOException, InterruptedException {
            URI url = URI.create("http://localhost:" + PORT + "/epics");
            HttpResponse<String> responsePost = sendPostRequest(url, toJsonString(epic));
            assertStatusCode(200, responsePost);
            final Epic actualEpic = gson.fromJson(responsePost.body(), new TypeToken<Epic>() {
            }.getType());
            assertNotNull(manager.getAllEpics(), "Эпики не возвращены");
            assertEquals(manager.findEpicById(1), actualEpic, "Эпики не совпадают");
        }

        @Test
        @DisplayName("POST должен создавать новую задачу")
        void shouldCreateNewTask() throws IOException, InterruptedException {
            URI url = URI.create(HOST_PORT + "/tasks");
            HttpResponse<String> responsePost = sendPostRequest(url, toJsonString(task));
            assertStatusCode(200, responsePost);
            assertNotNull(manager.getAllTasks(), "Задачи не возвращены");
        }

        @Test
        @DisplayName("POST должен создавать новую подзадачу")
        void shouldCreateNewSubTask() throws IOException, InterruptedException {
            manager.add(epic);
            URI url = URI.create(HOST_PORT + "/subtasks");
            HttpResponse<String> responsePost = sendPostRequest(url, toJsonString(subtask));
            assertEquals(200, responsePost.statusCode());
            assertNotNull(manager.getAllSubtasks(), "Подзадачи не возвращены");
            Subtask subtask2 = new Subtask(1, "Name", "Description", StatusOfTasks.DONE, epic.getId(), 15, "17.03.2024 03:00");
            String newSubtaskJson = gson.toJson(subtask2);
            url = URI.create(HOST_PORT + "/subtasks/1");
            HttpResponse<String> responsePostId = sendPostRequest(url, newSubtaskJson);
            assertStatusCode(200, responsePostId);
            assertEquals(manager.findSubtaskById(1), subtask2, "Подзадачи разные");
        }

        @Test
        @DisplayName("POST должен обновлять задачу по идентификатору")
        void shouldUpdateTaskById() throws IOException, InterruptedException {
            manager.add(task);
            Task task2 = new Task(task.getId(), "Task Name", "Description task", StatusOfTasks.DONE, 15, "17.03.2024 03:00");
            URI url = URI.create(HOST_PORT + "/tasks/0");
            HttpResponse<String> responsePost = sendPostRequest(url, toJsonString(task2));
            assertStatusCode(200, responsePost);
            assertEquals(manager.findTaskById(1), task2, "Задачи разные");
        }
    }

    @Nested
    @DisplayName("Тесты метода DELETE")
    class DeleteTests {

        @Test
        @DisplayName("DELETE должен удалять задачу по идентификатору")
        void shouldDeleteTaskById() throws IOException, InterruptedException {
            manager.add(task);
            URI url = URI.create(HOST_PORT + "/tasks/1");
            HttpResponse<String> responseDelete = sendDeleteRequest(url);
            assertEquals(200, responseDelete.statusCode());
            assertTrue(manager.getAllTasks().isEmpty(), "Задачи не пусты");
        }

        @Test
        @DisplayName("DELETE должен удалять эпик по идентификатору")
        void shouldDeleteEpicById() throws IOException, InterruptedException {
            manager.add(epic);
            URI url = URI.create(HOST_PORT + "/epics/1");
            HttpResponse<String> responseDelete = sendDeleteRequest(url);
            assertEquals(200, responseDelete.statusCode());
            assertTrue(manager.getAllEpics().isEmpty(), "Эпики не пусты");
        }

        @Test
        @DisplayName("DELETE должен удалять подзадачу по идентификатору")
        void shouldDeleteSubTaskById() throws IOException, InterruptedException {
            manager.add(epic);
            subtask.setEpicId(epic.getId());
            manager.add(subtask);
            URI url = URI.create(HOST_PORT + "/subtasks/2");
            HttpResponse<String> responseDelete = sendDeleteRequest(url);
            assertEquals(200, responseDelete.statusCode());
            assertTrue(manager.getAllSubtasks().isEmpty(), "Подзадачи не пусты");
        }
    }

    @Test
    @DisplayName("Пересечение задач должно вызывать ошибку с кодом 406")
    void crossingTaskShouldThrow406Code() throws IOException, InterruptedException {
        manager.add(epic);
        subtask.setEpicId(epic.getId());
        manager.add(subtask);
        Subtask checkSubtask = new Subtask(-1, "Name", "Description", StatusOfTasks.NEW, epic.getId(), 15, "17.03.2024 03:00");
        String subtaskJson = gson.toJson(checkSubtask);
        URI url = URI.create(HOST_PORT + "/subtasks");
        HttpResponse<String> responsePost = sendPostRequest(url, subtaskJson);
        assertEquals(406, responsePost.statusCode());
    }

    @Test
    @DisplayName("После обновления должна возвращаться корректная история")
    void shouldReturnCorrectHistoryAfterUpdate() throws IOException, InterruptedException {
        manager.add(epic);
        manager.findEpicById(epic.getId());
        URI url = URI.create(HOST_PORT + "/history");
        HttpResponse<String> responseGetHistoryBefore = sendGetRequest(url);
        assertEquals(200, responseGetHistoryBefore.statusCode());
        final List<Integer> actualHistoryBefore = gson.fromJson(responseGetHistoryBefore.body(),
                new TypeToken<List<Integer>>() {
                }.getType());
        assertEquals(manager.getHistoryTasks(), actualHistoryBefore, "Истории не совпадают");
        manager.add(task);
        manager.findTaskById(task.getId());
        HttpResponse<String> responseGetHistoryAfter = sendGetRequest(url);
        assertEquals(200, responseGetHistoryAfter.statusCode());
        final List<Integer> actualHistoryAfter = gson.fromJson(responseGetHistoryAfter.body(),
                new TypeToken<List<Integer>>() {
                }.getType());
        assertEquals(manager.getHistoryTasks(), actualHistoryAfter, "Истории не совпадают");
    }
}

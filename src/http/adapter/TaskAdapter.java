package http.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import model.Epic;
import model.StatusOfTasks;
import model.Subtask;
import model.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

import static util.TimeFormatter.TIME_FORMATTER;

public class TaskAdapter implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.has("subtaskId")) {
            return context.deserialize(object, Epic.class);
        } else if (object.has("epicId")) {
            return context.deserialize(object, Subtask.class);
        } else {
            Task task = new Task(object.get("title").getAsString(), object.get("description").getAsString(), StatusOfTasks.valueOf(object.get("status").getAsString()));
            if (object.has("id")) {
                task.setId(object.get("id").getAsInt());
            }
            if (object.has("duration")) {
                task.setDuration(Duration.ofDays(object.get("duration").getAsLong()));
            }
            if (object.has("startTime")) {
                String startTimeStr = object.get("startTime").getAsString();
                LocalDateTime startTime = LocalDateTime.parse(startTimeStr, TIME_FORMATTER);
                task.setStartTime(startTime);
            }
            return task;
        }
    }
}

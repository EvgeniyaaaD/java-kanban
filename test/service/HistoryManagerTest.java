package service;
import model.Task;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager = Managers.getHistoryManagement();

    @Test
    void addTaskToDisplayInHistory() {
        historyManager.addTask(1);
        List<Integer> result = historyManager.getHistory();

        if (!result.contains(1)) {
            throw new IllegalArgumentException();
        }
        assertEquals(1, result.get(0));
    }

    @Test
    void add() {
        historyManager.addTask(1);
        final List<Integer> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
    @Test
    void addSameTaskWillBeOverwrittenInHistory() {
        historyManager.addTask(1);
        historyManager.addTask(2);
        historyManager.addTask(1);

        List<Integer> result = historyManager.getHistory();

        if (!result.contains(1)) {
            throw new IllegalArgumentException();
        }
        assertEquals("[2, 1]", result.toString());
    }
    @Test
    void getHistory() {
    }

    @Test
    void addTask() {
    }

    @Test
    void remove() {
    }
}
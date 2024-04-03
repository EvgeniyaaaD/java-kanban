package service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {
    HistoryManager historyManager = Managers.getHistoryManagement();

    @Test
    void getHistoryShouldReturnEmptyListIfHistoryIsEmpty() {
        assertEquals(0, historyManager.getHistory().size());
    }

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
    void addShouldAddTaskToHistory() {
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
    void removeShouldRemoveTasksFromHistory() {
        historyManager.addTask(1);
        historyManager.addTask(2);
        historyManager.addTask(3);
        historyManager.addTask(4);
        historyManager.addTask(5);
        historyManager.remove(1);
        assertEquals("[2, 3, 4, 5]", historyManager.getHistory().toString());
        historyManager.remove(5);
        assertEquals("[2, 3, 4]", historyManager.getHistory().toString());
        historyManager.remove(3);
        assertEquals("[2, 4]", historyManager.getHistory().toString());
    }
}
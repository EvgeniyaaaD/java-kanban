package service;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagementTest {
    InMemoryHistoryManagement inMemoryHistoryManagement = new InMemoryHistoryManagement();
    HistoryManager historyManager = Managers.getHistoryManagement();

    @Test
    void addTaskToDisplayInHistory() {
        inMemoryHistoryManagement.addTask(1);
        List<Integer> result = inMemoryHistoryManagement.getHistory();

        if (!result.contains(1)) {
            throw new IllegalArgumentException();
        }
        assertEquals(1, result.get(0));
    }

    @Test
    void addSameTaskWillBeOverwrittenInHistory() {
        inMemoryHistoryManagement.addTask(1);
        inMemoryHistoryManagement.addTask(2);
        inMemoryHistoryManagement.addTask(1);

        List<Integer> result = inMemoryHistoryManagement.getHistory();

        if (!result.contains(1)) {
            throw new IllegalArgumentException();
        }
        assertEquals("[2, 1]", result.toString());
    }

}
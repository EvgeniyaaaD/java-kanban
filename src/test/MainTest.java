package test;

import service.HistoryManager;
import service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    public static void main(String[] args) {
        test1();

        System.out.println("All tests are passed");
    }

    private static void test1() {
        HistoryManager historyManager = Managers.getHistoryManagement();

        historyManager.addTask(1);
        List<Integer> result = historyManager.getHistory();

        if (!result.contains(1)) {
            throw new IllegalArgumentException();
        }
        assert result.contains(1);
    }
}
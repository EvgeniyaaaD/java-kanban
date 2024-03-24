package service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManagement implements HistoryManager {
    private final CustomLinkedList customLinkedList;
    private final Map<Integer, Node> taskIdNodeHistory;

    public InMemoryHistoryManagement() {
        this.customLinkedList = new CustomLinkedList();
        this.taskIdNodeHistory = new HashMap<>();
    }

    @Override
    public List<Integer> getHistory() {
        return customLinkedList.getAll();
    }

    @Override
    public void addTask(int taskId) {
        remove(taskId);
        taskIdNodeHistory.put(taskId, customLinkedList.addLast(taskId));

    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = taskIdNodeHistory.get(id);

        if (nodeToRemove == null) {
            return;
        }

        customLinkedList.remove(nodeToRemove);

        taskIdNodeHistory.remove(id);
    }

    final class CustomLinkedList {
        private final Node head;
        private final Node tail;

        public CustomLinkedList() {
            this.head = new Node(null, -1, null);
            this.tail = new Node(null, -1, null);

            head.next = tail;
            tail.prev = head;
        }

        public void remove(Node nodeToRemove) {
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;

            nodeToRemove.next = null;
            nodeToRemove.prev = null;
        }

        public List<Integer> getAll() {
            List<Integer> toReturn = new LinkedList<>();

            Node current = head.next;

            while (current != tail) {
                toReturn.add(current.taskId);
                current = current.next;

            }
            return toReturn;
        }

        private Node addLast(int taskId) {
            Node newNode = new Node(tail.prev, taskId, tail);

            tail.prev.next = newNode;
            tail.prev = newNode;
            return newNode;
        }
    }

    final class Node {
        private final int taskId;
        private Node prev;
        private Node next;

        public Node(Node prev, int taskId, Node next) {
            this.prev = prev;
            this.taskId = taskId;
            this.next = next;
        }
    }
}

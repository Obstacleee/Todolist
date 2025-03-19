import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TodoList {
    private List<Task> tasks;

    public TodoList() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    // Retourne la liste triée (les tâches urgentes en premier, puis par date)
    public List<Task> getTasks() {
        tasks.sort(new Comparator<Task>() {
            public int compare(Task t1, Task t2) {
                if (t1.isUrgent() && !t2.isUrgent()) return -1;
                if (!t1.isUrgent() && t2.isUrgent()) return 1;
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
        });
        return tasks;
    }

    public void markTaskDone(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setDone(true);
        }
    }

    public void postponeTask(int index, int days) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.setDueDate(task.getDueDate().plusDays(days));
        }
    }

    public void markTaskUrgent(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setUrgent(true);
        }
    }

    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
        }
    }

    // Remplace la liste actuelle des tâches (utilisé lors du chargement depuis le fichier)
    public void replaceTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}

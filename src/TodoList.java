import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TodoList {
    private List<Task> tasks;

    public TodoList() {
        tasks = new ArrayList<>();
    }

    // Ajoute une nouvelle tâche à la liste
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Retourne la liste triée avec les tâches urgentes en priorité, puis par date d'échéance
    public List<Task> getTasks() {
        tasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                if (t1.isUrgent() && !t2.isUrgent()) return -1;
                if (!t1.isUrgent() && t2.isUrgent()) return 1;
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
        });
        return tasks;
    }

    // Marque une tâche comme effectuée
    public void markTaskDone(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setDone(true);
        }
    }

    // Permet de reporter une tâche d'un nombre de jours donné
    public void postponeTask(int index, int days) {
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);
            task.setDueDate(task.getDueDate().plusDays(days));
        }
    }

    // Marque une tâche comme urgente
    public void markTaskUrgent(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setUrgent(true);
        }
    }

    // Supprime les tâches complétées
    public void removeCompletedTasks() {
        tasks.removeIf(Task::isDone);
    }
}

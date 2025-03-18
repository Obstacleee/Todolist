import java.time.LocalDate;

public class Task {
    private String name;
    private LocalDate dueDate;
    private Category category;
    private boolean done;
    private boolean urgent;

    public Task(String name, LocalDate dueDate, Category category) {
        this.name = name;
        this.dueDate = dueDate;
        this.category = category;
        this.done = false;   // Par défaut, la tâche n'est pas faite
        this.urgent = false; // Par défaut, la tâche n'est pas urgente
    }

    public String getName() {
        return name;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        String color = category.getColor();
        String status = done ? "[FAIT]" : "";
        String urgentMark = urgent ? "[URGENT]" : "";
        return color + name + " - " + dueDate + " - " + category.getName() + " " + urgentMark + " " + status;
    }
}

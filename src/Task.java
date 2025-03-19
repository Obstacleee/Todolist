import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Task {
    private String name;
    private ZonedDateTime dueDate; // Date en UTC+1
    private Category category;
    private boolean done;
    private boolean urgent;

    public Task(String name, ZonedDateTime dueDate, Category category) {
        this.name = name;
        this.dueDate = dueDate;
        this.category = category;
        this.done = false;
        this.urgent = false;
    }

    public String getName() {
        return name;
    }

    public ZonedDateTime getDueDate() {
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

    public void setDueDate(ZonedDateTime dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        // Format d'affichage avec date en UTC+1
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC+1'");
        String status = done ? "[FAIT]" : "";
        String urgentMark = urgent ? "[URGENT]" : "";
        return category.getColor() + name + " - " + dueDate.format(formatter)
                + " - " + category.getName() + " " + urgentMark + " " + status;
    }

    // Sérialisation en JSON (format contrôlé)
    public String toJson() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"name\":\"").append(escapeJson(name)).append("\",");
        sb.append("\"dueDate\":\"").append(dueDate.format(formatter)).append("\",");
        sb.append("\"category\":\"").append(category.name()).append("\",");
        sb.append("\"done\":").append(done).append(",");
        sb.append("\"urgent\":").append(urgent);
        sb.append("}");
        return sb.toString();
    }

    // Reconstruction d'une tâche à partir d'une chaîne JSON
    public static Task fromJson(String json) {
        String name = extractJsonValue(json, "name");
        String dueDateStr = extractJsonValue(json, "dueDate");
        String categoryStr = extractJsonValue(json, "category");
        String doneStr = extractJsonValue(json, "done");
        String urgentStr = extractJsonValue(json, "urgent");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // On parse la date en LocalDateTime puis on ajoute l'offset UTC+1
        ZonedDateTime dueDate = ZonedDateTime.of(
                java.time.LocalDateTime.parse(dueDateStr, formatter),
                ZoneOffset.ofHours(1)
        );

        Category cat = Category.valueOf(categoryStr);
        Task task = new Task(name, dueDate, cat);
        task.setDone(Boolean.parseBoolean(doneStr));
        task.setUrgent(Boolean.parseBoolean(urgentStr));
        return task;
    }

    // Méthode utilitaire pour extraire la valeur d'un champ JSON (format simple)
    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int index = json.indexOf(pattern);
        if (index == -1) return "";
        int start = index + pattern.length();
        char firstChar = json.charAt(start);
        String value = "";
        if (firstChar == '\"') {
            start++;
            int end = json.indexOf("\"", start);
            value = json.substring(start, end);
        } else {
            int end = json.indexOf(",", start);
            if (end == -1) {
                end = json.indexOf("}", start);
            }
            value = json.substring(start, end).trim();
        }
        return value;
    }

    // Méthode utilitaire pour échapper les guillemets dans une chaîne JSON
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

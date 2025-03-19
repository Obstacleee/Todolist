import java.time.LocalDate;
import java.util.Scanner;

public class TodoApp {
    // Pour activer le chiffrement, passez ENCRYPTION_ENABLED à true et modifiez ENCRYPTION_PASSWORD
    private static final boolean ENCRYPTION_ENABLED = false;
    private static final String ENCRYPTION_PASSWORD = "monMotDePasse";

    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        Scanner scanner = new Scanner(System.in);

        // Chargement automatique des tâches depuis tache.json au démarrage
        todoList.replaceTasks(JsonHandler.loadTasks(ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD));

        while (true) {
            // Affichage de la liste des tâches
            System.out.println("=== ToDo List ===");
            int i = 0;
            for (Task task : todoList.getTasks()) {
                System.out.println(i + ": " + task.toString() + "\u001B[0m");
                i++;
            }
            System.out.println("\nOptions:");
            System.out.println("1. Ajouter une tâche");
            System.out.println("2. Marquer une tâche comme faite");
            System.out.println("3. Reporter une tâche");
            System.out.println("4. Marquer une tâche comme urgente");
            System.out.println("5. Supprimer les tâches complétées");
            System.out.println("0. Quitter");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommation du saut de ligne

            switch (choix) {
                case 1:
                    System.out.println("Entrez le nom de la tâche:");
                    String nom = scanner.nextLine();
                    System.out.println("Entrez la date (YYYY-MM-DD):");
                    String dateStr = scanner.nextLine();
                    LocalDate date;
                    try {
                        date = LocalDate.parse(dateStr);
                    } catch (Exception ex) {
                        System.out.println("Format de date invalide!");
                        break;
                    }
                    System.out.println("Entrez la catégorie (PERSO, BOULOT, FAMILLE):");
                    String catStr = scanner.nextLine().toUpperCase();
                    Category category;
                    try {
                        category = Category.valueOf(catStr);
                    } catch (Exception ex) {
                        System.out.println("Catégorie invalide!");
                        break;
                    }
                    Task newTask = new Task(nom, date, category);
                    todoList.addTask(newTask);
                    System.out.println("Tâche ajoutée avec succès.");
                    JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                    break;
                case 2:
                    System.out.println("Entrez l'index de la tâche à marquer comme faite:");
                    int indexDone = scanner.nextInt();
                    todoList.markTaskDone(indexDone);
                    JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                    break;
                case 3:
                    System.out.println("Entrez l'index de la tâche à reporter:");
                    int indexPostpone = scanner.nextInt();
                    System.out.println("Entrez le nombre de jours pour reporter:");
                    int days = scanner.nextInt();
                    todoList.postponeTask(indexPostpone, days);
                    JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                    break;
                case 4:
                    System.out.println("Entrez l'index de la tâche à marquer comme urgente:");
                    int indexUrgent = scanner.nextInt();
                    todoList.markTaskUrgent(indexUrgent);
                    JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                    break;
                case 5:
                    todoList.removeCompletedTasks();
                    JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                    break;
                case 0:
                    System.out.println("Au revoir!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Option invalide!");
            }
        }
    }
}

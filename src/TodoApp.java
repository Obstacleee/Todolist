import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Scanner;

public class TodoApp {
    // Pour activer le chiffrement Mettre ENCRYPTION_ENABLED à true et q1définir un mot de passe dans ENCRYPTION_PASSWORD
    private static final boolean ENCRYPTION_ENABLED = false;
    private static final String ENCRYPTION_PASSWORD = "Test";

    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        Scanner scanner = new Scanner(System.in);

        // Chargement automatique des tâches depuis tache.json au démarrage
        List<Task> loadedTasks = JsonHandler.loadTasks(ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
        todoList.replaceTasks(loadedTasks);

        while (true) {
            // Affichage de la liste des tâches
            System.out.println("\n=== ToDo List ===");
            int i = 0;
            for (Task task : todoList.getTasks()) {
                System.out.println("-> Tâche " + i + " : " + task.toString() + "\u001B[0m");
                i++;
            }

            // Menu principal
            System.out.println("\nOptions:");
            System.out.println("1. Ajouter une tâche");
            System.out.println("2. Sélectionner une tâche pour action");
            System.out.println("0. Quitter");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommation du saut de ligne

            switch (choix) {
                case 1:
                    // Ajout d'une nouvelle tâche
                    System.out.println("Entrez le nom de la tâche:");
                    String nom = scanner.nextLine();
                    System.out.println("Entrez la date (YYYY-MM-DD):");
                    String dateStr = scanner.nextLine();
                    ZonedDateTime dueDate;
                    try {
                        LocalDate ld = LocalDate.parse(dateStr);
                        // Conversion en date début de journée en UTC+1
                        dueDate = ld.atStartOfDay(ZoneOffset.ofHours(1));
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
                    Task newTask = new Task(nom, dueDate, category);
                    todoList.addTask(newTask);
                    System.out.println("Tâche ajoutée avec succès.");
                    // Sauvegarde automatique après ajout
                    JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                    break;

                case 2:
                    // Sélection d'une tâche pour action
                    System.out.println("Entrez l'index de la tâche à sélectionner:");
                    int index = scanner.nextInt();
                    scanner.nextLine(); // Consommation du saut de ligne
                    if (index < 0 || index >= todoList.getTasks().size()) {
                        System.out.println("Index invalide!");
                        break;
                    }

                    // Sous-menu pour la tâche sélectionnée
                    boolean actionMenu = true;
                    while (actionMenu) {
                        System.out.println("\nAction pour la tâche " + index + " :");
                        System.out.println("1. Marquer la tâche comme faite");
                        System.out.println("2. Reporter la tâche");
                        System.out.println("3. Marquer la tâche comme urgente");
                        System.out.println("4. Supprimer la tâche");
                        System.out.println("0. Retour au menu principal");
                        int action = scanner.nextInt();
                        scanner.nextLine(); // Consommation du saut de ligne

                        switch (action) {
                            case 1:
                                todoList.markTaskDone(index);
                                System.out.println("Tâche marquée comme faite.");
                                JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                                break;
                            case 2:
                                System.out.println("Entrez le nombre de jours pour reporter la tâche:");
                                int days = scanner.nextInt();
                                scanner.nextLine();
                                todoList.postponeTask(index, days);
                                System.out.println("Tâche reportée de " + days + " jours.");
                                JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                                break;
                            case 3:
                                todoList.markTaskUrgent(index);
                                System.out.println("Tâche marquée comme urgente.");
                                JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                                break;
                            case 4:
                                todoList.removeTask(index);
                                System.out.println("Tâche supprimée.");
                                JsonHandler.saveTasks(todoList.getTasks(), ENCRYPTION_ENABLED, ENCRYPTION_PASSWORD);
                                actionMenu = false;
                                break;
                            case 0:
                                actionMenu = false;
                                break;
                            default:
                                System.out.println("Action invalide!");
                        }
                    }
                    break;

                case 0:
                    System.out.println("Au revoir!");
                    System.exit(0);
                default:
                    System.out.println("Option invalide!");
            }
        }
    }
}

import java.time.LocalDate;
import java.util.Scanner;

public class TodoApp {
    public static void main(String[] args) {
        TodoList todoList = new TodoList();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== ToDo List ===");
            int i = 0;
            for (Task task : todoList.getTasks()) {
                // Affichage de chaque tâche avec réinitialisation de la couleur
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
                    LocalDate date = LocalDate.parse(dateStr);
                    System.out.println("Entrez la catégorie (PERSO, BOULOT, FAMILLE):");
                    String catStr = scanner.nextLine().toUpperCase();
                    Category category = Category.valueOf(catStr);
                    Task newTask = new Task(nom, date, category);
                    todoList.addTask(newTask);
                    break;
                case 2:
                    System.out.println("Entrez l'index de la tâche à marquer comme faite:");
                    int indexDone = scanner.nextInt();
                    todoList.markTaskDone(indexDone);
                    break;
                case 3:
                    System.out.println("Entrez l'index de la tâche à reporter:");
                    int indexPostpone = scanner.nextInt();
                    System.out.println("Entrez le nombre de jours pour reporter:");
                    int days = scanner.nextInt();
                    todoList.postponeTask(indexPostpone, days);
                    break;
                case 4:
                    System.out.println("Entrez l'index de la tâche à marquer comme urgente:");
                    int indexUrgent = scanner.nextInt();
                    todoList.markTaskUrgent(indexUrgent);
                    break;
                case 5:
                    todoList.removeCompletedTasks();
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

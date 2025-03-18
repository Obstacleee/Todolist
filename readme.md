
# Rapport sur la réalisation d'une TodoList en Java

Ce document présente la conception et l’implémentation d’une application TodoList en Java. Le projet a été développé afin de gérer des tâches via la console, avec des fonctionnalités telles que la création de tâches, leur validation, report, marquage comme urgent, et affichage avec une couleur spécifique selon la catégorie.

## 1. Introduction

L’objectif de ce projet était de développer une application console en Java permettant de gérer des tâches sous forme de liste. Les fonctionnalités principales sont :

- **Création d'une tâche** : une tâche possède un nom, une date d’échéance et une catégorie (PERSO, BOULOT, FAMILLE).
- **Validation d'une tâche** : marquer une tâche comme effectuée.
- **Report d'une tâche** : permettre de modifier la date d’échéance d'une tâche.
- **Marquage comme urgent** : les tâches urgentes apparaissent en premier dans la liste.
- **Affichage coloré** : chaque catégorie est associée à une couleur (via les codes ANSI) pour améliorer la lisibilité.
- **Fonctionnalités supplémentaires** : suppression des tâches complétées, tri automatique, etc.

Ce rapport détaille le découpage en classes, les choix techniques et présente des extraits de code commentés.

## 2. Architecture et Choix Techniques

### 2.1 Découpage en Classes

L’application est structurée autour de quatre classes principales :

- **Category** : une énumération qui définit les catégories de tâches et leur couleur associée.
- **Task** : une classe modélisant une tâche, avec ses attributs (nom, date, catégorie, état, urgence) et une méthode `toString()` personnalisée pour l’affichage.
- **TodoList** : une classe qui gère une collection de tâches et offre des méthodes pour les ajouter, les modifier (validation, report, marquage urgent) et les trier.
- **TodoApp** : la classe principale contenant la méthode `main` qui gère l’interaction avec l’utilisateur via un menu en console.

### 2.2 Gestion des Couleurs

Pour améliorer l’expérience utilisateur, chaque catégorie de tâche est associée à une couleur via des codes ANSI :
- **PERSO** en vert
- **BOULOT** en bleu
- **FAMILLE** en magenta

Cette approche permet un affichage visuel distinctif lors de l’affichage des tâches.

### 2.3 Tri et Affichage

Les tâches sont triées selon deux critères :
1. Les tâches marquées comme urgentes apparaissent en premier.
2. Les tâches sont ensuite triées par date d’échéance.

Ce tri dynamique garantit que les tâches les plus critiques sont toujours mises en avant.

## 3. Implémentation Détaillée

### 3.1 Fichier : Category.java

```java
public enum Category {
    PERSO("Perso", "\u001B[32m"),   // Vert
    BOULOT("Boulot", "\u001B[34m"),  // Bleu
    FAMILLE("Famille", "\u001B[35m"); // Magenta

    private final String name;
    private final String color;

    Category(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
```

**Pourquoi ce choix ?**  
L’utilisation d’une énumération limite les valeurs possibles et centralise la configuration des couleurs associées à chaque catégorie.

### 3.2 Fichier : Task.java

```java
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
```

**Pourquoi ce choix ?**  
La classe `Task` encapsule toutes les informations nécessaires pour une tâche. La méthode `toString()` permet un affichage complet et coloré pour une lecture facile dans la console.

### 3.3 Fichier : TodoList.java

```java
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

    // Retourne la liste triée : tâches urgentes d'abord, puis par date d'échéance
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

    // Marque une tâche comme faite
    public void markTaskDone(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setDone(true);
        }
    }

    // Reporte une tâche d'un nombre de jours spécifié
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

    // Supprime les tâches complétées de la liste
    public void removeCompletedTasks() {
        tasks.removeIf(Task::isDone);
    }
}
```

**Pourquoi ce choix ?**  
La classe `TodoList` centralise la gestion des tâches. Le tri personnalisé assure que les tâches urgentes sont toujours affichées en premier, tandis que des méthodes dédiées permettent de modifier l'état des tâches.

### 3.4 Fichier : TodoApp.java (Main)

```java
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
            scanner.nextLine(); // Consomme le saut de ligne

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
```

**Pourquoi ce choix ?**  
La classe `TodoApp` constitue le point d'entrée de l'application. Elle affiche un menu interactif et lit les entrées utilisateur pour appeler les méthodes correspondantes dans la classe `TodoList`.

## 4. Fonctionnalités Supplémentaires et Conclusion

En plus des fonctionnalités principales, nous avons ajouté :

- **Suppression des tâches complétées** pour épurer la liste.
- **Tri automatique** qui affiche d'abord les tâches urgentes, puis les tâches par date d’échéance.

Ces ajouts améliorent l’expérience utilisateur et offrent une base solide pour une éventuelle extension (par exemple, intégration d’une sauvegarde ou d’une interface graphique).

## 5. Auteur

- **Lucas Delon** - [GitHub](https://github.com/Obstacleee)
- **Typhene** 

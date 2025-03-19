# Rapport sur la réalisation d'une TodoList V2 en Java avec Gestion de Fichier

Ce document présente la conception et l’implémentation d’une application TodoList en Java. Le projet a été développé pour gérer des tâches via la console, avec des fonctionnalités telles que la création de tâches, leur validation, report, marquage comme urgent, et affichage avec une couleur spécifique selon la catégorie. De plus, l'application persiste automatiquement les tâches dans un fichier JSON (**tache.json**) et les recharge au démarrage, avec une option de chiffrement (basée sur les API standard de Java).

## 1. Introduction

L’objectif de ce projet était de développer une application console en Java permettant de gérer des tâches sous forme de liste. Les fonctionnalités principales sont :

- **Création d'une tâche** : une tâche possède un nom, une date d’échéance et une catégorie (PERSO, BOULOT, FAMILLE).
- **Validation d'une tâche** : marquer une tâche comme effectuée.
- **Report d'une tâche** : modifier la date d’échéance d'une tâche.
- **Marquage comme urgent** : les tâches urgentes apparaissent en premier dans la liste.
- **Affichage coloré** : chaque catégorie est associée à une couleur (via les codes ANSI) pour améliorer la lisibilité.
- **Gestion de fichier** : les tâches sont sauvegardées automatiquement dans un fichier JSON (**tache.json**) et rechargées au démarrage. Une option de chiffrement permet de sécuriser le fichier en cas de vol.

Ce rapport détaille le découpage en classes, les choix techniques ainsi que l’implémentation de la gestion de fichier.

## 2. Architecture et Choix Techniques

### 2.1 Découpage en Classes

L’application est structurée autour de plusieurs classes principales :

- **Category** : une énumération définissant les catégories de tâches et leur couleur associée.
- **Task** : une classe modélisant une tâche avec ses attributs (nom, date, catégorie, état, urgence) et une méthode `toString()` pour l’affichage.
- **TodoList** : une classe qui gère une collection de tâches. Elle offre des méthodes pour ajouter, modifier (validation, report, marquage urgent) et trier les tâches.
- **JsonHandler** : une classe qui gère la persistance des tâches dans un fichier JSON (**tache.json**). Elle réalise la sauvegarde et le chargement, et peut appliquer un chiffrement optionnel à l’aide d’AES.
- **TodoApp** : la classe principale contenant la méthode `main` qui gère l’interaction avec l’utilisateur via un menu en console. Elle intègre la gestion de fichier pour charger et sauvegarder automatiquement les tâches.

### 2.2 Gestion des Couleurs

Chaque catégorie de tâche est associée à une couleur via des codes ANSI :
- **PERSO** en vert
- **BOULOT** en bleu
- **FAMILLE** en magenta

Cette approche permet un affichage visuel distinctif lors de l’affichage des tâches dans la console.

### 2.3 Gestion de la Persistance (Fichier JSON)

Pour assurer la persistance des données :
- **Chargement automatique** : Lors du démarrage, l’application lit le fichier **tache.json** (s'il existe) et recharge la liste des tâches.
- **Sauvegarde automatique** : Toute modification (ajout, modification, suppression) entraîne une mise à jour immédiate du fichier **tache.json**.
- **Chiffrement optionnel** : L’application permet de chiffrer le contenu du fichier à l’aide d’AES, garantissant la sécurité des données en cas de vol. L’activation du chiffrement et le mot de passe sont définis dans la classe `TodoApp`.

### 2.4 Tri et Affichage

Les tâches sont triées selon deux critères :
1. Les tâches marquées comme urgentes apparaissent en premier.
2. Les tâches sont ensuite triées par date d’échéance.

Ce tri dynamique permet de mettre en avant les tâches les plus critiques.

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
L’utilisation d’une énumération limite les valeurs possibles et centralise la configuration des couleurs pour chaque catégorie.

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
La classe `Task` encapsule toutes les informations nécessaires pour une tâche. La méthode `toString()` fournit un affichage complet et coloré pour une lecture aisée dans la console.

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

    // Remplace la liste actuelle des tâches (utilisé lors du chargement du fichier)
    public void replaceTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
```

**Pourquoi ce choix ?**  
La classe `TodoList` centralise la gestion des tâches, offre des méthodes dédiées pour les modifications et assure le tri dynamique afin de mettre en avant les tâches urgentes.

### 3.4 Fichier : JsonHandler.java

```java
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class JsonHandler {
    public static final String FILE_NAME = "tache.json";

    // Génère une clé secrète à partir d'un mot de passe
    private static SecretKeySpec getSecretKey(String myKey) throws Exception {
        byte[] key = myKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }

    // Chiffre une chaîne de caractères avec AES/ECB/PKCS5Padding
    public static String encrypt(String strToEncrypt, String secret) throws Exception {
        SecretKeySpec secretKey = getSecretKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Déchiffre une chaîne de caractères avec AES/ECB/PKCS5Padding
    public static String decrypt(String strToDecrypt, String secret) throws Exception {
        SecretKeySpec secretKey = getSecretKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        return new String(decrypted, "UTF-8");
    }

    // Sauvegarde la liste des tâches dans le fichier JSON (avec ou sans chiffrement)
    public static void saveTasks(List<Task> tasks, boolean encryptFlag, String password) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                sb.append(tasks.get(i).toJson());
                if (i < tasks.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("\n]");
            String json = sb.toString();
            if (encryptFlag) {
                json = encrypt(json, password);
            }
            Files.write(Paths.get(FILE_NAME), json.getBytes("UTF-8"));
            System.out.println("Tâches sauvegardées dans " + FILE_NAME);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    // Charge la liste des tâches depuis le fichier JSON (avec ou sans déchiffrement)
    public static List<Task> loadTasks(boolean decryptFlag, String password) {
        List<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(FILE_NAME))) {
                System.out.println("Fichier non trouvé, création automatique d'un fichier vide.");
                saveTasks(tasks, false, "");
                return tasks;
            }
            byte[] fileContent = Files.readAllBytes(Paths.get(FILE_NAME));
            String json = new String(fileContent, "UTF-8");
            if (decryptFlag) {
                json = decrypt(json, password);
            }
            // Nettoyage du JSON et extraction des objets individuels
            json = json.trim();
            if (json.startsWith("[")) {
                json = json.substring(1);
            }
            if (json.endsWith("]")) {
                json = json.substring(0, json.length() - 1);
            }
            String[] taskJsonArray = json.split("},");
            for (int i = 0; i < taskJsonArray.length; i++) {
                String taskJson = taskJsonArray[i].trim();
                if (!taskJson.endsWith("}")) {
                    taskJson = taskJson + "}";
                }
                if (taskJson.length() == 0) continue;
                Task task = Task.fromJson(taskJson);
                tasks.add(task);
            }
            return tasks;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return tasks;
        }
    }
}
```

**Pourquoi ce choix ?**  
La classe `JsonHandler` assure la persistance des données dans le fichier **tache.json** sans dépendance externe. Elle gère la sérialisation/désérialisation en construisant et en analysant manuellement le format JSON, avec une option de chiffrement pour sécuriser les données.

### 3.5 Fichier : TodoApp.java (Main)

```java
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
            scanner.nextLine(); // Consomme le saut de ligne

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
                default:
                    System.out.println("Option invalide!");
            }
        }
    }
}
```

**Pourquoi ce choix ?**  
La classe `TodoApp` sert de point d'entrée à l'application. Elle affiche un menu interactif, lit les entrées utilisateur et déclenche les opérations sur la liste des tâches. Chaque modification entraîne une sauvegarde immédiate dans le fichier **tache.json** pour garantir la persistance des données.

## 4. Fonctionnalités Supplémentaires et Conclusion

En plus des fonctionnalités principales, nous avons intégré :

- **Persistance automatique** : Le fichier **tache.json** est chargé au démarrage et mis à jour immédiatement après chaque modification.
- **Chiffrement optionnel** : La sauvegarde peut être sécurisée par chiffrement AES afin de protéger les données en cas de vol.
- **Tri dynamique** : Les tâches urgentes sont affichées en premier, suivies des tâches triées par date d’échéance.

Ces ajouts améliorent l’expérience utilisateur et offrent une base solide pour une éventuelle extension (par exemple, une interface graphique ou des fonctionnalités supplémentaires).

## 5. Contributions

- [Delon Lucas](https://github.com/Obstacleee) - A travaillé sur les classes `Task`, `Category`, `TodoList`, `JsonHandler`, `TodoApp` et a rédigé le rapport.

## 6. Références

- [Documentation officielle Java](https://docs.oracle.com/en/java/)
- [Guide de style Google Java](https://google.github.io/styleguide/javaguide.html)
- [Codes ANSI pour les couleurs](https://en.wikipedia.org/wiki/ANSI_escape_code#Colors)


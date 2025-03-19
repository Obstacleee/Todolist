# Rapport sur la réalisation d'une TodoListV2 en Java 

Ce document présente la conception et l’implémentation d’une application TodoList en Java. Le projet a été développé pour gérer des tâches via la console, avec des fonctionnalités telles que la création de tâches, leur validation, report, marquage comme urgent, et affichage avec une couleur spécifique selon la catégorie. La persistance des données est assurée par un fichier JSON (**tache.json**) géré à l'aide de Gson. De plus, une option de chiffrement (AES) permet de sécuriser le fichier en cas de vol.

## 1. Introduction

L’objectif de ce projet était de développer une application console en Java permettant de gérer des tâches sous forme de liste. Les fonctionnalités principales sont :

- **Création d'une tâche** : une tâche possède un nom, une date d’échéance et une catégorie (PERSO, BOULOT, FAMILLE).
- **Validation d'une tâche** : marquer une tâche comme effectuée.
- **Report d'une tâche** : modifier la date d’échéance d'une tâche.
- **Marquage comme urgent** : les tâches urgentes apparaissent en premier dans la liste.
- **Affichage coloré** : chaque catégorie est associée à une couleur (via les codes ANSI) pour améliorer la lisibilité.
- **Persistance automatique** : les tâches sont sauvegardées dans un fichier JSON (**tache.json**) et rechargées automatiquement au démarrage.
- **Chiffrement optionnel** : le fichier peut être chiffré via AES pour protéger les données.

Ce rapport détaille le découpage en classes, les choix techniques et présente des extraits de code commentés.

## 2. Architecture et Choix Techniques

### 2.1 Découpage en Classes

L’application est structurée autour de plusieurs classes principales :

- **Category** : une énumération définissant les catégories de tâches et leur couleur associée.
- **Task** : une classe modélisant une tâche avec ses attributs (nom, date d’échéance, catégorie, état, urgence) et une méthode `toString()` pour l’affichage.
- **TodoList** : une classe qui gère une collection de tâches et offre des méthodes pour les ajouter, modifier (validation, report, marquage urgent) et trier.
- **JsonHandler** : une classe qui gère la persistance des tâches dans un fichier JSON (**tache.json**). Elle utilise Gson pour la sérialisation/désérialisation et peut appliquer un chiffrement AES optionnel.
- **TodoApp** : la classe principale contenant la méthode `main` qui gère l’interaction avec l’utilisateur via un menu en console. Elle intègre la gestion de fichier pour charger et sauvegarder automatiquement les tâches.

### 2.2 Gestion des Couleurs

Chaque catégorie de tâche est associée à une couleur via des codes ANSI :
- **PERSO** en vert
- **BOULOT** en bleu
- **FAMILLE** en magenta

Cela permet un affichage visuel distinctif dans la console.

### 2.3 Persistance via Gson

Pour assurer la persistance des données, le fichier **tache.json** est :
- **Chargé automatiquement** au démarrage de l'application (si le fichier existe, il est désérialisé à l'aide de Gson).
- **Mis à jour automatiquement** après chaque modification (ajout, report, marquage ou suppression) en sérialisant la liste des tâches avec Gson.
- **Optionnellement chiffré** : une option permet de chiffrer le contenu du fichier à l'aide d'AES pour sécuriser les données.

### 2.4 Tri et Affichage

Les tâches sont triées de façon à afficher :
1. Les tâches marquées comme urgentes en premier.
2. Les tâches ensuite par date d’échéance.

Ce tri garantit que les tâches les plus critiques sont toujours mises en avant.

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
L’énumération permet de limiter les valeurs possibles et centralise la configuration des couleurs pour chaque catégorie.

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
        this.done = false;
        this.urgent = false;
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
La classe `Task` encapsule toutes les informations d’une tâche. La méthode `toString()` permet d’afficher rapidement toutes les informations de façon lisible et colorée.

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

    // Ajoute une nouvelle tâche
    public void addTask(Task task) {
        tasks.add(task);
    }

    // Retourne la liste triée : tâches urgentes en premier, puis par date d’échéance
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

    // Supprime les tâches complétées
    public void removeCompletedTasks() {
        tasks.removeIf(Task::isDone);
    }

    // Remplace la liste actuelle des tâches (utilisé lors du chargement depuis le fichier)
    public void replaceTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
```

**Pourquoi ce choix ?**  
La classe `TodoList` centralise la gestion des tâches et propose des méthodes dédiées pour leur modification et leur tri dynamique.

### 3.4 Fichier : JsonHandler.java

```java
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
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
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(tasks);
            if (encryptFlag) {
                json = encrypt(json, password);
            }
            Files.write(Paths.get(FILE_NAME), json.getBytes("UTF-8"));
            System.out.println("Tâches sauvegardées dans " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        } catch (Exception ex) {
            System.err.println("Erreur de chiffrement : " + ex.getMessage());
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
            Gson gson = new GsonBuilder().create();
            tasks = gson.fromJson(json, new TypeToken<List<Task>>(){}.getType());
            return tasks;
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return tasks;
        } catch (Exception ex) {
            System.err.println("Erreur de déchiffrement : " + ex.getMessage());
            return tasks;
        }
    }
}
```

**Pourquoi ce choix ?**  
La classe `JsonHandler` utilise désormais Gson pour sérialiser et désérialiser la liste des tâches. Elle gère aussi le chiffrement AES optionnel pour sécuriser le fichier de persistance (**tache.json**).

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
La classe `TodoApp` est le point d'entrée de l'application. Elle charge automatiquement la liste des tâches depuis **tache.json** et assure une sauvegarde immédiate après chaque modification pour garantir la persistance des données.

## 4. Fonctionnalités Supplémentaires et Conclusion

En plus des fonctionnalités principales, nous avons intégré :

- **Persistance automatique** : le fichier **tache.json** est chargé au démarrage et mis à jour après chaque modification.
- **Chiffrement optionnel** : la possibilité de chiffrer le fichier avec AES pour protéger les données sensibles.
- **Tri dynamique** : les tâches urgentes sont toujours affichées en premier, puis triées par date d’échéance.

Ces choix garantissent une application robuste et facilement extensible, pouvant être adaptée à de futures évolutions (interface graphique, nouvelles fonctionnalités, etc.).

## 5. Contributions

- [Delon Lucas](https://github.com/Obstacleee) – A travaillé sur les classes `Task`, `Category`, `TodoList`, `JsonHandler`, `TodoApp` et a rédigé ce rapport.

## 6. Références

- [Documentation officielle Java](https://docs.oracle.com/en/java/)
- [Guide de style Google Java](https://google.github.io/styleguide/javaguide.html)
- [Codes ANSI pour les couleurs](https://en.wikipedia.org/wiki/ANSI_escape_code#Colors)
- [Gson – Google JSON Library](https://github.com/google/gson)

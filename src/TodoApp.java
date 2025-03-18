import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class TodoApp extends JFrame {
    private TodoList todoList;
    private JComboBox<Task> taskComboBox;

    public TodoApp() {
        todoList = new TodoList();
        setTitle("Todo List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel pour la liste déroulante
        JPanel listPanel = new JPanel(new FlowLayout());
        taskComboBox = new JComboBox<>();
        taskComboBox.setPreferredSize(new Dimension(400, 30));
        listPanel.add(taskComboBox);

        // Panel pour les boutons d'action
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        JButton addButton = new JButton("Ajouter une tâche");
        JButton markDoneButton = new JButton("Marquer comme faite");
        JButton postponeButton = new JButton("Reporter la tâche");
        JButton markUrgentButton = new JButton("Marquer comme urgente");
        JButton removeCompletedButton = new JButton("Supprimer les tâches complétées");
        JButton refreshButton = new JButton("Rafraîchir la liste");
        JButton quitButton = new JButton("Quitter");

        buttonPanel.add(addButton);
        buttonPanel.add(markDoneButton);
        buttonPanel.add(postponeButton);
        buttonPanel.add(markUrgentButton);
        buttonPanel.add(removeCompletedButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(quitButton);

        add(listPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Action pour ajouter une tâche
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ajouterTache();
            }
        });

        // Action pour marquer une tâche comme faite
        markDoneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                marquerTacheFaite();
            }
        });

        // Action pour reporter une tâche
        postponeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reporterTache();
            }
        });

        // Action pour marquer une tâche comme urgente
        markUrgentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                marquerTacheUrgente();
            }
        });

        // Action pour supprimer les tâches complétées
        removeCompletedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                todoList.removeCompletedTasks();
                refreshTaskList();
            }
        });

        // Action pour rafraîchir la liste
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTaskList();
            }
        });

        // Action pour quitter l'application
        quitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        refreshTaskList();
    }

    // Méthode pour ajouter une nouvelle tâche via des boîtes de dialogue
    private void ajouterTache() {
        String nom = JOptionPane.showInputDialog(this, "Entrez le nom de la tâche:");
        if (nom == null || nom.trim().isEmpty()) {
            return;
        }
        String dateStr = JOptionPane.showInputDialog(this, "Entrez la date (YYYY-MM-DD):");
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Format de date invalide.");
            return;
        }
        String[] categories = {"PERSO", "BOULOT", "FAMILLE"};
        String catStr = (String) JOptionPane.showInputDialog(this, "Sélectionnez la catégorie:",
                "Catégorie", JOptionPane.QUESTION_MESSAGE,
                null, categories, categories[0]);
        Category category;
        try {
            category = Category.valueOf(catStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Catégorie invalide.");
            return;
        }
        Task newTask = new Task(nom, date, category);
        todoList.addTask(newTask);
        refreshTaskList();
    }

    // Méthode pour marquer la tâche sélectionnée comme faite
    private void marquerTacheFaite() {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche.");
            return;
        }
        int index = todoList.getTasks().indexOf(selectedTask);
        if (index != -1) {
            todoList.markTaskDone(index);
            refreshTaskList();
        }
    }

    // Méthode pour reporter la tâche sélectionnée
    private void reporterTache() {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche.");
            return;
        }
        int index = todoList.getTasks().indexOf(selectedTask);
        if (index != -1) {
            String daysStr = JOptionPane.showInputDialog(this, "Entrez le nombre de jours pour reporter la tâche:");
            try {
                int days = Integer.parseInt(daysStr);
                todoList.postponeTask(index, days);
                refreshTaskList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.");
            }
        }
    }

    // Méthode pour marquer la tâche sélectionnée comme urgente
    private void marquerTacheUrgente() {
        Task selectedTask = (Task) taskComboBox.getSelectedItem();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une tâche.");
            return;
        }
        int index = todoList.getTasks().indexOf(selectedTask);
        if (index != -1) {
            todoList.markTaskUrgent(index);
            refreshTaskList();
        }
    }

    // Mise à jour du contenu de la liste déroulante
    private void refreshTaskList() {
        taskComboBox.removeAllItems();
        for (Task t : todoList.getTasks()) {
            taskComboBox.addItem(t);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                new TodoApp().setVisible(true);
            }
        });
    }
}

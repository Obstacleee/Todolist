import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class TodoApp extends JFrame {
    private TodoList todoList;
    private JTabbedPane tabbedPane;
    private JPanel listPanel;
    private JPanel addTaskPanel;
    private JPanel tasksContainer; // Conteneur pour la liste des tâches

    public TodoApp() {
        todoList = new TodoList();
        setTitle("Todo List - Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Onglet "Liste des Tâches"
        listPanel = new JPanel(new BorderLayout());
        tasksContainer = new JPanel();
        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(tasksContainer);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        // Bouton de rafraîchissement en bas du panneau
        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                refreshTasks();
            }
        });
        listPanel.add(refreshButton, BorderLayout.SOUTH);

        // Onglet "Ajouter Tâche" avec un formulaire
        addTaskPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Nom de la tâche:");
        JTextField nameField = new JTextField(20);
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField(20);
        JLabel categoryLabel = new JLabel("Catégorie:");
        String[] categories = {"PERSO", "BOULOT", "FAMILLE"};
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        JButton addTaskButton = new JButton("Ajouter la tâche");

        gbc.gridx = 0;
        gbc.gridy = 0;
        addTaskPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        addTaskPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        addTaskPanel.add(dateLabel, gbc);
        gbc.gridx = 1;
        addTaskPanel.add(dateField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        addTaskPanel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        addTaskPanel.add(categoryComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addTaskPanel.add(addTaskButton, gbc);

        addTaskButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String dateStr = dateField.getText().trim();
                if(name.isEmpty() || dateStr.isEmpty()){
                    JOptionPane.showMessageDialog(TodoApp.this, "Veuillez remplir tous les champs.");
                    return;
                }
                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(TodoApp.this, "Format de date invalide.");
                    return;
                }
                String catStr = (String) categoryComboBox.getSelectedItem();
                Category category;
                try {
                    category = Category.valueOf(catStr);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(TodoApp.this, "Catégorie invalide.");
                    return;
                }
                Task newTask = new Task(name, date, category);
                todoList.addTask(newTask);
                JOptionPane.showMessageDialog(TodoApp.this, "Tâche ajoutée avec succès!");
                nameField.setText("");
                dateField.setText("");
                refreshTasks();
                tabbedPane.setSelectedIndex(0); // Retour à la liste après ajout
            }
        });

        tabbedPane.addTab("Liste des Tâches", listPanel);
        tabbedPane.addTab("Ajouter Tâche", addTaskPanel);

        add(tabbedPane);
        refreshTasks();
    }

    // Méthode de rafraîchissement de la liste des tâches
    private void refreshTasks() {
        tasksContainer.removeAll();
        List<Task> tasks = todoList.getTasks();
        for (Task task : tasks) {
            tasksContainer.add(createTaskPanel(task));
        }
        tasksContainer.revalidate();
        tasksContainer.repaint();
    }

    // Création d'un panneau pour une tâche avec des boutons d'action
    private JPanel createTaskPanel(Task task) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Affichage des informations de la tâche (utilisation du toString() pour inclure couleur)
        JLabel taskLabel = new JLabel(task.toString() + "\u001B[0m");
        panel.add(taskLabel, BorderLayout.CENTER);

        // Boutons d'action pour chaque tâche
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton doneButton = new JButton("Fait");
        JButton postponeButton = new JButton("Reporter");
        JButton urgentButton = new JButton("Urgent");

        // Action : marquer la tâche comme faite
        doneButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int index = todoList.getTasks().indexOf(task);
                if(index != -1) {
                    todoList.markTaskDone(index);
                    refreshTasks();
                }
            }
        });

        // Action : reporter la tâche
        postponeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String daysStr = JOptionPane.showInputDialog(TodoApp.this, "Nombre de jours pour reporter:");
                try {
                    int days = Integer.parseInt(daysStr);
                    int index = todoList.getTasks().indexOf(task);
                    if(index != -1) {
                        todoList.postponeTask(index, days);
                        refreshTasks();
                    }
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(TodoApp.this, "Veuillez entrer un nombre valide.");
                }
            }
        });

        // Action : marquer la tâche comme urgente
        urgentButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                int index = todoList.getTasks().indexOf(task);
                if(index != -1) {
                    todoList.markTaskUrgent(index);
                    refreshTasks();
                }
            }
        });

        buttonPanel.add(doneButton);
        buttonPanel.add(postponeButton);
        buttonPanel.add(urgentButton);
        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                new TodoApp().setVisible(true);
            }
        });
    }
}

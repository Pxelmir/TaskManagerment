import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;


class Task implements Serializable {
    
    private static final long serialVersionUID = 1L; // Add this line to ensure consistency during serialization
    // existing fields, constructors, and methods


    private String taskName;
    private String deadline;
    private String category;
    private String status;
    private ArrayList<String> subtasks;
    private Date reminderTime;

    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_INCOMPLETE = "Incomplete";

    public Task(String taskName, String deadline, String category, String status, Date reminderTime) {
        this.taskName = taskName;
        this.deadline = deadline;
        this.category = category;
        this.status = status;
        this.subtasks = new ArrayList<>();
        this.reminderTime = reminderTime;
    }

    public String getTaskName() { return taskName; }
    public String getDeadline() { return deadline; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public ArrayList<String> getSubtasks() { return subtasks; }
    public Date getReminderTime() { return reminderTime; }

    public void setTaskName(String taskName) { this.taskName = taskName; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public void setCategory(String category) { this.category = category; }
    public void setStatus(String status) { this.status = status; }
    public void setReminderTime(Date reminderTime) { this.reminderTime = reminderTime; }
    
    // public void addSubtask(String subtask) { subtasks.add(subtask); }
    // public void removeSubtask(int index) { subtasks.remove(index); }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | Reminder: %s",
                             taskName, deadline, category, status,
                             new SimpleDateFormat("dd/MM/yyyy HH:mm").format(reminderTime));
    }
}

public class addTask extends JFrame {
    JFrame frame;
    private JTextField taskInputField;
    private JSpinner deadlineSpinner;
    private JComboBox<String> categoryComboBox;
    private JSpinner reminderSpinner;
    private DefaultListModel<Task> incompleteTaskListModel;
    private JList<Task> incompleteTaskList;
    private DefaultListModel<Task> inProgressTaskListModel;
    private JList<Task> inProgressTaskList;
    private DefaultListModel<Task> completedTaskListModel;
    private JList<Task> completedTaskList;
    private ArrayList<Task> allTasks = new ArrayList<>();

    private static final String FILE_NAME = "tasks.ser";

      // Method to save tasks to a file
    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(allTasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     // Method to load tasks from a file
    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                allTasks = (ArrayList<Task>) ois.readObject();
                updateTaskList(); // Update the task lists based on loaded data
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public addTask() {
        setTitle("SoDa");
        setSize(1120, 630);
        setLocation(250, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);
        add(showDashboard(), BorderLayout.CENTER);

        loadTasks(); // Load tasks from the file when the app starts
        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around buttons
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make buttons stretch horizontally
        gbc.anchor = GridBagConstraints.WEST; // Align buttons to the left
    
        // Define button properties
        JButton homeButton = createSidebarButton("Home");
        JButton recommendationButton = createSidebarButton("Recommendation");
        JButton trendingTasksButton = createSidebarButton("Trending Tasks");
        JButton motivationButton = createSidebarButton("Motivation");

        homeButton.addActionListener(e -> showHome());
        recommendationButton.addActionListener(e -> showRecommendationPanel());
        motivationButton.addActionListener(e -> showMotivationPanel());
    
        // Add buttons to the panel with GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        sidebarPanel.add(homeButton, gbc);
    
        gbc.gridy = 1;
        sidebarPanel.add(recommendationButton, gbc);
    
        gbc.gridy = 2;
        sidebarPanel.add(trendingTasksButton, gbc);
    
        gbc.gridy = 3;
        sidebarPanel.add(motivationButton, gbc);

        gbc.gridy = 4;
        gbc.weighty = 1.0; // Make this row take up remaining vertical space
        sidebarPanel.add(new JLabel(), gbc);
    
        return sidebarPanel;
    }
    
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(200, 50));
    
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(255, 182, 193)); // Light pink color
            }
    
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
    
        return button;
    }
    
 
//new methods 1 and 2 added for moving back to dashboard if we switch tab (1)
private JPanel createDashboardPanel() {
    JTabbedPane tabbedPane = new JTabbedPane();
    
    JPanel taskPanel = createTaskPanel(); // Create the Task panel
    JPanel reminderPanel = createReminderPanel(); // Create the Reminder panel
    
    tabbedPane.addTab("Tasks", taskPanel);
    tabbedPane.addTab("Reminders", reminderPanel);
    
    JPanel dashboardPanel = new JPanel(new BorderLayout());
    dashboardPanel.add(tabbedPane, BorderLayout.CENTER);
    
    return dashboardPanel;
}
//(2)
private void showHome() {
    getContentPane().removeAll(); // Remove existing components
    add(createSidebarPanel(), BorderLayout.WEST); // Re-add sidebar
    add(createDashboardPanel(), BorderLayout.CENTER); // Add dashboard panel
    revalidate(); // Refresh the layout
    repaint(); // Redraw the component
}

    

    private void showRecommendationPanel() {
        JPanel recommendationPanel = new JPanel();
        recommendationPanel.setLayout(new BoxLayout(recommendationPanel, BoxLayout.Y_AXIS));

        String[] recommendations = {
            "Clean the house",
            "Organize your closet",
            "Try a new recipe",
            "Start a new DIY project",
            "Read a book"
        };

        for (String recommendation : recommendations) {
            JLabel recommendationLabel = new JLabel(recommendation);
            recommendationPanel.add(recommendationLabel);
        }

        getContentPane().removeAll();
        add(createSidebarPanel(), BorderLayout.WEST);
        add(recommendationPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showMotivationPanel() {
        JPanel motivationPanel = new JPanel();
        motivationPanel.setLayout(new BoxLayout(motivationPanel, BoxLayout.Y_AXIS));

        String[] quotes = {
            "The only way to do great work is to love what you do. - Steve Jobs",
            "Success is not final, failure is not fatal: It is the courage to continue that counts. - Winston Churchill",
            "Believe you can and you're halfway there. - Theodore Roosevelt",
            "You are never too old to set another goal or to dream a new dream. - C.S. Lewis"
        };

        for (String quote : quotes) {
            JLabel quoteLabel = new JLabel("<html><p style='width:200px'>" + quote + "</p></html>");
            motivationPanel.add(quoteLabel);
        }

        getContentPane().removeAll();
        add(createSidebarPanel(), BorderLayout.WEST);
        add(motivationPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel showDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel taskPanel = createTaskPanel();
        JPanel reminderPanel = createReminderPanel();

        tabbedPane.addTab("Tasks", taskPanel);
        tabbedPane.addTab("Reminders", reminderPanel);

        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);

        return dashboardPanel;
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image image) {
            this.backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Draw the background image
        }
    }
    private JPanel createTaskPanel() {
        // Create the task panel with the background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

        // Create the background panel with the image
        JPanel panel = new BackgroundPanel(i2);

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false); // Make it transparent so background shows through
        topPanel.add(new JLabel("Task: "));
        taskInputField = new JTextField(20);
        topPanel.add(taskInputField);
        topPanel.add(new JLabel("Deadline: "));
        deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "dd/MM/yyyy");
        deadlineSpinner.setEditor(dateEditor);
        deadlineSpinner.setValue(new Date());
        topPanel.add(deadlineSpinner);
        topPanel.add(new JLabel("Category: "));
        String[] categories = {"Work", "Personal", "Urgent", "Other"};
        categoryComboBox = new JComboBox<>(categories);
        topPanel.add(categoryComboBox);
        topPanel.add(new JLabel("Reminder: "));
        reminderSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor reminderEditor = new JSpinner.DateEditor(reminderSpinner, "dd/MM/yyyy HH:mm");
        reminderSpinner.setEditor(reminderEditor);
        reminderSpinner.setValue(new Date());
        topPanel.add(reminderSpinner);

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> addTask());
        topPanel.add(addTaskButton);

        JButton removeTaskButton = new JButton("Remove Task");
        removeTaskButton.addActionListener(e -> removeTask());
        topPanel.add(removeTaskButton);

        JButton updateTaskButton = new JButton("Update Task");
        updateTaskButton.addActionListener(e -> updateTask());
        topPanel.add(updateTaskButton);

        incompleteTaskListModel = new DefaultListModel<>();
        incompleteTaskList = new JList<>(incompleteTaskListModel);
        inProgressTaskListModel = new DefaultListModel<>();
        inProgressTaskList = new JList<>(inProgressTaskListModel);
        completedTaskListModel = new DefaultListModel<>();
        completedTaskList = new JList<>(completedTaskListModel);

        // Create a background panel for taskListPanel
        ImageIcon taskListPanelIcon = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
        Image taskListPanelImage = taskListPanelIcon.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

        BackgroundPanel taskListPanel = new BackgroundPanel(taskListPanelImage); // Add background image
        taskListPanel.setOpaque(false); // Transparent to show background
        taskListPanel.setLayout(new GridLayout(1, 3));

        JPanel incompletePanel = new JPanel(new BorderLayout());
        incompletePanel.add(new JLabel("Incomplete"), BorderLayout.NORTH);
        incompletePanel.add(new JScrollPane(incompleteTaskList), BorderLayout.CENTER);

        JPanel inProgressPanel = new JPanel(new BorderLayout());
        inProgressPanel.add(new JLabel("In Progress"), BorderLayout.NORTH);
        inProgressPanel.add(new JScrollPane(inProgressTaskList), BorderLayout.CENTER);

        JPanel completedPanel = new JPanel(new BorderLayout());
        completedPanel.add(new JLabel("Completed"), BorderLayout.NORTH);
        completedPanel.add(new JScrollPane(completedTaskList), BorderLayout.CENTER);

        taskListPanel.add(incompletePanel);
        taskListPanel.add(inProgressPanel);
        taskListPanel.add(completedPanel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(taskListPanel, BorderLayout.CENTER);

        return panel;
    }
    private JPanel createReminderPanel() {
        JPanel panel = new JPanel();
        JLabel reminderLabel = new JLabel("Reminders will pop up when a task is due.");
        panel.add(reminderLabel);
        return panel;
    }

    private void addTask() {
        String taskName = taskInputField.getText();
        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task name cannot be empty!");
            return;
        }
        String deadline = new SimpleDateFormat("dd/MM/yyyy").format((Date) deadlineSpinner.getValue());
        String category = (String) categoryComboBox.getSelectedItem();
        Date reminderTime = (Date) reminderSpinner.getValue();

        Task newTask = new Task(taskName, deadline, category, Task.STATUS_INCOMPLETE, reminderTime);
        allTasks.add(newTask);
        incompleteTaskListModel.addElement(newTask);

        taskInputField.setText("");
        saveTasks();
    }

    private void removeTask() {
        Task selectedTask = null;
    
        // Check if a task is selected from the Incomplete list
        if (!incompleteTaskList.isSelectionEmpty()) {
            selectedTask = incompleteTaskList.getSelectedValue();
            incompleteTaskListModel.removeElement(selectedTask);
        }
        // Check if a task is selected from the In Progress list
        else if (!inProgressTaskList.isSelectionEmpty()) {
            selectedTask = inProgressTaskList.getSelectedValue();
            inProgressTaskListModel.removeElement(selectedTask);
        }
        // Check if a task is selected from the Completed list
        else if (!completedTaskList.isSelectionEmpty()) {
            selectedTask = completedTaskList.getSelectedValue();
            completedTaskListModel.removeElement(selectedTask);
        }
    
        // If a task was selected and removed from the list, remove it from the allTasks list as well
        if (selectedTask != null) {
            allTasks.remove(selectedTask);
        } else {
            JOptionPane.showMessageDialog(this, "No task selected to remove!");
        }
        saveTasks();
    }
    

    private void updateTask() {
        Task selectedTask = incompleteTaskList.getSelectedValue();
        if (selectedTask == null) {
            selectedTask = inProgressTaskList.getSelectedValue(); // Also check in Progress list
        }
        if (selectedTask == null) {
            selectedTask = completedTaskList.getSelectedValue(); // Also check Completed list
        }
        
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(frame, "Please select a task to update.");
            return;
        }
    
        String newStatus = JOptionPane.showInputDialog(frame, "Enter new status (In Progress, Completed):");
        if (newStatus != null && !newStatus.isEmpty()) {
            // Convert the input to lowercase for comparison
            newStatus = newStatus.trim().toLowerCase();
    
            // Compare with valid statuses in a case-insensitive way
            if (newStatus.equals(Task.STATUS_IN_PROGRESS.toLowerCase())) {
                selectedTask.setStatus(Task.STATUS_IN_PROGRESS);
            } else if (newStatus.equals(Task.STATUS_COMPLETED.toLowerCase())) {
                selectedTask.setStatus(Task.STATUS_COMPLETED);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid status. Please enter 'In Progress' or 'Completed'.");
                return;
            }
            updateTaskList(); // Update task list after status change
        }
        saveTasks();
    }

    private void updateTaskList() {
        incompleteTaskListModel.clear();
        inProgressTaskListModel.clear();
        completedTaskListModel.clear();

        for (Task task : allTasks) {
            switch (task.getStatus()) {
                case Task.STATUS_INCOMPLETE:
                    incompleteTaskListModel.addElement(task);
                    break;
                case Task.STATUS_IN_PROGRESS:
                    inProgressTaskListModel.addElement(task);
                    break;
                case Task.STATUS_COMPLETED:
                    completedTaskListModel.addElement(task);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new addTask());
    }
}


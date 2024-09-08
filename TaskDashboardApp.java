import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class Task {
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
    
    public void addSubtask(String subtask) { subtasks.add(subtask); }
    public void removeSubtask(int index) { subtasks.remove(index); }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | Reminder: %s",
                             taskName, deadline, category, status,
                             new SimpleDateFormat("dd/MM/yyyy HH:mm").format(reminderTime));
    }
}

public class TaskDashboardApp {

    private JFrame frame;
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
    private ArrayList<User> users = new ArrayList<>();
    private User loggedInUser;
    private ArrayList<Task> allTasks = new ArrayList<>();
    private Timer reminderTimer;

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        JButton homeButton = new JButton("Home");
        JButton recommendationButton = new JButton("Recommendation");
        JButton trendingTasksButton = new JButton("Trending Tasks");
        JButton motivationButton = new JButton("Motivation");

        sidebarPanel.add(homeButton);
        sidebarPanel.add(recommendationButton);
        sidebarPanel.add(trendingTasksButton);
        sidebarPanel.add(motivationButton);

        homeButton.addActionListener(e -> showDashboard());

        recommendationButton.addActionListener(e -> showRecommendationPanel());
        motivationButton.addActionListener(e -> showMotivationPanel());

        return sidebarPanel;
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

        frame.getContentPane().removeAll();
        frame.add(createSidebarPanel(), BorderLayout.WEST);
        frame.add(recommendationPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
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

        frame.getContentPane().removeAll();
        frame.add(createSidebarPanel(), BorderLayout.WEST);
        frame.add(motivationPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    public TaskDashboardApp() {
        frame = new JFrame("Task Manager Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        showLoginPanel();
        frame.setVisible(true);
    }

    private void showLoginPanel() {
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        frame.getContentPane().removeAll();
        frame.add(loginPanel);
        frame.revalidate();
        frame.repaint();

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (authenticateUser(username, password)) {
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login credentials!");
            }
        });

        registerButton.addActionListener(e -> showRegistrationPanel());
    }

    private void showRegistrationPanel() {
        JPanel registerPanel = new JPanel(new GridLayout(3, 2));
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton registerButton = new JButton("Register");

        registerPanel.add(new JLabel("Username:"));
        registerPanel.add(usernameField);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(passwordField);
        registerPanel.add(registerButton);

        frame.getContentPane().removeAll();
        frame.add(registerPanel);
        frame.revalidate();
        frame.repaint();

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (registerUser(username, password)) {
                JOptionPane.showMessageDialog(frame, "Registration successful! Please login.");
                showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists! Please try a different one.");
            }
        });
    }

    private boolean registerUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        users.add(new User(username, password));
        return true;
    }

    private boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedInUser = user;
                return true;
            }
        }
        return false;
    }

    private void showDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel taskPanel = createTaskPanel();
        JPanel settingsPanel = createSettingsPanel();
        JPanel reminderPanel = createReminderPanel();

        tabbedPane.addTab("Tasks", taskPanel);
        tabbedPane.addTab("Settings", settingsPanel);
        tabbedPane.addTab("Reminders", reminderPanel);

        frame.getContentPane().removeAll();
        frame.add(createSidebarPanel(), BorderLayout.WEST);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        startReminderTimer();
    }

    private JPanel createTaskPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
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

        JPanel taskListPanel = new JPanel(new GridLayout(1, 3));
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

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));

        JLabel usernameLabel = new JLabel("Logged in as: " + loggedInUser.getUsername());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            loggedInUser = null;
            showLoginPanel();
        });

        panel.add(usernameLabel);
        panel.add(logoutButton);

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
            JOptionPane.showMessageDialog(frame, "Task name cannot be empty!");
            return;
        }
        String deadline = new SimpleDateFormat("dd/MM/yyyy").format((Date) deadlineSpinner.getValue());
        String category = (String) categoryComboBox.getSelectedItem();
        Date reminderTime = (Date) reminderSpinner.getValue();

        Task newTask = new Task(taskName, deadline, category, Task.STATUS_INCOMPLETE, reminderTime);
        allTasks.add(newTask);

        updateTaskList();
    }

    private void removeTask() {
        Task selectedTask = incompleteTaskList.getSelectedValue();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(frame, "Please select a task to remove.");
            return;
        }

        allTasks.remove(selectedTask);
        updateTaskList();
    }

    private void updateTask() {
        Task selectedTask = incompleteTaskList.getSelectedValue();
        if (selectedTask == null) {
            JOptionPane.showMessageDialog(frame, "Please select a task to update.");
            return;
        }

        String newStatus = JOptionPane.showInputDialog(frame, "Enter new status (In Progress, Completed):");
        if (newStatus != null && !newStatus.isEmpty()) {
            if (newStatus.equals(Task.STATUS_IN_PROGRESS) || newStatus.equals(Task.STATUS_COMPLETED)) {
                selectedTask.setStatus(newStatus);
                updateTaskList();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid status. Please enter 'In Progress' or 'Completed'.");
            }
        }
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

    private void startReminderTimer() {
        reminderTimer = new Timer(true);
        reminderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (Task task : allTasks) {
                    if (task.getReminderTime().before(new Date())) {
                        JOptionPane.showMessageDialog(frame, "Reminder: " + task.getTaskName());
                    }
                }
            }
        }, 0, 60 * 1000); // Check every minute
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TaskDashboardApp::new);
    }
}


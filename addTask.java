import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

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

public class addTask extends JFrame {
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

    public addTask() {
        setTitle("Task Manager");
        setSize(1120, 630);
        setLocation(250, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);
        add(showDashboard(), BorderLayout.CENTER);

        setVisible(true);
    }

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
    }

    private void removeTask() {
        Task selectedTask = incompleteTaskList.getSelectedValue();
        if (selectedTask != null) {
            incompleteTaskListModel.removeElement(selectedTask);
            allTasks.remove(selectedTask);
        } else {
            JOptionPane.showMessageDialog(this, "No task selected to remove!");
        }
    }

    private void updateTask() {
        // Implementation for updating a task
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new addTask());
    }
}

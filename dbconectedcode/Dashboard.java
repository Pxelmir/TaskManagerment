import java.awt.*;
import java.awt.event.MouseAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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

    public void startReminder() {
        if (reminderTime == null) {
            return; // No reminder set
        }

        Timer timer = new Timer();
        TimerTask reminderTask = new TimerTask() {
            @Override
            public void run() {
                // Show a reminder notification
                JOptionPane.showMessageDialog(null, "Reminder: " + taskName + " is due now!");
                timer.cancel(); // Stop the timer after showing the reminder
            }
        };

        // Schedule the reminder task
        long delay = reminderTime.getTime() - System.currentTimeMillis();
        if (delay > 0) {
            timer.schedule(reminderTask, delay);
        } else {
            JOptionPane.showMessageDialog(null, "The reminder time has already passed.");
        }
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

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | Reminder: %s",
                             taskName, deadline, category, status,
                             new SimpleDateFormat("yyyy/MM/dd HH:mm").format(reminderTime));
    }
}

public class Dashboard extends JFrame {
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
    private connection dbConnection;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private List<Task> tasks = new ArrayList<>();

    JLabel completedTasksLabel;
        JLabel inProgressTasksLabel;
        JLabel incompleteTasksLabel;
        JLabel messageLabel;
    
    

    public Dashboard() {
        dbConnection = new connection();
        setTitle("SoDa");
        setSize(1120, 630);
        setLocation(250, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    
        add(createSidebarPanel(), BorderLayout.WEST);
        add(showDashboard(), BorderLayout.CENTER);
        
    
        loadTasksFromDB(); // Load tasks from the database when the app starts
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
        trendingTasksButton.addActionListener(e-> showTrendingTaskPanel());
    
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

    private void loadTasksFromDB() {
        try {
            // Clear current task lists
            incompleteTaskListModel.clear();
            inProgressTaskListModel.clear();
            completedTaskListModel.clear();

            // Query to get all tasks
            String query = "SELECT * FROM tasks";
            ResultSet rs = dbConnection.statement.executeQuery(query);

            // Iterate over the result set and categorize tasks
            while (rs.next()) {
                String taskName = rs.getString("task_Name");
                String deadline = rs.getString("deadline");
                String category = rs.getString("category");
                String status = rs.getString("status");
                Date reminderTime = rs.getTimestamp("reminder_Time");

                Task task = new Task(taskName, deadline, category, status, reminderTime);

                switch (status) {
                    case "Incomplete":
                        incompleteTaskListModel.addElement(task);
                        break;
                    case "In Progress":
                        inProgressTaskListModel.addElement(task);
                        break;
                    case "Completed":
                        completedTaskListModel.addElement(task);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel showDashboard() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel taskPanel = createTaskPanel();
        JPanel statisticsPanel = createStatisticsPanel();


        tabbedPane.addTab("Tasks", taskPanel);
        tabbedPane.addTab("Statistics", statisticsPanel);

        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);

        revalidate();
        repaint();

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
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

    // Create the background panel with the image
        JPanel panel = new BackgroundPanel(i2);
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Task: "));
        taskInputField = new JTextField(20);
        topPanel.add(taskInputField);
        topPanel.add(new JLabel("Deadline: "));
        deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "yyyy/MM/dd");
        deadlineSpinner.setEditor(dateEditor);
        deadlineSpinner.setValue(new Date());
        topPanel.add(deadlineSpinner);
        topPanel.add(new JLabel("Category: "));
        String[] categories = {"Work", "Personal", "Urgent", "Other"};
        categoryComboBox = new JComboBox<>(categories);
        topPanel.add(categoryComboBox);
        topPanel.add(new JLabel("Reminder: "));
        reminderSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor reminderEditor = new JSpinner.DateEditor(reminderSpinner, "yyyy/MM/dd HH:mm");
        reminderSpinner.setEditor(reminderEditor);
        reminderSpinner.setValue(new Date());
        topPanel.add(reminderSpinner);

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> Dashboard());
        topPanel.add(addTaskButton);

        JButton removeTaskButton = new JButton("Remove Task");
        removeTaskButton.addActionListener(e -> removeTask());
        topPanel.add(removeTaskButton);

        JButton updateTaskButton = new JButton("Update Task");
        topPanel.add(updateTaskButton);

    // Assuming you have a method to get the selected task
    updateTaskButton.addActionListener(e -> {
        Task selectedTask = getSelectedTask(); // Get the currently selected task from the list
        if (selectedTask != null) {
            updateTask(selectedTask); // Call the updateTask method
        } else {
            JOptionPane.showMessageDialog(this, "No task selected!");
        }
    });

     // Create the Move Task button
     JButton moveTaskButton = new JButton("Move Task");
     moveTaskButton.addActionListener(e -> {
         Task selectedTask = getSelectedTask(); // Get the currently selected task
         if (selectedTask != null) {
             moveTask(selectedTask); // Call the moveTask method
         } else {
             JOptionPane.showMessageDialog(this, "No task selected!");
         }
     });
     topPanel.add(moveTaskButton);

     

        incompleteTaskListModel = new DefaultListModel<>();
        incompleteTaskList = new JList<>(incompleteTaskListModel);
        inProgressTaskListModel = new DefaultListModel<>();
        inProgressTaskList = new JList<>(inProgressTaskListModel);
        completedTaskListModel = new DefaultListModel<>();
        completedTaskList = new JList<>(completedTaskListModel);

        ImageIcon taskListPanelIcon = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
        Image taskListPanelImage = taskListPanelIcon.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

        BackgroundPanel taskListPanel = new BackgroundPanel(taskListPanelImage); // Add background image
        taskListPanel.setOpaque(false); // Transparent to show background
        taskListPanel.setLayout(new GridLayout(2, 3,10,80));

        JPanel incompletePanel = new JPanel(new BorderLayout());
        incompletePanel.add(new JLabel("Incomplete"), BorderLayout.NORTH);
        incompletePanel.add(new JScrollPane(incompleteTaskList), BorderLayout.CENTER);

        JPanel inProgressPanel = new JPanel(new BorderLayout());
        inProgressPanel.add(new JLabel("In Progress"), BorderLayout.NORTH);
        inProgressPanel.add(new JScrollPane(inProgressTaskList), BorderLayout.CENTER);

        JPanel completedPanel = new JPanel(new BorderLayout());
        completedPanel.add(new JLabel("Completed"), BorderLayout.NORTH);
        completedPanel.add(new JScrollPane(completedTaskList), BorderLayout.CENTER);

        JPanel notePanel = new JPanel(new BorderLayout());
        notePanel.add(new JLabel("Notes"), BorderLayout.NORTH); 
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        notePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel linkPanel = new JPanel(new BorderLayout());
        linkPanel.add(new JLabel("Links"), BorderLayout.NORTH); 
        JTextArea textArea2 = new JTextArea();
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        JScrollPane scrollPane2 = new JScrollPane(textArea2);
        linkPanel.add(scrollPane2, BorderLayout.CENTER);

        // SUPPOSED TO BE STREAK PANEL*
        // JPanel linkPanel = new JPanel(new BorderLayout());
        // linkPanel.add(new JLabel("Links"), BorderLayout.NORTH); 
        // JTextArea textArea2 = new JTextArea();
        // textArea2.setLineWrap(true);
        // textArea2.setWrapStyleWord(true);
        // JScrollPane scrollPane2 = new JScrollPane(textArea2);
        // linkPanel.add(scrollPane2, BorderLayout.CENTER);
        
        

        taskListPanel.add(incompletePanel);
        taskListPanel.add(inProgressPanel);
        taskListPanel.add(completedPanel);
        taskListPanel.add(notePanel);
        taskListPanel.add(linkPanel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(taskListPanel, BorderLayout.CENTER);
        

        return panel;
    }

    private Task getSelectedTask() {
        // Check if a task is selected in the incomplete list
        int incompleteIndex = incompleteTaskList.getSelectedIndex();
        if (incompleteIndex != -1) {
            return incompleteTaskListModel.getElementAt(incompleteIndex);
        }
    
        // Check if a task is selected in the in-progress list
        int inProgressIndex = inProgressTaskList.getSelectedIndex();
        if (inProgressIndex != -1) {
            return inProgressTaskListModel.getElementAt(inProgressIndex);
        }
    
        // Check if a task is selected in the completed list
        int completedIndex = completedTaskList.getSelectedIndex();
        if (completedIndex != -1) {
            return completedTaskListModel.getElementAt(completedIndex);
        }
    
        return null; // No task selected in any list
    }


    

        // Initially update the list of upcoming reminders


  
        

    
    
    private  JPanel createStatisticsPanel(){


        JPanel statpanel = new JPanel();
        completedTasksLabel = new JLabel("Completed Tasks: 0");
        inProgressTasksLabel = new JLabel("In-Progress Tasks: 0");
        incompleteTasksLabel = new JLabel("Incomplete Tasks: 0");
        messageLabel = new JLabel("");  // Message label for additional notifications
        
        // Add the labels to the statistics panel
        statpanel.add(completedTasksLabel);
        statpanel.add(inProgressTasksLabel);
        statpanel.add(incompleteTasksLabel);
        statpanel.add(messageLabel);
        
        return statpanel;
    }

    private void Dashboard() {
        String taskName = taskInputField.getText();
        Date deadlineDate = (Date) deadlineSpinner.getValue();
        String deadline = new SimpleDateFormat("yyyy/MM/dd").format(deadlineDate);
        String category = (String) categoryComboBox.getSelectedItem();
        Date reminderTime = (Date) reminderSpinner.getValue();
        Task newTask = new Task(taskName, deadline, category, Task.STATUS_INCOMPLETE, reminderTime);
        allTasks.add(newTask);
        incompleteTaskListModel.addElement(newTask);
        saveTaskToDB(newTask);
        clearTaskFields();
    }

    private void removeTask() {
        int selectedIndex = incompleteTaskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectedTask = incompleteTaskListModel.remove(selectedIndex);
            allTasks.remove(selectedTask);
            removeTaskFromDB(selectedTask);
        }

        int selectedIndex2 = inProgressTaskList.getSelectedIndex();
        if (selectedIndex2 != -1) {
            Task selectedTask = inProgressTaskListModel.remove(selectedIndex2);
            allTasks.remove(selectedTask);
            removeTaskFromDB(selectedTask);
        }

        int selectedIndex3 = completedTaskList.getSelectedIndex();
        if (selectedIndex3 != -1) {
            Task selectedTask = completedTaskListModel.remove(selectedIndex3);
            allTasks.remove(selectedTask);
            removeTaskFromDB(selectedTask);
        }
    }

    private void updateTask(Task task) {
        // Show dialog to update task details (like name, description)
        String newName = JOptionPane.showInputDialog(this, "Update Task Name:", task.getTaskName());
        
        if (newName != null && !newName.trim().isEmpty()) {
            task.setTaskName(newName); // Update the task's name
            updateTaskInDB(task); // Update the task in the database
        }
    }

    private void updateTaskInDB(Task task) {
        try {
            String query = "UPDATE tasks SET task_name = ? WHERE task_name = ?";
            PreparedStatement stmt = dbConnection.connection.prepareStatement(query);
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getTaskName()); // Assuming task_name is unique
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    

    private void moveTask(Task selectedTask) {
        // Create a JOptionPane to ask where to move the task
        Object[] options = {"Incomplete", "In Progress", "Completed"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Move the task to:",
            "Update Task Status",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
    
        // Remove task from the previous list
        removeTaskFromPreviousList(selectedTask);
    
        // Determine which list to move the task to based on user's choice
        switch (choice) {
            case 0: // Incomplete
                selectedTask.setStatus(Task.STATUS_INCOMPLETE);
                incompleteTaskListModel.addElement(selectedTask);
                updateTaskStatistics("incompleteListModel"); // Update stats for incomplete tasks
                break;
            case 1: // In Progress
                selectedTask.setStatus(Task.STATUS_IN_PROGRESS);
                inProgressTaskListModel.addElement(selectedTask);
                updateTaskStatistics("inProgressListModel"); // Update stats for in-progress tasks
                break;
            case 2: // Completed
                selectedTask.setStatus(Task.STATUS_COMPLETED);
                completedTaskListModel.addElement(selectedTask);
                updateTaskStatistics("completeTaskListModel"); // Update stats for completed tasks
                break;
            default:
                return; // If the user cancels the action
        }
    
        // Update the task status in the database
        updateTaskStatusInDB(selectedTask);
    }

    // Method to update task statistics when a task is moved
public void updateTaskStatistics(String taskListModel) {
    int completedTasksCount = 0;
    int inProgressTasksCount = 0;
    int incompleteTasksCount = 0;
    
    switch (taskListModel) {
        case "completeTaskListModel":
            completedTasksCount++;
            completedTasksLabel.setText("Completed Tasks: " + completedTasksCount);
            break;
        case "inProgressListModel":
            inProgressTasksCount++;
            inProgressTasksLabel.setText("In-Progress Tasks: " + inProgressTasksCount);
            break;
        case "incompleteListModel":
            incompleteTasksCount++;
            incompleteTasksLabel.setText("Incomplete Tasks: " + incompleteTasksCount);
            break;
    }

    // Check if specific conditions are met and update the message label
    if (incompleteTasksCount > 5) {
        messageLabel.setText("Stop being lazy!");
    } else if (inProgressTasksCount > 5) {
        messageLabel.setText("Too many in progress, do the work without being lazy!");
    } else {
        messageLabel.setText("");  // Clear the message if conditions are not met
    }
}

    
    
    

    private void removeTaskFromPreviousList(Task selectedTask) {
        if (incompleteTaskListModel.contains(selectedTask)) {
            incompleteTaskListModel.removeElement(selectedTask);
        } else if (inProgressTaskListModel.contains(selectedTask)) {
            inProgressTaskListModel.removeElement(selectedTask);
        } else if (completedTaskListModel.contains(selectedTask)) {
            completedTaskListModel.removeElement(selectedTask);
        }
    }
    
    
    
    

    private void saveTaskToDB(Task task) {
        try {
            connection dbConnection = new connection(); // This creates a new instance and connects to the database.
            Connection conn = dbConnection.connection;  // Access the connection object directly from the dbConnection instance.

            String query = "INSERT INTO tasks (task_name, deadline, category, status, reminder_time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, task.getTaskName());
            stmt.setString(2, task.getDeadline());
            stmt.setString(3, task.getCategory());
            stmt.setString(4, task.getStatus());
            stmt.setTimestamp(5, new java.sql.Timestamp(task.getReminderTime().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTaskStatusInDB(Task task) {
        try {
            String query = "UPDATE tasks SET status = ? WHERE task_name = ?";
            PreparedStatement stmt = dbConnection.connection.prepareStatement(query);
            stmt.setString(1, task.getStatus());  // Update the status
            stmt.setString(2, task.getTaskName()); // Assuming task_name is unique
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    

    private void removeTaskFromDB(Task task) {
        try {
            connection dbConnection = new connection(); // This creates a new instance and connects to the database.
            Connection conn = dbConnection.connection;  // Access the connection object directly from the dbConnection instance.

            String query = "DELETE FROM tasks WHERE task_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, task.getTaskName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearTaskFields() {
        taskInputField.setText("");
        deadlineSpinner.setValue(new Date());
        categoryComboBox.setSelectedIndex(0);
        reminderSpinner.setValue(new Date());
    }

    private JPanel createDashboardPanel() {
    JTabbedPane tabbedPane = new JTabbedPane();
    
    JPanel taskPanel = createTaskPanel();
    JPanel statisticspanel = createTaskPanel(); // Create the Task panel
    
    tabbedPane.addTab("Tasks", taskPanel);
    tabbedPane.addTab("Statistics",statisticspanel);

    
    JPanel dashboardPanel = new JPanel(new BorderLayout());
    dashboardPanel.add(tabbedPane, BorderLayout.CENTER);
    
    return dashboardPanel;
}

    private void showHome() {
        getContentPane().removeAll(); // Remove existing components
        add(createSidebarPanel(), BorderLayout.WEST); // Re-add sidebar
        add(createDashboardPanel(), BorderLayout.CENTER); // Add dashboard panel
        revalidate(); // Refresh the layout
        repaint(); // Redraw the component
    }

   private JPanel createRecommendationPanel(){
    ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
    Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

    // Create the background panel with the image
        JPanel recommednationPanel = new BackgroundPanel(i2);
        recommednationPanel.setLayout(new BorderLayout());

        recommednationPanel.setBackground(new Color(255, 255, 255)); // White background
        recommednationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] recommendation = {
            "Oraganize your workspace",
            "Preapare meal ideas for the week",
            "DIY home decor projects.",
            "Read a book",
            "Take the walk in the nature",
            "Visit your friends"
        };

        JLabel rJLabel = new JLabel(getRandomQuote(recommendation));
        rJLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rJLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton newTaskButton = new JButton("Get New Task");
        newTaskButton.setFont(new Font("Arial", Font.BOLD, 16));
        newTaskButton.setBackground(new Color(255, 182, 193)); // Light pink color
        newTaskButton.setFocusPainted(false);
        newTaskButton.addActionListener(e -> rJLabel.setText(getRandomQuote(recommendation)));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setSize(50, 50);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showHome()); // Navigate back to the dashboard

         // Add the components to the panel
         recommednationPanel.add(backButton,BorderLayout.WEST);
         recommednationPanel.add(rJLabel, BorderLayout.CENTER);
         recommednationPanel.add(newTaskButton, BorderLayout.SOUTH);
     
         return recommednationPanel;
   }

   // Method to get a random quote from the array
   private String getRandomQuotes(String[] recommedation) {
    int index = (int) (Math.random() * recommedation.length);
    return recommedation[index];
}

private void showRecommendationPanel() {
    JPanel motivationPanel = createRecommendationPanel();
    setContentPane(motivationPanel);
    revalidate(); // Refresh the frame to show the new content
    repaint();
    
}



    private JPanel createMotivationPanel() {

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

    // Create the background panel with the image
        JPanel motivationPanel = new BackgroundPanel(i2);
        motivationPanel.setLayout(new BorderLayout());
    
        // Set a background color or image
        motivationPanel.setBackground(new Color(255, 255, 255)); // White background
        motivationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Create a list of motivational quotes
        String[] quotes = {
            "Believe in yourself and all that you are.",
            "The only way to do great work is to love what you do.",
            "Success is not the key to happiness. Happiness is the key to success.",
            "Dream big and dare to fail.",
            "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
            "The future belongs to those who believe in the beauty of their dreams."
        };
    
        // Create a JLabel to display quotes
        JLabel quoteLabel = new JLabel(getRandomQuote(quotes));
        quoteLabel.setFont(new Font("Arial", Font.BOLD, 24));
        quoteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        quoteLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    
        // Add a button to get a new random quote
        JButton newQuoteButton = new JButton("Get New Quote");
        newQuoteButton.setFont(new Font("Arial", Font.BOLD, 16));
        newQuoteButton.setBackground(new Color(255, 182, 193)); // Light pink color
        newQuoteButton.setFocusPainted(false);
        newQuoteButton.addActionListener(e -> quoteLabel.setText(getRandomQuote(quotes)));

         // Create a button to navigate back to the dashboard
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setSize(50, 50);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> showHome()); // Navigate back to the dashboard
    
        // Add the components to the panel
        motivationPanel.add(backButton,BorderLayout.WEST);
        motivationPanel.add(quoteLabel, BorderLayout.CENTER);
        motivationPanel.add(newQuoteButton, BorderLayout.SOUTH);
    
        return motivationPanel;
    }
    
    // Method to get a random quote from the array
    private String getRandomQuote(String[] quotes) {
        int index = (int) (Math.random() * quotes.length);
        return quotes[index];
    }
    
    private void showMotivationPanel() {
        JPanel motivationPanel = createMotivationPanel();
        setContentPane(motivationPanel);
        revalidate(); // Refresh the frame to show the new content
        repaint();
        
    }

    private JPanel createTrendingtaskJPanel(){
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/BgLogin.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

        JPanel TrendingPanel = new BackgroundPanel(i2);
        TrendingPanel.setLayout(new BorderLayout());

        TrendingPanel.setBackground(new Color(255, 255, 255)); // White background
        TrendingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
            TrendingPanel.setLayout(new BoxLayout(TrendingPanel, BoxLayout.Y_AXIS));
            TrendingPanel.setBorder(BorderFactory.createTitledBorder("Trending Tasks"));
         TrendingPanel.setBackground(Color.LIGHT_GRAY);
    
            // Sample list of trending tasks (you can populate this dynamically from your database)
            String[] trendingTasks = {
                "Complete the project report",
                "Plan the upcoming team meeting",
                "Update the website design",
                "Organize the office files",
                "Prepare presentation slides"
            };
    
            // Create UI components for each task
            for (String task : trendingTasks) {
                JPanel taskPanel = new JPanel(new BorderLayout());
                taskPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                taskPanel.setBackground(Color.WHITE);
    
                JLabel taskLabel = new JLabel(task);
                taskLabel.setFont(new Font("Arial", Font.BOLD, 14));
    
                JButton detailsButton = new JButton("Details");
                detailsButton.setBackground(Color.PINK);
                detailsButton.setForeground(Color.WHITE);
                detailsButton.setFocusable(false);
    
                // Add task label and button to each task panel
                taskPanel.add(taskLabel, BorderLayout.CENTER);
                taskPanel.add(detailsButton, BorderLayout.EAST);
    
                // Add a hover effect to the task panel
                taskPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        taskPanel.setBackground(Color.PINK);
                    }
    
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        taskPanel.setBackground(Color.WHITE);
                    }
                });
    
                // Add the individual task panel to the main trending task panel
                TrendingPanel.add(taskPanel);
            }
    
        return TrendingPanel;
    }

    private void showTrendingTaskPanel() {
        JPanel motivationPanel = createMotivationPanel();
        setContentPane(motivationPanel);
        revalidate(); // Refresh the frame to show the new content
        repaint();
        
    }

    
    

    public static void main(String[] args) {
        Task task = new Task("Finish report", "2024-10-23", "Work", "Incomplete", new Date(System.currentTimeMillis() + 60000)); // 1 minute from now
        task.startReminder();

        new Dashboard();
    }
}

import java.awt.*;
import java.awt.event.ActionEvent;
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
   
    
    private List<Task> tasks = new ArrayList<>();
    private JTextArea textArea;
    private JTextArea textArea2;


    JLabel incompleteTasksLabel;
    JLabel inProgressTasksLabel;
        JLabel completedTasksLabel;
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
            loadNotesAndLinksFromDB(textArea, textArea2);
        
            setupKeyboardShortcuts();
        
            // Start a timer to check for expired tasks every minute
            // Timer deadlineChecker = new Timer();
            // deadlineChecker.scheduleAtFixedRate(new TimerTask() {
            //     @Override
            //     public void run() {
            //         checkTaskDeadlines();
            //     }
            // }, 0, 60000); // Check every minute
        
            setVisible(true);
        }
        // private Date parseDeadline(String deadline) {
        //     if (deadline == null || deadline.trim().isEmpty()) {
        //         System.out.println("Deadline is null or empty: " + deadline);
        //         return null;
        //     }
        //     try {
        //         System.out.println("Parsing deadline: " + deadline); // Debugging
        //         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd"); // Update format as needed
        //         Date parsedDate = dateFormat.parse(deadline);
        //         System.out.println("Parsed date: " + parsedDate); // Debugging
        //         return parsedDate;
        //     } catch (Exception e) {
        //         System.out.println("Error parsing deadline: " + deadline); // Debugging
        //         e.printStackTrace();
        //         return null;
        //     }
        // }

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
        JButton logoutButton = createSidebarButton("Logout"); // New logout button
    
        homeButton.addActionListener(e -> showHome());
        recommendationButton.addActionListener(e -> showRecommendationPanel());
        motivationButton.addActionListener(e -> showMotivationPanel());
        trendingTasksButton.addActionListener(e -> showTrendingTaskPanel());
        
        // Add action listener for logout button
        logoutButton.addActionListener(e -> logout());
    
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
        sidebarPanel.add(logoutButton, gbc); // Add logout button to the sidebar
    
        gbc.gridy = 5;
        gbc.weighty = 1.0; // Make this row take up remaining vertical space
        sidebarPanel.add(new JLabel(), gbc);
    
        return sidebarPanel;
    }
    
    private void logout() {
        // Clear the current dashboard
        setVisible(false);
        getContentPane().removeAll(); // Remove existing components
    
       new MyLogin();
    
        revalidate(); // Refresh the layout
        repaint(); // Redraw the component
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
            // Query to get all tasks
            String query = "SELECT * FROM tasks";
            ResultSet rs = dbConnection.statement.executeQuery(query);
    
            // Temporarily store tasks
            List<Task> newTasks = new ArrayList<>();
    
            while (rs.next()) {
                String taskName = rs.getString("task_Name");
                String deadline = rs.getString("deadline");
                String category = rs.getString("category");
                String status = rs.getString("status");
                Date reminderTime = rs.getTimestamp("reminder_Time");
    
                Task task = new Task(taskName, deadline, category, status, reminderTime);
                newTasks.add(task);
            }
    
            // Only clear and add if new tasks exist
            if (!newTasks.isEmpty()) {
                incompleteTaskListModel.clear();
                inProgressTaskListModel.clear();
                completedTaskListModel.clear();
    
                for (Task task : newTasks) {
                    switch (task.getStatus()) {
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
        dashboardPanel.setPreferredSize(new Dimension(1000,800));

        revalidate();
        repaint();

        return dashboardPanel;
    }

    private void refreshTaskList() {
        
        repaint(); // Repaint the UI to reflect changes
        revalidate(); // Revalidate to ensure proper rendering
        
    }

    private void setupKeyboardShortcuts() {
        JRootPane rootPane = getRootPane();
    
        // DELETE → Remove Task
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"), "removeTask");
        rootPane.getActionMap().put("removeTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTask();
            }
        });
    
        // SHIFT + M → Move Task
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl M"), "moveTask");
        rootPane.getActionMap().put("moveTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Task selectedTask = getSelectedTask();
                if (selectedTask != null) {
                    moveTask(selectedTask);
                } else {
                    JOptionPane.showMessageDialog(null, "No task selected!");
                }
            }
        });
    
        // CTRL + U → Update Task
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl U"), "updateTask");
        rootPane.getActionMap().put("updateTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Task selectedTask = getSelectedTask();
                if (selectedTask != null) {
                    updateTask(selectedTask);
                } else {
                    JOptionPane.showMessageDialog(null, "No task selected!");
                }
            }
        });
    
        // ENTER → Add Task
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "addTask");
        rootPane.getActionMap().put("addTask", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dashboard();
            }
        });
    
        // CTRL + S → Save Notes & Links
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl S"), "saveNotes");
        rootPane.getActionMap().put("saveNotes", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNotesAndLinksToDB(textArea.getText(), textArea2.getText());
            }
        });
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
        updateTaskButton.addActionListener(e -> {
        Task selectedTask = getSelectedTask(); // Get the currently selected task
        if (selectedTask != null) {
            updateTask(selectedTask); // Call the updateTask method
            } else {
                JOptionPane.showMessageDialog(this, "No task selected!");
        }
        });

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane4 = new JScrollPane(textArea);

        textArea2 = new JTextArea();
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        JScrollPane scrollPane5 = new JScrollPane(textArea2);

        JButton saveButton = new JButton("Save Notes & Links");
        saveButton.addActionListener(e -> saveNotesAndLinksToDB(textArea.getText(), textArea2.getText()));
        topPanel.add(saveButton);
        


topPanel.add(updateTaskButton);
 
    
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
        taskListPanel.setLayout(new GridLayout(3, 2,10,80));

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
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        notePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel linkPanel = new JPanel(new BorderLayout());
        linkPanel.add(new JLabel("Links"), BorderLayout.NORTH); 
        textArea2 = new JTextArea();
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
        messageLabel = new JLabel("this panel shows how many tasks you completed");  // Message label for additional notifications
        
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
        String oldName = task.getTaskName(); // Store the old name
        String newName = JOptionPane.showInputDialog(this, "Update Task Name:", task.getTaskName());
        
        if (newName != null && !newName.trim().isEmpty()) {
            task.setTaskName(newName);
            updateTaskInDB(task, oldName); // Pass both new and old names
            refreshTaskList();
        }
    }
    
    private void updateTaskInDB(Task task, String oldName) {
        try {
            String query = "UPDATE tasks SET task_name = ? WHERE task_name = ?";
            PreparedStatement stmt = dbConnection.connection.prepareStatement(query);
            stmt.setString(1, task.getTaskName()); // New name
            stmt.setString(2, oldName); // Old name (so the database finds the right row)
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

    // private void checkTaskDeadlines() {
    //     Date now = new Date();
    //     List<Task> tasksToRemove = new ArrayList<>();
    
    //     // Check all tasks in the incomplete list
    //     for (int i = 0; i < incompleteTaskListModel.size(); i++) {
    //         Task task = incompleteTaskListModel.getElementAt(i);
    //         Date deadline = parseDeadline(task.getDeadline());
    //         if (deadline != null && deadline.before(now)) {
    //             tasksToRemove.add(task);
    //         }
    //     }
        
    
        // // Check all tasks in the in-progress list
        // for (int i = 0; i < inProgressTaskListModel.size(); i++) {
        //     Task task = inProgressTaskListModel.getElementAt(i);
        //     Date deadline = parseDeadline(task.getDeadline());
        //     if (deadline != null && deadline.before(now)) {
        //         tasksToRemove.add(task);
        //     }
        // }
    
        // Check all tasks in the completed list
        // for (int i = 0; i < completedTaskListModel.size(); i++) {
        //     Task task = completedTaskListModel.getElementAt(i);
        //     Date deadline = parseDeadline(task.getDeadline());
        //     if (deadline != null && deadline.before(now)) {
        //         tasksToRemove.add(task);
        //     }
        // }
    
    //     // Remove expired tasks and show alerts
    //     for (Task task : tasksToRemove) {
    //         removeTaskFromPreviousList(task);
    //         removeTaskFromDB(task); // This removes the task from the database
    //         JOptionPane.showMessageDialog(this, "Task '" + task.getTaskName() + "' has reached its deadline and has been removed.");
    //     }
    // }
    
    
    

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
    private void saveNotesAndLinksToDB(String noteText, String linkText) {
        try {
            System.out.println("Saving Notes: " + noteText); // ✅ Debugging
            System.out.println("Saving Links: " + linkText); // ✅ Debugging
    
            String query = "INSERT INTO notesandlinks (note_text, link_text) VALUES (?, ?)";
            PreparedStatement stmt = dbConnection.connection.prepareStatement(query);
            stmt.setString(1, noteText);
            stmt.setString(2, linkText);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void loadNotesAndLinksFromDB(JTextArea textArea, JTextArea textArea2) {
        try {
            String query = "SELECT note_text, link_text FROM notesandlinks ORDER BY id DESC LIMIT 1";
            ResultSet rs = dbConnection.statement.executeQuery(query);
            if (rs.next()) {
                textArea.setText(rs.getString("note_text"));
                textArea2.setText(rs.getString("link_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    

    private JPanel createDashboardPanel() {
    JTabbedPane tabbedPane = new JTabbedPane();
    
    JPanel taskPanel = createTaskPanel();
    JPanel statisticspanel = createStatisticsPanel(); // Create the Task panel
    
    tabbedPane.addTab("Tasks", taskPanel);
    tabbedPane.addTab("Statistics",statisticspanel);

    
    JPanel dashboardPanel = new JPanel(new BorderLayout());
    dashboardPanel.add(tabbedPane, BorderLayout.CENTER);
    

    
    return dashboardPanel;
}

private void showHome() {
    getContentPane().removeAll();

    setContentPane(new JPanel(new BorderLayout())); // Reset the pane
    getContentPane().add(createSidebarPanel(), BorderLayout.WEST);
    getContentPane().add(createDashboardPanel(), BorderLayout.CENTER);

    loadTasksFromDB(); // Reload tasks
    revalidate();
    repaint();
}




   private JPanel createRecommendationPanel(){
    ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/create.png"));
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
            "Visit your friends",
            "Read a book for 30 minutes",
            "Watch an interesting documentary",
            "Learn a new word and use it in a sentence",
            "Meditate for 10 minutes",
            "Listen to an educational podcast",
            "Do a quick workout or stretch",
            "Try a healthy recipe",
            "Declutter your phone (delete old apps & photos)",
            "Try a new hobby or revisit an old one",
            "Write down a random idea or invention.",
            "Try a new type of food or drink"
            
            
        };

        JLabel rJLabel = new JLabel(getRandomQuote(recommendation));
        rJLabel.setForeground(Color.BLACK);
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
    getContentPane().removeAll(); // Remove all previous components
    JPanel recommendationPanel = createRecommendationPanel();
    
    // ✅ Reset the entire content pane to avoid background layering
    setContentPane(new JPanel(new BorderLayout())); 
    getContentPane().add(recommendationPanel, BorderLayout.CENTER);

    revalidate();
    repaint();
}



    private JPanel createMotivationPanel() {

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/trending.jpg"));
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
            "The future belongs to those who believe in the beauty of their dreams.",
            "Don't watch the clock; do what it does. Keep going.",
            "The secret of getting ahead is getting started.",
            "Opportunities don’t happen. You create them.",
            "The best way to predict the future is to create it.",
            "A river cuts through rock not because of its power, but because of its persistence." ,
            "The only way to do great work is to love what you do.",
            "Doubt kills more dreams than failure ever will.",
            "Your only limit is your mind.",
            "Don’t be pushed by your problems, be led by your dreams.",
            "Every day may not be good, but there is something good in every day.",
            "What you get by achieving your goals is not as important as what you become by achieving them.",
        };

    
        // Create a JLabel to display quotes
        JLabel quoteLabel = new JLabel(getRandomQuote(quotes));
        quoteLabel.setFont(new Font("Arial", Font.BOLD, 24));
        quoteLabel.setForeground(Color.BLACK);
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
        getContentPane().removeAll();
        JPanel motivationPanel = createMotivationPanel();
    
        setContentPane(new JPanel(new BorderLayout())); // Reset the pane
        getContentPane().add(motivationPanel, BorderLayout.CENTER);
    
        revalidate();
        repaint();
    }
    


    private JPanel createTrendingtaskJPanel(){
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/simple.png"));
        Image i2 = i1.getImage().getScaledInstance(1170, 650, Image.SCALE_DEFAULT);

    // Create the background panel with the image
        JPanel TrendingPanel = new BackgroundPanel(i2);
        TrendingPanel.setLayout(new BorderLayout());
    
        // Set a background color or image
        TrendingPanel.setBackground(new Color(255, 255, 255)); // White background
        TrendingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        // Create a list of motivational quotes
        String[] quotes = {
            "Playing Games with freinds",
            "The only way to do great work is to love what you do.",
            "Success is not the key to happiness. Happiness is the key to success.",
            "Dream big and dare to fail.",
            "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
            "The future belongs to those who believe in the beauty of their dreams.",
            "Visit a new café or restaurant.",
            "Explore AI tools for productivity.",
            "Read a trending book or bestseller",
            "Watch a new documentary or series",
            "Try a viral recipe from social media",
            "Attend a networking or professional event",
            "Experiment with AI-generated art or writing",
            "Explore ChatGPT & AI tools for productivity and automation",
            "Create a chatbot using OpenAI API",
            "Use AI for text summarization and automate note-taking",
            "Read research papers on the latest AI trends"


        };

    
        // Create a JLabel to display quotes
        JLabel quoteLabel = new JLabel(getRandomQuote(quotes));
        quoteLabel.setFont(new Font("Arial", Font.BOLD, 24));
        quoteLabel.setForeground(Color.BLACK);
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
        TrendingPanel.add(backButton,BorderLayout.WEST);
        TrendingPanel.add(quoteLabel, BorderLayout.CENTER);
        TrendingPanel.add(newQuoteButton, BorderLayout.SOUTH);
    
        return TrendingPanel;
    }

    private void showTrendingTaskPanel() {
        getContentPane().removeAll();
        JPanel TrendingJPanel = createTrendingtaskJPanel();

        setContentPane(new JPanel(new BorderLayout())); 
        getContentPane().add(TrendingJPanel, BorderLayout.CENTER);
        
        revalidate(); // Refresh the frame to show the new content
        repaint();
        
    }


    
    

    public static void main(String[] args) {
        Task task = new Task("Finish report", "2024-10-23", "Work", "Incomplete", new Date(System.currentTimeMillis() + 60000)); // 1 minute from now
        task.startReminder();

        new Dashboard();
    }
}

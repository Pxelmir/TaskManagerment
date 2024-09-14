import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

public class UpdateTask extends JFrame {
    private JTextField taskNameField;
    private JTextField expiryDateField;
    private JComboBox<String> categoryComboBox;
    private JCheckBox completeCheckBox;
    private JTextField subTaskField;
    private JButton updateTaskButton, addSubTaskButton, displayButton;
    private JTextArea taskDetailsArea;
    private ArrayList<String> subTasks;
    private String taskName, category;
    private Date expiryDate;
    private boolean isComplete;

    // Constructor accepting task details to update
    public UpdateTask(String taskName, String category, Date expiryDate, boolean isComplete, ArrayList<String> subTasks) {
        // Initialize components
        setTitle("Update Task");
        setLayout(new FlowLayout());
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.taskName = taskName;
        this.category = category;
        this.expiryDate = expiryDate;
        this.isComplete = isComplete;
        this.subTasks = subTasks != null ? subTasks : new ArrayList<>();

        // Input fields
        taskNameField = new JTextField(taskName, 15);
        expiryDateField = new JTextField(10); // Format: YYYY-MM-DD
        String[] categories = {"Work", "Personal", "Home", "Education"};
        categoryComboBox = new JComboBox<>(categories);
        completeCheckBox = new JCheckBox("Task Complete", isComplete);
        subTaskField = new JTextField(15);

        taskDetailsArea = new JTextArea(10, 30);

        // Buttons
        updateTaskButton = new JButton("Update Task");
        addSubTaskButton = new JButton("Add Subtask");
        displayButton = new JButton("Display Updated Task");

        // Add components to the frame
        add(new JLabel("Task Name:"));
        add(taskNameField);
        add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        add(expiryDateField);
        add(new JLabel("Category:"));
        add(categoryComboBox);
        add(completeCheckBox);
        add(new JLabel("Subtask:"));
        add(subTaskField);
        add(addSubTaskButton);
        add(updateTaskButton);
        add(displayButton);
        add(new JScrollPane(taskDetailsArea));

        // Action Listeners
        addSubTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSubTask();
            }
        });

        updateTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTaskDetails();
            }
        });

        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTaskDetails();
            }
        });
    }

    // Method to add a subtask
    private void addSubTask() {
        String subTask = subTaskField.getText();
        if (!subTask.isEmpty()) {
            subTasks.add(subTask);
            subTaskField.setText("");
            JOptionPane.showMessageDialog(this, "Subtask added!");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a subtask.");
        }
    }

    // Method to update task details
    private void updateTaskDetails() {
        taskName = taskNameField.getText();
        category = categoryComboBox.getSelectedItem().toString();
        isComplete = completeCheckBox.isSelected();
        expiryDate = parseDate(expiryDateField.getText());

        if (taskName.isEmpty() || expiryDate == null) {
            JOptionPane.showMessageDialog(this, "Please enter valid task details.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Task updated successfully!");
    }

    // Method to display updated task details
    private void displayTaskDetails() {
        StringBuilder taskDetails = new StringBuilder();
        taskDetails.append("Task Name: ").append(taskName).append("\n");
        taskDetails.append("Category: ").append(category).append("\n");
        taskDetails.append("Expiry Date: ").append(expiryDate).append("\n");
        taskDetails.append("Complete: ").append(isComplete ? "Yes" : "No").append("\n");
        taskDetails.append("Subtasks:\n");
        for (String subTask : subTasks) {
            taskDetails.append("- ").append(subTask).append("\n");
        }
        taskDetailsArea.setText(taskDetails.toString());
    }

    // Method to parse the expiry date
    private Date parseDate(String dateStr) {
        try {
            String[] parts = dateStr.split("-");
            int year = Integer.parseInt(parts[0]) - 1900; // Year offset for Date constructor
            int month = Integer.parseInt(parts[1]) - 1; // Month is 0-indexed
            int day = Integer.parseInt(parts[2]);
            return new Date(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        ArrayList<String> subtasks = new ArrayList<>();
        subtasks.add("First subtask");

        // Example Task to Update
        UpdateTask updateTaskFrame = new UpdateTask("My Task", "Work", new Date(), false, subtasks);
        updateTaskFrame.setVisible(true);
    }
}

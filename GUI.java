import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public GUI() {
        setTitle("Quiz Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        JPanel homePanel = createHomePanel();
        JPanel adminPanel = createAdminPanel();
        mainPanel.add(homePanel, "Home");
        mainPanel.add(adminPanel, "Admin");
        getContentPane().add(mainPanel);
    }


    private JPanel createHomePanel() {
        // Create a custom panel with a background image
        BackgroundPanel panel = new BackgroundPanel("BgLogin.jpg");
        panel.setLayout(new GridBagLayout()); // Set layout for components

        // Add your components to the panel
        JLabel titleLabel = new JLabel("Welcome to the Quiz System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(60, 60, 60));

        JButton adminButton = createStyledButton("ADMIN");
        JButton userButton = createStyledButton("USER");
        JButton exitButton = createStyledButton("EXIT");

        adminButton.setBackground(new Color(59, 89, 182));
        userButton.setBackground(new Color(59, 89, 182));
        exitButton.setBackground(new Color(59, 89, 182));

        adminButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 18));

        adminButton.setForeground(Color.WHITE);
        userButton.setForeground(Color.WHITE);
        exitButton.setForeground(Color.WHITE);

        adminButton.setBorderPainted(false);
        userButton.setBorderPainted(false);
        exitButton.setBorderPainted(false);

        adminButton.setFocusPainted(false);
        userButton.setFocusPainted(false);
        exitButton.setFocusPainted(false);

        adminButton.setPreferredSize(new Dimension(180, 50));
        userButton.setPreferredSize(new Dimension(180, 50));
        exitButton.setPreferredSize(new Dimension(180, 50));

        adminButton.addActionListener(e -> {
            if (managerLogin()) {
                cardLayout.show(mainPanel, "Admin");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Access Denied.");
            }
        });
        userButton.addActionListener(e -> openUserOptions());
        exitButton.addActionListener(e -> System.exit(0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        panel.add(adminButton, gbc);

        gbc.gridy = 2;
        panel.add(userButton, gbc);

        gbc.gridy = 3;
        panel.add(exitButton, gbc);

        return panel;
    }

    private void openUserOptions() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("User Options", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(59, 89, 182));
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        JButton loginButton = createStyledButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.addActionListener(e -> openUserLogin());
        panel.add(loginButton, gbc);

        gbc.gridy = 2;
        JButton registerButton = createStyledButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 50));
        registerButton.addActionListener(e -> registerUser());
        panel.add(registerButton, gbc);

        gbc.gridy = 3;
        JButton backButton = createStyledButton("Back");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        panel.add(backButton, gbc);

        JOptionPane.showOptionDialog(
                this,
                panel,
                "User Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},
                null
        );
    }
    public class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            this.backgroundImage = new ImageIcon("BgLogin.jpg").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void registerUser() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(59, 89, 182));
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(nameLabel, gbc);

        gbc.gridy = 2;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(nameField, gbc);

        gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(emailLabel, gbc);

        gbc.gridy = 4;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(emailField, gbc);

        gbc.gridy = 5;
        JButton registerButton = createStyledButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 50));
        registerButton.addActionListener(e -> {
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO competitors (FullName, Email, Score1, Score2, Score3, Score4, Score5, Level) VALUES (?, ?, 0, 0, 0, 0, 0, 'Beginner')";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, fullName);
                stmt.setString(2, email);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(registerButton, gbc);

        gbc.gridy = 6;
        JButton backButton = createStyledButton("Back");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> openUserOptions());
        panel.add(backButton, gbc);

        JOptionPane.showOptionDialog(
                this,
                panel,
                "User Registration",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},
                null
        );
    }

    private void openUserLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(59, 89, 182));
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(emailLabel, gbc);

        gbc.gridy = 2;
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(emailField, gbc);

        gbc.gridy = 3;
        JButton loginButton = createStyledButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String sql = "SELECT FullName FROM competitors WHERE Email = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
//                    openUserPanel(rs.getString("FullName"));
                } else {
                    JOptionPane.showMessageDialog(this, "User not found. Please register.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        JButton backButton = createStyledButton("Back");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> openUserOptions());
        panel.add(backButton, gbc);

        JOptionPane.showOptionDialog(
                this,
                panel,
                "User Login",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},
                null
        );
    }

    private boolean managerLogin() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Admin Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if ("admin".equals(username) && "admin123".equals(password)) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome, Admin.");
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Access Denied.");
                return false;
            }
        }
        return false;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JButton addQuestionButton = createStyledButton("Add Question");
        JButton deleteQuestionButton = createStyledButton("Delete Question");
        JButton viewScoreboardButton = createStyledButton("View Scoreboard");
        JButton updateQuestionButton = createStyledButton("Update Question");
        JButton viewDetailsButton = createStyledButton("View Competitor Details");
        JButton searchPlayerButton = createStyledButton("Search Player by ID");
        JButton backButton = createStyledButton("Back");

        addQuestionButton.addActionListener(e -> addQuestion());
        deleteQuestionButton.addActionListener(e -> deleteQuestion());
        updateQuestionButton.addActionListener(e -> updateQuestion());
        viewDetailsButton.addActionListener(e -> viewCompetitorDetails());
        searchPlayerButton.addActionListener(e -> searchPlayerByID());
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Home"));



        panel.add(addQuestionButton);
        panel.add(deleteQuestionButton);
        panel.add(viewScoreboardButton);
        panel.add(updateQuestionButton);
        panel.add(viewDetailsButton);
        panel.add(searchPlayerButton);
        panel.add(backButton);

        return panel;
    }

    private void addQuestion() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel levelLabel = new JLabel("Level:");
        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        JComboBox<String> levelComboBox = new JComboBox<>(levels);

        JLabel questionLabel = new JLabel("Question:");
        JTextField questionField = new JTextField(20);

        JLabel option1Label = new JLabel("Option 1:");
        JTextField option1Field = new JTextField(20);

        JLabel option2Label = new JLabel("Option 2:");
        JTextField option2Field = new JTextField(20);

        JLabel option3Label = new JLabel("Option 3:");
        JTextField option3Field = new JTextField(20);

        JLabel option4Label = new JLabel("Option 4:");
        JTextField option4Field = new JTextField(20);

        JLabel correctLabel = new JLabel("Correct Answer:");
        JTextField correctField = new JTextField(20);

        panel.add(levelLabel);
        panel.add(levelComboBox);
        panel.add(questionLabel);
        panel.add(questionField);
        panel.add(option1Label);
        panel.add(option1Field);
        panel.add(option2Label);
        panel.add(option2Field);
        panel.add(option3Label);
        panel.add(option3Field);
        panel.add(option4Label);
        panel.add(option4Field);
        panel.add(correctLabel);
        panel.add(correctField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Question", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String selectedLevel = (String) levelComboBox.getSelectedItem();
            String question = questionField.getText();
            String option1 = option1Field.getText();
            String option2 = option2Field.getText();
            String option3 = option3Field.getText();
            String option4 = option4Field.getText();
            String correct = correctField.getText();

            if (question.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty() || correct.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String sql = "INSERT INTO questions (Question, Option1, Option2, Option3, Option4, Correct, Level) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, question);
                stmt.setString(2, option1);
                stmt.setString(3, option2);
                stmt.setString(4, option3);
                stmt.setString(5, option4);
                stmt.setString(6, correct);
                stmt.setString(7, selectedLevel);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Question Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteQuestion() {
        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        String selectedLevel = (String) JOptionPane.showInputDialog(this, "Choose the level of the question to delete:", "Select Level", JOptionPane.QUESTION_MESSAGE, null, levels, levels[0]);

        if (selectedLevel == null) {
            JOptionPane.showMessageDialog(this, "No level selected. Deletion canceled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, Question FROM questions WHERE Level = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selectedLevel);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> questionsList = new ArrayList<>();
            ArrayList<Integer> questionIDs = new ArrayList<>();

            while (rs.next()) {
                questionsList.add(rs.getString("Question"));
                questionIDs.add(rs.getInt("id"));
            }

            if (questionsList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available for the selected level.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String questionToDelete = (String) JOptionPane.showInputDialog(this, "Select a question to delete:", "Delete Question", JOptionPane.QUESTION_MESSAGE, null, questionsList.toArray(), questionsList.get(0));

            if (questionToDelete != null) {
                int index = questionsList.indexOf(questionToDelete);
                int questionID = questionIDs.get(index);

                String deleteSQL = "DELETE FROM questions WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
                deleteStmt.setInt(1, questionID);
                deleteStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Question deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateQuestion() {
        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        String selectedLevel = (String) JOptionPane.showInputDialog(this, "Choose the level of the question to update:", "Select Level", JOptionPane.QUESTION_MESSAGE, null, levels, levels[0]);

        if (selectedLevel == null) {
            JOptionPane.showMessageDialog(this, "No level selected. Update canceled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id, Question FROM questions WHERE Level = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selectedLevel);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String> questionsList = new ArrayList<>();
            ArrayList<Integer> questionIDs = new ArrayList<>();

            while (rs.next()) {
                questionsList.add(rs.getString("Question"));
                questionIDs.add(rs.getInt("id"));
            }

            if (questionsList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No questions available for the selected level.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String questionToUpdate = (String) JOptionPane.showInputDialog(this, "Select a question to update:", "Update Question", JOptionPane.QUESTION_MESSAGE, null, questionsList.toArray(), questionsList.get(0));

            if (questionToUpdate != null) {
                int index = questionsList.indexOf(questionToUpdate);
                int questionID = questionIDs.get(index);

                JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel questionLabel = new JLabel("Question:");
                JTextField questionField = new JTextField(questionToUpdate, 20);

                JLabel option1Label = new JLabel("Option 1:");
                JTextField option1Field = new JTextField(20);

                JLabel option2Label = new JLabel("Option 2:");
                JTextField option2Field = new JTextField(20);

                JLabel option3Label = new JLabel("Option 3:");
                JTextField option3Field = new JTextField(20);

                JLabel option4Label = new JLabel("Option 4:");
                JTextField option4Field = new JTextField(20);

                JLabel correctLabel = new JLabel("Correct Answer:");
                JTextField correctField = new JTextField(20);

                panel.add(questionLabel);
                panel.add(questionField);
                panel.add(option1Label);
                panel.add(option1Field);
                panel.add(option2Label);
                panel.add(option2Field);
                panel.add(option3Label);
                panel.add(option3Field);
                panel.add(option4Label);
                panel.add(option4Field);
                panel.add(correctLabel);
                panel.add(correctField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Update Question", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String newQuestion = questionField.getText();
                    String option1 = option1Field.getText();
                    String option2 = option2Field.getText();
                    String option3 = option3Field.getText();
                    String option4 = option4Field.getText();
                    String correct = correctField.getText();

                    if (newQuestion.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty() || correct.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String updateSQL = "UPDATE questions SET Question = ?, Option1 = ?, Option2 = ?, Option3 = ?, Option4 = ?, Correct = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                    updateStmt.setString(1, newQuestion);
                    updateStmt.setString(2, option1);
                    updateStmt.setString(3, option2);
                    updateStmt.setString(4, option3);
                    updateStmt.setString(5, option4);
                    updateStmt.setString(6, correct);
                    updateStmt.setInt(7, questionID);
                    updateStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Question updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewCompetitorDetails() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT FullName, Level, Email, Score1, Score2, Score3, Score4, Score5 FROM competitors";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder("Competitor Details:\n\n");
            while (rs.next()) {
                sb.append("Name: ").append(rs.getString("FullName"))
                        .append("\nLevel: ").append(rs.getString("Level"))
                        .append("\nEmail: ").append(rs.getString("Email"))
                        .append("\nScores: [")
                        .append(rs.getInt("Score1")).append(", ")
                        .append(rs.getInt("Score2")).append(", ")
                        .append(rs.getInt("Score3")).append(", ")
                        .append(rs.getInt("Score4")).append(", ")
                        .append(rs.getInt("Score5")).append("]\n")
                        .append("----------------------\n");
            }

            if (sb.toString().equals("Competitor Details:\n\n")) {
                JOptionPane.showMessageDialog(this, "No competitor details available.", "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, sb.toString(), "Competitor Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPlayerByID() {
        String competitorID = JOptionPane.showInputDialog(this, "Enter Competitor ID:", "Search Player", JOptionPane.QUESTION_MESSAGE);

        if (competitorID == null || competitorID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Competitor ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM competitors WHERE CompetitorID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(competitorID));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String details = "Competitor ID: " + rs.getInt("CompetitorID") + "\n"
                        + "Full Name: " + rs.getString("FullName") + "\n"
                        + "Email: " + rs.getString("Email") + "\n"
                        + "Level: " + rs.getString("Level") + "\n";
                JOptionPane.showMessageDialog(this, details, "Player Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No player found with ID: " + competitorID, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 80));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border border = BorderFactory.createLineBorder(Color.WHITE, 2);
        button.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setBorder(BorderFactory.createLineBorder(new Color(59, 89, 182), 2, true));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(40, 60, 150));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(59, 89, 182));
            }
        });

        return button;
    }

    // Other methods remain unchanged...

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}
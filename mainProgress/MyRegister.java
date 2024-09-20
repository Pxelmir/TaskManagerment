import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MyRegister extends JFrame implements ActionListener {
    JTextField tusername, tpassword, temail;
    JButton register, back;

    MyRegister() {
        JLabel username = new JLabel("Username");
        username.setBounds(40, 20, 100, 30);
        add(username);

        tusername = new JTextField();
        tusername.setBounds(150, 20, 150, 30);
        add(tusername);

        JLabel password = new JLabel("Password");
        password.setBounds(40, 70, 100, 30);
        add(password);

        tpassword = new JPasswordField();
        tpassword.setBounds(150, 70, 150, 30);
        add(tpassword);

        register = new JButton("Register");
        register.setBounds(150, 170, 100, 30);
        register.setBackground(Color.BLACK);
        register.setForeground(Color.white);
        register.addActionListener(this);
        add(register);

        back = new JButton("Back");
        back.setBounds(260, 170, 100, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.white);
        back.addActionListener(this);
        add(back);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/BgRegister.jpg"));
        Image i2 = i1.getImage().getScaledInstance(600, 300, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel img = new JLabel(i3);
        img.setBounds(0, 0, 400, 250);
        add(img);

        setSize(400, 250);
        setLocation(450, 200);
        setLayout(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == register) {
            try {
                String username = tusername.getText();
                String password = tpassword.getText();

                // Validate inputs
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required!");
                    return;
                }

                // Add user to the database
                connection conn = new connection();
                String query = "INSERT INTO login (username, password) VALUES ('" + username + "', '" + password + "')";
                conn.statement.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Registration successful!");

                setVisible(false);
                new MyLogin();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == back) {
            setVisible(false);
            new MyLogin();
        }
    }
}

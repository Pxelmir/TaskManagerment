import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Home extends JFrame {
    
    Home(){
        // Setting up the background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("img/Home.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1120, 630, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel img = new JLabel(i3);
        img.setBounds(0, 0, 1120, 630);
        add(img);

        // Setting up the heading
        JLabel heading = new JLabel("Task Manager");
        heading.setBounds(340, 155, 400, 40);
        heading.setFont(new Font("Raleway", Font.BOLD, 25));
        img.add(heading);

        // Adding the "Add Task" button
        JButton add = new JButton("Add Task");
        add.setBounds(335, 270, 150, 40);
        add.setForeground(Color.WHITE);
        add.setBackground(Color.BLACK);
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new addTask().setVisible(true);  // Opens the addTask window
                    setVisible(false);  // Hides the Home window
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });
        img.add(add);

        // Frame settings
        setSize(1120, 630);
        setLocation(250, 100);
        setVisible(true);
        setLayout(null);
    }

    public static void main(String[] args) {
        new Home();  // Launch the Home window
    }
}

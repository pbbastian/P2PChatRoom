import javax.swing.*;
import java.awt.*;

public class ApplicationGUI {
    public ApplicationGUI() {
        JFrame frame = new JFrame("TEST");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5,5,5,5));
        panel.add(new JButton("HEJ"), "wrap");

        frame.setVisible(true);
        frame.pack();
    }
    public static void main(String[] args) {
        ApplicationGUI applicationGUI = new ApplicationGUI();
    }
}

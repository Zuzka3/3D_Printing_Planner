import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Menu menu = new Menu();
            JFrame frame = new JFrame("Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame. setIconImage(new ImageIcon("3d-printer.png").getImage());


            frame.add(menu);
            frame.setVisible(true);

        });

    }
}
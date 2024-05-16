import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Menu extends JPanel implements ActionListener {

    private JButton printer1B, printer2B, printer3B;
    private Map<String, Planner> printers;
    private Planner currentP;
    private boolean playing;
    public static final int Height = 60;
    public static final int HEIGHT = 200;
    // public static final int WIDTH2 = 517, HEIGHT2 = 1039;




    public Menu(){





        setBackground(new Color(54, 54, 54));
        playing = false;
        printers = new HashMap<>();

        setLayout(new GridLayout(3, 1, 0, 30));



        printer1B = createButton("printer 1");
        printer2B = createButton("printer 2");
        printer3B = createButton("printer 3");

        printer1B.setForeground(Color.WHITE);
        printer2B.setForeground(Color.WHITE);
        printer3B.setForeground(Color.WHITE);

        add(printer1B);
        add(printer2B);
        add(printer3B);

        printers.put("printer 1", new Planner("printer 1"));
        printers.put("printer 2", new Planner("printer 2"));
        printers.put("printer 3", new Planner("printer 3"));
        currentP = null;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 50));
        button.setBackground(new Color(0xFF626262, true));
        button.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        button.addActionListener(this);
        return button;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!playing) {
            //playing = true;
            JButton clickedB = (JButton) e.getSource();
            String printerN = clickedB.getText();

            Planner printerPanel = printers.get(printerN);

            if (printerPanel != null) {
                if (currentP != null) {
                    currentP.setVisible(false);
                }

                currentP = printerPanel;
                currentP.setVisible(true);
            } else {
                System.out.println("Tiskárna nenalezena");
            }

        }
    }




}


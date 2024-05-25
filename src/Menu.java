import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Menu extends JPanel implements ActionListener {

    private ArrayList<JButton> buttons = new ArrayList<>();
    private Map<String, Planner> printers;
    private Planner currentP;
    private boolean playing;

    public static final int HEIGHT = 200;


    public Menu(){
        setBackground(new Color(54,54,54));
        playing = false;
        printers = new HashMap<>();

        setLayout(new GridLayout(buttons.size() +2, 1, 0, 30));

        JButton deleteAllB = new JButton("DELETE ALL SAVED FILES");
        deleteAllB.setFont(new Font("Serif", Font.BOLD, 30));
        deleteAllB.setBackground(new Color(0xFFFF0000, true));
        deleteAllB.setForeground(Color.WHITE);
        deleteAllB.addActionListener(e -> {
            for(Map.Entry<String, Planner> entry : printers.entrySet()){
                String key = entry.getKey();
                deleteSave(key + ".txt");
            }
        });

        JButton addPrinterButton = new JButton("ADD PRINTER");
        addPrinterButton.setFont(new Font("Serif", Font.BOLD, 50));
        addPrinterButton.setBackground(new Color(0xFFFFFFFF, true));
        addPrinterButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this,"Enter printer name: ");
            if(name != null && !name.trim().isEmpty()){
                boolean isSame = false;
                for(Map.Entry<String, Planner> entry : printers.entrySet()) {
                    String key = entry.getKey();

                    if (name.equalsIgnoreCase(key)) {
                        JOptionPane.showMessageDialog(this, "Name is taken!");
                        isSame = true;
                        break;
                    }
                }

                if(!isSame){
                    addPrinter(name);
                    this.remove(addPrinterButton);
                    this.add(addPrinterButton);

                    this.remove(deleteAllB);
                    this.add(deleteAllB);
                }
            }
        });
        add(addPrinterButton);
        add(deleteAllB);

        currentP = null;
    }

    public void addPrinter(String name){
        JButton button = createButton(name);

        button.setForeground(Color.WHITE);
        add(button);
        buttons.add(button);

        printers.put(name, new Planner(name));

        setLayout(new GridLayout(buttons.size() + 3, 1,0,30));
        revalidate();
        repaint();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Serif", Font.BOLD, 50));
        button.setBackground(new Color(0xFF626262, true));
        button.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        button.addActionListener(this);
        return button;
    }

    public void deleteSave(String name){
        File file = findFile(name, new File("saved"));
        if(file.delete()){
            System.out.println("Delete file \\\"\" + file.getName() + \"\\\".\".");
        }else{
            System.out.println("Failed to delete file \\\"\" + file.getName() + \"\\\".\".");
        }
    }

    public File findFile(String name, File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    return findFile(name, f);
                } else if (name.equalsIgnoreCase(f.getName())) {
                    return f;
                }
            }
        }
        System.out.println("FILE WAS NOT FOUND" + name);
        return null;
    }

    public File[] getFilesFromDirectory(File directory){
        return directory.listFiles();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!playing) {
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
                System.out.println("Printer not found.");
            }

        }

    }

    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    public void setButtons(ArrayList<JButton> buttons) {
        this.buttons = buttons;
    }

    public Map<String, Planner> getPrinters() {
        return printers;
    }

    public void setPrinters(Map<String, Planner> printers) {
        this.printers = printers;
    }

    public Planner getCurrentP() {
        return currentP;
    }

    public void setCurrentP(Planner currentP) {
        this.currentP = currentP;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}

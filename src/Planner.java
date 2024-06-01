import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Planner extends JFrame implements Serializable {


    private String printerName;

    private List<Item> items = new ArrayList<>();

    private JButton addButton, editButton, showDetails, deleteButton, saveButton;
    private JPanel projectPanel, controlPanel, timePanel, projectStatusPanel;



    private JLabel currentTimeLabel, currentDateLabel, eventStatusLabel;
    private JTable projectTable;
    private Timer timer;

    private long currentDate;

    private Random r = new Random();


    public Planner(String name) {
        this.printerName = name;

        setTitle(this.printerName);
        readFromFile();

        JLabel backgroundLabel = new JLabel();
        projectPanel = new JPanel(new BorderLayout());
        projectPanel.add(backgroundLabel, BorderLayout.CENTER);
        projectPanel.setOpaque(false);


        setSize(800, 600);
        initComponents();
        setIconImage(new ImageIcon(this.getClass().getResource("3d-printer.png")).getImage());


        setLocationRelativeTo(null);
        setVisible(false);
    }

    /**
     * Initializes the components of the planner window.
     */
    private void initComponents() {
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        showDetails = new JButton("Show details");
        deleteButton = new JButton("Delete");
        saveButton = new JButton(("Save"));

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 1));
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(showDetails);
        controlPanel.add(deleteButton);
        controlPanel.add(saveButton);

        addButton.setBackground(Color.lightGray);
        addButton.setFont(new Font("Serif", Font.BOLD,20));
        editButton.setBackground(Color.lightGray);
        editButton.setFont(new Font("Serif", Font.BOLD,20));
        showDetails.setBackground(Color.lightGray);
        showDetails.setFont(new Font("Serif", Font.BOLD,20));
        deleteButton.setBackground(Color.lightGray);
        deleteButton.setFont(new Font("Serif", Font.BOLD,20));
        saveButton.setBackground(Color.lightGray);
        saveButton.setFont(new Font("Serif", Font.BOLD,20));


        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Item");
        tableModel.addColumn("Material");
        tableModel.addColumn("How long");
        tableModel.addColumn("Date and time");
        tableModel.addColumn("Status");
        tableModel.addColumn("Details");


        projectTable = new JTable(tableModel);
        projectTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(projectTable);

        projectPanel = new JPanel();
        projectPanel.setLayout(new BorderLayout());
        projectPanel.add(scrollPane, BorderLayout.CENTER);

        projectPanel.revalidate();

        currentTimeLabel = new JLabel();
        currentDateLabel = new JLabel();
        updateDateTimeLabels();

        timePanel = new JPanel();
        timePanel.setLayout(new GridLayout(2, 1));
        timePanel.add(currentTimeLabel);
        timePanel.add(currentDateLabel);

        eventStatusLabel = new JLabel("Printing is not active. Next project: ");
        eventStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        projectStatusPanel = new JPanel();
        projectStatusPanel.setBackground(Color.GREEN);
        projectStatusPanel.setBorder(BorderFactory.createTitledBorder("STATUS"));
        projectStatusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        projectStatusPanel.add(eventStatusLabel);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(timePanel, BorderLayout.WEST);
        sidePanel.add(projectStatusPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(projectPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(sidePanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        addButton.addActionListener(e -> addProject());
        editButton.addActionListener(e -> editProject());
        deleteButton.addActionListener(e -> deleteProject());
        saveButton.addActionListener(e -> saveToFile());

        showDetails.addActionListener(e -> {
            int selectedRow = projectTable.getSelectedRow();
            if (selectedRow != -1) {
                String details = items.get(selectedRow).getOptionalInfo();
                if (details != null && !details.isEmpty()) {
                    JOptionPane.showMessageDialog(this, details, "Project Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No details provided for this project.", "Project Details", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an project to show details.");
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDateTimeLabels();
                updateProjectStatus();
            }
        }, 0, 500);

    }


    /**
     * Opens a dialog to get user input for a new project and adds it to the list.
     */
    private void addProject() {
        String[] materials = {"PLA - normal", "PETG - waterproof", "PC - tough"};

        String[] printDurations = {"1 min", "5 min", "10 min", "15 min", "30 min", "1 hour", "2 hours", "3 hours", "4h", "5h", "6h", "7h"};



        JComboBox<String> materialComboBox = new JComboBox<>(materials);
        int materialIndex = JOptionPane.showConfirmDialog(this, materialComboBox, "Select Material", JOptionPane.OK_CANCEL_OPTION);
        if (materialIndex == JOptionPane.OK_OPTION) {
            String material = materialComboBox.getSelectedItem().toString();

            String name;
            String aiOption = aiSuggestedItemNames(material);
            int aiIndex = JOptionPane.showConfirmDialog(null, "AI suggested: " + aiOption);
            if(aiIndex == JOptionPane.YES_OPTION) name = aiOption;
            else if(aiIndex == JOptionPane.CANCEL_OPTION) name = null;
            else name = JOptionPane.showInputDialog(this, "Enter project name");

            if(name != null && !name.trim().isEmpty()){


                JComboBox<String> durationComboBox = new JComboBox<>(printDurations);
                int durationIndex = JOptionPane.showConfirmDialog(this, durationComboBox, "Select Printing Duration", JOptionPane.OK_CANCEL_OPTION);
                if (durationIndex == JOptionPane.OK_OPTION) {
                    String duration = durationComboBox.getSelectedItem().toString();

                    String dateStr = JOptionPane.showInputDialog(this, "How soon should the printing start? (HH:mm):");
                    if (isValidDate(dateStr)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        try {
                            Date startDate = new Date(currentDate + dateFormat.parse(dateStr).getTime() + 3600000);

                            int selectedMinutes = Item.convertDurationToMinutes(duration);

                            Date endDate = new Date(startDate.getTime() + selectedMinutes * 60000L);


                            JTextArea optionalInfoArea = new JTextArea(5, 20);
                            JScrollPane scrollPane = new JScrollPane(optionalInfoArea);
                            int optionalInfoOption = JOptionPane.showConfirmDialog(this, scrollPane, "Enter Optional Information", JOptionPane.OK_CANCEL_OPTION);
                            String optionalInfo = "";
                            if (optionalInfoOption == JOptionPane.OK_OPTION) {
                                optionalInfo = optionalInfoArea.getText();
                            }

                            double dfgd = ((double) (currentDate - endDate.getTime()) / (endDate.getTime() - startDate.getTime()));
                            int status = (int) Math.max(0, Math.min(100, (100 + Math.round(dfgd * 100))));


                            Item item = new Item(name, material, startDate, endDate, status, optionalInfo);


                            items.add(item);

                            updateProjectTable();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(this, "Project not added.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Project not added.");
                    }
                }
            }
        }
        updateProjectStatus();
    }

    /**
     * Updates the table model to reflect the current list of projects.
     */
    private void updateProjectTable() {
        DefaultTableModel model = (DefaultTableModel) projectTable.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        for (Item item : items) {
            String formattedDate = dateFormat.format(item.getDateStart());
            String durationStr = calculateDurationString(item.getDateStart(), item.getDateEnd());

            model.addRow(new Object[]{item.getName(), item.getMaterial(), durationStr, formattedDate, item.getStatus() + "%", item.getOptionalInfo()});
        }
    }

    /**
     * Calculates a string representation of the project duration.
     *
     * @param startDate The start date of the project.
     * @param endDate The end date of the project.
     * @return A string representing the project duration (e.g., "1h 30min").
     */
    public String calculateDurationString(Date startDate, Date endDate) {
        long durationMillis = endDate.getTime() - startDate.getTime();
        int minutes = (int) (durationMillis / (60 * 1000));
        int hours = minutes / 60;
        minutes = minutes % 60;

        String durationStr;
        if (hours > 0) {
            durationStr = hours + "h " + minutes + "min";
        } else {
            durationStr = minutes + "min";
        }
        return durationStr;
    }

    /**
     * Checks if the provided date string is in a valid HH:mm format.
     *
     * @param dateStr The date string to validate.
     * @return True if the date string is valid, false otherwise.
     */
    public boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Opens a dialog to get user input and edits the selected project.
     */
    private void editProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an project to edit.");
            return;
        }

        Item selectedItem = items.get(selectedRow);
        String newName = JOptionPane.showInputDialog(this, "Enter new project name:", selectedItem.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            String newDateStr = JOptionPane.showInputDialog(this, "Enter new time (HH:mm):", selectedItem.getDateStr());
            if (isValidDate(newDateStr)) {
                String newDetails = JOptionPane.showInputDialog(this, "Enter new project details:", selectedItem.getDetails());
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date newDate = dateFormat.parse(newDateStr);
                    selectedItem.setName(newName);
                    selectedItem.setDateStart(newDate);
                    selectedItem.setDetails(newDetails);
                    updateProjectTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Project not edited.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid date format. Project not edited.");
            }
        }
    }

    /**
     * Deletes the selected project from the list.
     */
    private void deleteProject() {
        int selectedRow = projectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an project to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this project?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            items.remove(selectedRow);
            updateProjectTable();
        }
    }

    /**
     * Updates the labels displaying the current time and date.
     */
    public void updateDateTimeLabels() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();
        currentTimeLabel.setText("Current Time: " + timeFormat.format(now));
        currentDateLabel.setText("Current Date: " + dateFormat.format(now));

        currentDate = now.getTime();
    }

    /**
     * Updates the label displaying the project status.
     */
    private void updateProjectStatus() {
        Item nextItem = null;
        long currentTimeMillis = System.currentTimeMillis();
        for (Item item : items) {
            if (item.getStatus() < 100){
                double statusPercent = ((double) (currentDate - item.getDateEnd().getTime()) /
                        (item.getDateEnd().getTime() - item.getDateStart().getTime()));
                item.setStatus((int) Math.max(0, Math.min(100, (100 + Math.round(statusPercent * 100)))));
                updateProjectTable();
            }

            long eventStartMillis = item.getDateStart().getTime();
            if (eventStartMillis - currentTimeMillis <= 60000 && eventStartMillis > currentTimeMillis) {
                nextItem = item;
                break;
            }
        }

        if (nextItem != null) {
            eventStatusLabel.setText("Next project: " + nextItem.getName() +
                    " - " + nextItem.getDateStr());
            projectStatusPanel.setBackground(Color.orange);
        } else {
            eventStatusLabel.setText("No upcoming project");
            projectStatusPanel.setBackground(Color.pink);
        }

    }


    /**
     * Saves the list of printing projects to a file.
     *
     * @throws IOException If an I/O error occurs while saving the data.
     */
    public void saveToFile(){
        try {
            String fileName = printerName + ".txt";
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream obj = new ObjectOutputStream(file);

            obj.writeObject(this.items);
            obj.flush();
            obj.close();
            System.out.println("SAVED TO " + fileName + ".");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Reads the list of printing projects from a file.
     *
     * @throws IOException If an I/O error occurs while reading the data.
     * @throws ClassNotFoundException If the class of the objects in the file is not found.
     */
    public void readFromFile(){
        try {
            String fileName =  printerName + ".txt";
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream obj = new ObjectInputStream(file);

            this.items = (ArrayList<Item>) obj.readObject();
            obj.close();
            System.out.println("READ FROM" + fileName + ".");
        }
        catch (IOException e){
            System.out.println("FILE for \"" + printerName.toUpperCase() + "\" WAS NOT FOUND.");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Suggests a project name based on the provided material.
     * This method attempts to read a text file containing suggestions specific to the material type.
     * If the file is not found, it returns null.
     *
     * @param material The material type for the project (PLA, PETG, PC).
     * @return A suggested project name, or null if no suggestion is found.
     */
    public String aiSuggestedItemNames(String material){
        char[] chars = material.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c : chars){
            if(Character.isUpperCase(c)){
                sb.append(c);
            }
            else break;
        }

        String fileName = "ai/" + sb + ".txt";
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));
            ArrayList<String> sugItem = new ArrayList<>();
            int lines= 0;

            br.readLine();
            String s = "";
            while((s = br.readLine()) != null){
                sugItem.add(s);
                lines++;
            }

            return sugItem.get(r.nextInt(lines));
        }
        catch (Exception ignored){
        }
        return null;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
    public JLabel getCurrentTimeLabel() {
        return currentTimeLabel;
    }

    public void setCurrentTimeLabel(JLabel currentTimeLabel) {
        this.currentTimeLabel = currentTimeLabel;
    }
}

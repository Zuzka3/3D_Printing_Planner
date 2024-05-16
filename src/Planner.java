import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Planner extends JFrame {
    private String printerName;



    private List<Item> items = new ArrayList<>();
    private JButton addButton, editButton, showDetails, deleteButton;
    private JPanel eventPanel, controlPanel, timePanel, eventStatusPanel;
    private JLabel currentTimeLabel, currentDateLabel, eventStatusLabel;
    private JTable eventTable;
    private Timer timer;

    private long currentDate;

    public Planner(String name) {
        this.printerName = name;

        setTitle(this.printerName);

        JLabel backgroundLabel = new JLabel();
        eventPanel = new JPanel(new BorderLayout());
        eventPanel.add(backgroundLabel, BorderLayout.CENTER);
        eventPanel.setOpaque(false);


        setSize(800, 600);
        initComponents();
        setIconImage(new ImageIcon("3d-printer.png").getImage());

        setLocationRelativeTo(null);
        setVisible(false);
    }

    private void initComponents() {
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        showDetails = new JButton("Show details");
        deleteButton = new JButton("Delete");

        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 1));
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(showDetails);
        controlPanel.add(deleteButton);

        addButton.setBackground(Color.lightGray);
        addButton.setFont(new Font("Serif", Font.BOLD,20));
        editButton.setBackground(Color.lightGray);
        editButton.setFont(new Font("Serif", Font.BOLD,20));
        showDetails.setBackground(Color.lightGray);
        showDetails.setFont(new Font("Serif", Font.BOLD,20));
        deleteButton.setBackground(Color.lightGray);
        deleteButton.setFont(new Font("Serif", Font.BOLD,20));


        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Item");
        tableModel.addColumn("Material");
        tableModel.addColumn("How long");
        tableModel.addColumn("Date and time");
        tableModel.addColumn("Status");
        tableModel.addColumn("Details");


        eventTable = new JTable(tableModel);
        eventTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(eventTable);

        eventPanel = new JPanel();
        eventPanel.setLayout(new BorderLayout());
        eventPanel.add(scrollPane, BorderLayout.CENTER);

        eventPanel.revalidate();

        currentTimeLabel = new JLabel();
        currentDateLabel = new JLabel();
        updateDateTimeLabels();

        timePanel = new JPanel();
        timePanel.setLayout(new GridLayout(2, 1));
        timePanel.add(currentTimeLabel);
        timePanel.add(currentDateLabel);

        eventStatusLabel = new JLabel("Není aktivní tisk. Další projekt: ");
        eventStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        eventStatusPanel = new JPanel();
        eventStatusPanel.setBackground(Color.GREEN);
        eventStatusPanel.setBorder(BorderFactory.createTitledBorder("STATUS"));
        eventStatusPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        eventStatusPanel.add(eventStatusLabel);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(timePanel, BorderLayout.WEST);
        sidePanel.add(eventStatusPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(eventPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(sidePanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        addButton.addActionListener(e -> addEvent());
        editButton.addActionListener(e -> editEvent());
        deleteButton.addActionListener(e -> deleteEvent());

        //sonic tlacitko - nevim jestli facha
        showDetails.addActionListener(e -> {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                String details = items.get(selectedRow).getOptionalInfo();
                if (details != null && !details.isEmpty()) {
                    JOptionPane.showMessageDialog(this, details, "Event Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No details provided for this event.", "Event Details", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an event to show details.");
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDateTimeLabels();
                updateEventStatus();
            }
        }, 0, 500);


    }



    private void addEvent() {
        String[] materials = {"PLA", "PETG", "PC"};

        String[] printDurations = {"1 min", "5 min", "10 min", "15 min", "30 min", "1 hour", "2 hours", "3 hours", "4h", "5h", "6h", "7h"};


        String name = JOptionPane.showInputDialog(this, "Enter event name:");
        if (name != null && !name.trim().isEmpty()) {

            //boxik pla, petggg
            JComboBox<String> materialComboBox = new JComboBox<>(materials);
            int materialIndex = JOptionPane.showConfirmDialog(this, materialComboBox, "Select Material", JOptionPane.OK_CANCEL_OPTION);
            if (materialIndex == JOptionPane.OK_OPTION) {
                String material = materialComboBox.getSelectedItem().toString();

                //jak dlpuho to bude tisknout - je tam boxik na vyberos
                JComboBox<String> durationComboBox = new JComboBox<>(printDurations);
                int durationIndex = JOptionPane.showConfirmDialog(this, durationComboBox, "Select Printing Duration", JOptionPane.OK_CANCEL_OPTION);
                if (durationIndex == JOptionPane.OK_OPTION) {
                    String duration = durationComboBox.getSelectedItem().toString();

                    String dateStr = JOptionPane.showInputDialog(this, "How soon should the printing start? (HH:mm):");
                    if (isValidDate(dateStr)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        try {
                            Date startDate = new Date(currentDate + dateFormat.parse(dateStr).getTime() + 3600000);


                            int selectedMinutes = convertDurationToMinutes(duration);


                            Date endDate = new Date(startDate.getTime() + selectedMinutes * 60000L);

                            //k tomu pridani tech super informaci navic
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

                            updateEventTable();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(this, "Event not added.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Event not added.");
                    }
                }
            }
        }
        updateEventStatus();
    }

    private int convertDurationToMinutes(String duration) {
        int minutes = 0;
        switch (duration) {
            case "1 min":
                minutes = 1;
                break;
            case "5 min":
                minutes = 5;
                break;
            case "10 min":
                minutes = 10;
                break;
            case "15 min":
                minutes = 15;
                break;
            case "30 min":
                minutes = 30;
                break;
            case "1 hour":
                minutes = 60;
                break;
            case "2 hours":
                minutes = 120;
                break;
            case "3 hours":
                minutes = 180;
                break;
            case "4 hours":
                minutes = 240;
                break;
            case "5 hours":
                minutes = 300;
                break;
            case "6 hours":
                minutes = 360;
                break;
            case "7 hours":
                minutes = 420;
                break;
            default:
                break;
        }
        return minutes;
    }



    private void updateEventTable() {
        DefaultTableModel model = (DefaultTableModel) eventTable.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        for (Item item : items) {
            String formattedDate = dateFormat.format(item.getDateStart());
            String durationStr = calculateDurationString(item.getDateStart(), item.getDateEnd());

            model.addRow(new Object[]{item.getName(), item.getMaterial(), durationStr, formattedDate, item.getStatus() + "%", item.getOptionalInfo()});
        }
    }

    private String calculateDurationString(Date startDate, Date endDate) {
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

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void editEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to edit.");
            return;
        }

        Item selectedItem = items.get(selectedRow);
        String newName = JOptionPane.showInputDialog(this, "Enter new event name:", selectedItem.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            String newDateStr = JOptionPane.showInputDialog(this, "Enter new time (HH:mm):", selectedItem.getDateStr());
            if (isValidDate(newDateStr)) {
                String newDetails = JOptionPane.showInputDialog(this, "Enter new event details:", selectedItem.getDetails());
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date newDate = dateFormat.parse(newDateStr);
                    selectedItem.setName(newName);
                    selectedItem.setDateStart(newDate);
                    selectedItem.setDetails(newDetails);
                    updateEventTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Event not edited.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid date format. Event not edited.");
            }
        }
    }

    private void deleteEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            items.remove(selectedRow);
            updateEventTable();
        }
    }

    private void updateDateTimeLabels() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date now = new Date();
        currentTimeLabel.setText("Current Time: " + timeFormat.format(now));
        currentDateLabel.setText("Current Date: " + dateFormat.format(now));

        currentDate = now.getTime();
    }

    private void updateEventStatus() {
        Item nextItem = null;
        long currentTimeMillis = System.currentTimeMillis();
        for (Item item : items) {
            if (item.getStatus() < 100){
                double dfgd = ((double) (currentDate - item.getDateEnd().getTime()) /
                        (item.getDateEnd().getTime() - item.getDateStart().getTime()));
                item.setStatus((int) Math.max(0, Math.min(100, (100 + Math.round(dfgd * 100)))));
                updateEventTable();
            }

            long eventStartMillis = item.getDateStart().getTime();
            if (eventStartMillis - currentTimeMillis <= 60000 && eventStartMillis > currentTimeMillis) {
                nextItem = item;
                break;
            }
        }

        if (nextItem != null) {
            eventStatusLabel.setText("Next Event: " + nextItem.getName() +
                    " - " + nextItem.getDateStr());
            eventStatusPanel.setBackground(Color.orange);
        } else {
            eventStatusLabel.setText("No upcoming event");
            eventStatusPanel.setBackground(Color.pink);
        }

    }


}






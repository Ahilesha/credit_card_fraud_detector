import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.*;
import java.time.format.*;

// Explicitly import java.util.List and ArrayList to avoid ambiguity
import java.util.List;
import java.util.ArrayList;

class Transaction {
    String cardNumber;
    String location;
    double amount;
    String formattedTimestamp;
    boolean isFraud;

    public Transaction(String cardNumber, String location, double amount, String formattedTimestamp, boolean isFraud) {
        this.cardNumber = cardNumber;
        this.location = location;
        this.amount = amount;
        this.formattedTimestamp = formattedTimestamp;
        this.isFraud = isFraud;
    }
}

public class FraudDetector {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Transaction Table");
        JButton loadButton = new JButton("Load Transactions");
        JButton selectFileButton = new JButton("Select CSV File");
        JButton exportButton = new JButton("Export CSV");
        JTextField searchField = new JTextField(15);
        JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(new String[]{"Card Number", "Location", "Amount ($)", "Timestamp", "Fraud"}, 0);
        table.setModel(model);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        JFileChooser fileChooser = new JFileChooser();
        final String[] selectedFile = {"transactions.csv"};

        selectFileButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile().getAbsolutePath();
            }
        });

        loadButton.addActionListener(e -> {
            List<Transaction> transactions = readTransactions(selectedFile[0]);
            model.setRowCount(0);
            for (Transaction t : transactions) {
                model.addRow(new Object[]{t.cardNumber, t.location, t.amount, t.formattedTimestamp, t.isFraud ? "YES" : "NO"});
                if (t.isFraud) {
                    JOptionPane.showMessageDialog(frame, "Fraud Alert! Suspicious transaction detected for card: " + t.cardNumber, "Fraud Alert", JOptionPane.WARNING_MESSAGE);
                }
            }
            saveFraudulentTransactions(transactions);
        });

        searchField.addActionListener(e -> {
            String searchText = searchField.getText();
            sorter.setRowFilter(RowFilter.regexFilter(searchText));
        });
        
        exportButton.addActionListener(e -> exportTransactions(model));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                boolean isFraud = "YES".equals(table.getValueAt(row, 4));
                if (isFraud) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(selectFileButton);
        topPanel.add(loadButton);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(exportButton);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setSize(700, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static List<Transaction> readTransactions(String fileName) {
        List<Transaction> transactions = new ArrayList<>();
        Map<String, List<Double>> userSpending = new HashMap<>();
        Map<String, List<Long>> userTimestamps = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 4) {
                    System.err.println("Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String cardNumber = data[0].trim();
                    String location = data[1].trim();
                    double amount = Double.parseDouble(data[2].trim());
                    long timestamp = Long.parseLong(data[3].trim().replaceAll("[^0-9]", ""));
                    String formattedTimestamp = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    boolean isFraud = amount > 5000;
                    userSpending.putIfAbsent(cardNumber, new ArrayList<>());
                    List<Double> amounts = userSpending.get(cardNumber);
                    if (!amounts.isEmpty()) {
                        double avgSpending = amounts.stream().mapToDouble(a -> a).average().orElse(0);
                        if (amount > 5 * avgSpending) {
                            isFraud = true;
                        }
                    }
                    amounts.add(amount);

                    userTimestamps.putIfAbsent(cardNumber, new ArrayList<>());
                    List<Long> timestamps = userTimestamps.get(cardNumber);
                    timestamps.add(timestamp);
                    timestamps.removeIf(t -> timestamp < timestamps.get(timestamps.size() - 1) - 60000);
                    if (timestamps.size() >= 5) {
                        isFraud = true;
                    }

                    transactions.add(new Transaction(cardNumber, location, amount, formattedTimestamp, isFraud));
                } catch (NumberFormatException ex) {
                    System.err.println("Skipping malformed transaction entry: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return transactions;
    }

    private static void saveFraudulentTransactions(List<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("fraudulent_transactions.csv"))) {
            writer.println("Card Number,Location,Amount ($),Timestamp,Fraud");
            for (Transaction t : transactions) {
                if (t.isFraud) {
                    writer.println(t.cardNumber + "," + t.location + "," + t.amount + "," + t.formattedTimestamp + ",YES");
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving fraudulent transactions: " + e.getMessage());
        }
    }

    private static void exportTransactions(DefaultTableModel model) {
        // Placeholder for export functionality
    }
}

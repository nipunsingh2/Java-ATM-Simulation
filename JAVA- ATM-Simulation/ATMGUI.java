import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

class Transaction {
    String dateTime;
    String credit;
    String debit;

    public Transaction(String credit, String debit) {
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.credit = credit;
        this.debit = debit;
    }

    public Object[] toRow() {
        return new Object[]{dateTime, credit, debit};
    }
}

class BankAccount {
    protected double balance;
    protected String accountName;
    protected List<Transaction> transactions;

    public BankAccount(double initialBalance, String name) {
        this.balance = initialBalance;
        this.accountName = name;
        this.transactions = new ArrayList<>();
    }

    public String checkBalance() {
        return "Current Balance: ₹" + balance;
    }

    public String deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction("₹" + amount, ""));
            return "₹" + amount + " deposited.";
        } else {
            return "Invalid deposit amount.";
        }
    }

    public String withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactions.add(new Transaction("", "₹" + amount));
            return "₹" + amount + " withdrawn.";
        } else {
            return "Invalid or insufficient funds.";
        }
    }

    public List<Transaction> getMiniStatement() {
        return transactions;
    }
}

class ATM extends BankAccount {
    private final String pin;

    public ATM(double initialBalance, String name, String pin) {
        super(initialBalance, name);
        this.pin = pin;
    }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }
}

public class ATMGUI {
    private JFrame frame;
    private ATM atm;

    public ATMGUI() {
        atm = new ATM(2000, "Nipun", "1203");
        showLoginScreen();
    }

    private void showLoginScreen() {
        frame = new JFrame("ATM Login");
        frame.setSize(350, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel label = new JLabel("Enter 4-digit PIN:");
        JPasswordField pinField = new JPasswordField();

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String enteredPin = new String(pinField.getPassword());
            if (atm.verifyPin(enteredPin)) {
                showMainMenu();
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect PIN");
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.add(label);
        inputPanel.add(pinField);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(loginButton, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setVisible(true);
    }

    private void showMainMenu() {
        frame.getContentPane().removeAll();
        frame.setTitle("ATM - Welcome, " + atm.accountName);
        frame.setSize(400, 350);

        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton balanceBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton miniStmtBtn = new JButton("Mini Statement");
        JButton exitBtn = new JButton("Exit");

        balanceBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, atm.checkBalance()));

        depositBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter amount to deposit:");
            try {
                double amount = Double.parseDouble(input);
                JOptionPane.showMessageDialog(frame, atm.deposit(amount));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        });

        withdrawBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Enter amount to withdraw:");
            try {
                double amount = Double.parseDouble(input);
                JOptionPane.showMessageDialog(frame, atm.withdraw(amount));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input.");
            }
        });

        miniStmtBtn.addActionListener(e -> showMiniStatementTable());

        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Thank you for using the ATM!");
            System.exit(0);
        });

        panel.add(balanceBtn);
        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(miniStmtBtn);
        panel.add(exitBtn);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void showMiniStatementTable() {
        JFrame stmtFrame = new JFrame("Mini Statement");
        stmtFrame.setSize(500, 300);
        stmtFrame.setLocationRelativeTo(null);

        String[] columns = {"Date & Time", "Credit", "Debit"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Transaction t : atm.getMiniStatement()) {
            model.addRow(t.toRow());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        stmtFrame.add(scrollPane);

        stmtFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATMGUI::new);
    }
}

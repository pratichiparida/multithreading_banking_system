import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

class BankAccount {
        private final String accountNumber;
        private double balance;

        public BankAccount(String accountNumber, double initialBalance) {
                this.accountNumber = accountNumber;
                this.balance = initialBalance;
        }

        public synchronized void deposit(double amount) {
                balance += amount;
        }

        public synchronized void withdraw(double amount) {
                if (balance >= amount) {
                        balance -= amount;
                } else {
                        System.out.println("Insufficient funds!");
                }
        }

        public synchronized double getBalance() {
                return balance;
        }

        public String getAccountNumber() {
                return accountNumber;
        }
}

class TransactionLog {
        public static synchronized void logTransaction(String message) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = dateFormat.format(new Date());
                System.out.println(timestamp + ": " + message);
        }
}

class BankGUI extends JFrame {
        private JTextField accountNumberField;
        private JTextField amountField;
        private JTextArea logArea;
        private BankAccount selectedAccount;

        public BankGUI() {
                setTitle("Multi-Threaded Banking System");
                setSize(400, 300);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                accountNumberField = new JTextField(20);
                amountField = new JTextField(10);
                logArea = new JTextArea(10, 30);
                logArea.setEditable(false);
                JButton depositButton = new JButton("Deposit");
                JButton withdrawButton = new JButton("Withdraw");
                JButton balanceButton = new JButton("Check Balance");
                depositButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                performTransaction("Deposit", Double.parseDouble(amountField.getText()));
                        }
                });
                withdrawButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                performTransaction("Withdraw", Double.parseDouble(amountField.getText()));
                        }
                });
                balanceButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (selectedAccount != null) {
                                        logTransaction("Balance: $" + selectedAccount.getBalance());
                                } else {
                                        logTransaction("No account selected.");
                                }
                        }
                });
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(4, 2));
                panel.add(new JLabel("Account Number:"));
                panel.add(accountNumberField);
                panel.add(new JLabel("Amount:"));
                panel.add(amountField);
                panel.add(depositButton);
                panel.add(withdrawButton);
                panel.add(balanceButton);
                add(panel, BorderLayout.NORTH);
                add(new JScrollPane(logArea), BorderLayout.CENTER);
                setVisible(true);
        }

        void performTransaction(String transactionType, double amount) {
                String enteredAccountNumber = accountNumberField.getText();
                if (!isNumeric(enteredAccountNumber)) {
                        logTransaction("Please enter a valid numeric account number.");
                        return;
                }
                synchronized (this) {
                        if (selectedAccount == null || !selectedAccount.getAccountNumber().equals(enteredAccountNumber)) {
                                selectedAccount = new BankAccount(enteredAccountNumber, 0);
                                logTransaction("New account selected: " + enteredAccountNumber);
                        }
                        if (transactionType.equals("Deposit")) {
                                selectedAccount.deposit(amount);
                                logTransaction("Deposit of $" + amount + " into account " +

                                                selectedAccount.getAccountNumber());
                        } else if (transactionType.equals("Withdraw")) {
                                selectedAccount.withdraw(amount);
                                logTransaction("Withdrawal of $" + amount + " from account " + selectedAccount.getAccountNumber());
                        }
                }
        }

        void logTransaction(String message) {
                TransactionLog.logTransaction(message);
                logArea.append(message + "\n");
        }

        private boolean isNumeric(String str) {
                return str.matches("\\d+");
        }
}

public class BankingSystem {
        public static void main(String[] args) {
                SwingUtilities.invokeLater(() -> {
                        new BankGUI();
                });
        }
}
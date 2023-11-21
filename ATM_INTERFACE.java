import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class User {
    private int accountNumber;
    private String fullName;
    private String username;
    private int pin;
    private double balance;

    public User(int accountNumber, String fullName, String username, int pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.fullName = fullName;
        this.username = username;
        this.pin = pin;
        this.balance = initialBalance;  // set initial balance as available balance
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public int getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
interface AtmOperationInterf {
    void viewBalance();
    void withdrawAmount(double withdrawAmount);
    void depositAmount(double depositAmount);
    void viewMiniStatement();
    void transferMoney(int targetAccountNumber, double amount);
    void setUser(User user);
}


class AtmOperationImpl implements AtmOperationInterf {
    private ATM atm;
    private User user;
    private Map<Double, String> ministmt = new HashMap<>();

     public AtmOperationImpl(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void viewBalance() {
        if (user != null) {
            System.out.println("\nAvailable Balance is: " + user.getBalance());
        } else {
            System.out.println("User not set. Please log in.");
        }
    }

    @Override
   public void withdrawAmount(double withdrawAmount) {
    System.out.println("\nRequested Withdrawal Amount: " + withdrawAmount);
    System.out.println("Available Balance: " + user.getBalance());

    if (withdrawAmount % 500 == 0) {
        if (withdrawAmount <= user.getBalance()) {
            ministmt.put(-withdrawAmount, "Amount Withdrawn");
            System.out.println("Collect the Cash: " + withdrawAmount);
            user.setBalance(user.getBalance() - withdrawAmount);
        } else {
            System.out.println("Insufficient Balance!");
            return; // Added to exit the method if there's insufficient balance
        }
    } else {
        System.out.println("Please enter the amount in multiples of 500");
        return; // Added to exit the method if the amount is not in multiples of 500
    }

    viewBalance();
}

    @Override
    public void depositAmount(double depositAmount) {
        if (user != null) {
            System.out.println(depositAmount + " Deposited Successfully!!");
            ministmt.put(depositAmount, "Amount Deposited");
            atm.setBalance(atm.getBalance() + depositAmount);
            user.setBalance(user.getBalance() + depositAmount);
            viewBalance();
        } else {
            System.out.println("User not set. Please log in.");
        }
    }

    @Override
    public void viewMiniStatement() {
         System.out.println("Mini Statement:");
        for (Map.Entry<Double, String> entry : ministmt.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        //for (String entry : miniStatement) {
          //  System.out.println(entry);
        //}
    }

    
    @Override
    public void transferMoney(int targetAccountNumber, double amount) 
    {
        if (user != null) 
        {
            User targetUser = atm.getUser(targetAccountNumber);

            if (targetUser != null) 
            {
                if (amount <= user.getBalance()) 
                {
                    ministmt.put(-amount, "Amount Transferred to " + targetUser.getUsername());
                    user.setBalance(user.getBalance() - amount);

                    ministmt.put(amount, "Amount Received from " + user.getUsername() + " (Account: " + user.getAccountNumber() + ")");
                    targetUser.setBalance(targetUser.getBalance() + amount);

                    System.out.println("Amount transferred successfully!");
                    viewBalance();
                } else 
                {
                    System.out.println("Insufficient Balance!");
                }
            } else 
            {
                System.out.println("Target account not found!");
            }
        } 
        else 
        {
            System.out.println("User not set. Please log in.");
        }
    }


    @Override
    public void setUser(User user) {
        this.user = user;
    }
}

class ATM {
    private double balance;
    private UserManager userManager;  // Add a reference to UserManager

    public ATM(UserManager userManager) {
        this.userManager = userManager;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Add this method to retrieve a user by account number
    public User getUser(int accountNumber) {
        return userManager.getUser(accountNumber);
    }
}


class UserManager {
    private Map<Integer, User> users = new HashMap<>();
    private int nextAccountNumber = 1001; // Starting account number

    public User registerUser(String fullName, String username, int pin, double initialBalance) {
        int accountNumber = nextAccountNumber++;
        User user = new User(accountNumber, fullName, username, pin, initialBalance);
        user.setBalance(initialBalance); 
        users.put(accountNumber, user);
        return user;
    }

    public User getUser(int accountNumber) {
        return users.get(accountNumber);
    }
    
    public User getUserByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    public User getUserByPin(int pin) {
        for (User user : users.values()) {
            if (user.getPin() == pin) {
                return user;
            }
        }
        return null;
    }
}

public class ATM_INTERFACE {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        ATM atm = new ATM(userManager);  // Pass userManager to ATM
        AtmOperationInterf op = new AtmOperationImpl(atm);
        Scanner in = new Scanner(System.in);

        User user = null; // Declare the user variable

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("\nEnter Choice: ");
            int option = in.nextInt();

            if (option == 1) {
                // Create Account
                System.out.print("\nEnter Full Name: ");
                String fullName = in.next();

                System.out.print("Enter Username: ");
                String username = in.next();

                System.out.print("Enter PIN: ");
                int pin = in.nextInt();

                System.out.print("Enter Initial Balance: ");
                double initialBalance = in.nextDouble();

                user = userManager.registerUser(fullName, username, pin, initialBalance);
                System.out.println("\nAccount created successfully! Your account number is: " + user.getAccountNumber());
            } else if (option == 2) {
                 
                System.out.print("\nEnter Account Number: ");
                int accountNumberInput = in.nextInt();

                System.out.print("Enter PIN: ");
                int pinInput = in.nextInt();

                 user = userManager.getUser(accountNumberInput);

                if (user != null && user.getPin() == pinInput) {
                    System.out.println("\nWelcome to ATM Interface, " + user.getUsername() + "!");
                    op.setUser(user);
                    performOperations(op, in, user);
                } else {
                    System.out.println("\nIncorrect Username or PIN");
                }
            } else if (option == 3) {
                // Exit
                System.exit(0);
            } else {
                System.out.println("\n\nInvalid option. Please try again.");
            }
        }
    }

    private static void performOperations(AtmOperationInterf op, Scanner in, User user) {
        op.setUser(user);

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View Available Balance");
            System.out.println("2. Withdraw Amount");
            System.out.println("3. Deposit Amount");
            System.out.println("4. View Ministatement");
            System.out.println("5. Transfer Money");
            System.out.println("6. Exit");
            System.out.print("\nEnter Choice: ");
            int ch = in.nextInt();

            switch (ch) {
                case 1:
                    op.viewBalance();
                    break;
                case 2:
                    System.out.println("Enter amount to withdraw: ");
                    double withdrawAmount = in.nextDouble();
                    op.withdrawAmount(withdrawAmount);
                    break;
                case 3:
                    System.out.println("Enter Amount to Deposit: ");
                    double depositAmount = in.nextDouble();
                    op.depositAmount(depositAmount);
                    break;
                case 4:
                    op.viewMiniStatement();
                    break;
                
                case 5: // Add this case for transferring money
                    System.out.println("Enter target account number: ");
                    int targetAccountNumber = in.nextInt();
                    System.out.println("Enter amount to transfer: ");
                    double transferAmount = in.nextDouble();
                    op.transferMoney(targetAccountNumber, transferAmount);
                    break;
                case 6:
                    System.out.println("\n\nCollect your ATM Card. Thank you for using ATM Machine!!");
                    System.exit(0);
                default:
                    System.out.println("\nPlease enter a valid choice.");
            }
        }
    }
}

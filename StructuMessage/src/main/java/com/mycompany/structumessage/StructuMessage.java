package com.mycompany.structumessage;

import javax.swing.*;

public class StructuMessage {

    static Message[] sentMessages = new Message[10];
    static Message[] disregardedMessages = new Message[10];
    static Message[] storedMessages = new Message[10];
    static String[] messageHashes = new String[10];
    static String[] messageIDs = new String[10];
    static int sentCount = 0;
    static int discardCount = 0;
    static int storeCount = 0;

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat Registration");

        String firstName = JOptionPane.showInputDialog("Enter your first name:");
        String lastName = JOptionPane.showInputDialog("Enter your last name:");

        String username = getValidUsername();
        String password = getValidPassword();
        String cellphone = getValidCellphone();

        QuickChatUser user = new QuickChatUser(username, password, cellphone, firstName, lastName);
        JOptionPane.showMessageDialog(null, user.register());

        JOptionPane.showMessageDialog(null, "Please log in");
        String inputUsername = JOptionPane.showInputDialog("Username:");
        String inputPassword = JOptionPane.showInputDialog("Password:");

        if (user.login(inputUsername, inputPassword)) {
            JOptionPane.showMessageDialog(null, user.loginStatusMessage(true));
            runApp();
        } else {
            JOptionPane.showMessageDialog(null, user.loginStatusMessage(false));
        }
    }

    public static void runApp() {
        boolean running = true;
        while (running) {
            String option = JOptionPane.showInputDialog("""
                Welcome to QuickChat!
                
                Choose an option:
                1) Send Message
                2) Show Recently Sent Messages
                3) Disregard Message/Quit
                4) Reports
                """);

            if (option.equals("1")) {
                int total = Integer.parseInt(JOptionPane.showInputDialog("How many messages would you like to send?"));
                for (int i = 0; i < total; i++) {
                    boolean success = sendMessage(i);
                    if (!success) {
                        JOptionPane.showMessageDialog(null, "Message not sent. Skipping...");
                    }
                }
                JOptionPane.showMessageDialog(null, "Total messages processed: " + sentCount);

            } else if (option.equals("2")) {
                if (sentCount == 0) {
                    JOptionPane.showMessageDialog(null, "No messages sent yet.");
                } else {
                    displayReport();
                }
            } else if (option.equals("3")) {
                running = false;
            } else if (option.equals("4")) {
                String reportOption = JOptionPane.showInputDialog("""
                    Reports Menu:
                    1) Show Sender & Recipients
                    2) Longest Message
                    3) Search by Message ID
                    4) Search by Recipient
                    5) Delete by Message Hash
                    6) Full Sent Report
                    """);

                switch (reportOption) {
                    case "1" -> showSenderAndRecipients();
                    case "2" -> showLongestMessage();
                    case "3" -> searchByMessageID(JOptionPane.showInputDialog("Enter Message ID:"));
                    case "4" -> searchByRecipient(JOptionPane.showInputDialog("Enter Recipient:"));
                    case "5" -> deleteByMessageHash(JOptionPane.showInputDialog("Enter Message Hash:"));
                    case "6" -> displayReport();
                    default -> JOptionPane.showMessageDialog(null, "Invalid report option.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }
    }

    public static boolean sendMessage(int msgNum) {
        String recipient = JOptionPane.showInputDialog("Enter recipient phone number (e.g., +27834567890):");
        if (!Message.checkRecipientCell(recipient)) {
            JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted.");
            return false;
        }

        String content = JOptionPane.showInputDialog("Enter your message (max 250 characters):");
        String feedback = Message.validateMessageLength(content);
        JOptionPane.showMessageDialog(null, feedback);

        if (!feedback.equals("Message ready to send.")) {
            return false;
        }

        Message msg = new Message(recipient, content, msgNum);
        JOptionPane.showMessageDialog(null, "Message #" + (msgNum + 1) +
                "\nMessage Hash: " + msg.getMessageHash());

        String[] options = {"Send", "Discard", "Store"};
        int action = JOptionPane.showOptionDialog(null, "What would you like to do with this message?",
                "Message Options", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (action == 0) {
            if (sentCount < sentMessages.length) {
                sentMessages[sentCount] = msg;
                messageHashes[sentCount] = msg.getMessageHash();
                messageIDs[sentCount] = msg.getMessageID();
                sentCount++;
                JOptionPane.showMessageDialog(null, msg.printDetails());
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Message storage full.");
                return false;
            }
        } else if (action == 1) {
            disregardedMessages[discardCount++] = msg;
            JOptionPane.showMessageDialog(null, "Message discarded.");
            return true;
        } else if (action == 2) {
            msg.storeMessageToJson();
            storedMessages[storeCount++] = msg;
            JOptionPane.showMessageDialog(null, "Message successfully stored.");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Invalid option.");
            return false;
        }
    }

    public static void showSenderAndRecipients() {
        StringBuilder result = new StringBuilder("\uD83D\uDCEC Sent Messages:\n");
        for (int i = 0; i < sentCount; i++) {
            result.append("Message ID: ").append(sentMessages[i].getMessageID())
                  .append("\nRecipient: ").append(sentMessages[i].getRecipient())
                  .append("\nMessage: ").append(sentMessages[i].getMessage())
                  .append("\n\n");
        }
        JOptionPane.showMessageDialog(null, result.toString());
    }

    public static void showLongestMessage() {
        String longest = "";
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].getMessage().length() > longest.length()) {
                longest = sentMessages[i].getMessage();
            }
        }
        JOptionPane.showMessageDialog(null, "Longest message:\n" + longest);
    }

    public static void searchByMessageID(String id) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].getMessageID().equals(id)) {
                JOptionPane.showMessageDialog(null,
                    "Recipient: " + sentMessages[i].getRecipient() +
                    "\nMessage: " + sentMessages[i].getMessage());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message ID not found.");
    }

    public static void searchByRecipient(String recipient) {
        StringBuilder found = new StringBuilder("Messages sent/stored to " + recipient + ":\n");

        for (Message m : sentMessages) {
            if (m != null && m.getRecipient().equals(recipient)) {
                found.append("- ").append(m.getMessage()).append("\n");
            }
        }
        for (Message m : storedMessages) {
            if (m != null && m.getRecipient().equals(recipient)) {
                found.append("- ").append(m.getMessage()).append(" (stored)\n");
            }
        }

        JOptionPane.showMessageDialog(null, found.length() > 0 ? found.toString() : "No messages found.");
    }

    public static void deleteByMessageHash(String hash) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].getMessageHash().equals(hash)) {
                JOptionPane.showMessageDialog(null,
                    "Message \"" + sentMessages[i].getMessage() + "\" successfully deleted.");
                sentMessages[i] = null;
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Hash not found.");
    }

    public static void displayReport() {
        StringBuilder report = new StringBuilder("\uD83D\uDCC4 Full Sent Messages Report:\n\n");
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null) {
                report.append("Message Hash: ").append(sentMessages[i].getMessageHash()).append("\n");
                report.append("Recipient: ").append(sentMessages[i].getRecipient()).append("\n");
                report.append("Message: ").append(sentMessages[i].getMessage()).append("\n\n");
            }
        }
        JOptionPane.showMessageDialog(null, report.toString());
    }

    public static String getValidUsername() {
        String username;
        while (true) {
            username = JOptionPane.showInputDialog("Enter username (must contain _ and be ≤ 5 chars):");
            if (QuickChatUser.checkUserName(username)) {
                return username;
            }
            JOptionPane.showMessageDialog(null, "Invalid username format.");
        }
    }

    public static String getValidPassword() {
        String password;
        while (true) {
            password = JOptionPane.showInputDialog("Enter password (8+ chars, 1 capital, 1 number, 1 special):");
            if (QuickChatUser.checkPasswordComplexity(password)) {
                return password;
            }
            JOptionPane.showMessageDialog(null, "Invalid password format.");
        }
    }

    public static String getValidCellphone() {
        String phone;
        while (true) {
            phone = JOptionPane.showInputDialog("Enter cellphone number (+27XXXXXXXXX):");
            if (QuickChatUser.checkCellPhoneNumber(phone)) {
                return phone;
            }
            JOptionPane.showMessageDialog(null, "Invalid cellphone number.");
        }
    }
}

package com.mycompany.structumessage;

import javax.swing.*;
import java.util.Arrays;

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

        boolean loggedIn = false;
        while (!loggedIn) {
            String inputUsername = JOptionPane.showInputDialog("Username:");
            String inputPassword = JOptionPane.showInputDialog("Password:");
            if (user.login(inputUsername, inputPassword)) {
                JOptionPane.showMessageDialog(null, user.loginStatusMessage(true));
                loadStoredMessagesFromJson();
                runApp();
                loggedIn = true;
            } else {
                JOptionPane.showMessageDialog(null, user.loginStatusMessage(false));
            }
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

            switch (option) {
                case "1" -> {
                    int total = Integer.parseInt(JOptionPane.showInputDialog("How many messages would you like to send?"));
                    for (int i = 0; i < total; i++) {
                        boolean success = sendMessage(i);
                        if (!success) {
                            JOptionPane.showMessageDialog(null, "Message not sent. Skipping...");
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Total messages processed: " + sentCount);
                }
                case "2" -> {
                    if (sentCount == 0) {
                        JOptionPane.showMessageDialog(null, "No messages sent yet.");
                    } else {
                        displayReport();
                    }
                }
                case "3" -> running = false;
                case "4" -> showReports();
                default -> JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }
    }

    public static void showReports() {
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
            if (storeCount < storedMessages.length) {
                storedMessages[storeCount++] = msg;
            }
            JOptionPane.showMessageDialog(null, "Message successfully stored.");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Invalid option.");
            return false;
        }
    }

    public static void loadStoredMessagesFromJson() {
        java.util.List<Message> loaded = Message.loadMessagesFromJson();
        if (loaded != null) {
            for (Message msg : loaded) {
                if (msg != null && storeCount < storedMessages.length) {
                    storedMessages[storeCount++] = msg;
                }
            }
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
    Message longestMsg = null;
    for (int i = 0; i < sentCount; i++) {
        if (sentMessages[i] != null && sentMessages[i].getMessage().length() > longest.length()) {
            longest = sentMessages[i].getMessage();
            longestMsg = sentMessages[i];
        }
    }
    if (longestMsg != null) {
        JOptionPane.showMessageDialog(null, longestMsg.printDetails());
    } else {
        JOptionPane.showMessageDialog(null, "No messages found.");
    }
}

    public static void searchByMessageID(String id) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null && sentMessages[i].getMessageID().equals(id)) {
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
        if (sentMessages[i] != null && sentMessages[i].getMessageHash().equals(hash)) {
            JOptionPane.showMessageDialog(null,
                "Message \"" + sentMessages[i].getMessage() + "\" successfully deleted.");

            // Shift elements left to fill the gap
            for (int j = i; j < sentCount - 1; j++) {
                sentMessages[j] = sentMessages[j + 1];
                messageHashes[j] = messageHashes[j + 1];
                messageIDs[j] = messageIDs[j + 1];
            }

            // Clear last duplicate slot
            sentMessages[sentCount - 1] = null;
            messageHashes[sentCount - 1] = null;
            messageIDs[sentCount - 1] = null;
            sentCount--;

            return;
        }
    }
    JOptionPane.showMessageDialog(null, "Message hash not found.");
}


    public static void displayReport() {
    if (sentCount == 0) {
        JOptionPane.showMessageDialog(null, "No messages have been sent yet.");
        return;
    }

    StringBuilder report = new StringBuilder("📄 Full Sent Messages Report\n");
    report.append("Total Sent: ").append(sentCount).append("\n\n");

    for (int i = 0; i < sentCount; i++) {
        Message msg = sentMessages[i];
        if (msg != null) {
            report.append("📨 Message #").append(i + 1).append("\n");
            report.append("ID: ").append(msg.getMessageID()).append("\n");
            report.append("Hash: ").append(msg.getMessageHash()).append("\n");
            report.append("To: ").append(msg.getRecipient()).append("\n");
            report.append("Body: ").append(msg.getMessage()).append("\n\n");
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

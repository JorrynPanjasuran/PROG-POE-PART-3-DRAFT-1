package com.mycompany.structumessage;

import java.io.*;
import java.util.*;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private boolean isSent;

    private static int totalMessages = 0;

    public Message(String recipient, String message, int messageNumber) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash(this.messageID, messageNumber, message);
        this.isSent = false;
    }

    public static String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) id.append(rand.nextInt(10));
        return id.toString();
    }

    public static boolean checkMessageID(String id) {
    return id != null && !id.isEmpty() && id.length() <= 10;
}


    public static boolean checkRecipientCell(String number) {
        return number.startsWith("+") && number.length() >= 11 && number.length() <= 13;
    }

    public static String validateMessageLength(String msg) {
        return (msg.length() <= 250)
                ? "Message ready to send."
                : "Message exceeds 250 characters by " + (msg.length() - 250) + ", please reduce size.";
    }

    public static String createMessageHash(String id, int msgNum, String msg) {
        String[] words = msg.trim().split("\\s+");
        String first = words.length > 0 ? words[0].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        String last = words.length > 1 ? words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        return (id.substring(0, 2) + ":" + msgNum + ":" + first + last).toUpperCase();
    }

    public String sendOptions(String choice) {
        switch (choice.toLowerCase()) {
            case "send" -> {
                isSent = true;
                totalMessages++;
                return "Message successfully sent.";
            }
            case "discard" -> {
                return "Press 0 to delete message.";
            }
            case "store" -> {
                storeMessageToJson();
                return "Message successfully stored.";
            }
            default -> {
                return "Invalid option.";
            }
        }
    }

    public String printDetails() {
        return "Message ID: " + messageID +
                "\nMessage Hash: " + messageHash +
                "\nRecipient: " + recipient +
                "\nMessage: " + message;
    }

    public static int returnTotalMessages() {
        return totalMessages;
    }

    public void storeMessageToJson() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("message.json", true))) {
            String jsonBlock = "{"
                    + "\"messageHash\":\"" + messageHash.replace("\"", "\\\"") + "\","
                    + "\"recipient\":\"" + recipient.replace("\"", "\\\"") + "\","
                    + "\"message\":\"" + message.replace("\"", "\\\"") + "\""
                    + "}";
            writer.write(jsonBlock);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving message.");
        }
    }

    public static List<Message> loadMessagesFromJson() {
        List<Message> messages = new ArrayList<>();
        File file = new File("message.json");

        if (!file.exists()) return messages;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // crude manual parsing from line
                String hash = extractJsonField(line, "messageHash");
                String recipient = extractJsonField(line, "recipient");
                String msg = extractJsonField(line, "message");

                Message m = new Message(recipient, msg, messages.size());
                m.messageHash = hash;
                messages.add(m);
            }
        } catch (IOException e) {
            System.out.println("Error reading stored messages.");
        }
        return messages;
    }

    private static String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public boolean isSent() {
        return isSent;
    }
}


// Title: StructuMessage Application – Main Class  
// Author: Oracle, Stack Overflow, TheServerSide, W3Schools, GeeksforGeeks, Baeldung, TutorialsPoint, JavaCodeGeeks, Mozilla MDN, The IIE / Rochelle Moodley  
// Date: 26 May 2025  
// Version: 1.0  
// Available: https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html  
// Additional Reference: 
// Title: JOptionPane Input Validation Example  
// Author: Stack Overflow  
// Date: 2025  
// Available: https://stackoverflow.com/questions/3544521/user-input-validation-for-joptionpane-showinputdialog  
// Additional Reference: 
// Title: Java Array Size Explained by Example  
// Author: TheServerSide  
// Date: 2025  
// Available: https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/Java-array-size-explained-by-example  
// Additional Reference: 
// Title: Java Conditions (if, else, switch)  
// Author: W3Schools  
// Date: 2025  
// Available: https://www.w3schools.com/java/java_conditions.asp  
// Additional Reference: 
// Title: Arrays in Java  
// Author: GeeksforGeeks  
// Date: 2025  
// Available: https://www.geeksforgeeks.org/arrays-in-java/  
// Additional Reference: 
// Title: Introduction to Java Swing  
// Author: Baeldung  
// Date: 2025  
// Available: https://www.baeldung.com/java-swing  
// Additional Reference: 
// Title: Java Strings Tutorial  
// Author: TutorialsPoint  
// Date: 2025  
// Available: https://www.tutorialspoint.com/java/java_strings.htm  
// Additional Reference: 
// Title: Input Validation in Java  
// Author: JavaCodeGeeks  
// Date: 2025  
// Available: https://www.javacodegeeks.com/2019/01/input-validation-in-java.html  
// Additional Reference: 
// Title: JavaScript String.substring() (used for understanding string logic)  
// Author: Mozilla Developer Network (MDN)  
// Date: 2025  
// Available: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/substring  
// Additional Reference: 
// Title: PROG5121 Lecture Slides  
// Author: The Independent Institute of Education / Rochelle Moodley  
// Date: 2025  
// Available: Internal Slide Deck (Unpublished Material)

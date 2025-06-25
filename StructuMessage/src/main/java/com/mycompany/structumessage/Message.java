package com.mycompany.structumessage;

import java.io.*;
import java.util.*;

/**
 * Domain object representing a single QuickChat message.
 * <p>
 * • Generates unique IDs and hashes<br>
 * • Performs input validation (ID / recipient / length)<br>
 * • Tracks read / sent / received flags<br>
 * • Provides JSON-line persistence helpers (store ↔ read)<br>
 * • Keeps a running counter of successfully sent messages
 * <p>
 * Part 3 adds <code>messageType</code> for Reports &amp; storage categories.
 *
 * Assessment context: PROG5121 – Part 3 (POE).
 *
 * @author Jorryn Panjasuran 2025
 */
/* ───────────────────── Attribution Headers ───────────────────── */

 /*
  Title   : Random-ID & StringBuilder Pattern  
  Author  : Oracle Java SE 8 API  
  Date    : 25 Jun 2025  
  Version : 1.0  
  Sources : 
    • Random — https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
    • StringBuilder — https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
 */

 /*
  Title   : South-African “+27” Phone-Regex Example  
  Author  : Stack Overflow user Laurence (Q 33477950)  
  Date    : 25 Jun 2025  
  Version : 1.0  
  Source  : https://stackoverflow.com/questions/33477950/java-regex-phone-number
 */

 /*
  Title   : Password Complexity Regex with Look-aheads  
  Author  : Stack Overflow (Q 19605150)  
  Date    : 25 Jun 2025  
  Version : 1.0  
  Source  : https://stackoverflow.com/questions/19605150/
 */

 /*
  Title   : BufferedWriter + FileWriter Append Pattern  
  Author  : DigitalOcean Tutorial “Java append to file”  
  Date    : 25 Jun 2025  
  Version : 1.0  
  Source  : https://www.digitalocean.com/community/tutorials/java-append-to-file
 */

 /*
  Title   : BufferedReader Line-by-Line File Read  
  Author  : DigitalOcean Tutorial “Java Read File”  
  Date    : 25 Jun 2025  
  Version : 1.0  
  Source  : https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line
 */

 /*
  Title   : String.repeat (int) (Java 11)  
  Author  : Oracle Java SE 11 API  
  Date    : 25 Jun 2025  
  Version : 1.0  
  Source  : https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#repeat(int)
 */

 /* ─────────────────────────────────────────────────────────────── */
public class Message {

    /* ─────────── Immutable instance data ─────────── */
    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;

    /* ─────────── Runtime flags ─────────── */
    private boolean isSent;
    private boolean isReceived;
    private boolean isRead;

    /* ─────────── Static / class-wide state ─────────── */
    private static int totalMessages = 0;        // increments on “send”
    private String messageType;                  // sent | stored | disregarded

    /* ────────────────────────── Constructors ────────────────────────── */
    /**
     * Build a new <em>sent</em> message straight from user input.
     *
     * @param recipient E.164 number e.g. <code>+2783…</code>
     * @param message Body text (≤ 250 chars recommended)
     * @param messageNumber Zero-based index in the current send batch
     */
    public Message(String recipient, String message, int messageNumber) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash(this.messageID, messageNumber, message);

        this.messageType = "sent";  // default category
        this.isSent = true;    // flags default to true for “sent” messages
        this.isReceived = true;
        this.isRead = true;
    }

    /* ───────────────────── Static Validation Helpers ─────────────────── */
    /**
     * Generate a random 10-digit numeric string (ID).
     */
    public static String generateMessageID() {
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Return <code>true</code> for non-empty IDs ≤ 10 digits.
     */
    public static boolean checkMessageID(String id) {
        return id != null && !id.isEmpty() && id.length() <= 10;
    }

    /**
     * Minimal E.164 recipient validation: +27XXXXXXXXX (11–13 chars).
     */
    public static boolean checkRecipientCell(String number) {
        return number.startsWith("+") && number.length() >= 11 && number.length() <= 13;
    }

    /**
     * Human-readable feedback on message length vs 250-char limit.
     */
    public static String validateMessageLength(String msg) {
        return (msg.length() <= 250)
                ? "Message ready to send."
                : "Message exceeds 250 characters by " + (msg.length() - 250)
                + ", please reduce size.";
    }

    /**
     * Build SHA-lite hash: first2ID:msgNum:FirstLastWord (upper-cased).
     */
    public static String createMessageHash(String id, int msgNum, String msg) {
        String[] words = msg.trim().split("\\s+");
        String first = words.length > 0 ? words[0].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        String last = words.length > 1 ? words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        return (id.substring(0, 2) + ":" + msgNum + ":" + first + last).toUpperCase();
    }

    /* ─────────────────────── Send / Store Workflow ───────────────────── */
    /**
     * Route message according to UI choice (“send”, “discard”, “store”). Flags
     * and counters are updated as side-effects.
     *
     * @param choice lowercase command from menu
     * @return human-readable status string
     */
    public String sendOptions(String choice) {
        switch (choice.toLowerCase()) {
            case "send" -> {
                markAsSent();
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

    /**
     * Pretty multi-line debug dump used by reports.
     */
    public String printDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message
                + "\nSent: " + isSent
                + "\nReceived: " + isReceived
                + "\nRead: " + isRead;
    }

    /**
     * How many messages have been <em>sent</em> across the whole session.
     */
    public static int returnTotalMessages() {
        return totalMessages;
    }

    /* ───────────────────────── JSON Persistence ─────────────────────── */
    /**
     * Append this message as a one-line JSON object in
     * <code>messages.json</code>.
     */
    public void storeMessageToJson() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("messages.json", true))) {
            String jsonBlock = "{"
                    + "\"messageHash\":\"" + messageHash.replace("\"", "\\\"") + "\","
                    + "\"recipient\":\"" + recipient.replace("\"", "\\\"") + "\","
                    + "\"message\":\"" + message.replace("\"", "\\\"") + "\","
                    + "\"messageType\":\"" + messageType + "\""
                    + "}";
            writer.write(jsonBlock);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving message.");
        }
    }

    /**
     * Read a newline-delimited JSON file and reconstruct <code>Message</code>
     * objects. NB: Simple string parsing (no external JSON library to keep POE
     * lightweight).
     */
    public static List<Message> readMessagesFromFile(String fileName) {
        List<Message> messages = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            return messages;   // nothing to read → empty list
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String hash = extractJsonField(line, "messageHash");
                String recipient = extractJsonField(line, "recipient");
                String msgBody = extractJsonField(line, "message");
                String type = extractJsonField(line, "messageType");

                Message m = new Message(recipient, msgBody, messages.size());
                m.setMessageType(type);
                m.messageHash = hash;          // preserve original hash
                messages.add(m);
            }
        } catch (IOException e) {
            System.out.println("Error reading messages from file: " + e.getMessage());
        }
        return messages;
    }

    /* Quick-and-dirty JSON field extractor (no nested objects). */
    private static String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) {
            return "";
        }
        start += search.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }

    /* ───────────────────── Getters / Setters / Flags ─────────────────── */
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String type) {
        this.messageType = type;
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

    public boolean isReceived() {
        return isReceived;
    }

    public boolean isRead() {
        return isRead;
    }

    /**
     * Flip sent flag to <code>true</code>.
     */
    public void markAsSent() {
        this.isSent = true;
    }

    /**
     * Flip received flag to <code>true</code>.
     */
    public void markAsReceived() {
        this.isReceived = true;
    }

    /**
     * Flip read flag to <code>true</code>.
     */
    public void markAsRead() {
        this.isRead = true;
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
//
// Title   : Arrays.fill() Array Reset
// Author  : Oracle Arrays API; W3Schools
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#fill--
//   • https://www.w3schools.com/java/ref_arrays_fill.asp
//
// Title   : BufferedReader Line-by-Line File Read
// Author  : DigitalOcean Tutorial
// Date    : 25 Jun 2025
// Version : 1.0
// Source  : https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line
//
// Title   : BufferedWriter + FileWriter Append Pattern
// Author  : DigitalOcean Tutorial “Java append to file”
// Date    : 25 Jun 2025
// Version : 1.0
// Source  : https://www.digitalocean.com/community/tutorials/java-append-to-file
//
// Title   : BufferedWriter JSON-Line Append
// Author  : Stack Overflow Q/39333219
// Date    : 25 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/39333219/
//
// Title   : File.exists() for Persistence Check
// Author  : Stack Overflow Q/1816673; GeeksforGeeks
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://stackoverflow.com/questions/1816673/
//   • https://www.geeksforgeeks.org/java/file-exists-method-in-java-with-examples/
//
// Title   : JUnit 5 Assertion Methods
// Author  : JUnit Team – Official API
// Date    : 25 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/
//
// Title   : JOptionPane Dialog Pattern
// Author  : Oracle Docs; TheServerSide; Mkyong
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html
//   • https://www.theserverside.com/               (example article)
//   • https://mkyong.com/swing/java-swing-joptionpane-showinputdialog-example/
//
// Title   : Modern “switch → arrow” Syntax
// Author  : Oracle JEP 361; nipafx.dev
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://docs.oracle.com/en/java/javase/13/language/switch-expressions.html
//   • https://nipafx.dev/java-switch/
//
// Title   : Password-Complexity Regex with Look-aheads
// Author  : Stack Overflow Q/19605150; Q/12090077
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://stackoverflow.com/questions/19605150/
//   • https://stackoverflow.com/questions/12090077/
//
// Title   : Random-ID & StringBuilder Pattern
// Author  : Oracle Java SE 8 API
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
//   • https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
//
// Title   : SA (+27) Cell-Number Regex
// Author  : validate.js issue #235; Stack Overflow Q/33477950
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://github.com/ansman/validate.js/issues/235
//   • https://stackoverflow.com/questions/33477950/java-regex-phone-number
//
// Title   : String.repeat(int) (Java 11)
// Author  : Oracle Java SE 11 API
// Date    : 25 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#repeat(int)
//
// Title   : StringBuilder for Efficient Concatenation
// Author  : Oracle Docs; Reddit /r/learnprogramming discussion
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
//   • https://www.reddit.com/r/learnprogramming/
//
// Title   : StructuMessage Application – Main Class
// Author  : Oracle, Stack Overflow, TheServerSide, W3Schools, GeeksforGeeks,
//           Baeldung, TutorialsPoint, JavaCodeGeeks, MDN, The IIE / Rochelle Moodley
// Date    : 26 May 2025
// Version : 1.0
// Available : https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html
//   *Additional references (all 2025 unless noted)*
//   • JOptionPane Input Validation Example – Stack Overflow  
//     https://stackoverflow.com/questions/3544521/
//   • Java Array Size Explained by Example – TheServerSide  
//     https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/Java-array-size-explained-by-example
//   • Java Conditions (if, else, switch) – W3Schools  
//     https://www.w3schools.com/java/java_conditions.asp
//   • Arrays in Java – GeeksforGeeks  
//     https://www.geeksforgeeks.org/arrays-in-java/
//   • Introduction to Java Swing – Baeldung  
//     https://www.baeldung.com/java-swing
//   • Java Strings Tutorial – TutorialsPoint  
//     https://www.tutorialspoint.com/java/java_strings.htm
//   • Input Validation in Java – JavaCodeGeeks  
//     https://www.javacodegeeks.com/2019/01/input-validation-in-java.html
//   • JavaScript String.substring() – MDN  
//     https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/substring
//   • PROG5121 Lecture Slides – The IIE / Rochelle Moodley (internal, unpublished)
//
// Title   : Username Regex “contains _ and ≤5 chars”
// Author  : Stack Overflow Q/336210; GeeksforGeeks
// Date    : 25 Jun 2025
// Version : 1.0
// Sources :
//   • https://stackoverflow.com/questions/336210/regular-expression-for-alphanumeric-and-underscores
//   • https://www.geeksforgeeks.org/how-to-validate-a-username-using-regular-expressions-in-java/

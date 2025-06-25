package com.mycompany.structumessage;

import javax.swing.*;
import java.util.Arrays;
import java.io.File;
import java.util.List;

/**
 * StructuMessage – Main driver class for the QuickChat console / Swing app.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Startup workflow: load JSON, register user, prompt for login.</li>
 * <li>Run an interactive menu loop (send / show / disregard / reports /
 * quit).</li>
 * <li>Maintain in-memory arrays for sent / stored / disregarded messages.</li>
 * <li>Provide small helper utilities (validation prompts, test-data preload,
 * etc.).</li>
 * </ul>
 * <p>
 * Part 3 adds: JSON persistence, hash-based deletes, message reports, and
 * counters exposed through getters for JUnit tests.
 *
 * @author Jorryn Panjasuran 2025
 */
public class StructuMessage {

    /* ──────────────── Static Data Stores ─────────────── */
    /**
     * Messages actually sent in this session (+ those re-hydrated on start-up).
     */
    static Message[] sentMessages = new Message[100];
    /**
     * Messages that the user explicitly discarded.
     */
    static Message[] disregardedMessages = new Message[100];
    /**
     * Valid messages the user chose to keep for later.
     */
    static Message[] storedMessages = new Message[100];

    /**
     * Parallel arrays for quick hash / ID look-ups (constant-time search).
     */
    static String[] messageHashes = new String[100];
    static String[] messageIDs = new String[100];

    /* Counters let us treat the fixed-size arrays as dynamic lists. */
    private static int sentCount = 0;
    private static int discardCount = 0;
    private static int storeCount = 0;

    /* ──────────────── Public accessors (used by unit tests) ─────────────── */
    public static int getSentCount() {
        return sentCount;
    }

    public static int getStoreCount() {
        return storeCount;
    }

    public static int getDiscardCount() {
        return discardCount;
    }

    /* ───────────────────────────── Main ─────────────────────────────────── */
    /**
     * Application entry-point. 1. Load any previously saved JSON messages. 2.
     * Register a new QuickChatUser. 3. Loop until a valid login occurs. 4.
     * Launch the main application menu.
     */
    //  Title: JOptionPane Dialog Pattern
    //  Author(s): Oracle Docs, TheServerSide, Mkyong
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://docs.oracle.com/javase/8/docs/api/javax/swing/JOptionPane.html
    //    • https://www.theserverside.com/
    //    • https://mkyong.com/swing/java-swing-joptionpane-showinputdialog-example/
    //
    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat Registration");
        loadMessagesFromFile();           // Step 1 – read persisted messages

        /* — User Registration sequence — */
        String firstName = JOptionPane.showInputDialog("Enter your first name:");
        String lastName = JOptionPane.showInputDialog("Enter your last name:");

        String username = getValidUsername();
        String password = getValidPassword();
        String cellphone = getValidCellphone();

        QuickChatUser user = new QuickChatUser(username, password, cellphone, firstName, lastName);
        JOptionPane.showMessageDialog(null, user.register());

        /* — Login loop — */
        boolean loggedIn = false;
        while (!loggedIn) {
            String inputUsername = JOptionPane.showInputDialog("Username:");
            String inputPassword = JOptionPane.showInputDialog("Password:");

            if (user.login(inputUsername, inputPassword)) {
                JOptionPane.showMessageDialog(null, user.loginStatusMessage(true));
                loadStoredMessagesFromJson();  // hydrate <stored> category only
                populateTestMessages();        // demo data for markers
                runApp();                      // main menu
                loggedIn = true;
            } else {
                JOptionPane.showMessageDialog(null, user.loginStatusMessage(false));
            }
        }
    }

    /* ─────────────────────── Application Menu ─────────────────────────── */
    /**
     * Display and handle the menu until the user chooses “Quit”. Splitting this
     * into its own method keeps {@code main} concise.
     */
    //  Title: Modern “switch → arrow” Syntax
    //  Author(s): Oracle JEP 361, nipafx.dev
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://docs.oracle.com/en/java/javase/13/language/switch-expressions.html
    //    • https://nipafx.dev/java-switch/
    // 
    public static void runApp() {
        boolean running = true;
        while (running) {
            String option = JOptionPane.showInputDialog(
                    "Welcome to QuickChat!\n\n"
                    + "Choose an option:\n"
                    + "1) Send Message\n"
                    + "2) Show Recently Sent Messages\n"
                    + "3) Disregard a Message\n"
                    + "4) Quit\n"
                    + "5) Reports"
            );

            switch (option) {
                case "1" -> {
                    int total = Integer.parseInt(
                            JOptionPane.showInputDialog("How many messages would you like to send?"));
                    for (int i = 0; i < total; i++) {
                        if (!sendMessage(i)) {
                            JOptionPane.showMessageDialog(null, "Message not sent. Skipping…");
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
                case "3" ->
                    disregardMessage();
                case "4" ->
                    running = false;
                case "5" ->
                    showReports();
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1-5.");
            }
        }
    }

    /* ───────────────────────── Reports Sub-menu ────────────────────────── */
    /**
     * Let the user choose a specialised report without exiting the main loop.
     */
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
            case "1" ->
                showSenderAndRecipients();
            case "2" ->
                showLongestMessage();
            case "3" ->
                searchByMessageID(
                        JOptionPane.showInputDialog("Enter Message ID:"));
            case "4" ->
                searchByRecipient(
                        JOptionPane.showInputDialog("Enter Recipient:"));
            case "5" ->
                deleteByMessageHash(
                        JOptionPane.showInputDialog("Enter Message Hash:"));
            case "6" ->
                displayReport();
            default ->
                JOptionPane.showMessageDialog(null, "Invalid report option.");
        }
    }

    /* ───────────────────── Message Creation & Routing ──────────────────── */
    // ─── AI Attribution ─────────────────────────────────────────────────
    //  This method was developed with the support of OpenAI's ChatGPT …
    // ───────────────────────────────────────────────────────────────────
    /**
     * Gather message details, validate input, and route to Sent / Stored /
     * Disregarded arrays based on the user's decision.
     *
     * @param msgNum zero-based position in the current batch (“Message #”)
     * @return {@code true} if the message made it into any category;
     * {@code false} if validation failed or storage arrays are full.
     */
    //  Title: SA (+27) Cell-Number Regex
    //  Author: validate.js GitHub issue #235
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Source: https://github.com/ansman/validate.js/issues/235
    //  (Regex re-used in Message.checkRecipientCell)
    //
    public static boolean sendMessage(int msgNum) {
        /* — 1. Recipient — */
        String recipient = JOptionPane.showInputDialog(
                "Enter recipient phone number (e.g., +27834567890):");
        if (!Message.checkRecipientCell(recipient)) {
            JOptionPane.showMessageDialog(null, "Cell phone number is incorrectly formatted.");
            return false;
        }

        /* — 2. Content — */
        String content = JOptionPane.showInputDialog("Enter your message (max 250 characters):");
        String feedback = Message.validateMessageLength(content);
        JOptionPane.showMessageDialog(null, feedback);
        if (!"Message ready to send.".equals(feedback)) {
            return false;
        }

        /* — 3. Construct Message object — */
        Message msg = new Message(recipient, content, msgNum);

        /* — 4. Ask the user what to do with it — */
        String[] options = {"Send", "Discard", "Store"};
        int action = JOptionPane.showOptionDialog(
                null, "What would you like to do with this message?",
                "Message Options", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        /* — 5. Route according to choice — */
        switch (action) {
            case 0 -> {                               // 5-A: SEND
                msg.setMessageType("sent");
                msg.storeMessageToJson();             // optional, but keeps full audit
                if (sentCount < sentMessages.length) {
                    sentMessages[sentCount] = msg;
                    messageHashes[sentCount] = msg.getMessageHash();
                    messageIDs[sentCount] = msg.getMessageID();
                    sentCount++;
                    JOptionPane.showMessageDialog(null, msg.printDetails());
                    return true;
                }
                JOptionPane.showMessageDialog(null, "Message storage full.");
                return false;
            }
            case 1 -> {                               // 5-B: DISCARD
                msg.setMessageType("disregarded");
                msg.storeMessageToJson();
                if (discardCount < disregardedMessages.length) {
                    disregardedMessages[discardCount++] = msg;
                    JOptionPane.showMessageDialog(null, "Message discarded.");
                    return true;
                }
                JOptionPane.showMessageDialog(null, "Disregarded message storage full.");
                return false;
            }
            case 2 -> {                               // 5-C: STORE
                msg.setMessageType("stored");
                msg.storeMessageToJson();
                if (storeCount < storedMessages.length) {
                    storedMessages[storeCount++] = msg;
                    JOptionPane.showMessageDialog(null, "Message successfully stored.");
                    return true;
                }
                JOptionPane.showMessageDialog(null, "Stored message array full.");
                return false;
            }
            default -> {
                JOptionPane.showMessageDialog(null, "Invalid option.");
                return false;
            }
        }
    }

    /* ─────────────────────── JSON Hydration Helpers ───────────────────── */
    /**
     * Load only “stored” messages into RAM after a successful login
     * (sent/disregarded are already in arrays; this avoids duplication).
     */
    //  Title: BufferedReader Line-by-Line File Read
    //  Author: DigitalOcean Tutorial
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Source: https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line
    //
    public static void loadStoredMessagesFromJson() {
        List<Message> loaded = Message.readMessagesFromFile("messages.json");
        if (loaded != null) {
            for (Message msg : loaded) {
                if (msg != null && "stored".equalsIgnoreCase(msg.getMessageType())
                        && storeCount < storedMessages.length) {
                    storedMessages[storeCount++] = msg;
                }
            }
        }
    }

    /* ───────────────────────── Report Generators ─────────────────────── */
    /**
     * Print a simple list of all sent messages with recipient details.
     */
    //
    //  Title: StringBuilder for Efficient Concatenation
    //  Author(s): Oracle Docs; Reddit /r/learnprogramming discussion
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
    //    • https://www.reddit.com/r/learnprogramming/
    //
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

    /**
     * Identify the longest <sent> message for quick QA checks.
     */
    public static void showLongestMessage() {
        String longest = "";
        Message longestMsg = null;
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getMessage().length() > longest.length()) {
                longest = sentMessages[i].getMessage();
                longestMsg = sentMessages[i];
            }
        }
        JOptionPane.showMessageDialog(null,
                longestMsg != null ? longestMsg.printDetails() : "No messages found.");
    }

    /**
     * Linear search by ID (arrays are tiny; O(n) is fine).
     */
    public static void searchByMessageID(String id) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null && sentMessages[i].getMessageID().equals(id)) {
                JOptionPane.showMessageDialog(null,
                        "Recipient: " + sentMessages[i].getRecipient()
                        + "\nMessage: " + sentMessages[i].getMessage());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message ID not found.");
    }

    /**
     * Combine sent + stored look-ups so the user doesn’t have to search twice.
     */
    public static void searchByRecipient(String recipient) {
        StringBuilder found = new StringBuilder(
                "Messages sent/stored to " + recipient + ":\n");

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

        JOptionPane.showMessageDialog(null,
                found.length() > 0 ? found.toString() : "No messages found.");
    }

    /**
     * Remove a sent message permanently via its SHA-256 hash.
     */
    public static void deleteByMessageHash(String hash) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getMessageHash().equals(hash)) {
                JOptionPane.showMessageDialog(null,
                        "Message \"" + sentMessages[i].getMessage() + "\" successfully deleted.");
                removeSentMessageAtIndex(i); // compact array
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message hash not found.");
    }

    /**
     * Pretty console-style report of every sent message.
     */
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
                report.append("📨 Message #").append(i + 1).append("\n")
                        .append("ID: ").append(msg.getMessageID()).append("\n")
                        .append("Hash: ").append(msg.getMessageHash()).append("\n")
                        .append("To: ").append(msg.getRecipient()).append("\n")
                        .append("Body: ").append(msg.getMessage()).append("\n\n");
            }
        }
        JOptionPane.showMessageDialog(null, report.toString());
    }

    /* ────────────────── Validation / Prompt Helpers ──────────────────── */
    //  Title: Password-Complexity Regex (look-ahead)
    //  Author(s): Stack Overflow Q19605150; GeeksforGeeks
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://stackoverflow.com/questions/19605150/
    //    • https://www.geeksforgeeks.org/how-to-validate-a-password-using-regular-expressions-in-java/
    //
    private static String getValidUsername() {
        while (true) {
            String username = JOptionPane.showInputDialog(
                    "Enter username (must contain _ and be ≤ 5 chars):");
            if (QuickChatUser.checkUserName(username)) {
                return username;
            }
            JOptionPane.showMessageDialog(null, "Invalid username format.");
        }
    }

    private static String getValidPassword() {
        while (true) {
            String password = JOptionPane.showInputDialog(
                    "Enter password (8+ chars, 1 capital, 1 number, 1 special):");
            if (QuickChatUser.checkPasswordComplexity(password)) {
                return password;
            }
            JOptionPane.showMessageDialog(null, "Invalid password format.");
        }
    }

    private static String getValidCellphone() {
        while (true) {
            String phone = JOptionPane.showInputDialog(
                    "Enter cellphone number (+27XXXXXXXXX):");
            if (QuickChatUser.checkCellPhoneNumber(phone)) {
                return phone;
            }
            JOptionPane.showMessageDialog(null, "Invalid cellphone number.");
        }
    }

    /* ─────────────────── Sent-Message Maintenance ───────────────────── */
    /**
     * Move a message to the <disregarded> array without breaking indices.
     */
    //  Title: Arrays.fill() Array Reset
    //  Author(s): Oracle Arrays API; W3Schools
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html#fill--
    //    • https://www.w3schools.com/java/ref_arrays_fill.asp
    //
    public static void disregardMessage() {
        if (getSentCount() == 0) {
            JOptionPane.showMessageDialog(null, "No sent messages to disregard.");
            return;
        }

        String id = JOptionPane.showInputDialog("Enter Message ID to disregard:");
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null && sentMessages[i].getMessageID().equals(id)) {
                disregardedMessages[discardCount++] = sentMessages[i];
                JOptionPane.showMessageDialog(null, "Message " + id + " moved to disregarded.");
                removeSentMessageAtIndex(i);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message ID not found.");
    }

    /**
     * Remove one element from {@code sentMessages} and keep the array compact.
     *
     * @param index position to remove
     */
    public static void removeSentMessageAtIndex(int index) {
        for (int j = index; j < sentCount - 1; j++) {
            sentMessages[j] = sentMessages[j + 1];
            messageHashes[j] = messageHashes[j + 1];
            messageIDs[j] = messageIDs[j + 1];
        }
        // Null out dangling last slot
        sentMessages[--sentCount] = null;
        messageHashes[sentCount] = null;
        messageIDs[sentCount] = null;
    }

    /* ─────────────────────── Demo / Test Helpers ─────────────────────── */
    /**
     * Inject five canned messages so markers don’t have to type.
     */
    //  Title: BufferedWriter JSON-Line Append
    //  Author: Stack Overflow Q39333219
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Source: https://stackoverflow.com/questions/39333219/
    //
    public static void populateTestMessages() {
        Message msg1 = new Message("+27834557896", "Hi, this is pre-populated test message 1", sentCount);
        Message msg2 = new Message("+27831231234", "It is dinner time!", sentCount + 1);
        Message msg3 = new Message("+27831110000", "Yohoooo, I am at your gate.", sentCount + 2);
        Message msg4 = new Message("+27832221111", "Fine. I'll meet you there.", sentCount + 3);
        Message msg5 = new Message("+27839998888", "Ok, I am leaving without you.", sentCount + 4);

        sentMessages[sentCount++] = msg1;
        sentMessages[sentCount++] = msg2;
        disregardedMessages[discardCount++] = msg3;
        storedMessages[storeCount++] = msg4;
        storedMessages[storeCount++] = msg5;

        messageHashes[0] = msg1.getMessageHash();
        messageHashes[1] = msg2.getMessageHash();
        messageIDs[0] = msg1.getMessageID();
        messageIDs[1] = msg2.getMessageID();

        JOptionPane.showMessageDialog(null,
                "📦 5 test messages pre-loaded: 2 sent, 1 discarded, 2 stored.");
    }

    /* ───────────────────── JSON Loader at Start-up ───────────────────── */
    // ─── AI Attribution ─────────────────────────────────────────────────────────────
// This method was developed with the support of OpenAI's ChatGPT to assist in
// reading JSON files into a List structure for Part 3 of the PROG5121 Portfolio.
// Final testing and integration were done by the student.
// OpenAI. 2024. ChatGPT (Version 3.0). [Large language model]. Available at: https://chat.openai.com/ [Accessed: 25 June 2025].
// ────────────────────────────────────────────────────────────────────────────────
    /**
     * Pull any previously saved messages from <code>messages.json</code> on
     * disk and distribute them into the relevant in-memory arrays.
     * <p>
     * This runs <em>before</em> user registration so that counters are ready.
     */
    public static void loadMessagesFromFile() {
        try {
            File file = new File("messages.json");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "No saved messages found.");
                return;
            }

            List<Message> loadedMessages = Message.readMessagesFromFile("messages.json");
            for (Message msg : loadedMessages) {
                String type = msg.getMessageType().toLowerCase();
                switch (type) {
                    case "sent" -> {
                        sentMessages[sentCount] = msg;
                        messageHashes[sentCount] = msg.getMessageHash();
                        messageIDs[sentCount] = msg.getMessageID();
                        sentCount++;
                    }
                    case "stored" ->
                        storedMessages[storeCount++] = msg;
                    case "disregarded" ->
                        disregardedMessages[discardCount++] = msg;
                }
            }
            JOptionPane.showMessageDialog(null, "Messages loaded from file successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error loading messages from file: " + e.getMessage());
        }
    }

    /**
     * Visible-for-tests reset. Clears all message arrays and zeroes counters so
     * each JUnit test starts from a known state. *NOT* used in production code.
     */
    static void _resetForUnitTests() {
        Arrays.fill(sentMessages, null);
        Arrays.fill(storedMessages, null);
        Arrays.fill(disregardedMessages, null);
        sentCount = storeCount = discardCount = 0;
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
//

package com.mycompany.structumessage;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-test suite for {@link Message}.
 * <p>
 * Coverage matrix:</p>
 * <ul>
 * <li>ID / hash generation & validation</li>
 * <li>Recipient & length validation helpers</li>
 * <li>sendOptions routing (“send / discard / store / invalid”)</li>
 * <li>JSON persistence helpers (read / write)</li>
 * <li>Boolean flag mutators & accessors (sent / received / read)</li>
 * <li>Part 3 extras: constructor integrity, uniqueness checks, etc.</li>
 * </ul>
 *
 * Assessment context: PROG5121 – Part 3 (JUnit + CI marks).
 *
 * @author Jorryn Panjasuran 2025
 */

/* ───────────────────────── Attribution Notes ─────────────────────────
       Title: JUnit 5 Assertion Methods
       Author(s): Oracle JUnit 5 API, Petri Kainulainen, Medium (Javarevisited)
       Date: 25 Jun 2025
       Version: 1.0
       Sources:
         • https://junit.org/junit5/docs/current/api/           (assertEquals / assertTrue)
         • https://www.petrikainulainen.net/...                 (assertDoesNotThrow)
         • https://medium.com/javarevisited/...                (assertDoesNotThrow example)
    --------------------------------------------------------------------- */

 /* ───────────────────────── Existing Unit Tests ───────────────────── */
//  Title: Random-ID Generation Regex (^\\d{10}$)
//  Author: Stack Overflow Q/14811198
//  Date: 25 Jun 2025
//  Version: 1.0
//  Source: https://stackoverflow.com/questions/14811198/
/* ───────────────────────── Existing Unit Tests ───────────────────── */
public class MessageTest {

    /* ───────────────────────── Existing Unit Tests ───────────────────── */
    /**
     * Generated ID must be non-null, numeric, and exactly 10 chars long.
     */
    @Test
    public void testGenerateMessageID() {
        String id = Message.generateMessageID();
        assertNotNull(id);
        assertEquals(10, id.length());
        assertTrue(id.matches("\\d{10}"));
    }

    /**
     * A valid 10-digit ID string should pass the regex check.
     */
    @Test
    public void testCheckMessageID_Valid() {
        assertTrue(Message.checkMessageID("1234567890"));
    }

    /**
     * An ID longer than 10 digits must be rejected.
     */
    @Test
    public void testCheckMessageID_Invalid() {
        assertFalse(Message.checkMessageID("123456789012"));
    }

    /**
     * E.164-style cell number (“+27…”) should validate.
     */
    @Test
    public void testCheckRecipientCell_Valid() {
        assertTrue(Message.checkRecipientCell("+27718693002"));
    }

    /**
     * Local format without “+27” prefix should fail.
     */
    @Test
    public void testCheckRecipientCell_Invalid() {
        assertFalse(Message.checkRecipientCell("0841234567"));
    }

    /**
     * ≤250-char message returns the ready-to-send feedback string.
     */
    /* ───────────────────── New Unit Tests for Part 3 ─────────────────── */
    //  Title: String.repeat() (Java 11) for long-message setup
    //  Author(s): GeeksforGeeks, Stack Overflow Q/1235179
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://www.geeksforgeeks.org/java/string-class-repeat-method-in-java-with-examples/
    //    • https://stackoverflow.com/questions/1235179/
    //
    @Test
    public void testValidateMessageLength_Valid() {
        String msg = "Hello, how are you?";
        assertEquals("Message ready to send.", Message.validateMessageLength(msg));
    }

    /**
     * >250 chars must return the “exceeds” error with delta value.
     */
    @Test
    public void testValidateMessageLength_Invalid() {
        String longMsg = "A".repeat(260);
        assertEquals(
                "Message exceeds 250 characters by 10, please reduce size.",
                Message.validateMessageLength(longMsg));
    }

    /**
     * Hash format “first2ID:msgNum:UPPERCASEBODYNO­SPACES”.
     */
    @Test
    public void testCreateMessageHash() {
        String hash = Message.createMessageHash(
                "1234567890", 0, "Hi Mike, let's meet tonight");
        assertEquals("12:0:HITONIGHT", hash);
    }

    /**
     * sendOptions("send") must set the sent flag and confirm message.
     */
    @Test
    public void testSendOptions_Send() {
        Message m = new Message("+27718693002", "Test message", 1);
        assertEquals("Message successfully sent.", m.sendOptions("send"));
        assertTrue(m.isSent());
    }

    /**
     * sendOptions("discard") path – behaviour matches current logic.
     */
    //  Title: File.exists() for persistence check
    //  Author(s): Stack Overflow Q/1816673, GeeksforGeeks
    //  Date: 25 Jun 2025
    //  Version: 1.0
    //  Sources:
    //    • https://stackoverflow.com/questions/1816673/
    //    • https://www.geeksforgeeks.org/java/file-exists-method-in-java-with-examples/
    //
    @Test
    public void testSendOptions_Discard() {
        Message m = new Message("+27718693002", "Discard this", 2);
        m.markAsSent(); // simulate a flag already set
        String result = m.sendOptions("discard");

        assertEquals("Press 0 to delete message.", result);
        assertTrue(m.isSent()); // class currently keeps flag unchanged
    }

    /**
     * sendOptions("store") should persist to disk and keep a JSON file.
     */
    @Test
    public void testSendOptions_Store() {
        Message m = new Message("+27718693002", "Store this message", 3);
        String result = m.sendOptions("store");
        assertEquals("Message successfully stored.", result);
        assertTrue(new File("message.json").exists());
    }

    /**
     * Counter should increment when a message is sent.
     */
    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();
        Message m = new Message("+27718693002", "New send", 4);
        m.sendOptions("send");
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    /**
     * JSON reader must never return null (empty list OK).
     */
    @Test
    public void testReadMessagesFromFile() {
        List<Message> messages = Message.readMessagesFromFile("messages.json");
        assertNotNull(messages);
    }

    /**
     * Basic getter coverage for recipient, body, ID & hash.
     */
    @Test
    public void testGetters() {
        Message m = new Message("+27834567890", "Unit test message", 5);
        assertEquals("+27834567890", m.getRecipient());
        assertEquals("Unit test message", m.getMessage());
        assertNotNull(m.getMessageID());
        assertNotNull(m.getMessageHash());
    }

    /* ───────────────────── New Unit Tests for Part 3 ─────────────────── */
    /**
     * Two consecutive IDs should never clash → uniqueness guarantee.
     */
    @Test
    public void testMessageID_IsUnique() {
        assertNotEquals(Message.generateMessageID(), Message.generateMessageID());
    }

    /**
     * Constructor must assign all supplied values and generate IDs/hashes.
     */
    @Test
    public void testMessageConstructor_AssignsCorrectValues() {
        Message m = new Message("+27830000000", "Hello", 1);
        assertEquals("+27830000000", m.getRecipient());
        assertEquals("Hello", m.getMessage());
        assertNotNull(m.getMessageID());
        assertNotNull(m.getMessageHash());
    }

    /**
     * Hash regex: AA:digits:ALPHANUM (checked with a tiny number).
     */
    @Test
    public void testMessageHash_FormatStructure() {
        Message m = new Message("+2783", "Hello again", 2);
        assertTrue(m.getMessageHash().matches("[A-Z0-9]{2}:\\d+:[A-Z0-9]+"));
    }

    /**
     * Invalid menu choice should return graceful error string.
     */
    @Test
    public void testSendOptions_InvalidChoice() {
        Message m = new Message("+2783", "Hello", 2);
        assertEquals("Invalid option.", m.sendOptions("unknown"));
    }

    /**
     * readMessagesFromFile must always return a java.util.List instance.
     */
    @Test
    public void testReadMessagesFromFile_ReturnsList() {
        assertInstanceOf(
                List.class, Message.readMessagesFromFile("messages.json"));
    }

    /**
     * storeMessageToJson() should never throw an exception.
     */
    @Test
    public void testStoreMessageToJson_DoesNotThrow() {
        Message m = new Message("+2783", "Test", 0);
        assertDoesNotThrow(m::storeMessageToJson);
    }

    /**
     * Empty string should fail ID validation.
     */
    @Test
    public void testCheckMessageID_EmptyString() {
        assertFalse(Message.checkMessageID(""));
    }

    /**
     * populateTestMessages() must fill the expected arrays & counters.
     */
    @Test
    public void testPopulateTestMessages_PopulatesCorrectly() {

        StructuMessage._resetForUnitTests();   // ⬅️ one clean call
        StructuMessage.populateTestMessages(); // re-insert demo data

        // use public getters, not fields
        assertEquals(2, StructuMessage.getSentCount());
        assertEquals(2, StructuMessage.getStoreCount());
        assertEquals(1, StructuMessage.getDiscardCount());

        // spot-check specific records
        assertEquals("+27834557896",
                StructuMessage.sentMessages[0].getRecipient());
        assertEquals("It is dinner time!",
                StructuMessage.sentMessages[1].getMessage());
        assertEquals("Yohoooo, I am at your gate.",
                StructuMessage.disregardedMessages[0].getMessage());
        assertEquals("Ok, I am leaving without you.",
                StructuMessage.storedMessages[1].getMessage());
    }

    /**
     * Newly constructed message default flags (adjust expectations if needed).
     */
    @Test
    public void testMessageFlags_DefaultState() {
        Message m = new Message("+27834567890", "Testing flags", 1);
        assertTrue(m.isSent());
        assertTrue(m.isReceived());
        assertTrue(m.isRead());
    }

    /**
     * markAsSent() must flip the sent flag.
     */
    @Test
    public void testMarkAsSent_SetsFlagTrue() {
        Message m = new Message("+27834567890", "Send flag test", 1);
        m.markAsSent();
        assertTrue(m.isSent());
    }

    /**
     * markAsReceived() must flip the received flag.
     */
    @Test
    public void testMarkAsReceived_SetsFlagTrue() {
        Message m = new Message("+27834567890", "Receive flag test", 1);
        m.markAsReceived();
        assertTrue(m.isReceived());
    }

    /**
     * markAsRead() must flip the read flag.
     */
    @Test
    public void testMarkAsRead_SetsFlagTrue() {
        Message m = new Message("+27834567890", "Read flag test", 1);
        m.markAsRead();
        assertTrue(m.isRead());
    }

    /**
     * printDetails() should echo all three boolean flags.
     */
    @Test
    public void testPrintDetails_IncludesAllFlags() {
        Message m = new Message("+27834567890", "Test print details", 1);
        m.markAsSent();
        m.markAsReceived();
        m.markAsRead();

        String details = m.printDetails();
        assertTrue(details.contains("Sent: true"));
        assertTrue(details.contains("Received: true"));
        assertTrue(details.contains("Read: true"));
    }

    /* ───────────────────── JSON Loader Smoke Tests ───────────────────── */
    // ─── AI Attribution ────────────────────────────────────────────────
    //  This method was developed with the support of OpenAI's ChatGPT …
    // ───────────────────────────────────────────────────────────────────
    @Test
    public void testLoadMessagesFromJson() {
        List<Message> messages = Message.readMessagesFromFile("messages.json");
        assertNotNull(messages);
    }

    // ─── AI Attribution ────────────────────────────────────────────────
    //  This method was developed with the support of OpenAI's ChatGPT …
    // ───────────────────────────────────────────────────────────────────
    @Test
    public void testLoadMessagesFromJson_ReturnsList() {
        List<Message> messages = Message.readMessagesFromFile("messages.json");
        assertInstanceOf(List.class, messages);
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

package com.mycompany.structumessage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Unit tests for the Message class – PROG5121 Part 2
 * 
 * Title: Java Unit Testing with JUnit
 * Author: Oracle, Stack Overflow, GeeksforGeeks
 * Date: 2025
 * Sources:
 * - https://www.geeksforgeeks.org/unit-testing-java-using-junit/
 * - https://junit.org/junit5/docs/current/user-guide/
 * - https://stackoverflow.com/questions/6070690
 * - The Independent Institute of Education. (2025). PROG5121.
 */
public class MessageTest {

    @Test
    public void testGenerateMessageID() {
        String id = Message.generateMessageID();
        assertNotNull(id); // ID should not be null
        assertEquals(10, id.length()); // Should be exactly 10 digits
        assertTrue(id.matches("\\d{10}")); // Must be numeric
    }

    @Test
    public void testCheckMessageID_Valid() {
        assertTrue(Message.checkMessageID("1234567890"));
    }

    @Test
    public void testCheckMessageID_Invalid() {
        assertFalse(Message.checkMessageID("123456789012")); // Too long
    }

    @Test
    public void testCheckRecipientCell_Valid() {
        assertTrue(Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testCheckRecipientCell_Invalid() {
        assertFalse(Message.checkRecipientCell("08575975889")); // Missing +27
    }

    @Test
    public void testValidateMessageLength_Valid() {
        String msg = "Hi Mike, can you join us for dinner tonight";
        assertEquals("Message ready to send.", Message.validateMessageLength(msg));
    }

    @Test
    public void testValidateMessageLength_Invalid() {
        String longMsg = "A".repeat(260);
        assertEquals("Message exceeds 250 characters by 10, please reduce size.",
                Message.validateMessageLength(longMsg));
    }

    @Test
    public void testCreateMessageHash() {
        String result = Message.createMessageHash("0012345678", 0, "Hi Mike, can you join us for dinner tonight");
        assertEquals("00:0:HITONIGHT", result); // Format: ID prefix : msgNum : first+last word (no space)
    }

    @Test
    public void testSendOptions_Send() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight", 0);
        assertEquals("Message successfully sent.", m.sendOptions("send"));
        assertTrue(m.isSent());
    }

    @Test
    public void testSendOptions_Discard() {
        Message m = new Message("+27718693002", "Hi Keegan, did you receive the payment?", 1);
        assertEquals("Press 0 to delete message.", m.sendOptions("discard"));
        assertFalse(m.isSent());
    }

    @Test
    public void testSendOptions_Store() {
        Message m = new Message("+27718693002", "Message to be stored", 2);
        assertEquals("Message successfully stored.", m.sendOptions("store"));
        // Optionally verify file creation manually
    }

    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();
        Message m = new Message("+27718693002", "Another message", 3);
        m.sendOptions("send");
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    @Test
    public void testGetters() {
        Message m = new Message("+27834567890", "Sample test message", 5);
        assertNotNull(m.getMessageID());
        assertEquals("Sample test message", m.getMessage());
        assertEquals("+27834567890", m.getRecipient());
        assertNotNull(m.getMessageHash());
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

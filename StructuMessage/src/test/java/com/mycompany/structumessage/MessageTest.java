package com.mycompany.structumessage;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testGenerateMessageID() {
        String id = Message.generateMessageID();
        assertNotNull(id);
        assertEquals(10, id.length());
        assertTrue(id.matches("\\d{10}"));
    }

    @Test
    public void testCheckMessageID_Valid() {
        assertTrue(Message.checkMessageID("1234567890"));
    }

    @Test
    public void testCheckMessageID_Invalid() {
        assertFalse(Message.checkMessageID("123456789012"));
    }

    @Test
    public void testCheckRecipientCell_Valid() {
        assertTrue(Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testCheckRecipientCell_Invalid() {
        assertFalse(Message.checkRecipientCell("0841234567")); // Missing +27
    }

    @Test
    public void testValidateMessageLength_Valid() {
        String msg = "Hello, how are you?";
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
        String hash = Message.createMessageHash("1234567890", 0, "Hi Mike, let's meet tonight");
        assertEquals("12:0:HITONIGHT", hash);
    }

    @Test
    public void testSendOptions_Send() {
        Message m = new Message("+27718693002", "Test message", 1);
        assertEquals("Message successfully sent.", m.sendOptions("send"));
        assertTrue(m.isSent());
    }

    @Test
    public void testSendOptions_Discard() {
        Message m = new Message("+27718693002", "Discard this", 2);
        assertEquals("Press 0 to delete message.", m.sendOptions("discard"));
        assertFalse(m.isSent());
    }

    @Test
    public void testSendOptions_Store() {
        Message m = new Message("+27718693002", "Store this message", 3);
        String result = m.sendOptions("store");
        assertEquals("Message successfully stored.", result);
        File file = new File("message.json");
        assertTrue(file.exists());
    }

    @Test
    public void testReturnTotalMessages() {
        int before = Message.returnTotalMessages();
        Message m = new Message("+27718693002", "New send", 4);
        m.sendOptions("send");
        assertEquals(before + 1, Message.returnTotalMessages());
    }

    @Test
    public void testLoadMessagesFromJson() {
        List<Message> messages = Message.loadMessagesFromJson();
        assertNotNull(messages);
        assertTrue(messages.size() >= 0); // Will be 0 if nothing stored yet
    }

    @Test
    public void testGetters() {
        Message m = new Message("+27834567890", "Unit test message", 5);
        assertNotNull(m.getMessageID());
        assertNotNull(m.getMessageHash());
        assertEquals("+27834567890", m.getRecipient());
        assertEquals("Unit test message", m.getMessage());
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

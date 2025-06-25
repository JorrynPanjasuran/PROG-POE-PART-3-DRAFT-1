package com.mycompany.structumessage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the QuickChatUser class – PROG5121 Part 3
 */
public class QuickChatUserTest {

    @Test
    public void testCheckUserName_Valid() {
        assertTrue(QuickChatUser.checkUserName("usr_1"));
    }

    @Test
    public void testCheckUserName_Invalid() {
        assertFalse(QuickChatUser.checkUserName("invalidusername"));
    }

    @Test
    public void testCheckUserName_Null() {
        assertFalse(QuickChatUser.checkUserName(null));
    }

    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertTrue(QuickChatUser.checkPasswordComplexity("Secure@123"));
    }

    @Test
    public void testCheckPasswordComplexity_Invalid() {
        assertFalse(QuickChatUser.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckPasswordComplexity_Null() {
        assertFalse(QuickChatUser.checkPasswordComplexity(null));
    }

    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertTrue(QuickChatUser.checkCellPhoneNumber("+27812345678"));
    }

    @Test
    public void testCheckCellPhoneNumber_Invalid() {
        assertFalse(QuickChatUser.checkCellPhoneNumber("0812345678"));
    }

    @Test
    public void testCheckCellPhoneNumber_Null() {
        assertFalse(QuickChatUser.checkCellPhoneNumber(null));
    }

    @Test
    public void testRegister_Success() {
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Username and password successfully captured.\nCell phone number successfully added.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testRegister_InvalidUsername() {
        QuickChatUser user = new QuickChatUser("invalidusername", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Username is not correctly formatted.\n" +
                          "It must contain an underscore (_) and be no more than five characters long.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testRegister_InvalidPassword() {
        QuickChatUser user = new QuickChatUser("usr_1", "weakpass", "+27812345678", "Test", "User");
        String expected = "Password is not correctly formatted.\n" +
                          "It must be at least eight characters long and include:\n" +
                          "- A capital letter\n- A number\n- A special character";
        assertEquals(expected, user.register());
    }

    @Test
    public void testRegister_InvalidCell() {
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "0812345678", "Test", "User");
        String expected = "Cell phone number is not correctly formatted.\n" +
                          "It must start with +27 and be followed by exactly 9 digits.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testLogin_Success() {
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        assertTrue(user.login("usr_1", "Secure@123"));
    }

    @Test
    public void testLogin_Failure() {
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        assertFalse(user.login("wronguser", "wrongpass"));
    }

    @Test
    public void testLoginStatusMessage_Success() {
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Welcome Test User, it is great to see you again.";
        assertEquals(expected, user.loginStatusMessage(true));
    }

    @Test
    public void testLoginStatusMessage_Failure() {
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Username or password incorrect, please try again.";
        assertEquals(expected, user.loginStatusMessage(false));
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

/*
 * Unit tests for the QuickChatUser class – PROG5121 Part 2
 *
 * Title: How to code a very simple login system with Java
 * Author: Various contributors (Stack Overflow Community)
 * Date: 10 June 2013
 * Available: https://stackoverflow.com/questions/16627910/how-to-code-a-very-simple-login-system-with-java
 */
package com.mycompany.structumessage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuickChatUserTest {

    // Title: Java String contains() Method
    // Author: W3Schools
    // Date: 2025
    // Available: https://www.w3schools.com/java/ref_string_contains.asp
    @Test
    public void testCheckUserName_Valid() {
        // Test case: Valid username containing an underscore and length <= 5
        assertTrue(QuickChatUser.checkUserName("usr_1"));
    }

    // Title: Java String contains() Method
    // Author: W3Schools
    // Date: 2025
    // Available: https://www.w3schools.com/java/ref_string_contains.asp
    @Test
    public void testCheckUserName_Invalid() {
        // Test case: Invalid username without an underscore and length > 5
        assertFalse(QuickChatUser.checkUserName("invalidusername"));
    }

    // Title: Java Password Validation with Regular Expressions
    // Author: Baeldung
    // Date: 2025
    // Available: https://www.baeldung.com/java-regex-password-validation
    @Test
    public void testCheckPasswordComplexity_Valid() {
        // Test case: Valid password meeting all complexity requirements
        assertTrue(QuickChatUser.checkPasswordComplexity("Secure@123"));
    }

    // Title: Java Password Validation with Regular Expressions
    // Author: Baeldung
    // Date: 2025
    // Available: https://www.baeldung.com/java-regex-password-validation
    @Test
    public void testCheckPasswordComplexity_Invalid() {
        // Test case: Invalid password lacking required complexity
        assertFalse(QuickChatUser.checkPasswordComplexity("password"));
    }

    // Title: Java String startsWith() Method
    // Author: TutorialsPoint
    // Date: 2025
    // Available: https://www.tutorialspoint.com/java/java_string_startswith.htm
    @Test
    public void testCheckCellPhoneNumber_Valid() {
        // Test case: Valid South African phone number starting with +27 and length 12
        assertTrue(QuickChatUser.checkCellPhoneNumber("+27812345678"));
    }

    // Title: Java String startsWith() Method
    // Author: TutorialsPoint
    // Date: 2025
    // Available: https://www.tutorialspoint.com/java/java_string_startswith.htm
    @Test
    public void testCheckCellPhoneNumber_Invalid() {
        // Test case: Invalid phone number not starting with +27
        assertFalse(QuickChatUser.checkCellPhoneNumber("0812345678"));
    }

    @Test
    public void testRegister_Success() {
        // Test case: Successful registration with valid credentials
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Username and password successfully captured.\nCell phone number successfully added.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testRegister_InvalidUsername() {
        // Test case: Registration failure due to invalid username
        QuickChatUser user = new QuickChatUser("invalidusername", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testRegister_InvalidPassword() {
        // Test case: Registration failure due to invalid password
        QuickChatUser user = new QuickChatUser("usr_1", "weakpass", "+27812345678", "Test", "User");
        String expected = "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testRegister_InvalidCell() {
        // Test case: Registration failure due to invalid cellphone number
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "0812345678", "Test", "User");
        String expected = "Cell phone number incorrectly formatted or does not contain international code.";
        assertEquals(expected, user.register());
    }

    @Test
    public void testLogin_Success() {
        // Test case: Successful login with correct credentials
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        assertTrue(user.login("usr_1", "Secure@123"));
    }

    @Test
    public void testLogin_Failure() {
        // Test case: Failed login with incorrect credentials
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        assertFalse(user.login("wronguser", "wrongpass"));
    }

    @Test
    public void testLoginStatusMessage_Success() {
        // Test case: Login status message for successful login
        QuickChatUser user = new QuickChatUser("usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Welcome Test User, it is great to see you again.";
        assertEquals(expected, user.loginStatusMessage(true));
    }

    @Test
    public void testLoginStatusMessage_Failure() {
        // Test case: Login status message for failed login
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

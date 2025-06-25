package com.mycompany.structumessage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-test suite for {@link QuickChatUser}.
 * <p>
 * Coverage:
 * <ul>
 * <li>Username / password / cellphone validators</li>
 * <li>register() success & all failure paths</li>
 * <li>login() credential match</li>
 * <li>loginStatusMessage() wording</li>
 * </ul>
 *
 * Assessment context: PROG5121 – Part 3 (JUnit marks).
 *
 * @author Jorryn Panjasuran 2025
 */
/* ───────────────────────── Attribution Headers ─────────────────────────
   Title   : JUnit 5 Assertion Methods (assertTrue, assertFalse, assertEquals)
   Author  : JUnit Team – Official API
   Date    : 25 Jun 2025
   Source  : https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html :contentReference[oaicite:0]{index=0}
──────────────────────────────────────────────────────────────────────── */
public class QuickChatUserTest {

    /* ─────────── Username validation ─────────── */
    //  Title  : Username Regex “contains _ and ≤5 chars”
    //  Author : Stack Overflow answer 336210
    //  Date   : 25 Jun 2025
    //  Source : https://stackoverflow.com/questions/336210/regular-expression-for-alphanumeric-and-underscores :contentReference[oaicite:1]{index=1}
    /**
     * Username containing “_” and ≤5 chars must validate.
     */
    @Test
    public void testCheckUserName_Valid() {
        assertTrue(QuickChatUser.checkUserName("usr_1"));
    }

    /**
     * Long username without underscore should fail.
     */
    @Test
    public void testCheckUserName_Invalid() {
        assertFalse(QuickChatUser.checkUserName("invalidusername"));
    }

    /**
     * Null input must fail gracefully.
     */
    @Test
    public void testCheckUserName_Null() {
        assertFalse(QuickChatUser.checkUserName(null));
    }

    /* ─────────── Password complexity ─────────── */
    //  Title  : Password-Complexity Regex with Look-aheads
    //  Author : Stack Overflow answer 19605150
    //  Date   : 25 Jun 2025
    //  Source : https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a 
    /**
     * Meets ≥8 chars, 1 upper, 1 digit, 1 special.
     */
    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertTrue(QuickChatUser.checkPasswordComplexity("Secure@123"));
    }

    /**
     * Simple lowercase word must fail complexity check.
     */
    @Test
    public void testCheckPasswordComplexity_Invalid() {
        assertFalse(QuickChatUser.checkPasswordComplexity("password"));
    }

    /**
     * Null password should return false.
     */
    @Test
    public void testCheckPasswordComplexity_Null() {
        assertFalse(QuickChatUser.checkPasswordComplexity(null));
    }

    /* ─────────── Cell-phone validation ─────────── */
    //  Title  : South-African “+27” Phone Regex
    //  Author : Stack Overflow answer 33477950
    //  Date   : 25 Jun 2025
    //  Source : https://stackoverflow.com/questions/33477950/java-regex-phone-number
    /**
     * Correct +27 format should pass.
     */
    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertTrue(QuickChatUser.checkCellPhoneNumber("+27812345678"));
    }

    /**
     * Local format without +27 prefix must fail.
     */
    @Test
    public void testCheckCellPhoneNumber_Invalid() {
        assertFalse(QuickChatUser.checkCellPhoneNumber("0812345678"));
    }

    /**
     * Null cellphone should be invalid.
     */
    @Test
    public void testCheckCellPhoneNumber_Null() {
        assertFalse(QuickChatUser.checkCellPhoneNumber(null));
    }

    /* ─────────── register() workflow ─────────── */
    //  Title  : String.repeat(int) for test data
    //  Author : Oracle Java 11 API
    //  Date   : 25 Jun 2025
    //  Source : https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#repeat(int)
    /**
     * All fields valid → success message.
     */
    @Test
    public void testRegister_Success() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected
                = "Username and password successfully captured.\n"
                + "Cell phone number successfully added.";
        assertEquals(expected, user.register());
    }

    /**
     * Invalid username triggers correct error string.
     */
    @Test
    public void testRegister_InvalidUsername() {
        QuickChatUser user = new QuickChatUser(
                "invalidusername", "Secure@123", "+27812345678", "Test", "User");
        String expected
                = "Username is not correctly formatted.\n"
                + "It must contain an underscore (_) and be no more than five characters long.";
        assertEquals(expected, user.register());
    }

    /**
     * Weak password should return complexity error.
     */
    @Test
    public void testRegister_InvalidPassword() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "weakpass", "+27812345678", "Test", "User");
        String expected
                = "Password is not correctly formatted.\n"
                + "It must be at least eight characters long and include:\n"
                + "- A capital letter\n- A number\n- A special character";
        assertEquals(expected, user.register());
    }

    /**
     * Wrong phone format → phone-specific error message.
     */
    @Test
    public void testRegister_InvalidCell() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "Secure@123", "0812345678", "Test", "User");
        String expected
                = "Cell phone number is not correctly formatted.\n"
                + "It must start with +27 and be followed by exactly 9 digits.";
        assertEquals(expected, user.register());
    }

    /* ─────────── login() and status message ─────────── */
    /**
     * Correct credentials should authenticate.
     */
    @Test
    public void testLogin_Success() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "Secure@123", "+27812345678", "Test", "User");
        assertTrue(user.login("usr_1", "Secure@123"));
    }

    /**
     * Wrong credentials should fail authentication.
     */
    @Test
    public void testLogin_Failure() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "Secure@123", "+27812345678", "Test", "User");
        assertFalse(user.login("wronguser", "wrongpass"));
    }

    /**
     * loginStatusMessage(true) returns personalised greeting.
     */
    @Test
    public void testLoginStatusMessage_Success() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Welcome Test User, it is great to see you again.";
        assertEquals(expected, user.loginStatusMessage(true));
    }

    /**
     * loginStatusMessage(false) returns generic error.
     */
    @Test
    public void testLoginStatusMessage_Failure() {
        QuickChatUser user = new QuickChatUser(
                "usr_1", "Secure@123", "+27812345678", "Test", "User");
        String expected = "Username or password incorrect, please try again.";
        assertEquals(expected, user.loginStatusMessage(false));
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

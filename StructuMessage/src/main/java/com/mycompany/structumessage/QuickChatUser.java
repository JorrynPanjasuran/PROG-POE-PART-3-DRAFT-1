package com.mycompany.structumessage;

/**
 * Represents a registered user of the QuickChat application.
 * <p>
 * • Stores login credentials and basic profile info.<br>
 * • Provides static validators for username / password / cellphone format.<br>
 * • Offers <code>register()</code> and <code>login()</code> helpers that return
 * user-friendly feedback strings for the Swing UI.<br>
 *
 * Assessment context: PROG5121 – Part&nbsp;3 (validation + authentication
 * marks).
 *
 * @author Jorryn Panjasuran 2025
 */
public class QuickChatUser {

    /* ─────────── Immutable user fields ─────────── */
    private final String username;
    private final String password;
    private final String cellphone;
    private final String firstName;
    private final String lastName;

    /* ────────────────────────── Constructor ────────────────────────── */
    /**
     * Build a QuickChatUser after <em>client-side</em> validation has
     * succeeded. Raw strings are stored as-is; no hashing because POE scope is
     * local.
     */
    public QuickChatUser(String username, String password,
            String cellphone, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /* ───────────────── Static Validation Utilities ────────────────── */
    //  Title   : Username Regex “contains _ and ≤5 chars”
    //  Author  : Stack Overflow Q/336210; GeeksforGeeks tutorial
    //  Date    : 25 Jun 2025
    //  Sources :
    //    • https://stackoverflow.com/questions/336210/regular-expression-for-alphanumeric-and-underscores 
    //    • https://www.geeksforgeeks.org/how-to-validate-a-username-using-regular-expressions-in-java/ 
    //
    /**
     * Username must contain “_” and be ≤ 5 chars (positive look-ahead).
     */
    public static boolean checkUserName(String username) {
        return username != null && username.matches("^(?=.*_).{1,5}$");
    }

    //  Title   : Password-Complexity Regex with Look-aheads
    //  Author  : Stack Overflow Q/19605150, Q/12090077
    //  Date    : 25 Jun 2025
    //  Sources :
    //    • https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a :contentReference[oaicite:2]{index=2}
    //    • https://stackoverflow.com/questions/12090077/javascript-regular-expression-password-validation-having-special-characters :contentReference[oaicite:3]{index=3}
    /**
     * Password complexity: ≥ 8 chars, 1 uppercase, 1 digit, 1 special.
     * Implemented with positive look-ahead assertions.
     */
    /**
     * Password complexity: ≥8 chars, 1 uppercase, 1 digit, 1 special. Uses
     * positive look-aheads for each required character group.
     */
    //  Title   : South-African “+27” Cell-Number Regex
    //  Author  : Stack Overflow Q/33477950; validate.js issue #235
    //  Date    : 25 Jun 2025
    //  Sources :
    //    • https://stackoverflow.com/questions/33477950/java-regex-phone-number :contentReference[oaicite:4]{index=4}
    //    • https://github.com/ansman/validate.js/issues/235 :contentReference[oaicite:5]{index=5}
    //
    public static boolean checkPasswordComplexity(String password) {
        return password != null
                && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
    }

    /**
     * Cell number must follow South-African E.164 <code>+27XXXXXXXXX</code>.
     */
    public static boolean checkCellPhoneNumber(String cellphone) {
        return cellphone != null && cellphone.matches("^\\+27\\d{9}$");
    }

    /* ─────────────────── Registration & Login ─────────────────────── */
    /**
     * Validate all fields and return a human-readable outcome string. This
     * method is called <em>after</em> dialog prompts collect data.
     */
    public String register() {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted.\n"
                    + "It must contain an underscore (_) and be no more than five characters long.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted.\n"
                    + "It must be at least eight characters long and include:\n"
                    + "- A capital letter\n- A number\n- A special character";
        }

        if (!checkCellPhoneNumber(cellphone)) {
            return "Cell phone number is not correctly formatted.\n"
                    + "It must start with +27 and be followed by exactly 9 digits.";
        }

        return "Username and password successfully captured.\n"
                + "Cell phone number successfully added.";
    }

    /**
     * Simple credential match – no hashing because POE runs offline.
     */
    public boolean login(String inputUsername, String inputPassword) {
        return username.equals(inputUsername) && password.equals(inputPassword);
    }

    /**
     * Friendly welcome vs. error message based on login outcome.
     */
    public String loginStatusMessage(boolean loginStatus) {
        return loginStatus
                ? "Welcome " + firstName + " " + lastName + ", it is great to see you again."
                : "Username or password incorrect, please try again.";
    }

    /* ───────────────────── Getters (encapsulated) ─────────────────── */
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellphone() {
        return cellphone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

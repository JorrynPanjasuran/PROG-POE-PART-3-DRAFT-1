package com.mycompany.structumessage;

public class QuickChatUser {

    // Private fields to store user information
    private String username;
    private String password;
    private String cellphone;
    private String firstName;
    private String lastName;

    // Constructor to initialize a new QuickChatUser object with provided details
    public QuickChatUser(String username, String password, String cellphone, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellphone = cellphone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // === Username Check ===
    // Method to validate the username format using regex
    // Criteria: Must contain an underscore and be no more than 5 characters long
    public static boolean checkUserName(String username) {
        return username.matches("^(?=.*_).{1,5}$");
    }

    // === Password Complexity Check ===
    // Method to validate the password complexity using regex
    // Criteria: At least 8 characters, includes uppercase letter, digit, and special character
    public static boolean checkPasswordComplexity(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
    }

    // === Cellphone Number Check ===
    // Method to validate the cellphone number using regex
    // Criteria: Must start with +27 and be followed by exactly 9 digits
    public static boolean checkCellPhoneNumber(String cellphone) {
        return cellphone.matches("^\\+27\\d{9}$");
    }

    // === Registration Result Message ===
    // Method to return a message based on the validation of username, password, and cellphone number
    public String register() {
        if (!checkUserName(this.username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(this.password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(this.cellphone)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        return "Username and password successfully captured.\nCell phone number successfully added.";
    }

    // === Login Authentication ===
    // Method to authenticate user login by comparing input credentials with stored credentials
    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }

    // === Login Status Message ===
    // Method to return a welcome message if login is successful or an error message if not
    public String loginStatusMessage(boolean loginStatus) {
        if (loginStatus) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // === Getters (optional if needed) ===
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

// Title: StructuMessage Application – QuickChatUser Class
// Author: Oracle, Stack Overflow, W3Schools, GeeksforGeeks, The IIE / Rochelle Moodley
// Date: 26 May 2025
// Version: 1.0
// References maintained as previously documented


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

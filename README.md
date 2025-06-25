# QuickChat – Java 17 Swing Messaging Demo

QuickChat is a small desktop program written with **plain Java and Swing**.  
It lets a single user — for example your marker or a classmate — register, log in, and move short text messages between three simple states:

| State | Meaning |
|-------|---------|
| **Sent** | Message has been “sent”. |
| **Stored** | Message is saved as a draft to send later. |
| **Disregarded** | Message is binned and no longer shown in reports. |

Everything happens in pop-up windows made with `JOptionPane`, so the app runs on any PC with Java 17 installed.

---

## ⭐ Main things you can do

* **Register** – Checks  
  * username has an **underscore** and is at most **5** characters,  
  * password is at least 8 chars with a capital, a number and a special symbol,  
  * phone number starts with **+27** and has exactly 9 digits.  
* **Log in** – Matches what you just registered.
* **Send / Store / Discard** a message – Choose what to do after typing the text.
* **Reports menu** –  
  * list all recipients,  
  * show the longest message,  
  * search by ID or by recipient,  
  * delete a message by its hash,  
  * print a full “Sent” report.
* **Load demo data** – Adds five sample messages so the reports show something even if you do not type.

---


---

## 📂 Folder layout


Title and Purpose: Spikes Scheduler, Scheduling application that allows the user to manage customers, their appointments, and generate reports.

Author: Russell Spikes
Contact Information: rspike3@wgu.edu 702-470-6230
Application Version and Date: 3 | 10/25/2023

IntelliJ IDEA 2023.2.2 (Community Edition)
Build #IC-232.9921.47, built on September 12, 2023
Runtime version: 17.0.8+7-b1000.22 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Windows 10.0
GC: G1 Young Generation, G1 Old Generation
Memory: 2048M
Cores: 4
Registry:
    ide.experimental.ui=true


Kotlin: 232-1.9.0-IJ9921.47

JDK Version: Java SE Development Kit 17, Update 17.0.9
Java FX version : JavaFX-SDK 17.0.6+3

How to run program: 
Step 1: Open Intellij Idea 2023.2.2 from the desktop.
Step 2: Open project Spikes Scheduler from downloads if it didn't start opened.

Additional Report: Created an additional report that breaks down the countries in the database and the divisions that fall under those countries.
If the application were expanded the report could include the number of customers by country, appointmnets by country or division, etc.


MySQL Connector driver version number: mysql-connector-java-8.0.25


*Bug fixes*
+Program will not automatically delete customer appointments in the database before deleting the customer to maintain referential integrity.

+Program will now show appointments for the current month, when the Month radial button is clicked.

+Program will now show appointments for the current week, when the Week radial button is clicked.

+Program now displays two kinds of alert for appointments. One is a pop-up message showing if there is an appointment within 15 minute, or a message that displays that there are no upcoming appointments within 15 minutes.

+Second alert will show above the appointments themselves as a label that is updated.

+Program uses Lambda interfaces and inline lambda expressions to improve the code.

+Date and time lambda interface expression was added to handle appointment time checking and returns a boolean. This simplifies the code and enhances its ability to handle time checking for not just appointments but other time objects in the future.

+Login activity lambda interface expression was added to handle writes to the login file and can be utlized wherever an update needs to be done.

+Other inline lambdas are explained in the Javadoc index.html file that has been included.

*Content updates*

+README file has been updated to include JDK version and JavaFX version 

+README file has been updated to include bug fixes and content updates.


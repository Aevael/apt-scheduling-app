Title: Scheduling Application

Purpose: This application allows for scheduling for customer appointments. Customers appointments, and location data are stored in a local database. The application includes input validation (including time checks), localization of dates/times according to system location, as well as database queries and updates implemented through a JDBC connection to a MySQL database.

Author: Alexandre Do

Contact info: alexdo167@gmail.com

Application Version: 1.1
Changelog 1.0 -> 1.1: updated IDE, JDK, JavaFX and MySQL Connector/J versions. Added detailed instructions to run program.

Date: 06/02/2025

VERSIONS
IDE: IntelliJ IDEA 2025.1.1.1 (Community Edition)
JDK: Microsoft OpenJDK 17.0.15
JavaFX: JavaFX 17.0.15[LTS]
MySQL Connector/J: MySQL Connector J 9.3.0

INSTRUCTIONS:
DOWNLOAD LINKS FOUND AT BOTTOM


LOCAL MYSQL DATABASE SETUP

Setting up the local database on Windows:
Download and install MySQL Community Server.
During installation, when prompted to provide a password for the "root" account, put "password".
A different password can be chosen, but doing so will require updating the password string at
src/main/java/scheduling/schedulingapplication/Model/JDBC.java line 16 to the new password to ensure the database connection works.
To start the server, launch Services (can be found by searching in the Windows search bar).
Scroll down to MySQL (this will have a number next to it related to the version, such as MySQL93 for version 9.3.0).
Press on the MySQL service, then press Start to start the service if it is not already running.
Download, install and launch MySQL Workbench.
Select the Local instance with the username "root" and port localhost:3306.
Input the password created earlier.
In the Query tab that opens, copy and paste the query found in C195_db_ddl.txt and execute the query by pressing the simple
lightning button directly to the right of the save button.
Erase all of the query text, then copy and paste the query found in C195_db_dml.txt and execute the query.
The initial data for the program has now been created, and the database is ready for the program to use.

Note: if the username or port number differ (these can be seen when selecting the local server instance), you will need to update the respective strings
found in src/main/java/scheduling/schedulingapplication/Model/JDBC.java (line 11 for portnumber and line 15 for username) for the database connection to work.

Setting up the local database on macOS:
Download and install MySQL Community Server.
During installation, when prompted to provide a password for the "root" username, put "password".
A different password can be chosen, but doing so will require updating the password string at
src/main/java/scheduling/schedulingapplication/Model/JDBC.java line 16 to the new password to ensure the database connection works.
To start the server, go into System Settings, scroll down the sidebar on the left to MySQL,
then click "Start MySQL Server" if the server is not already running (red symbol indicates the server is not running).
If MySql is not in the sidebar, try closing and reopening the System Settings.
Download, install and launch MySQL Workbench (incompatability warnings may pop up, but the program will work fine).
Select the Local instance with the username "root" and port localhost:3306.
Input the password created earlier.
In the Query tab that opens, copy and paste the query found in C195_db_ddl.txt and execute the query by pressing the simple
lightning button directly to the right of the save button.
Erase all of the query text, then copy and paste the query found in C195_db_dml.txt and execute the query.
The initial data for the program has now been created, and the database is ready for the program to use.

Note: if the username or port number differ (these can be seen when selecting the local server instance), you will need to update the respective strings
found in src/main/java/scheduling/schedulingapplication/Model/JDBC.java (line 11 for portnumber and line 15 for username) for the database connection to work.


APPLICATION SETUP

To open the project, download, install and lauch Intellij, select "Clone Repository" and clone the repository from Github using the repository link.
Press on the gear icon at the top right of Intellij, then select Project Structure.
Select Project under Project Settings, then in the SDK dropdown menu, click "Download JDK...".
Select Version 17 and Vendor Microsoft OpenJDK 17.0.15, then press download.
Click Apply, then click OK.
Note: This may create a second copy of SDK 17 named ms-17(2). This second copy can be removed for cleanliness by clicking on "SDKs" under "Platform Settings" in the Project Structure window.
From there, right click ms-17(2) and click delete. Go back to "Project" under "Project Settings" then set the SDK to "ms-17" and apply.
If imports from within the project are unable to resolve, right click the src folder, select "Mark Directory as" and select "Sources Root".
Note: If you already have Microsoft OpenJDK 17.0.15, then there is no need to download it. It also may be possible to use another version of JDK 17,
but Microsoft OpenJDK 17.0.15 is the version this program was tested with.

Download JavaFX 17.0.15 and extract the contents, resulting in a javafx-sdk-17.0.15 folder.
Press on the gear icon at the top right of Intellij and select "Settings...".
Under Appearance and Behavior, select Path Variables, then click the + button to add a variable.
Name the variable "PATH_TO_FX".
Set the value to the lib folder within the javafx-sdk-17.0.15 folder that was extracted earlier
(the proper directory address will end with \javafx-sdk-17.0.15\lib ). Make sure to apply the changes.

Press on the gear icon at the top right of Intellij and select "Project Structure". Under "Project Settings" select "Libraries".
Click the + button above the list of libraries on the left side labeled "New Project Library" and select "Java" from the dropdown menu that appears.
Select the lib folder within the javafx-sdk-17.0.15 folder that was extracted earlier (the proper directory address will end with \javafx-sdk-17.0.15\lib ). Make sure to apply the changes.

Download MySQL Connector/J and extract the contents, resulting in a "mysql-connector-j-9.3.0" folder (for version 9.3.0).
Press on the gear icon at the top right of Intellij and select "Project Structure". Under "Project Settings" select "Libraries".
Click the + button above the list of libraries on the left side labeled "New Project Library" and select "Java" from the dropdown menu that appears.
Select the folder that was extracted earlier (ex: \mysql-connector-j-9.3.0). Click OK on all of the following prompts, and make sure to apply the changes.


At the top of the Intellij window, click on the "Current File" dropdown menu and select "Edit Configurations...".
Click the + button at the top left of the Run/Debug Configurations window that opens, and select "Application".
In the "Name" field, replace "Unnamed" with "SchedulingApplication".
Click on the "Modify options" dropdown menu, and select "Add VM options" under "Java".
In the "VM options" field that was added, input "--module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics".
In the "Main Class" field, select browse on the right side of the text box, and select "SchedulingApplication" (this should be the only option).
The "Main Class" field should now contain "scheduling.schedulingapplication.SchedulingApplication" (you can also choose to type this into the field).
Make sure to apply the changes.

The program can now be run by clicking the green play/run button at the top of the Intellij window (next to the SchedulingApplication dropdown menu).


DOWNLOAD LINKS

Download MySQL Community Server: https://dev.mysql.com/downloads/mysql/
This program has been tested on MySQL Community Server versions:
MySQL Community Server v9.3.0 Innovation Windows (x86, 64-bit), MSI Installer
MySQL Community Server v9.3.0 Innovation macOS 15 (ARM, 64-bit), DMG Archive
MySQL Community Server v8.0.42 macOS 15 (ARM, 64-bit), DMG Archive

Download MySQL Workbench: https://dev.mysql.com/downloads/workbench/
This program has been tested on MySQL Workbench versions:
MySQL Workbench v8.0.42 Windows (x86, 64-bit), MSI Installer
MySQL Workbench v8.0.42 macOS (ARM, 64-bit), DMG Archive

Download Intellij IDEA Community Edition: www.jetbrains.com/idea/download/

Download JavaFX: https://gluonhq.com/products/javafx/
This program has been tested using versions:
JavaFX v17.0.15[LTS] Windows x64 SDK
JavaFX v17.0.15[LTS] macOS aarch64 SDK

Download MySQL Connector/J: https://dev.mysql.com/downloads/connector/j/
This program has been tested using version:
MySQL Connector/J v9.3.0 Platform Independent (Architecture Independent), ZIP Archive

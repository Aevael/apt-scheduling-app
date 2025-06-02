Title: Scheduling Application

Purpose: This application allows for scheduling for customer appointments. Customers appointments, and location data are stored in a database. The application includes input validation (including time checks), localization of dates/times according to system location, as well as database queries and updates implemented through a JDBC connection to a MySQL database.

Author: Alexandre Do

Contact info: alexdo167@gmail.com

Application Version: 1.0

Date: 12/31/2023

IDE: IntelliJ IDEA 2023.2.2 (Community Edition)
JDK: Java SE 17.0.8
SDK: JavaFX-SDK-17.0.8
MySQL Connector driver version: mysql-connector-j-8.2.0


To run program:
Run program in IntelliJ 
The program requires setting up a local client side MySQL database.
This can be done by downloading MySQL and running on the database the scripts in the files "C195_db_ddl.txt" and "C195_db_dml.txt" found in the root folder.
The application requires a SQL connection driver to connect to the database.
Make sure to install one and add it to the library of the project (in project structure), then add the connector library to dependencies.
The run configuration should be set up, if not then run the application from the SchedulingApplication file in the src/main/java/scheduling/schedulingapplication folder.
Make to sure set PATH_TO_FX to javafx-sdk 17 lib (in File- Settings- Path Variables, add variable with name "PATH_TO_FX" and set value to javafx-sdk 17 lib folder).
Make sure to add javafx-sdk 17 "lib" directory to Project Libraries (in File- Project Structure - Libraries, add the javafx-sdk 17 lib folder).
Make sure VM options of run configuration is set to: --module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics

Additional report: The additional report that was implemented is a report on how many appointments each customer has, as well as a schedule of their appointments.

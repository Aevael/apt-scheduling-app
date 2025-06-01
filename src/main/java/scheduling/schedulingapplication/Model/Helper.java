/** @author Alexandre Do */
package scheduling.schedulingapplication.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.EventObject;
import java.util.function.Function;

import static scheduling.schedulingapplication.Controller.CustomersController.allCustomers;
import static scheduling.schedulingapplication.Controller.MainController.allAppointments;
import static scheduling.schedulingapplication.Controller.MainController.filteredAppointments;
import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Helper class with static helper methods meant to reduce code redundancy, shorten code and improve code readability. */
public class Helper {

    /** This static DateTimeFormatter is used to format a time object into string of a standard Hour/Minute AM/PM format. */
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
    /** This static DateTimeFormatter is used to format a time object into a string that may be passed into TimeStamp's valueOf method. */
    public static DateTimeFormatter instantToTimeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /** This static DateTimeFormatter is used to format a time object into a string of standard American date format. */
    public static DateTimeFormatter displayFormatterDate = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    /** This static DateTimeFormatter is used to format a time object into a string of Hour/Minute AM/PM format, with hours going up to 23 hours for clarity when displaying. */
    public static DateTimeFormatter displayFormatterTime = DateTimeFormatter.ofPattern("HH:mm a");

    /** This method displays an error alert.
     * This method creates an error alert using the argument strings.
     * @param title The error's title.
     * @param message The error's message.
     */
    public static void error(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** This method creates a new scene and directs the program to it.
     * This method uses the parameters to create a new scene and direct to it while centering the screen.
     * @param c The class that contains the calling action event, the parent of the new scene.
     * @param resource The reference path of the FXML file to be loaded and displayed.
     * @param windowWidth The width of the new scene.
     * @param windowHeight The height of the new scene.
     * @param title The title of the new scene.
     * @param actionEvent The calling action event.
     */
    public static void direct(Class c, String resource, int windowWidth, int windowHeight, String title, EventObject actionEvent) {
        try {
            Parent root = FXMLLoader.load(c.getResource(resource));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, windowWidth, windowHeight);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /** This method returns the next available customer ID.
     * The method acquires all customer IDs from the database, adding them to a list. The method then sorts the list and loops through the IDs, returning the first missing ID to be recycled if one is found. If none is found, the method returns a new ID equal to the list size + one.
     * @return Returns the next available customer ID, otherwise returns -1 if a SQL error occurs.
     */
    public static int getNextCustomerID() {
        try {
            /* Get current IDs  and add to list */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID FROM client_schedule.customers");
            ObservableList<Integer> ids = FXCollections.observableArrayList();
            while (rs.next()) {
                ids.add(rs.getInt("Customer_ID"));
            }
            /* Sort and loop through IDs, returning a missing ID for recycling if one is found */
            FXCollections.sort(ids);
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i) > (i + 1)) {
                    return (i + 1);
                }
            }
            /* If no missing ID for recycling, return a new ID */
            return (ids.size() + 1);
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return -1 if SQL error */
        return -1;
    }

    /** This method returns the next available appointment ID.
     * The method acquires all appointment IDs from the database, adding them to a list. The method then sorts the list and loops through the IDs, returning the first missing ID to be recycled if one is found. If none is found, the method returns a new ID equal to the list size + one.
     * @return Returns the next available appointment ID, otherwise returns -1 if a SQL error occurs.
     */
    public static int getNextAppointmentID() {
        try {
            /* Get current IDs  and add to list */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Appointment_ID FROM client_schedule.appointments");
            ObservableList<Integer> ids = FXCollections.observableArrayList();
            while (rs.next()) {
                ids.add(rs.getInt("Appointment_ID"));
            }
            /* Sort and loop through IDs, returning a missing ID for recycling if one is found */
            FXCollections.sort(ids);
            for (int i = 0; i < ids.size(); i++) {
                if (ids.get(i) > (i + 1)) {
                    return (i + 1);
                }
            }
            /* If no missing ID for recycling, return a new ID */
            return (ids.size() + 1);
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return -1 if SQL error */
        return -1;
    }

    /** This method takes a division's name as an argument and returns the corresponding division's ID from the database.
     * The method uses a JDBC query to get the corresponding division ID, and then returns it as an int.
     * @param division The division's name.
     * @return Returns the corresponding division ID from the database, otherwise returns -1 if invalid division name or if a SQL error occurs.
     */
    public static int getDivisionID(String division) {
        try {
            /* Get the corresponding division ID from the database and return it */
            Statement stmt = connection.createStatement();
            String query = String.format("SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division ='%s'", division);
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            return rs.getInt("Division_ID");

        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return -1 if SQL error */
        return -1;
    }

    /** This method takes a division's ID as an argument and returns the corresponding division's name from the database.
     * The method uses a JDBC query to get the corresponding division name, and then returns it as a String.
     * @param divisionID The division's ID.
     * @return Returns the corresponding division's name from the database, otherwise returns null if invalid division ID or if a SQL error occurs.
     */
    public static String getDivision(int divisionID) {
        try {
            /* Get the corresponding division name from the database and return it */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Division FROM client_schedule.first_level_divisions WHERE Division_ID='%s'", divisionID));
            rs.next();
            return rs.getString("Division");
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return null if SQL error */
        return null;
    }

    /** This method takes a country's ID as an argument and returns a list of all the names of the country's divisions.
     * The method uses a JDBC query to get all the names of the country's divisions, then loops through the ResultSet to add them to a list and returns the list.
     * @param countryID The country's ID.
     * @return Returns a list of all the names of the country's divisions, otherwise returns null if invalid country ID or if a SQL error occurs.
     */
    public static ObservableList<String> getCountryDivisions(int countryID) {
        try {
            /* Get all division names from database */
            ObservableList<String> divisions = FXCollections.observableArrayList();
            Statement stmt = connection.createStatement();
            String query = String.format("SELECT division FROM client_schedule.first_level_divisions WHERE Country_ID =%s", countryID);
            ResultSet rs = stmt.executeQuery(query);
            /* Add division names to list and return list */
            while (rs.next()) {
                divisions.add(rs.getString("division"));
            }
            return divisions;
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return null if SQL error */
        return null;
    }

    /** This method takes a division's ID as a parameter and returns the name of the division's country.
     * The method uses a JDBC query to get the corresponding country ID from the database, then returns the country's name as a String based on the country ID.
     * @param divisionID The division's ID.
     * @return Returns the name of the division's country, otherwise returns null if invalid division ID, or if a SQL error occurs.
     */
    public static String getCountryFromDivisionID(int divisionID) {
        try {
            /* Get division's country id from first level divisions table */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Country_ID FROM client_schedule.first_level_divisions WHERE Division_ID ='%s'", divisionID));
            /* Return the country name depending on the division's country ID */
            rs.next();
            String countryID = String.valueOf(rs.getInt("Country_ID"));
            switch (countryID) {
                case "1" -> {
                    return "United States";
                }
                case "2" -> {
                    return "United Kingdom";
                }
                case "3" -> {
                    return "Canada";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return null if SQL error */
        return null;
    }

    /** This method takes a country's name as a parameter and returns the country's ID.
     * The method takes the country's name and then returns the corresponding country ID as an integer.
     * @param country The country's name.
     * @return Returns the country's ID, otherwise returns -1 if invalid country name.
     */
    public static int getCountryID(String country) {
        /* Return ID based on country name */
        switch (country) {
            case "United States" -> {
                return 1;
            }
            case "United Kingdom" -> {
                return 2;
            }
            case "Canada" -> {
                return 3;
            }
        }
        /* Return -1 if no matching case error */
        return -1;
    }

    /** This method populates a ComboBox with a country's divisions.
     *  The method takes a ComboBox of Strings and a country's name as arguments. The method gets the country's ID using the Helper getCountryID method and then passes that ID into the Helper getCountryDivisions method to create a list of all the names of the country's divisions.
     *  The method then sets the argument ComboBox's items to the list.
     * @param box The ComboBox to be populated with the country's divisions.
     * @param country The name of the country whose division names are to be set as the ComboBox's items.
     */
    public static void populateDivisions(ComboBox<String> box, String country) {
        /* Create list and set box's items to the list */
        ObservableList<String> divisionsList = Helper.getCountryDivisions(Helper.getCountryID(country));
        box.setItems(divisionsList);
    }

    /** This method checks whether a TextField is empty.
     * The method gets the TextField's text as a String, then checks whether that String is empty.
     * @param textField The TextField to be checked.
     * @return Returns true if TextField is not empty, returns false if TextField is empty.
     */
    public static boolean validateTextField(TextField textField) {
        /* Return true if TextField is not empty, return false if TextField is empty */
        return !textField.getText().isEmpty();
    }

    /** This method takes a contact's ID as an argument and returns the corresponding contact's name.
     * The method uses a JDBC query to get the corresponding contact's name from the database, and then returns it as a String.
     * @param contactID The contact's ID.
     * @return Returns the corresponding contact's name, otherwise returns null if no invalid contact ID or if a SQL error occurs.
     */
    public static String getContactName(int contactID) {
        try {
            /* Get corresponding contact name from database and return it */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Contact_Name FROM client_schedule.contacts WHERE Contact_ID='%s'", contactID));
            rs.next();
            return rs.getString("Contact_Name");

        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return null if SQL error */
        return null;
    }

    /** This method takes a contact's name as an argument and returns the corresponding contact's ID.
     * The method uses a JDBC query to get the corresponding contact's ID and then returns it as an int.
     * @param contactName The contact's name.
     * @return Returns the corresponding contact's ID, otherwise returns -1 if no invalid contact name or if a SQL error occurs.
     */
    public static int getContactID(String contactName) {
        try {
            /* Get corresponding contact ID from database and return it */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Contact_ID FROM client_schedule.contacts WHERE Contact_Name='%s'", contactName));
            rs.next();
            return rs.getInt("Contact_ID");
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return -1 if SQL error */
        return -1;
    }

    /** This method returns a list of all contacts' names.
     * The method uses a JDBC query to get all contacts' names from the database, then loops through the ResultSet and adds each name to a list. The method then returns the list.
     * @return Returns a list of all contacts' names
     */
    public static ObservableList<String> getAllContacts() {
        /* Get all contacts' names from database and add them to list */
        ObservableList<String> contacts = FXCollections.observableArrayList();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Contact_Name FROM client_schedule.contacts");
            while (rs.next()) {
                contacts.add(rs.getString("Contact_Name"));
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return list */
        return contacts;
    }

    /** This method takes as arguments a LocalDate and a String representing time in a timeFormatter format and creates a corresponding ZonedDateTime in local time zone.
     * The method creates a LocalTime from the time argument String using the timeFormatter DateTimeFormatter, then creates a local time zone ZonedDateTime from the new LocalTime and argument LocalDate. The method then returns this new ZonedDateTime.
     * @param date The date to be turned into a ZonedDateTime.
     * @param time The time to be turned into a ZonedDateTime.
     * @return Returns a local time zone ZonedDateTime object created from the arguments.
     */
    public static ZonedDateTime createZonedDateTime(LocalDate date, String time) {
        /* Create LocalTime from string using timeFormatter */
        LocalTime newTime = LocalTime.parse(time, timeFormatter);
        /* Create a ZonedDateTime with the local time zone from LocalDate and Localtime */
        return ZonedDateTime.of(date, newTime, ZoneId.systemDefault());
    }

    /** This method takes a ZonedDateTime as an argument and returns a corresponding TimeStamp in UTC time zone.
     * The method converts the argument ZonedDateTime to UTC and uses the instantToTimeStamp DateTimeFormatter to turn it into a string format that can be parsed by TimeStamp's valueOf method. The method then passes the string into Timestamp's valueOf method to turn it into a Timestamp and return it.
     * @param dateTime The ZonedDateTime to be turned into a UTC Timestamp.
     * @return Returns a Timestamp corresponding to the ZonedDateTime in UTC.
     */
    public static Timestamp convertToTimeStampUTC(ZonedDateTime dateTime) {
        /* Convert ZonedDateTime to UTC and format it using instantToTimeStamp into a string format that can be parsed by TimeStamp's valueOf method */
        String formatted = dateTime.withZoneSameInstant(ZoneId.of("UTC")).format(instantToTimeStamp);
        /* Create Timestamp from string and return it */
        return Timestamp.valueOf(formatted);
    }

    /** This method takes a user's ID as an argument and returns true if the user's ID exists within the database, or false if the user's ID does not exist within the database.
     * The method uses a JDBC connection query to get all user IDs from the database, then loops through the ResultSet to check whether the argument user ID exists.
     * @param userID The user ID whose existence is to be checked.
     * @return Returns true if the user's ID exists within the database, returns false if the user's ID does not exist within the database.
     */
    public static boolean validUserID(int userID) {
        try {
            /* Get all IDs from the database */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT User_ID FROM client_schedule.users");
            /* Return true if argument ID matches any ID */
            while (rs.next()) {
                if (rs.getInt("User_ID") == userID) {
                    return true;
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return false if no ID matches */
        return false;
    }

    /** This method takes a customer's ID as an argument and returns true if the customer's ID exists within the database, or false if the customer's ID does not exist within the database.
     * The method uses a JDBC connection query to get all customer IDs from the database, then loops through the ResultSet to check whether the argument customer ID exists.
     * @param customerID The customer ID whose existence is to be checked.
     * @return Returns true if the customer's ID exists within the database, returns false if the customer's ID does not exist within the database.
     */
    public static boolean validCustomerID(int customerID) {
        try {
            /* Get all IDs from the database */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID FROM client_schedule.customers");
            /* Return true if argument ID matches any ID */
            while (rs.next()) {
                if (rs.getInt("Customer_ID") == customerID) {
                    return true;
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return false if no ID matches */
        return false;
    }

    /** This method checks whether a customer has any appointments for Foreign Key restriction purposes.
     * The method uses a JDBC connection query to get all appointments' customer IDs, then loops through the ResultSet to check whether any appointment's customer ID matches the argument customer ID.
     * @param customerID The customer's ID.
     * @return Returns true if customer has no appointments, returns false if customer has any appointment.
     */
    public static boolean customerFKCheck(int customerID) {
        try {
            /* Get all appointments' customer IDs */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID FROM client_schedule.appointments");
            /* Return false if any customer ID matches argument */
            while (rs.next()) {
                if (rs.getInt("Customer_ID") == customerID) {
                    return false;
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return true if no match */
        return true;
    }

    /** This method sets the static filteredAppointments list's items to all appointments within the current week.
     * The method first clears filteredAppointments, then uses the current date/time to get the first and last day of the current week, then loops through the static allAppointments list to add to filteredAppointments any appointment within the current week.
     */
    public static void filterByCurrentWeek() {
        /* Clear static list */
        filteredAppointments.clear();
        /* Get first and last day of current week */
        ZonedDateTime today = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime firstDayOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime lastDayOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).withHour(23).withMinute(59).withSecond(59);
        /* Add to static list any appointment that falls between first and last day of current week */
        for (Appointment apt : allAppointments) {
            if (!apt.getStartDateTime().isBefore(firstDayOfWeek) && !apt.getEndDateTime().isAfter(lastDayOfWeek)) {
                filteredAppointments.add(apt);
            }
        }
    }

    /** This method sets the static filteredAppointments list's items to all appointments within the current month.
     * The method first clears filteredAppointments, then uses the current date/time to get the first and last day of the current month, then loops through the static allAppointments list to add to filteredAppointments any appointment within the current month.
     */
    public static void filterByCurrentMonth() {
        /* Clear static list */
        filteredAppointments.clear();
        /* Get first and last day of current month */
        ZonedDateTime today = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        /* Add to static list any appointment that falls between first and last day of current month */
        for ( Appointment apt : allAppointments) {
            if (!apt.getStartDateTime().isBefore(firstDayOfMonth) && !apt.getEndDateTime().isAfter(lastDayOfMonth)) {
                filteredAppointments.add(apt);
            }
        }
    }

    /** This method takes an appointment ZonedDateTime as an argument and checks whether it is within business hours (8AM-10PM ET Monday-Friday).
     * The method converts the appointment time to ET, then checks whether it is during a weekend or outside business hours.
     * @param appointment The appointment time to be checked.
     * @return Returns true if it is within ET business hours, returns false if it is outside ET business hours.
     */
    public static boolean inBusinessHours(ZonedDateTime appointment) {
        /* Convert appointment time to ET time zone */
        ZonedDateTime appointmentTimeET = appointment.withZoneSameInstant(ZoneId.of("America/New_York"));
        /* Return false if appointment time is during a weekend */
        if (appointmentTimeET.getDayOfWeek() == DayOfWeek.SATURDAY || appointmentTimeET.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return false;
        }
        /* Return true if appointment is within ET business hours, return false if it is outside ET business hours */
        else return appointmentTimeET.getHour() >= 8 && appointmentTimeET.getHour() <= 22;
    }

    /** This method takes an appointment's start and end times, as well as a customer's ID as arguments and checks whether the customer already has an appointment within the time frame.
     * The method uses a JDBC connection to get the start and end time of all the customer's appointments from the database. The method then loops though the appointments, converting each appointment's start and end time to ZonedDateTimes at local time zone and checking for conflict with the argument appointment times while ensuring that the appointment being compared is not the same appointment as the one being updated.
     * @param customerID The ID of the customer whose availability is to be checked.
     * @param newStart The proposed appointment's start time.
     * @param newEnd The proposed appointment's end time.
     * @return Returns true if customer is available within that time frame, returns false if customer is not available within that time frame.
     */
    public static boolean customerAvailable(int customerID, int appointmentID, ZonedDateTime newStart, ZonedDateTime newEnd) {
        try {
            /* Get start and end time of all the customer's appointments from database */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Appointment_ID, Start, End FROM client_schedule.appointments WHERE Customer_ID ='%s'", customerID));
            while (rs.next()) {
                /* Convert each appointment's start and end time to ZonedDateTimes at local time zone */
                ZonedDateTime oldStart = ZonedDateTime.ofInstant(rs.getTimestamp("Start").toInstant(), ZoneId.systemDefault());
                ZonedDateTime oldEnd = ZonedDateTime.ofInstant(rs.getTimestamp("End").toInstant(), ZoneId.systemDefault());
                int currentDBAppointment = rs.getInt("Appointment_ID");
                /* Check if appointment being compared is the same appointment as is being updated */
                if (currentDBAppointment != appointmentID) {
                    /* Return false if proposed appointment end time is after existing appointment's start time and is not after existing appointment's end time*/
                    if (newEnd.isAfter(oldStart) && !newEnd.isAfter(oldEnd)) {
                        return false;
                    }
                    /* Return false if proposed appointment end time is after existing appointment's end time and its start time is not after or equal to existing appointment's end time */
                    else if (newEnd.isAfter(oldEnd) && newStart.isBefore(oldEnd)) {
                        return false;
                    }
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return true if no time conflict */
        return true;
    }

    /** This method takes an appointment's start and end times, as well as a contact's ID as arguments and checks whether the contact already has an appointment within the time frame.
     * The method uses a JDBC connection to get the start and end time of all the contact's appointments from the database. The method then loops though the appointments, converting each appointment's start and end time to ZonedDateTimes at local time zone and checking for conflict with the argument appointment times while ensuring that the appointment being compared is not the same appointment as the one being updated.
     * @param contactID The ID of the contact whose availability is to be checked.
     * @param newStart The proposed appointment's start time.
     * @param newEnd The proposed appointment's end time.
     * @return Returns true if contact is available within that time frame, returns false if contact is not available within that time frame.
     */
    public static boolean contactAvailable(int contactID, int appointmentID, ZonedDateTime newStart, ZonedDateTime newEnd) {
        try {
            /* Get start and end time of all the contact's appointments from database */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Appointment_ID, Start, End FROM client_schedule.appointments WHERE Contact_ID ='%s'", contactID));
            while (rs.next()) {
                ZonedDateTime oldStart = ZonedDateTime.ofInstant(rs.getTimestamp("Start").toInstant(), ZoneId.systemDefault());
                ZonedDateTime oldEnd = ZonedDateTime.ofInstant(rs.getTimestamp("End").toInstant(), ZoneId.systemDefault());
                int currentDBAppointment = rs.getInt("Appointment_ID");
                /* Check if appointment being compared is the same appointment as is being updated */
                if (currentDBAppointment != appointmentID) {
                    /* Return false if proposed appointment end time is after existing appointment's start time and is not after existing appointment's end time*/
                    if (newEnd.isAfter(oldStart) && !newEnd.isAfter(oldEnd)) {
                        return false;
                    }
                    /* Return false if proposed appointment end time is after existing appointment's end time and its start time is not after or equal to existing appointment's end time */
                    else if (newEnd.isAfter(oldEnd) && newStart.isBefore(oldEnd)) {
                        return false;
                    }
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return true if no time conflict */
        return true;
    }

    /** This method returns a list of appointments happening within 15 minutes of the current time.
     * The method creates a list and loops through allAppointments, adding to the new list any appointments happening within 15 minutes of the current time, while excluding any past appointments.
     * @return Returns a list of appointments happening within 15 minutes of the current time.
     */
    public static ObservableList<Appointment> appointmentsSoon() {
        /* Create list and add any appointment happening within 15 minutes of current time, while excluding any past appointments */
        ObservableList<Appointment> soon = FXCollections.observableArrayList();
        ZonedDateTime now = ZonedDateTime.now();
        for (Appointment apt : allAppointments) {
            if (now.until(apt.getStartDateTime(), ChronoUnit.MINUTES) <= 15 && now.until(apt.getStartDateTime(), ChronoUnit.MINUTES) > 0) {
                soon.add(apt);
            }
        }
        /* Return list */
        return soon;
    }

    /** This method populates a ComboBox with all current appointment types.
     * The method creates a list of Strings, then uses a JDBC connection query to get all appointment types. The method loops through the ResultSet to add to the list any appointment type not already in the list. The method then sets the argument ComboBox's items to the list.
     * @param box The ComboBox to be populated.
     */
    public static void populateTypes(ComboBox<String> box) {
        ObservableList<String> types = FXCollections.observableArrayList();
        try {
            /* Get all appointment types */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Type FROM client_schedule.appointments");
            /* Add unique types to list */
            while (rs.next()) {
                String type = rs.getString("Type");
                if (!types.contains(type)) {
                    types.add(type);
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Set ComboBox's items to list */
        box.setItems(types);
    }

    /** This method populates a ComboBox with a list of 13 months, starting from the current month and ending with the same month a year from the current date.
     *  The method uses the current date to format the current month and the next 12 months into strings of format "MONTH Year" (ex: DECEMBER 2023), adding each String to a list. The method then sets the argument ComboBox's items to the list.
     * @param box The ComboBox to be populated.
     */
    public static void populateMonths(ComboBox<String> box) {
        /* Create list */
        ObservableList<String> months = FXCollections.observableArrayList();
        /* Add to list the current month and the next 12 months */
        ZonedDateTime now = ZonedDateTime.now();
        for (int i = 0; i < 13; i++) {
            months.add(String.format(now.plusMonths(i).getMonth() + " " + now.plusMonths(i).getYear()));
        }
        /* Set ComboBox's items to list */
        box.setItems(months);
    }

    /** This method returns of list of all current customer names in the database.
     * The method uses a JDBC connection query to get all customer names from the database, then loops through the ResultSet to add each name to a list. The method then returns the list.
     * @return Returns a list of all current customer names as Strings.
     */
    public static ObservableList<String> getAllCustomers() {
        /* Create list */
        ObservableList<String> customers = FXCollections.observableArrayList();
        try {
            /* Get all customer names from database. */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Customer_Name FROM client_schedule.customers");
            /* Add each name to list */
            while (rs.next()) {
                customers.add(rs.getString("Customer_Name"));
            }
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return list */
        return customers;
    }

    /** This method takes a customer's ID as an argument and returns the corresponding name.
     * The method uses a JDBC connection query to get the corresponding name from the database, then returns it.
     * @param id The customer's ID.
     * @return Returns the customer's name, otherwise returns null if no invalid customer ID or if a SQL error occurs
     */
    public static String getCustomerName(int id) {
        try {
            /* Get corresponding name from database and return it */
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT Customer_Name FROM client_schedule.customers WHERE Customer_ID ='%s'", id));
            rs.next();
            return rs.getString("Customer_Name");
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Return null if SQL error */
        return null;
    }

    public static String makeLabel(String word) {
        return word.concat(":");
    }

}

/** @author Alexandre Do */
package scheduling.schedulingapplication.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import scheduling.schedulingapplication.Model.Appointment;
import scheduling.schedulingapplication.Model.Helper;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Controller.MainController.allAppointments;
import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Controller for the AddAppointment form. */
public class AddAppointmentController implements Initializable {

    public TextField appointmentID;
    public TextField customerID;
    public DatePicker startDate;
    public DatePicker endDate;
    public ComboBox<String> contact;
    public TextField userID;
    public TextField location;
    public TextField type;
    public TextField title;
    public TextField startTime;
    public TextField endTime;
    public Button cancelButton;
    public TextArea description;

    /** This is the override initialize method that executes when the AddAppointment form is initialized.
     * The method uses various Helper methods to set the appointment ID text field, populate the contact ComboBox, and set the cancelButton action event to direct to the Main form. It also implements the startDate DatePicker action event to set the endDate DatePicker to the same date.
     * The method uses two lambdas to implement the startDate and cancelButton action events. These lambdas remove the need to use Controller Class methods to handle the relevant action events, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appointmentID.setText(String.valueOf(Helper.getNextAppointmentID()));
        startDate.setOnAction(actionEvent -> endDate.setValue(startDate.getValue()));
        contact.setItems(Helper.getAllContacts());
        cancelButton.setOnAction(actionEvent -> Helper.direct(AddAppointmentController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent));
    }

    /** This is the method that handles the actionEvent for the Confirm Button.
     * The method validates all inputs in the form, checking if TextFields are empty and ensuring IDs are numerical.
     * The method grabs values from the form and creates ZonedDateTimes from the date and time inputs and checks provided times are valid (appointments times must be within business hours, start time may not be in the past, end time may not be before start time, and both customer and contact must be free within the time frame).
     * If provided values are valid, the method adds the appointment to the database (converting the contact to contactID and ZonedDateTimes to UTC using Helper class methods), creates a new appointment object with local times and adds the object to the allAppointments static list. The method then directs back to the Main form.
     * @param actionEvent
     */
    public void onConfirm(ActionEvent actionEvent) {
        try {
            /* Validate inputs */
            if (!Helper.validateTextField(customerID)) {
                Helper.error("Appointment Add Error", "Customer ID field is empty.");
            } else if (!Helper.validCustomerID(Integer.parseInt(customerID.getText()))) {
                Helper.error("Appointment Add Error", "Invalid Customer ID.");
            } else if (contact.getSelectionModel().isEmpty()) {
                Helper.error("Appointment Add Error", "Please select a Contact.");
            } else if (!Helper.validateTextField(userID)) {
                Helper.error("Appointment Add Error", "User ID field is empty.");
            } else if (!Helper.validUserID(Integer.parseInt(userID.getText()))) {
                Helper.error("Appointment Add Error", "Invalid User ID.");
            } else if (!Helper.validateTextField(location)) {
                Helper.error("Appointment Add Error", "Location field is empty.");
            } else if (!Helper.validateTextField(type)) {
                Helper.error("Appointment Add Error", "Type field is empty.");
            } else if (startDate.getValue() == null) {
                Helper.error("Appointment Add Error", "Please pick a Start Date.");
            } else if (endDate.getValue() == null) {
                Helper.error("Appointment Add Error", "Please pick an End Date.");
            } else if (!Helper.validateTextField(startTime)) {
                Helper.error("Appointment Add Error", "Start Time field is empty.");
            } else if (!Helper.validateTextField(endTime)) {
                Helper.error("Appointment Add Error", "End Time field is empty.");
            } else if (!Helper.validateTextField(title)) {
                Helper.error("Appointment Add Error", "Title field is empty.");
            } else if (description.getText().isEmpty()) {
                Helper.error("Appointment Add Error", "Description field is empty.");
            } else {
                /* Grab values from form while creating ZonedDateTimes from date and time fields. */
                int newAppointmentID = Integer.parseInt(appointmentID.getText());
                String newContact = contact.getValue();
                int newCustomerID = Integer.parseInt(customerID.getText());
                int newUserID = Integer.parseInt(userID.getText());
                /* Get contact ID from contact ComboBox input */
                int newContactID = Helper.getContactID(newContact);
                String newLocation = location.getText();
                String newType = type.getText();
                LocalDate newStartDate = startDate.getValue();
                String newStartTime = startTime.getText();
                /* Create new ZonedDateTime from start inputs */
                ZonedDateTime newStartZonedDateTime = Helper.createZonedDateTime(newStartDate, newStartTime);
                LocalDate newEndDate = endDate.getValue();
                String newEndTime = endTime.getText();
                /* Create new ZonedDateTime from end inputs */
                ZonedDateTime newEndZonedDateTime = Helper.createZonedDateTime(newEndDate, newEndTime);
                String newTitle = title.getText();
                String newDescription = description.getText();
                /* Check for invalid Appointment times */
                /* Appointment date in past */
                if (newStartZonedDateTime.isBefore(ZonedDateTime.now(ZoneId.systemDefault()))) {
                    Helper.error("Appointment Add Error", "Invalid Appointment Start Date/Time.\n\nAppointment Start Time may not be in the past.");
                }
                /* Appointment end time is before or at same time as appointment start time */
                else if (!newEndZonedDateTime.isAfter(newStartZonedDateTime)) {
                    Helper.error("Appointment Add Error", "Invalid Appointment End Time.\n\nAppointment End Time may not be before or at the same time as Start Time.");
                }
                /* Appointment is outside of business hours */
                else if (!Helper.inBusinessHours(newStartZonedDateTime) || !Helper.inBusinessHours(newEndZonedDateTime)) {
                    Helper.error("Appointment Add Error", "Appointment Start and End time must be within business hours (Monday-Friday 8:00AM - 10:00PM ET).");
                }
                /* Appointment overlaps with another appointment for the same customer */
                else if (!Helper.customerAvailable(newCustomerID, newAppointmentID, newStartZonedDateTime, newEndZonedDateTime)) {
                    Helper.error("Appointment Add Error", "Customer already has an appointment within this time frame.");
                }
                /* Appointment overlaps with another appointment for the same contact */
                else if (!Helper.contactAvailable(newContactID, newAppointmentID, newStartZonedDateTime, newEndZonedDateTime)) {
                    Helper.error("Appointment Add Error", "Contact already has an appointment within this time frame.");
                }
                /* Update database with new appointment, converting ZonedDateTimes to UTC */
                else {
                    Statement stmt = connection.createStatement();
                    /* Convert start and end time to UTC */
                    Timestamp newStartUTC = Helper.convertToTimeStampUTC(newStartZonedDateTime);
                    Timestamp newEndUTC = Helper.convertToTimeStampUTC(newEndZonedDateTime);
                    String update = String.format("INSERT INTO client_schedule.appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", newAppointmentID, newTitle, newDescription, newLocation, newType, newStartUTC, newEndUTC, newCustomerID, newUserID, newContactID);
                    stmt.executeUpdate(update);
                    /* Construct new Appointment object and add it to display list */
                    Appointment appointment = new Appointment(newAppointmentID, newTitle, newDescription, newLocation, newContact, newType, newStartZonedDateTime, newEndZonedDateTime, newCustomerID, newUserID);
                    allAppointments.add(appointment);
                    /* Direct back to main */
                    Helper.direct(AddAppointmentController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent);
                }
            }
        }
        catch(DateTimeParseException d){
            Helper.error("Appointment Add Error", "Please make sure Time fields are in HH:MM AM/PM format. \n\nHours should not exceed 12, minutes should not exceed 59, and make sure to put a space between the time and AM/PM.\n\n");
        }
        catch(NumberFormatException n){
            Helper.error("Appointment Add Error", "Please make sure ID fields are numbers.");
        }
        catch(SQLException s){
            System.out.println("Error:" + s.getMessage());
        }
    }

}

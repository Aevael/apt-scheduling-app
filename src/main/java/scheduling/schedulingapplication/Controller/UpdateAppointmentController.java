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

import static scheduling.schedulingapplication.Model.Helper.timeFormatter;
import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Controller for the UpdateAppointment form. */
public class UpdateAppointmentController implements Initializable {
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
    /** This static appointment is used to store an appointment passed from MainController. */
    private static Appointment targetAppointment = null;

    /** This method is a static method used to pass an appointment from MainController.
     * The method assigns the static targetAppointment with the appointment passed into the method.
     * @param appointment The appointment to be passed into UpdateAppointmentController from MainController.
     */
    public static void passAppointment(Appointment appointment) {
        targetAppointment = appointment;
    }

    /** This is the override initialize method that executes when the UpdateAppointment form is initialized.
     * The method populates the form with the values from the targetAppointment passed from MainController, as well as populating the contact ComboBox with the list returned from a Helper method.
     * It uses a Helper method to set the cancelButton action event to direct to the Main form. It also implements the startDate DatePicker action event to set the endDate DatePicker to the same date.
     * The method uses two lambdas to implement the startDate and cancelButton action events. These lambdas remove the need to use Controller Class methods to handle the relevant action events, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Populate the form */
        appointmentID.setText(String.valueOf(targetAppointment.getAppointmentID()));
        customerID.setText(String.valueOf(targetAppointment.getCustomerID()));
        contact.setItems(Helper.getAllContacts());
        contact.setValue(targetAppointment.getContact());
        userID.setText(String.valueOf(targetAppointment.getUserID()));
        location.setText(targetAppointment.getLocation());
        type.setText(targetAppointment.getType());
        startDate.setValue(targetAppointment.getStartDateTime().toLocalDate());
        endDate.setValue(targetAppointment.getEndDateTime().toLocalDate());
        startTime.setText(targetAppointment.getStartDateTime().toLocalTime().format(timeFormatter));
        endTime.setText(targetAppointment.getEndDateTime().toLocalTime().format(timeFormatter));
        title.setText(targetAppointment.getTitle());
        description.setText(targetAppointment.getDescription());
        /* Action event setters */
        cancelButton.setOnAction(actionEvent -> Helper.direct(UpdateAppointmentController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent));
        startDate.setOnAction(actionEvent -> endDate.setValue(startDate.getValue()));
    }


    /** This is the method that implements the actionEvent for the Confirm Button.
     * The method validates all inputs in the form, checking if TextFields are empty and ensuring IDs are numerical.
     * The method grabs values from the form and creates ZonedDateTimes from the date and time inputs and checks provided times are valid (appointments times must be within business hours, start time may not be in the past, end time may not be before start time, and both customer and contact must be free within the time frame).
     * If provided values are valid, the method updates the appointment in the database (converting the contact to contactID and ZonedDateTimes to UTC using Helper class methods) and updates the appointment object with the new values. The method then directs back to the Main form.
     * @param actionEvent
     */
    public void onConfirm(ActionEvent actionEvent) {
        try {
            /* Validate inputs */
            if (!Helper.validateTextField(customerID)) {
                Helper.error("Appointment Update Error", "Customer ID field is empty.");
            } else if (!Helper.validCustomerID(Integer.parseInt(customerID.getText()))) {
                Helper.error("Appointment Update Error", "Invalid Customer ID.");
            } else if (contact.getSelectionModel().isEmpty()) {
                Helper.error("Appointment Update Error", "Please select a Contact.");
            } else if (!Helper.validateTextField(userID)) {
                Helper.error("Appointment Update Error", "User ID field is empty.");
            } else if (!Helper.validUserID(Integer.parseInt(userID.getText()))) {
                Helper.error("Appointment Update Error", "Invalid User ID.");
            } else if (!Helper.validateTextField(location)) {
                Helper.error("Appointment Update Error", "Location field is empty.");
            } else if (!Helper.validateTextField(type)) {
                Helper.error("Appointment Update Error", "Type field is empty.");
            } else if (startDate.getValue() == null) {
                Helper.error("Appointment Update Error", "Please pick a Start Date.");
            } else if (endDate.getValue() == null) {
                Helper.error("Appointment Update Error", "Please pick an End Date.");
            } else if (!Helper.validateTextField(startTime)) {
                Helper.error("Appointment Update Error", "Start Time field is empty.");
            } else if (!Helper.validateTextField(endTime)) {
                Helper.error("Appointment Update Error", "End Time field is empty.");
            } else if (!Helper.validateTextField(title)) {
                Helper.error("Appointment Update Error", "Title field is empty.");
            } else if (description.getText().isEmpty()) {
                Helper.error("Appointment Update Error", "Description field is empty.");
            } else {
                /* Grab values from form */
                int currentAppointmentID = targetAppointment.getAppointmentID();
                int newCustomerID = Integer.parseInt(customerID.getText());
                String newContact = contact.getValue();
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
                    Helper.error("Appointment Update Error", "Invalid Appointment Start Date/Time.\n\nAppointment Start Time may not be in the past.");
                }
                /* Appointment end time is before or at same time as appointment start time */
                else if (!newEndZonedDateTime.isAfter(newStartZonedDateTime)) {
                    Helper.error("Appointment Update Error", "Invalid Appointment End Time.\n\nAppointment End Time may not be before or at the same time as Start Time.");
                }
                /* Appointment not within business hours */
                else if (!Helper.inBusinessHours(newStartZonedDateTime) || !Helper.inBusinessHours(newEndZonedDateTime)) {
                    Helper.error("Appointment Update Error", "Appointment Start and End time must be within business hours (Monday-Friday 8:00AM - 10:00PM ET).");
                }
                /* Appointment overlaps with another appointment for the same customer */
                else if (!Helper.customerAvailable(newCustomerID, currentAppointmentID, newStartZonedDateTime, newEndZonedDateTime)) {
                    Helper.error("Appointment Update Error", "Customer already has an appointment within this time frame.");
                }
                /* Appointment overlaps with another appointment for the same contact */
                else if (!Helper.contactAvailable(newContactID, currentAppointmentID, newStartZonedDateTime, newEndZonedDateTime)) {
                    Helper.error("Appointment Add Error", "Contact already has an appointment within this time frame.");
                }
                else {
                    /* Update database */
                    Statement stmt = connection.createStatement();
                    /* Convert start and end time to UTC */
                    Timestamp newStartUTC = Helper.convertToTimeStampUTC(newStartZonedDateTime);
                    Timestamp newEndUTC = Helper.convertToTimeStampUTC(newEndZonedDateTime);
                    String update = String.format("UPDATE client_schedule.appointments SET Title = '%s', Description = '%s', Location = '%s', Type = '%s', Start = '%s', End = '%s', Customer_ID = '%s', User_ID = '%s', Contact_ID = '%s' WHERE Appointment_ID ='%s'", newTitle, newDescription, newLocation, newType, newStartUTC, newEndUTC, newCustomerID, newUserID, newContactID, currentAppointmentID);
                    stmt.executeUpdate(update);
                    /* Update appointment object */
                    targetAppointment.setTitle(newTitle);
                    targetAppointment.setDescription(newDescription);
                    targetAppointment.setLocation(newLocation);
                    targetAppointment.setContact(newContact);
                    targetAppointment.setType(newType);
                    targetAppointment.setStartDateTime(newStartZonedDateTime);
                    targetAppointment.setEndDateTime(newEndZonedDateTime);
                    targetAppointment.setCustomerID(newCustomerID);
                    targetAppointment.setUserID(newUserID);
                    /* Direct to Main */
                    Helper.direct(AddAppointmentController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent);
                }
            }
        }
        catch (DateTimeParseException d) {
            Helper.error("Appointment Update Error", "Please make sure Time fields are in HH:MM AM/PM format. \n\nHours should not exceed 12, minutes should not exceed 59, and make sure to put a space between the time and AM/PM.\n\n");
        }
        catch (NumberFormatException n) {
            Helper.error("Appointment Update Error", "Please make sure ID fields are numbers.");
        }
        catch (SQLException s) {
            System.out.println("Error:" + s.getMessage());
        }
    }
}


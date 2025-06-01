/** @author Alexandre Do */
package scheduling.schedulingapplication.Model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static scheduling.schedulingapplication.Model.Helper.displayFormatterDate;
import static scheduling.schedulingapplication.Model.Helper.displayFormatterTime;

/** This class creates an Appointment object. */
public class Appointment implements Comparable<Appointment> {

    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private ZonedDateTime startDateTime;
    /** A string representation of startDateTime used for displaying. */
    private String startDisplay;
    private ZonedDateTime endDateTime;
    /** A string representation of endDateTime used for displaying. */
    private String endDisplay;
    private int customerID;
    private int userID;

    /** This method is the constructor for an Appointment object.
     * The method creates an Appointment object based on arguments, setting the startDisplay and endDisplay strings based on startDateTime and endDateTime arguments.
     * @param appointmentID The appointment's ID.
     * @param title The appointment's title.
     * @param description The appointment's description.
     * @param location The appointment's location.
     * @param contact The name of the appointment's contact.
     * @param type The appointment's type.
     * @param startDateTime The appointment's start date and time.
     * @param endDateTime The appointment's end date and time.
     * @param customerID The ID of the appointment's customer.
     * @param userID The ID of the appointment's user.
     */
    public Appointment(int appointmentID, String title, String description, String location, String contact, String type, ZonedDateTime startDateTime, ZonedDateTime endDateTime, int customerID, int userID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.customerID = customerID;
        this.userID = userID;
        this.startDisplay = String.format(startDateTime.format(displayFormatterDate) + " at " + startDateTime.format(displayFormatterTime));
        this.endDisplay = String.format(endDateTime.format(displayFormatterDate) + " at " + endDateTime.format(displayFormatterTime));
    }

    /** This method returns the appointment's ID.
     * @return Returns the appointment's ID.
     */
    public int getAppointmentID() {
        return this.appointmentID;
    }

    /** This method returns the appointment's title.
     * @return Returns the appointment's title.
     */
    public String getTitle() {
        return this.title;
    }

    /** This method returns the appointment's description.
     * @return Returns the appointment's description.
     */
    public String getDescription() {
        return this.description;
    }

    /** This method returns the appointment's location.
     * @return Returns the appointment's location.
     */
    public String getLocation() {
        return this.location;
    }

    /** This method returns the name of the appointment's contact.
     * @return Returns the name of the appointment's contact.
     */
    public String getContact() {
        return this.contact;
    }

    /** This method returns the appointment's type.
     * @return Returns the appointment's type.
     */
    public String getType() {
        return this.type;
    }

    /** This method returns a ZonedDateTime representing the appointment's start date and time.
     * @return Returns a ZonedDateTime representing the appointment's start date and time.
     */
    public ZonedDateTime getStartDateTime() {
        return this.startDateTime;
    }

    /** This method returns a String representing the appointment's start date and time.
     * @return Returns a String representing the appointment's start date and time.
     */
    public String getStartDisplay() {
        return this.startDisplay;
    }

    /** This method returns a ZonedDateTime representing the appointment's end date and time.
     * @return Returns a ZonedDateTime representing the appointment's end date and time.
     */
    public ZonedDateTime getEndDateTime() {
        return this.endDateTime;
    }

    /** This method returns a String representing the appointment's end date and time.
     * @return Returns a String representing the appointment's end date and time.
     */
    public String getEndDisplay() {
        return this.endDisplay;
    }


    /** This method returns the ID of the appointment's customer.
     * @return Returns the ID of the appointment's customer.
     */
    public int getCustomerID() {
        return this.customerID;
    }

    /** This method returns the ID of the appointment's user.
     * @return Returns the ID of the appointment's user.
     */
    public int getUserID() {
        return this.userID;
    }

    /** This method updates the appointment's ID.
     * @param appointmentID The ID to set.
     */
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /** This method updates the appointment's title.
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** This method updates the appointment's description.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** This method updates the appointment's location.
     * @param location The location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /** This method updates the name of the appointment's contact.
     * @param contact The contact name to set.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /** This method updates the appointment's type.
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /** This method updates the appointment's start date and time.
     * The method updates the ZonedDateTime startDateTime to the argument ZonedDateTime, then formats the ZonedDateTime to a String and updates startDisplay.
     * @param startDateTime The start date and time to set.
     */
    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
        this.startDisplay = String.format(startDateTime.format(displayFormatterDate) + " at " + startDateTime.format(displayFormatterTime));
    }

    /** This method updates the appointment's end date and time.
     * The method updates the ZonedDateTime endDateTime to the argument ZonedDateTime, then formats the ZonedDateTime to a String and updates endDisplay.
     * @param endDateTime The end date and time to set.
     */
    public void setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
        this.endDisplay = String.format(endDateTime.format(displayFormatterDate) + " at " + endDateTime.format(displayFormatterTime));
    }

    /** This method updates the ID of the appointment's customer.
     * @param customerID The customer ID to set.
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /** This method updates the ID of the appointment's user.
     * @param userID The user ID to set.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /** This method compares an appointment to another appointment based on ID.
     * This method enables comparison between appointments by comparing their IDs using Integer's compare method.
     * @param a The appointment to be compared.
     * @return Returns 1 if calling appointment's ID is greater, returns -1 if calling appointment's ID is lesser, returns 0 if appointment IDs are the same.
     */
    @Override
    public int compareTo(Appointment a) {
        int currentID = this.appointmentID;
        int otherID = a.getAppointmentID();
        return Integer.compare(currentID, otherID);
    }
}

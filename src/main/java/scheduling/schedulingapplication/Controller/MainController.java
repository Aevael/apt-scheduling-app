/** @author Alexandre Do */
package scheduling.schedulingapplication.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduling.schedulingapplication.Model.Appointment;
import scheduling.schedulingapplication.Model.Helper;
import scheduling.schedulingapplication.Model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Controller for the Main form. */
public class MainController implements Initializable {
    public Button viewCustomersButton;
    public Button addAppointmentButton;
    public Button deleteAppointmentButton;
    public Button logOutButton;
    public Button exitButton;
    public RadioButton viewByWeekButton;
    public RadioButton viewByMonthButton;
    public RadioButton viewAllButton;
    public ToggleGroup view;
    /** This TableView displays Appointments */
    public TableView<Appointment> appointmentsTable;
    public TableColumn<Appointment, Integer> appointmentIDColumn;
    public TableColumn<Appointment, String> titleColumn;
    public TableColumn<Appointment, String> descriptionColumn;
    public TableColumn<Appointment, String> locationColumn;
    public TableColumn<Appointment, String> contactColumn;
    public TableColumn<Appointment, String> typeColumn;
    public TableColumn<Appointment, ZonedDateTime> startColumn;
    public TableColumn<Appointment, ZonedDateTime> endColumn;
    public TableColumn<Appointment, Integer> customerIDColumn;
    public TableColumn<Appointment, Integer> userIDColumn;
    /** This static list holds all Appointment objects. It is initialized and populated from the database by scheduling.schedulingapplication.SchedulingApplication's Main method during boot up. */
    public static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    /** This static list holds Appointment objects filtered by current week or month. */
    public static ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    /** This static list holds all possible Report type String values. */
    public static ObservableList<String> reports = FXCollections.observableArrayList("Appointments by Month/Type", "Contact Schedules", "Customer Schedules");
    public ComboBox<String> reportBox;
    public Button toReportButton;

    /** This is the override initialize method that executes when the Main form is initialized.
     * The method sorts and displays allAppointments in the appointmentsTable TableView and populates the reportBox ComboBox with the reports list's values.
     * The method uses four lambdas to implement the viewCustomers, addAppointment, and logOut buttons action events to direct to the relevant forms, as well as to implement the exitButton's action event to system exit. These lambdas remove the need to use Controller Class methods to handle the relevant action events, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Sort list and display in table */
        FXCollections.sort(allAppointments);
        appointmentsTable.setItems(allAppointments);
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDisplay"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDisplay"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        viewCustomersButton.setOnAction(actionEvent -> Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/Customers.fxml", 700, 494, "Customers", actionEvent));
        addAppointmentButton.setOnAction(actionEvent -> Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/AddAppointment.fxml", 590, 435, "Add Appointment", actionEvent));
        logOutButton.setOnAction(actionEvent -> {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Return to Login page?");
                    Optional<ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/Login.fxml", 350, 240, "Login", actionEvent);
                    }
                });
        exitButton.setOnAction(actionEvent -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Exit program?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.exit(0);
            }
        });
        /* Populate report ComboBox */
        reportBox.setItems(reports);
    }

    /** This method implements the update appointment button's action event.
     * The method gets the selected appointment from the table, displaying an error if none is selected. The method then passes the appointment to the UpdateAppointmentController using its static passAppointment method.
     * The method then directs to the UpdateAppointment form.
     * @param actionEvent
     */
    public void toUpdateAppointment(ActionEvent actionEvent) {
        /* Get selected appointment */
        Appointment tempAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        /* Display error if no appointment selected */
        if (tempAppointment == null) {
            Helper.error("Appointment Update Error", "No appointment selected.");
        }
        /* Pass appointment and direct */
        else {
            UpdateAppointmentController.passAppointment(tempAppointment);
            Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/UpdateAppointment.fxml", 590, 435, "Update Appointment", actionEvent);
        }
    }

    /** This method implements the delete button's action event.
     * The method gets the selected appointment from the table, displaying an error if none is selected.
     * The method then deletes the appointment from the database and the allAppointments list, displaying an information message indicating successful removal.
     * @param actionEvent
     */
    public void onDeleteAppointment(ActionEvent actionEvent) {
        /* Get selected appointment */
        Appointment targetAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        /* Display error if no appointment selected */
        if (targetAppointment == null) {
            Helper.error("Appointment Delete Error", "No Appointment Selected");
        }
        /* Delete appointment */
        else {
            /* Confirmation */
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this appointment?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    /* Delete from database */
                    Statement stmt = connection.createStatement();
                    int appointmentID = targetAppointment.getAppointmentID();
                    String appointmentType = targetAppointment.getType();
                    stmt.executeUpdate(String.format("DELETE FROM client_schedule.appointments WHERE Appointment_ID ='%s'", appointmentID));
                    /* Delete from list */
                    allAppointments.remove(targetAppointment);
                    /* Display success message */
                    String successMessage = String.format("Appointment successfully canceled.\n\nAppointment ID:  %s.\n\nAppointment Type:  %s.\n", appointmentID, appointmentType);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, successMessage);
                    successAlert.showAndWait();
                } catch (SQLException e) {
                    System.out.println("Error:" + e.getMessage());
                }
            }
        }
    }

    /** This method implements the View All button's action event.
     * The method sorts the allAppointments list and displays it in the appointmentsTable TableView.
     * @param actionEvent
     */
    public void viewAll(ActionEvent actionEvent) {
        FXCollections.sort(allAppointments);
        appointmentsTable.setItems(allAppointments);
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDisplay"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDisplay"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /** This method implements the View by Week button's action event.
     * The method filters appointments by current week (storing results in the static displayFilteredAppointments list) using a Helper method.
     * The method then sorts displayFilteredAppointments and displays it in the appointmentsTable TableView.
     * @param actionEvent
     */
    public void viewByWeek(ActionEvent actionEvent) {
        Helper.filterByCurrentWeek();
        FXCollections.sort(filteredAppointments);
        appointmentsTable.setItems(filteredAppointments);
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDisplay"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDisplay"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /** This method implements the View by Month button's action event.
     * The method filters appointments by current month (storing results in the static displayFilteredAppointments list) using a Helper method.
     * The method then sorts displayFilteredAppointments and displays it in the appointmentsTable TableView.
     * @param actionEvent
     */
    public void viewByMonth(ActionEvent actionEvent) {
        Helper.filterByCurrentMonth();
        FXCollections.sort(filteredAppointments);
        appointmentsTable.setItems(filteredAppointments);
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startDisplay"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endDisplay"));
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /** This method implements the View Report button's action event.
     * The method gets the report type from the reportBox ComboBox, displaying an error if none is selected.
     * The method then uses a Helper function to direct to the relevant report based on report type.
     * The method uses three lambdas to implement the cases in the switch block used to implement directing based on report type. These lambdas remove the need to implement code blocks for each case, shortening code and improving readability.
     * @param actionEvent
     */
    public void toReport(ActionEvent actionEvent) {
        /* Get report type from ComboBox */
        String page = reportBox.getValue();
        /* Display error if no report type selected */
        if (page == null) {
            Helper.error("Report Error", "No Report Selected.");
        }
        /* Direct depending on report type */
        else {
            switch (page) {
                case "Appointments by Month/Type" -> Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/MonthTypeReport.fxml", 1150, 655, "Month/Type Report", actionEvent);
                case "Contact Schedules" -> Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/ContactReport.fxml", 1150, 655, "Contact Schedules", actionEvent);
                case "Customer Schedules" -> Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/CustomerReport.fxml", 1150, 655, "Customer Schedules", actionEvent);
            }
        }
    }
}

/** @author Alexandre Do */
package scheduling.schedulingapplication.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.schedulingapplication.Model.Appointment;
import scheduling.schedulingapplication.Model.Helper;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Controller.MainController.allAppointments;

/** This class creates a Controller for the CustomerReport form. */
public class CustomerReportController implements Initializable {
    /** This TableView displays appointments relevant to the report. */
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
    public ComboBox<String> customerSelectorBox;
    /** This label displays the total number of appointments based on customerSelectorBox value. */
    public Label totalAppointments;
    public Button exitButton;
    /** This list holds appointments for TableView display based on selected Customer. */
    public ObservableList<Appointment> reportDisplay = FXCollections.observableArrayList();

    /** This is the override initialize method that executes when the CustomerReport form is initialized.
     * The method populates the customerSelectorBox with all customers using a helper method, and sets the exitButton action event to direct back to the Main form.
     * The method uses a lambda to set the exitButton action event, removing the need to implement a Controller Class method to handle the action event, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerSelectorBox.setItems(Helper.getAllCustomers());
        exitButton.setOnAction(actionEvent -> Helper.direct(CustomerReportController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent));
    }

    /** This method implements the action event for the customerSelectorBox.
     * The method first clears the reportDisplay list of any previous appointments. It then adds relevant appointments to the reportDisplay list based on customerSelectorBox value. The method then updates the totalAppointments label with the list's size and populates the appointmentsTable TableView with the sorted reportDisplay list.
     * @param actionEvent
     */
    public void customerSelector(ActionEvent actionEvent) {
        /* Clear reportDisplay list */
        reportDisplay.clear();
        /* Add relevant appointments to reportDisplay list based on customerSelectorBox value */
        String boxValue = customerSelectorBox.getValue();
        for (Appointment apt : allAppointments) {
            if (Helper.getCustomerName(apt.getCustomerID()).equals(boxValue)) {
                reportDisplay.add(apt);
            }
        }
        /* Update total appointments */
        totalAppointments.setText(String.format("Total Appointments: %s", reportDisplay.size()));
        /* Populate table with sorted reportDisplay list */
        FXCollections.sort(reportDisplay);
        appointmentsTable.setItems(reportDisplay);
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
}

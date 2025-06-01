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

/** This class creates a Controller for the MonthTypeReport form. */
public class MonthTypeReportController implements Initializable {
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
    public RadioButton viewByTypeButton;
    public ToggleGroup reportType;
    public RadioButton viewByMonthButton;
    public ComboBox<String> selectorBox;
    public Label totalAppointments;
    public Button exitButton;
    /** This integer represents which type of report is requested in this form, with a value of 0 representing report by Type and 1 representing report by Month). */
    public int reportSelected;
    /** This list holds appointments for display based on selected Month/Type. */
    public ObservableList<Appointment> reportDisplay = FXCollections.observableArrayList();

    /** This is the override initialize method that executes when the MonthTypeReport form is initialized.
     * The method populates the typeSelectorBox with all current appointment types using a Helper method.
     * The method sets the reportSelected value to 0 to match the form being initialized with View by Type selected.
     * The method uses a lambda to set the exitButton action event, removing the need to implement a Controller Class method to handle the action event, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Helper.populateTypes(selectorBox);
        reportSelected = 0;
        exitButton.setOnAction(actionEvent -> Helper.direct(MonthTypeReportController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent));
    }

    /** This method implements the View by Type button's action event.
     * The method populates the selectorBox with all current possible types using a Helper method, and sets reportSelected to 0.
     * @param actionEvent
     */
    public void viewByType(ActionEvent actionEvent) {
        Helper.populateTypes(selectorBox);
        selectorBox.setPromptText("Select Type");
        reportSelected = 0;
    }

    /** This method implements the View by Month button's action event.
     * The method populates the selectorBox with upcoming months for the next year from current month using a Helper method, sets the selectorBox's prompt to Select Month and sets reportSelected to 1.
     * @param actionEvent
     */
    public void viewByMonth(ActionEvent actionEvent) {
        Helper.populateMonths(selectorBox);
        selectorBox.setPromptText("Select Month");
        reportSelected = 1;
    }

    /** This method implements the dataSelector ComboBox's action event.
     * The method gets the value from the ComboBox, then populates the reportDisplay list with relevant appointments based on ComboBox value and reportSelected value (representing whether report by type or month is desired).
     * The method then updates total appointments and sorts and displays the reportDisplay list.
     * @param actionEvent
     */
    public void dataSelector(ActionEvent actionEvent) {
        /* Get ComboBox value */
        String boxValue = selectorBox.getValue();
        /* If desired report is type, populate with relevant appointments based on ComboBox value */
        if (reportSelected == 0) {
            reportDisplay.clear();
            for (Appointment apt : allAppointments) {
                if (apt.getType().equals(boxValue)) {
                    reportDisplay.add(apt);
                }
            }
        }
        /* If desired report is month, populate with relevant appointments based on ComboBox value */
        else if (reportSelected == 1) {
            reportDisplay.clear();
            for (Appointment apt : allAppointments) {
                ZonedDateTime date = apt.getStartDateTime();
                /* Format start DateTime into string for comparison */
                String monthYear = String.format(date.getMonth() + " " + date.getYear());
                if (monthYear.equals(boxValue)) {
                    reportDisplay.add(apt);
                }
            }
        }
        /* Update total appointments */
        totalAppointments.setText(String.format("Total Appointments: %s", reportDisplay.size()));
        /* Sort and display list */
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

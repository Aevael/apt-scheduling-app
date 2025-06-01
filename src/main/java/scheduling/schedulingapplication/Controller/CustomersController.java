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
import scheduling.schedulingapplication.Model.Customer;
import scheduling.schedulingapplication.Model.Helper;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Controller for the Customers form. */
public class CustomersController implements Initializable {

    /** This TableView displays Customer data. */
    public TableView<Customer> customersTable;
    public TableColumn<Customer,Integer> customerIDColumn;
    public TableColumn<Customer,String> customerNameColumn;
    public TableColumn<Customer,String> addressColumn;
    public TableColumn<Customer,String> postalColumn;
    public TableColumn<Customer,String> phoneColumn;
    public TableColumn<Customer,Integer> divisionColumn;
    public Button toAppointmentsButton;
    public Button toAddCustomerButton;
    public Button toUpdateCustomerButton;
    public Button deleteCustomerButton;
    public Button logOutButton;
    public Button exitButton;
    /** This static list holds all Customer objects. It is initialized and populated from the database by scheduling.schedulingapplication.SchedulingApplication's Main method during boot up. */
    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    /** This is the override initialize method that executes when the Customers form is initialized.
     * The method sorts and displays allCustomers in the customersTable TableView. The method sets the action events for the toAppointments, toAddCustomer, logOut and exit buttons to direct to the relevant forms using a Helper method.
     * The method uses four lambdas to implement the toAppointments, toAddCustomer, logOut and exit buttons' action events. These lambdas remove the need to use Controller Class methods to handle the relevant action events, shortening code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Populate table using sorted displayCustomers list */
        FXCollections.sort(allCustomers);
        customersTable.setItems(allCustomers);
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
        toAppointmentsButton.setOnAction(actionEvent -> Helper.direct(CustomersController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", actionEvent));
        toAddCustomerButton.setOnAction(actionEvent -> Helper.direct(CustomersController.class, "/scheduling/schedulingapplication/View/AddCustomer.fxml", 340, 400, "Add Customer", actionEvent));
        logOutButton.setOnAction(actionEvent -> {
            /* Confirmation */
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Return to Login page?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Helper.direct(MainController.class, "/scheduling/schedulingapplication/View/Login.fxml", 350, 240, "Login", actionEvent);
            }
        });
        exitButton.setOnAction(actionEvent -> {
            /* Confirmation */
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Exit program?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    /** This method implements the action event for the toUpdateCustomerButton.
     * The method passes the selected customer to the UpdateCustomerController form using its static passCustomer method (displaying an error if no customer is selected).
     * The method then directs to the UpdateCustomer form.
     * @param actionEvent
     */
    public void toUpdateCustomer(ActionEvent actionEvent) {
        /* Get selected Customer */
        Customer tempCustomer = customersTable.getSelectionModel().getSelectedItem();
        /* Display error if no customer is selected */
        if (tempCustomer == null) {
            Helper.error("Customer Update Error", "No customer selected.");
        }
        /* Pass customer and direct */
        else {
            UpdateCustomerController.passCustomer(tempCustomer);
            Helper.direct(CustomersController.class, "/scheduling/schedulingapplication/View/UpdateCustomer.fxml", 340, 400, "Update Customer", actionEvent);
        }
    }

    /** This method implements the action event for the deleteCustomerButton.
     * The method displays an error if no customer is selected. If a customer is selected, the method confirms the removal, ensures customer has no appointments using the Helper customerFKCheck method, and removes the customer from both the database and allCustomers list (displaying an information message indicating successful removal).
     * @param actionEvent
     */
    public void onDeleteCustomer(ActionEvent actionEvent) {
        /* Get selected customer */
        Customer targetCustomer = customersTable.getSelectionModel().getSelectedItem();
        /* Display error if no customer is selected */
        if (targetCustomer == null) {
            Helper.error("Customer Delete Error", "No customer selected.");
        }
        /* Remove customer */
        else {
            /* Confirmation */
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this customer?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                /* Check customer has no appointments */
                int targetCustomerID = targetCustomer.getCustomerID();
                /* Display error if customer has appointment */
                if (!Helper.customerFKCheck(targetCustomerID)) {
                    Helper.error("Customer Delete Error", "Customer's appointments must be cancelled before customer can be removed.");
                }
                /* Delete customer */
                else {
                    try {
                        /* Delete from database */
                        Statement stmt = connection.createStatement();
                        stmt.executeUpdate(String.format("DELETE FROM client_schedule.customers WHERE Customer_ID ='%s'", targetCustomerID));
                        /* Delete from list */
                        allCustomers.remove(targetCustomer);
                        /* Display success message */
                        Alert success = new Alert(Alert.AlertType.INFORMATION, "Customer successfully removed.");
                        success.showAndWait();
                    } catch (SQLException e) {
                        System.out.println("Error:" + e.getMessage());
                    }
                }
            }
        }
    }

}


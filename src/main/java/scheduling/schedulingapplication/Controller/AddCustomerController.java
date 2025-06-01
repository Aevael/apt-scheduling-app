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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import scheduling.schedulingapplication.Model.Customer;
import scheduling.schedulingapplication.Model.Helper;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Controller.CustomersController.allCustomers;
import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Controller for the AddCustomer form. */
public class AddCustomerController implements Initializable {
    public TextField customerID;
    public TextField customerName;
    public TextField customerPostal;
    public TextField customerAddress;
    public TextField customerPhone;
    public Button cancelButton;
    public ComboBox<String> customerCountry;
    public ComboBox<String> customerDivision;
    /** This static lists holds all possible country String values. */
    public static ObservableList<String> countries = FXCollections.observableArrayList("United States", "United Kingdom", "Canada");

    /** This is the override initialize method that executes when the AddCustomer form is initialized.
     * It uses various Helper methods to set the customer ID text field, populate the customerCountry ComboBox, set the customerCountry ComboBox action event to populate the customerDivision ComboBox based on input, and set the cancelButton action event to direct to the Main form.
     * The method uses two lambdas to implement the customerCountry and cancelButton action events. These lambdas remove the need to use Controller Class method to handle the relevant action events, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerID.setText(String.valueOf(Helper.getNextCustomerID()));
        customerCountry.setItems(countries);
        customerCountry.setOnAction(actionEvent -> Helper.populateDivisions(customerDivision, customerCountry.getValue()));
        cancelButton.setOnAction(actionEvent -> Helper.direct(AddCustomerController.class, "/scheduling/schedulingapplication/View/Customers.fxml", 700, 494, "Customers", actionEvent));
    }

    /** This is the method that handles the actionEvent for the Confirm Button.
     * It validates form inputs to ensure they are not empty. If inputs are valid, the method grabs inputs from the form (converting the selected division to a division ID using a Helper method), adds the customer to the database, creates a new customer and adds it to the allCustomers static list. The method then directs back to the Main form.
     * @param actionEvent
     */
    public void onConfirm(ActionEvent actionEvent) {
        /* Validate inputs */
        if (!Helper.validateTextField(customerName)) {
            Helper.error("Customer Add Error", "Customer Name field is empty.");
        }
        else if (!Helper.validateTextField(customerAddress)) {
            Helper.error("Customer Add Error", "Address field is empty.");
        }
        else if (!Helper.validateTextField(customerPostal)) {
            Helper.error("Customer Add Error", "Postal Code field is empty.");
        }
        else if (customerCountry.getSelectionModel().isEmpty()) {
            Helper.error("Customer Add Error", "Please select a Country.");
        }
        else if (customerDivision.getSelectionModel().isEmpty()) {
            Helper.error("Customer Add Error", "Please select a Division");
        }
        else if (!Helper.validateTextField(customerPhone)) {
            Helper.error("Customer Add Error", "Phone Number field is empty.");
        }
        else {
            /* Get values from form */
            int newCustomerID = Integer.parseInt(customerID.getText());
            String newCustomerName = customerName.getText();
            String newCustomerAddress = customerAddress.getText();
            String newCustomerPostal = customerPostal.getText();
            String newCustomerPhone = customerPhone.getText();
            int newCustomerDivision = Helper.getDivisionID(customerDivision.getValue());
            /* Create new customer in database and add to display customers list */
            try {
                /* Update database */
                Statement stmt = connection.createStatement();
                String update = String.format("INSERT INTO client_schedule.customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')", newCustomerID, newCustomerName, newCustomerAddress, newCustomerPostal, newCustomerPhone, newCustomerDivision);
                stmt.executeUpdate(update);
                /* Create customer object and add to display list */
                Customer newCustomer = new Customer(newCustomerID, newCustomerName, newCustomerAddress, newCustomerPostal, newCustomerPhone, newCustomerDivision);
                allCustomers.add(newCustomer);
                Helper.direct(AddCustomerController.class, "/scheduling/schedulingapplication/View/Customers.fxml", 700, 494, "Customers", actionEvent);
            }
            catch (SQLException e) {
                System.out.println("Error:" + e.getMessage());
            }

        }
    }
}





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
import scheduling.schedulingapplication.Model.Customer;
import scheduling.schedulingapplication.Model.Helper;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Controller.AddCustomerController.countries;
import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates a Controller for the UpdateCustomer form. */
public class UpdateCustomerController implements Initializable {

    public TextField customerID;
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPostal;
    public TextField customerPhone;
    public ComboBox<String> customerCountry;
    public ComboBox<String> customerDivision;
    public Button cancelButton;
    /** This static customer is used to store a customer passed from MainController. */
    private static Customer targetCustomer = null;

    /** This method is a static method used to pass a customer from MainController.
     * The method assigns the static targetCustomer with the customer passed into the method.
     * @param customer The customer to be passed into UpdateCustomerController from MainController.
     */
    public static void passCustomer(Customer customer) {
        targetCustomer = customer;
    }

    /** This is the override initialize method that executes when the UpdateCustomer form is initialized.
     * The method populates the form with the values from the targetCustomer passed from MainController, as well as populating the customerCountry ComboBox with the countries static list.
     * The method uses two lambdas to set the customerCountry ComboBox action event to populate the customerDivision ComboBox based on input, and set the cancelButton action event to direct to the Main form.
     * These lambdas remove the need to use Controller Class methods to handle the relevant action events, uncluttering code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Populate the form */
        customerID.setText(String.valueOf(targetCustomer.getCustomerID()));
        customerName.setText(targetCustomer.getCustomerName());
        customerAddress.setText(targetCustomer.getAddress());
        customerPostal.setText(targetCustomer.getPostalCode());
        customerCountry.setItems(countries);
        customerCountry.setValue(Helper.getCountryFromDivisionID(targetCustomer.getDivisionID()));
        Helper.populateDivisions(customerDivision, customerCountry.getValue());
        customerDivision.setValue(Helper.getDivision(targetCustomer.getDivisionID()));
        customerPhone.setText(targetCustomer.getPhone());
        /* Action event setters */
        customerCountry.setOnAction(actionEvent -> Helper.populateDivisions(customerDivision, customerCountry.getValue()));
        cancelButton.setOnAction(actionEvent -> Helper.direct(UpdateCustomerController.class, "/scheduling/schedulingapplication/View/Customers.fxml", 700, 494, "Customers", actionEvent));
    }

    /** This is the method that handles the actionEvent for the Confirm Button.
     * It validates form inputs to ensure they are not empty. If inputs are valid, the method grabs inputs from the form (converting the selected division to a division ID using a Helper method), updates the customer in the database, and updates customer object's and data. The method then directs back to the Main form.
     * @param actionEvent
     */
    public void onConfirm(ActionEvent actionEvent) {
        /* Validate inputs */
        if (!Helper.validateTextField(customerName)) {
            Helper.error("Customer Update Error", "Customer Name field is empty.");
        }
        else if (!Helper.validateTextField(customerAddress)) {
            Helper.error("Customer Update Error", "Address field is empty.");
        }
        else if (!Helper.validateTextField(customerPostal)) {
            Helper.error("Customer Update Error", "Postal Code field is empty.");
        }
        else if (customerCountry.getSelectionModel().isEmpty()) {
            Helper.error("Customer Update Error", "Please select a Country.");
        }
        else if (customerDivision.getSelectionModel().isEmpty()) {
            Helper.error("Customer Update Error", "Please select a Division");
        }
        else if (!Helper.validateTextField(customerPhone)) {
            Helper.error("Customer Update Error", "Phone Number field is empty.");
        }
        else {
            /* Get values from form */
            String updatedCustomerName = customerName.getText();
            String updatedCustomerAddress = customerAddress.getText();
            String updatedCustomerPostal = customerPostal.getText();
            String updatedCustomerPhone = customerPhone.getText();
            int updatedCustomerDivision = Helper.getDivisionID(customerDivision.getValue());
            /* Create new customer in database and add to display customers list */
            try {
                /* Update database */
                Statement stmt = connection.createStatement();
                String update = String.format(String.format("UPDATE client_schedule.customers SET Customer_Name = '%s', Address = '%s', Postal_Code = '%s', Phone = '%s', Division_ID = '%s' WHERE Customer_ID='%s'", updatedCustomerName, updatedCustomerAddress, updatedCustomerPostal, updatedCustomerPhone, updatedCustomerDivision, targetCustomer.getCustomerID()));
                stmt.executeUpdate(update);
                /* Update customer object */
                targetCustomer.setCustomerName(updatedCustomerName);
                targetCustomer.setAddress(updatedCustomerAddress);
                targetCustomer.setPostalCode(updatedCustomerPostal);
                targetCustomer.setPhone(updatedCustomerPhone);
                targetCustomer.setDivisionID(updatedCustomerDivision);
                /* Direct back to customers */
                Helper.direct(UpdateCustomerController.class, "/scheduling/schedulingapplication/View/Customers.fxml", 700, 494, "Customers", actionEvent);
            }
            catch (SQLException e) {
                System.out.println("Error:" + e.getMessage());
            }

        }
    }
}

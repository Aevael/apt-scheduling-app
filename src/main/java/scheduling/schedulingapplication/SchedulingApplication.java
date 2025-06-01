/** @author Alexandre Do */
package scheduling.schedulingapplication;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduling.schedulingapplication.Model.Appointment;
import scheduling.schedulingapplication.Model.Customer;
import scheduling.schedulingapplication.Model.Helper;
import scheduling.schedulingapplication.Model.JDBC;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Controller.CustomersController.allCustomers;
import static scheduling.schedulingapplication.Controller.MainController.allAppointments;
import static scheduling.schedulingapplication.Model.JDBC.connection;

/** This class creates an app that schedules Appointments. Most current version. */
public class SchedulingApplication extends Application {

    public static String language;
    public static ResourceBundle frenchBundle;

    /** This is the start method.
     * This method create a stage and displays the Login form.
     * @param stage The stage on which the scene is to be set.
     */
    @Override
    public void start(Stage stage) throws IOException {
        /* Get Locale information */
        Locale location = Locale.getDefault();
        language = location.getDisplayLanguage();
        if (language.equals("français")) {
            Locale locale = new Locale("fr");
            frenchBundle = ResourceBundle.getBundle("SchedulingApp", locale);
        }
        /* Direct to Log In Form */
        FXMLLoader fxmlLoader = new FXMLLoader(SchedulingApplication.class.getResource("View/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 350, 240);
        if (language.equals("français")) {
            stage.setTitle(frenchBundle.getString("login"));
        }
        else {
            stage.setTitle("Login");
        }
        stage.setScene(scene);
        stage.show();
    }

    /** This is the main method.
     * This is the first method that is called when the java program runs. It opens a JDBC connection and populates the allCustomers allAppointments lists from the database.
     * Javadoc folder is located in root directory.
     */
    public static void main(String[] args) {
        /* Open connection */
        JDBC.openConnection();
        /* Populate displayCustomers list with customers from database */
        try {
            Statement stmt = connection.createStatement();
            /* Populate allCustomers from database */
            ResultSet rs = stmt.executeQuery("SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM client_schedule.customers");
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("Customer_ID"), rs.getString("Customer_Name"), rs.getString("Address"), rs.getString("Postal_Code"), rs.getString("Phone"), rs.getInt("Division_ID"));
                allCustomers.add(customer);
            }
            /* Populate allAppointments from database */
            rs = stmt.executeQuery("SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM client_schedule.appointments");
            while (rs.next()) {
                Appointment appointment = new Appointment(rs.getInt("Appointment_ID"), rs.getString("Title"), rs.getString("Description"), rs.getString("Location"), Helper.getContactName(rs.getInt("Contact_ID")), rs.getString("Type"), ZonedDateTime.ofInstant(rs.getTimestamp("Start").toInstant(), ZoneId.systemDefault()), ZonedDateTime.ofInstant(rs.getTimestamp("End").toInstant(), ZoneId.systemDefault()), rs.getInt("Customer_ID"), rs.getInt("User_ID"));
                allAppointments.add(appointment);
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        /* Launch application */
        launch();
    }
}
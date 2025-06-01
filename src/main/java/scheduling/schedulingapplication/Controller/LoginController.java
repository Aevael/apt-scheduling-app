/** @author Alexandre Do */
package scheduling.schedulingapplication.Controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import scheduling.schedulingapplication.Model.Appointment;
import scheduling.schedulingapplication.Model.Helper;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.EventObject;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static scheduling.schedulingapplication.Model.JDBC.connection;
import static scheduling.schedulingapplication.SchedulingApplication.language;
import static scheduling.schedulingapplication.SchedulingApplication.frenchBundle;

/** This class creates a Controller for Login the form. */
public class LoginController implements Initializable {


    public Label loginLocationLabel;
    public TextField loginUN;
    public PasswordField loginPW;
    public Button loginButton;
    public Label loginUNLabel;
    public Label loginPWLabel;
    public Label loginTitle;
    /** This label displays the login location */
    public Label loginLocationZone;
    public Button exitButton;

    /** This is the override initialize method that executes when the Login form is initialized.
     * The method gets Locale information and translates the Login form and its errors if the Locale's language is French (while also reformatting the form to fit new text).
     * The method gets the system default ZoneID and displays the ZoneID as login location.
     * The method uses lambdas to set the loginButton, loginUN and loginPW action events to login using the LoginController's login method, as well as to set the exitButton's action to system exit. These lambdas remove the need to use Controller Class methods to handle the relevant action events, shortening code and improving readability.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Get and display ZoneID as login location */
        ZoneId zone = ZoneId.systemDefault();
        loginLocationZone.setText((String.valueOf(zone)));
        /* Change FXML text if system language is French */
        if (language.equals("français")) {
            loginTitle.setText(frenchBundle.getString("loginTitle"));
            loginTitle.setMinWidth(192);
            loginTitle.setLayoutX(79);
            loginLocationLabel.setText(Helper.makeLabel(frenchBundle.getString("location")));
            loginLocationZone.setLayoutX(127.5);
            loginUN.setLayoutX(127.5);
            loginPW.setLayoutX(127.5);
            loginUNLabel.setText(Helper.makeLabel(frenchBundle.getString("username")));
            loginUN.setPromptText(frenchBundle.getString("username"));
            loginPWLabel.setText(Helper.makeLabel(frenchBundle.getString("password")));
            loginPW.setPromptText(frenchBundle.getString("password"));
            loginButton.setMinWidth(75);
            loginButton.setLayoutX(176.5);
            loginButton.setText(frenchBundle.getString("login"));
        }
        loginButton.setOnAction(actionEvent -> login(actionEvent));
        loginUN.setOnAction(actionEvent -> login(actionEvent));
        loginPW.setOnAction(actionEvent -> login(actionEvent));
        exitButton.setOnAction(actionEvent -> {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Exit program?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    /** This method implements the LoginController's login function.
     * The method grabs the UserName and Password from the form and initializes a variable representing login success (with a value of 0 indicating login failure and a value of 1 indicating login success).
     * The method then grabs users from the database and compares the UserName and Password user data. If username and password match, loginSuccess is set to 1, the successful login is written to login_activity.txt and the program directs to the Main form while displaying an information alert indicating whether there is no appointment, one appointment or multiple appointments soon (including appointment ID and date/time).
     * If login was unsuccessful (indicated by a loginSuccess value of 0), the unsuccessful login attempt is logged to login_activity.txt and an error message is displayed depending on the system location/language.
     * @param event The event that triggers the login method.
     */
    public void login(EventObject event) {
        String UN = loginUN.getText();
        String PW = loginPW.getText();
        /* Variable indicating login success, 0 is failure, 1 is success */
        int loginSuccess = 0;
        /* Create String for login logging */
        String attempt = String.format("Login Attempt, User: %s, Date and time: %s, ", UN, ZonedDateTime.now());
        try {
            Statement stmt = connection.createStatement();
            String query = String.format("SELECT User_Name, Password FROM client_schedule.users WHERE User_Name='%s'", UN);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (rs.getString("User_Name").equals(UN) && rs.getString("Password").equals(PW)) {
                    loginSuccess = 1;
                    /* Log successful login */
                    try {
                        String log = attempt + "Login Successful.\n";
                        Files.write(Paths.get("login_activity.txt"), log.getBytes(), StandardOpenOption.APPEND);
                    }
                    catch (IOException e) {
                        System.out.println("Error:" + e.getMessage());
                    }
                    /* Direct to Main form */
                    Helper.direct(LoginController.class, "/scheduling/schedulingapplication/View/Main.fxml", 1150, 665, "Scheduling Application", event);
                    /* Display upcoming appointments */
                    ObservableList<Appointment> soonAppointments = Helper.appointmentsSoon();
                    /* No appointments */
                    if (soonAppointments.isEmpty()) {
                        Alert soon = new Alert(Alert.AlertType.INFORMATION, "No Upcoming Appointments.");
                        soon.showAndWait();
                    }
                    /* One appointment */
                    else if (soonAppointments.size() == 1) {
                        String notification = String.format("You have an upcoming appointment.\n\nAppointment ID: %s.\n\nAppointment Date and Time: %s.\n\n", soonAppointments.get(0).getAppointmentID(), soonAppointments.get(0).getStartDisplay());
                        Alert soon = new Alert(Alert.AlertType.INFORMATION, notification);
                        soon.showAndWait();
                    }
                    /* Multiple appointments */
                    else {
                        String notification = "You have multiple upcoming appointments.\n\n";
                        String ids = "Appointment IDs:";
                        String dates = "Appointment Date and Times:";
                        for (Appointment apt : soonAppointments) {
                            ids = String.format(ids + String.format(" %s,", apt.getAppointmentID()));
                            dates = String.format(dates + String.format(" %s,", apt.getStartDisplay()));
                        }
                        /* Remove last comma and add period and new line to each substring */
                        ids = ids.substring(0, ids.length() - 1) + ".\n\n";
                        dates = dates.substring(0, dates.length() - 1) + ".\n\n";
                        Alert soon = new Alert(Alert.AlertType.INFORMATION, notification + ids + dates);
                        soon.showAndWait();
                    }
                    break;
                }
            }
            if (loginSuccess == 0) {
                /* Log unsuccessful login attempt */
                try {
                    String log = attempt + "Login Failed.\n";
                    Files.write(Paths.get("login_activity.txt"), log.getBytes(), StandardOpenOption.APPEND);
                }
                catch (IOException e) {
                    System.out.println("Error:" + e.getMessage());
                }
                /* Display error in English */
                if (language.equals("English")) {
                    Helper.error("Login Error", "Incorrect Login Credentials");
                }
                /* Display error in French */
                else if (language.equals("français")) {
                    Helper.error("Erreur d'itentification", "Nom d'itulisateur ou Mot de Passe Incorrect");
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
}


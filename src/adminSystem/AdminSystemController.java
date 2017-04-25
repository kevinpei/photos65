package adminSystem;

import java.util.ArrayList;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import adminSystem.AdminSystem;
import adminSystem.model.User;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */
public class AdminSystemController {
	/**
	 * The tableview that contains all the users in the admin system
	 */
    @FXML private TableView<User> userTable;
    /**
     * The table column that contains all the usernames of the users
     */
    @FXML private TableColumn<User, String> usernameColumn;
    /**
     * The table column that contains the number of albums created for each user
     */
    @FXML private TableColumn<User, Integer> albumsCreatedColumn;
    /**
     * The table column that contains the date each user was created
     */
    @FXML private TableColumn<User, String> dateCreatedColumn;
    /**
     * The main application that uses this class as a controller
     */
    @FXML private AdminSystem mainApp = new AdminSystem();
    
    public AdminSystemController() {
    }

    /**
     * Initializes the list of users
     * 
     * Sets the value of each column to the appropriate values for each user
     */
    @FXML private void initialize() {
        // Initialize the user table with the appropriate columns.
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().getUsername());
        albumsCreatedColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbumNumber().asObject());
        dateCreatedColumn.setCellValueFactory(cellData -> cellData.getValue().getDateCreated());
    }

    /**
     * Sets the mainApp
     * 
     * Sets the main app that uses this class as a controller
     * 
     * @param mainApp The main app that uses this class as a controller
     */
    public void setMainApp(AdminSystem mainApp) {
        this.mainApp = mainApp;
    }

    public void setUserList() {
    	if (mainApp.getUserData() != null) {
    		userTable.setItems(mainApp.getUserData());
    	}
    }
    
    /**
     * Deletes the selected user
     * 
     * Deletes the selected user. If no user is selected, then an alert is shown asking to choose a
     * user. If a user is selected, a confirmation alert will open asking whether the admin really wants
     * to delete the selected user. If done, then the selected user is removed and the next user in the
     * list is chosen. If there are no more users, then no user is selected.
     */
    @FXML private void deleteUser() {
        int selectedIndex = userTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= userTable.getItems().size()) {
        	// Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user in the table.");
            alert.showAndWait();
            return;
        }
        // This is the confirmation pop up.
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.initOwner(mainApp.getPrimaryStage());
        confirm.setTitle("Delete?");
        confirm.setHeaderText("Do you wish to delete the selected user?");
        Optional<ButtonType> result = confirm.showAndWait();
        // If the user hits OK, the following code is executed.
        if (result.get() == ButtonType.OK) {
        	userTable.getItems().remove(selectedIndex);
            if (selectedIndex < userTable.getItems().size()) {
        		userTable.getSelectionModel().select(selectedIndex);
        		userTable.getFocusModel().focus(selectedIndex);
            } else {
            	if (userTable.getItems().size() == 0) {
            	} else {
            		userTable.getSelectionModel().select(selectedIndex - 1);
            		userTable.getFocusModel().focus(selectedIndex - 1);
            	}
            }
        }
    }

    /**
     * Adds a user to the user list
     * 
     * Adds a user to the end of the current list of users. First, a linear search is performed on the
     * list to see if there are any other users with the same username. If so, then return -1. Otherwise,
     * add the user and return the position of that user in the list.
     * 
     * @param list The ObservableList that the users are stored in
     * @param user The user that is being added to the list of users
     * @return The position of the added user in the list. Returns -1 if a user with that username already
     * exists.
     */
    private static int addUser(ObservableList<User> list, User user) {
        if (list.size() == 0) {
            list.add(user);
            return 0;
        }
        int i = 0;
        while (i < list.size()) {
            if (user.username.compareToIgnoreCase(list.get(i).username) == 0) {
            	return -1;
            }
            i++;
        }
        list.add(user);
        return list.size() - 1;
    }
    
    /**
     * Shows an error when adding a user
     * 
     * If the admin tries to add a user with the same username as a preexisting user, then an
     * alert is opened that a user with the given username already exists.
     */
    @FXML private void additionError() {
    	Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Invalid User");
        alert.setHeaderText("Same user is already present");
        alert.setContentText("A user with this username already exists.");

        alert.showAndWait();
    }
    
    /**
     * Checks the output of addUser
     * 
     * If the output of addUser is -1, then that means that there is already a user with the same
     * username as the user that was attempted to be added. If the output is not -1, then the user
     * at that index is selected, which is the user that was just added.
     * 
     * @param check The output of addUser, which is checked for errors.
     */
    private void userCheck(int check) {
    	if (check != -1) {
			userTable.getSelectionModel().select(check);
			userTable.getFocusModel().focus(check);
		} else {
			additionError();
		}
    }
    
   /**
    * Adds a user to the list
    * 
    * This function asks the admin for a username. If no username is given, then an alert is shown
    * and the admin is prompted again for a username. After confirming the addition of a user, it
    * is tested to see whether there is already a user with that username. If not, then it is selected.
    */
    @FXML private void addUser() {
    	String username = "";
    	while (username.equals("") || username.equalsIgnoreCase("admin")) {
    		TextInputDialog dialogBox = new TextInputDialog("");
        	dialogBox.setTitle("Create a New User");
        	dialogBox.setHeaderText("Create a new user");
        	dialogBox.setContentText("Please enter the new username:");
        	Optional<String> result = dialogBox.showAndWait();
        	if (result.isPresent()){
        		username = result.get();
        	} else {
        		return;
        	}
        	if (username.equals("")) {
        		Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid Username");
                alert.setHeaderText("No username given");
                alert.setContentText("Please enter a username for this user.");
                alert.showAndWait();
        	} else if (username.equalsIgnoreCase("admin")) {
        		Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid Username");
                alert.setHeaderText("Admin username is reserved");
                alert.setContentText("The admin username is reserved. Please enter a different username for this user.");
                alert.showAndWait();
        	}
    	}
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Add?");
        alert.setHeaderText("Do you wish to add a user with the given username?");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.get() == ButtonType.OK) {
        	userCheck(addUser(userTable.getItems(), new User(username)));
        }
    }
    
    public void saveUsers() {
    	mainApp.saveData.userlist = new ArrayList<User>();
    	for (User u : userTable.getItems()) {
    		mainApp.saveData.userlist.add(u);
    	}
    }
    
    @FXML public void logOff() {
    	mainApp.logoff();
    }
    
    /**
     * Returns a list of all users
     * 
     * Returns an ObservableList of all users currently registered in the admin subsystem
     * 
     * @return An ObservableList of all users
     */
    @FXML public ObservableList<User> returnUserList(){
        return userTable.getItems();
    }

    /**
     * Shows the help window for this page
     *
     * Shows the help window to provide the user with some advice on how to use the Admin System
     */
    @FXML public void help(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About Admin System");
        alert.setHeaderText(null);
        alert.setContentText("From here you can add and delete Users.\n\nThis window also shows important user information like the number of albums a user has, and when the account was created.\n\nYou can perform various tasks on the user list using the menu bar at the top of the page\n\nShould you want to return to the log in screen simply press Log out under the file drop down menu on the menu bar at the top.");

        alert.showAndWait();
    }
}


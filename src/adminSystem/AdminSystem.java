package adminSystem;

import java.io.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import adminSystem.model.User;
import javafx.collections.*;
import photos65.Photos;
import photos65.model.SaveData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AdminSystem extends Application {

	/**
	 * Store the Primary Stage object of the Application
	 */
    private Stage primaryStage;
    /**
     * Stores current User List in an Observable List structure
     */
    public ObservableList<User> users = FXCollections.observableArrayList();
    /**
     *  Maintain the current serializable SaveData object to store user data
     */
    public SaveData saveData;
    /**
     * Stores the current given controller for the application
     */
    AdminSystemController controller;
    /**
     * A FXML loader to read the fxml for the Admin Systems GUI
     */
    FXMLLoader loader = new FXMLLoader();

    /**
     * A constructor.
     *
     * A constructor with no arguments.
     */
    public AdminSystem(){

    }
    /**
     * The constructor of AdminSystem objects
     *
     * <p>
     *     Pulls from the serializable saveData object the User objects to populate a list of users
     * </p>
     *
     * @param saveData Description: the serializable saveData file that stores current system data
     */
    public AdminSystem(SaveData saveData) {
    	this.saveData = saveData;
    	for (User u : saveData.userlist) {
    		users.add(u);
    	}
    }

    /**
     * Generates a ObservableList of users
     *
     * <p>
     *     checks if user list has any members, if it does it returns the user list to generate the UserList view
     * </p>
     *
     *
     * @return Observable List of User objects
     */
    public ObservableList<User> getUserData() {
    	if (users != null) {
    		 return users;
    	} else {
    		return null;
    	}
    }

    /**
     * Overwrites the default start method of Application
     * <p>
     *    Overwrites start to further set key information and to show userList page
     * </p>
     *
     * @param primaryStage Description: the primary stage of the JavaFX app
     */
    @Override public void start(Stage primaryStage) {
    	
        this.primaryStage = primaryStage;
        //We call the window "Song Library".
        this.primaryStage.setTitle("Admin Subsystem");
        showUserOverview();
        //We set the window to not be resizable to avoid formatting errors.
        primaryStage.setResizable(false);
    }

    /**
     * This shows the main window.
     *
     * <p>
     *     Loads the controller for the AdminSystem and the fxml related to it, then Shows the primary stage as that window
     * </p>
     */
    public void showUserOverview() {
        try {
            loader.setLocation(AdminSystem.class.getResource("view/AdminSystem.fxml"));
            AnchorPane userOverview = (AnchorPane) loader.load();
            Scene scene = new Scene(userOverview);
            primaryStage.setScene(scene);
            primaryStage.show();
            controller = loader.getController();
            controller.setMainApp(this);
            controller.setUserList();
            primaryStage.setOnCloseRequest(event -> {
                save();
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the primary Stage of the application
     *
     * <p>
     *     Returns the current primaryStage of this Application to update and show.
     * </p>
     *
     * @return the primary Stage of this Application.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * This method overrides the default stop method.
     *
     * <p>
     *     Overwrites the default stop method to implement the save functionality and store the user data before closing.
     * </p>
     */
    @Override public void stop() {
    	saveData.searchedAlbum = null;
    	saveData.filteredPhotos = null;
    	save();
    }

    /**
     * Saves the current User data
     *
     * <p>
     *    Saves the current userdata to the SaveData object for serialization.
     * </p>
     */
    public void save() {
    	controller.saveUsers();
    	try {
			SaveData.writeData(saveData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Logs out of the Admin Subsystem
     *
     * <p>
     *     Saves the current User data and then logs out of the Admin system.
     *     Then prompts the user with the main login menu again.
     * </p>
     */
    public void logoff() {
    	save();
    	primaryStage.close();
    	Photos photoLibrary = new Photos(this.saveData);
    	photoLibrary.login(this.saveData);
    }
}
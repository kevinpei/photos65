package photos65;

import adminSystem.AdminSystem;
import albumList.AlbumList;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import photos65.model.SaveData;

import java.io.IOException;
import java.util.Optional;

public class Photos extends Application {

	/**
	 * The primary stage that everything will be displayed on.
	 */
    private Stage primaryStage = new Stage();
    /**
     * The save data that is passed between every main App to ensure all changes are saved.
     */
    public SaveData saveData;

    /**
     * Photo constructor with saveData
     * 
     * Photos constructor including a saveData object in order to initialize this class with save data.
     * 
     * @param saveData The saveData that's being passed between every main App.
     */
    public Photos(SaveData saveData) {
        this.saveData = saveData;
    }

    /**
     * Photo constructor
     *
     *Photo constructor without any save data. It creates a new empty save data object. This is most
     *commonly called when Photos is run for the first time.
     */
    public Photos() {
        this.saveData = new SaveData();
    }

    /**
     *Start method
     *
     *An overridden start method that sets save data to whatever is read from the save data file.
     *If there is no save data, then a new savedata is created.
     *
     * @param primaryStage The stage that everything is being run on.
     * @throws Exception Part of the overridden start method. An exception is thrown if anything
     * goes wrong during the start.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        saveData = SaveData.readData();
        if (saveData == null) {
            saveData = new SaveData();
        }
        login(saveData);
    }

    /**
     *Logs in a user.
     *
     *Logs in a user. It automatically sets the current save data equal to the input save data.
     *This is usually the function called by the other main Apps in order to log out. If admin
     *is given as the username, then the admin system window is opened. Otherwise, it logs in
     *as the given user. If the username does not exist, then it shows an error and asks you
     *to log in again.
     *
     * @param saveData The saveData transferred to this main App.
     */
    public void login(SaveData saveData) {
        this.saveData = saveData;
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Log In");
            dialog.setHeaderText("Please insert Username");
            dialog.setContentText("Username:");
            Optional<String> result = dialog.showAndWait();
            while (result.isPresent()) {
                if (result.get().equalsIgnoreCase("admin")) {
                    AdminSystem adminSystem = new AdminSystem(saveData);
                    adminSystem.start(primaryStage);
                    return;
                } else {
                    if (saveData.userlist != null) {
                        for (int i = 0; i < saveData.userlist.size(); i++) {
                            if (saveData.userlist.get(i).username.equalsIgnoreCase(result.get())) {
                                saveData.currentUser = saveData.userlist.get(i);
                                AlbumList albumList = new AlbumList(saveData);
                                albumList.start(primaryStage);
                                return;
                            }
                            
                        }
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Invalid Login");
                        alert.setHeaderText("Username not found");
                        alert.setContentText("Please enter a User that has already been registered");
                        alert.showAndWait();
                    }
                }
                result = dialog.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *Saves data.
     *
     *Saves the current data into save.dat, located withn the dat folder in this workspace.
     */
    public void save() {
        try {
            SaveData.writeData(saveData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *Overrides the stop method.
     *
     *This overrides the stop method, making sure that it saves all data and sets all unnecessary
     *fields, i.e. searchedAlbum and filteredPhotos, to null.
     */
    @Override
    public void stop() {
        saveData.searchedAlbum = null;
        saveData.filteredPhotos = null;
        save();
    }

    /**
     *The main method
     *
     *The main method for the entire application. It launches the login.
     *
     * @param args Arguments for main method.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *Returns primary stage
     *
     *Returns the stage that everything in the application will use.
     *
     * @return The primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}

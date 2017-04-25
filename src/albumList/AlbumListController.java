package albumList;

import java.util.ArrayList;
import java.util.Optional;

import adminSystem.model.User;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import albumList.AlbumList;
import albumList.model.Album;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */
public class AlbumListController {
	/**
	 * The tableview that contains all the albums the current user has.
	 */
    @FXML private TableView<Album> albumTable;
    /**
     * The table column that contains all the names of the albums.
     */
    @FXML private TableColumn<Album, String> nameColumn;
    /**
     * The table column that contains the number of photos in each album.
     */
    @FXML private TableColumn<Album, Integer> photoCountColumn;
    /**
     * The table column that contains the earliest photo date in the album.
     */
    @FXML private TableColumn<Album, String> earliestDateColumn;
    /**
     * The table column that contains the latest photo date in the album.
     */
    @FXML private TableColumn<Album, String> latestDateColumn;
    /**
     * The main application that uses this class as a controller.
     */
    @FXML private AlbumList mainApp = new AlbumList();

    public AlbumListController() {
    }

    /**
     * Initializes the list of albums.
     * 
     * Sets the value of each column to the appropriate values for each album. Also allows the
     * user to double click a row to open that album.
     */
    @FXML private void initialize() {
        // Initialize the user table with the appropriate columns.
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        photoCountColumn.setCellValueFactory(cellData -> cellData.getValue().getPhotoCount().asObject());
        earliestDateColumn.setCellValueFactory(cellData -> cellData.getValue().getEarliestDate());
        latestDateColumn.setCellValueFactory(cellData -> cellData.getValue().getLatestDate());
        albumTable.setRowFactory( tv -> {
            TableRow<Album> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Album rowData = row.getItem();
                    openAlbum();
                }
            });
            return row ;
        });
    }

    /**
     * Sets the mainApp.
     * 
     * Sets the main app that uses this class as a controller.
     * 
     * @param mainApp The main app that uses this class as a controller.
     */
    public void setMainApp(AlbumList mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Sets the album list.
     * 
     * Sets the album list to the album list retrieved by the main app from the save data.
     */
    public void setAlbumList() {
    	if (mainApp.getAlbumData() != null) {
    		albumTable.setItems(mainApp.getAlbumData());
    	}
    }
    
    /**
     * Deletes the selected album.
     * 
     * Deletes the selected album. If no album is selected, then an alert is shown asking to choose an
     * album. If an album is selected, a confirmation alert will open asking whether the user really wants
     * to delete the selected album. If done, then the selected album is removed and the next album in the
     * list is chosen. If there are no more albums, then no album is selected.
     */
    @FXML private void deleteAlbum() {
        int selectedIndex = albumTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= albumTable.getItems().size()) {
        	// Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Album Selected");
            alert.setContentText("Please select an album in the table.");
            alert.showAndWait();
            return;
        }
        // This is the confirmation pop up.
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.initOwner(mainApp.getPrimaryStage());
        confirm.setTitle("Delete?");
        confirm.setHeaderText("Do you wish to delete the selected album?");
        Optional<ButtonType> result = confirm.showAndWait();
        // If the user hits OK, the following code is executed.
        if (result.get() == ButtonType.OK) {
        	albumTable.getItems().remove(selectedIndex);
            if (selectedIndex < albumTable.getItems().size()) {
            	albumTable.getSelectionModel().select(selectedIndex);
            	albumTable.getFocusModel().focus(selectedIndex);
            } else {
            	if (albumTable.getItems().size() == 0) {
            	} else {
            		albumTable.getSelectionModel().select(selectedIndex - 1);
            		albumTable.getFocusModel().focus(selectedIndex - 1);
            	}
            }
        }
    }

    /**
     * Adds an album to the album list.
     * 
     * Adds an album to the end of the current list of albums. First, a linear search is performed on the
     * list to see if there are any other albums with the same name. If so, then return -1. Otherwise,
     * add the album and return the position of that album in the list.
     * 
     * @param list The ObservableList that the albums are stored in
     * @param album The album that is being added to the list of users
     * @return The position of the added album in the list. Returns -1 if an album with that name already
     * exists.
     */
    private static int addAlbum(ObservableList<Album> list, Album album) {
        if (list.size() == 0) {
            list.add(album);
            return 0;
        }
        int i = 0;
        while (i < list.size()) {
            if (album.name.compareToIgnoreCase(list.get(i).name) == 0) {
            	return -1;
            }
            i++;
        }
        list.add(album);
        return list.size() - 1;
    }
    
    /**
     * Shows an error when adding an album.
     * 
     * If the user tries to add an album with the same name as a preexisting album, then an
     * alert is opened that an album with the given name already exists.
     */
    @FXML private void additionError() {
    	Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Invalid Album");
        alert.setHeaderText("Same album is already present");
        alert.setContentText("An album with this name already exists.");

        alert.showAndWait();
    }
    
    /**
     * Checks the output of addAlbum.
     * 
     * If the output of addAlbum is -1, then that means that there is already an album with the same
     * name as the album that was attempted to be added. If the output is not -1, then the album
     * at that index is selected, which is the album that was just added.
     * 
     * @param check The output of addAlbum, which is checked for errors.
     */
    private void albumCheck(int check) {
    	if (check != -1) {
    		albumTable.getSelectionModel().select(check);
    		albumTable.getFocusModel().focus(check);
		} else {
			additionError();
		}
    }
    
   /**
    * Adds an album to the list.
    * 
    * This function asks the user for a name. If no name is given, then an alert is shown
    * and the user is prompted again for a name. After confirming the addition of a album, it
    * is tested to see whether there is already an album with that name. If not, then it is added
    * and selected.
    */
    @FXML private void addAlbum() {
    	String name = "";
    	while (name.equals("")) {
    		TextInputDialog dialogBox = new TextInputDialog("");
        	dialogBox.setTitle("Create a New Album");
        	dialogBox.setHeaderText("Create a new album");
        	dialogBox.setContentText("Please name this new album:");
        	Optional<String> result = dialogBox.showAndWait();
        	if (result.isPresent()){
        		name = result.get();
        	} else {
        		return;
        	}
        	if (name.equals("")) {
        		Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid Name");
                alert.setHeaderText("No name given");
                alert.setContentText("Please enter a name for this album.");
                alert.showAndWait();
        	}
    	}
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Add?");
        alert.setHeaderText("Do you wish to add an album with the given name?");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.get() == ButtonType.OK) {
        	albumCheck(addAlbum(albumTable.getItems(), new Album(name)));
        }
    }
    
    /**
     * Renames the selected album.
     * 
     * If no album is selected, it opens an alert saying no album is selected. Otherwise, the user is
     * prompted to enter a new name. If the name already exists, then an error is shown and the user
     * is asked to enter another name. Otherwise, assuming a valid name is given, the name of the selected
     * album is changed.
     */
    @FXML private void renameAlbum() {
    	int selectedIndex = albumTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= albumTable.getItems().size()) {
        	// Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Album Selected");
            alert.setContentText("Please select an album in the table.");
            alert.showAndWait();
            return;
        }
    	String name = "";
    	while (name.equals("")) {
    		TextInputDialog dialogBox = new TextInputDialog("");
        	dialogBox.setTitle("Rename an Album");
        	dialogBox.setHeaderText("Rename this album");
        	dialogBox.setContentText("Please give a new name for this album:");
        	Optional<String> result = dialogBox.showAndWait();
        	if (result.isPresent()){
        		name = result.get();
        	} else {
        		return;
        	}
        	if (name.equals("")) {
        		Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid Name");
                alert.setHeaderText("No name given");
                alert.setContentText("Please enter a new name for this album.");
                alert.showAndWait();
        	}
    	}
    	Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Rename?");
        alert.setHeaderText("Do you wish to rename this album with the given name?");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.get() == ButtonType.OK) {
        	ObservableList<Album> list = albumTable.getItems();
        	int i = 0;
            while (i < list.size()) {
                if (name.compareToIgnoreCase(list.get(i).name) == 0) {
                	Alert alreadyExists = new Alert(AlertType.WARNING);
                	alreadyExists.initOwner(mainApp.getPrimaryStage());
                	alreadyExists.setTitle("Invalid Name");
                	alreadyExists.setHeaderText("Name already exists");
                	alreadyExists.setContentText("There is already an album with this name.");
                	alreadyExists.showAndWait();
                	return;
                }
                i++;
            }
        	albumTable.getItems().get(selectedIndex).name = name;
        	albumTable.getColumns().get(0).setVisible(false);
        	albumTable.getColumns().get(0).setVisible(true);
        }
    }
    
    /**
     * Logs off the current user.
     * 
     * Logs off the current user by closing AlbumList and opening Photos, calling its login function.
     */
    @FXML public void logOff() {
    	mainApp.logoff();
    }
    
    /**
     * Saves the current album list.
     * 
     * Saves the current list of albums and its size in SaveData.
     */
    public void saveAlbums() {
    	mainApp.saveData.currentUser.albums = new ArrayList<Album>();
    	for (Album a : albumTable.getItems()) {
    		mainApp.saveData.currentUser.albums.add(a);
    	}
    	mainApp.saveData.currentUser.albumNumber = albumTable.getItems().size();
    }
    
    /**
     * Opens the selected album.
     * 
     * Opens the selected PhotoAlbum. If none is selected, an error is shown. Otherwise, everything
     * is saved and a new PhotoAlbum is opened for the selected album in the table.
     */
    @FXML public void openAlbum() {
    	int selectedIndex = albumTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= albumTable.getItems().size()) {
        	// Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Album Selected");
            alert.setContentText("Please select an album in the table.");
            alert.showAndWait();
            return;
        }
        mainApp.saveData.currentAlbum = albumTable.getItems().get(selectedIndex);
    	mainApp.openAlbum();
    }
    
    /**
     * Returns a list of all albums.
     * 
     * Returns an ObservableList of all albums currently registered under the user.
     * 
     * @return An ObservableList of all albums.
     */
    @FXML public ObservableList<Album> returnAlbumList(){
        return albumTable.getItems();
    }

    /**
     * Shows the help window for this page
     *
     * Shows the help window to provide the user with some advice on how to use the Album List
     */
    @FXML public void help(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About the Album List");
        alert.setHeaderText(null);
        alert.setContentText("From here you can view your Albums.\n\nThis system also shows important information about the albums in the list like their names, the number of photos that each album has, and when the first and last photo in the album was created.\n\nAlbums can be selected by single clicking on the list. Albums can be viewed by either double clicking the album in the list or by selecting the album and then using the open album option in the Edit drop down menu on the menu bar.\n\nFurther actions that can be enacted on the albums are also found in the menu bar at the top of the page. Actions such as rename album and delete album act on the currently selected album.\n\nFurther should you want to return to the log in screen simply press Log out under the file drop down menu on the menu bar at the top");

        alert.showAndWait();
    }
}

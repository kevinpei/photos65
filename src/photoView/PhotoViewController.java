package photoView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import photoView.PhotoView;
import photoAlbum.model.Photo;
import albumList.model.Album;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */
public class PhotoViewController {
	/**
	 * The tableview that contains the current photo in the album.
	 */
    @FXML private TableView<Photo> photoTable;
    /**
     * The table column that contains the caption of the current photo.
     */
    @FXML private TableColumn<Photo, String> captionColumn;
    /**
     * The table column that contains the tags of the current photo.
     */
    @FXML private TableColumn<Photo, String> tagColumn;
    /**
     * The table column that contains the date the current photo was taken.
     */
    @FXML private TableColumn<Photo, String> dateCreatedColumn;
    /**
     * The ImageView that contains the current photo.
     */
    @FXML private ImageView imageView;
    /**
     * The main application that uses this class as a controller.
     */
    @FXML private PhotoView mainApp = new PhotoView();

    public PhotoViewController() {
    }

    /**
     * Initializes the photo details.
     * 
     * Sets the value of each column to the appropriate values for the photo.
     */
    @FXML private void initialize() {
        // Initialize the user table with the appropriate columns.
        captionColumn.setCellValueFactory(cellData -> cellData.getValue().getCaption());
        tagColumn.setCellValueFactory(cellData -> cellData.getValue().getTags());
        dateCreatedColumn.setCellValueFactory(cellData -> cellData.getValue().getDateCreated());    
    }

    /**
     * Sets the mainApp
     * 
     * Sets the main app that uses this class as a controller.
     * 
     * @param mainApp The main app that uses this class as a controller.
     */
    public void setMainApp(PhotoView mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Sets the current photo.
     * 
     * Sets the current photo to whatever the current photo is in the main App. It updates the tableView
     * and imageView accordingly.
     */
    public void setPhoto() {
    	if (mainApp.getPhoto() != null) {
    		photoTable.getItems().add(mainApp.getPhoto());
    		Image image = new Image("file:" + mainApp.getPhoto().fileProperty());
    		imageView.setImage(image);
    	}
    }

    /**
     * Opens a dialog box to change the caption of the photo
     *
     * Changes the caption of the photo by prompting the user with a dialog box to change the current caption.
     * If it already exists in the album, it shows an error and asks the user to input another caption.
     */
    @FXML private void editCaption() {
        String caption = "";
        while (caption.equals("")) {
            TextInputDialog dialogBox = new TextInputDialog("");
            dialogBox.setTitle("Rename a caption");
            dialogBox.setHeaderText("Rename this caption");
            dialogBox.setContentText("Please give a new caption for this photo:");
            Optional<String> result = dialogBox.showAndWait();
            if (result.isPresent()){
                caption = result.get();
            } else {
                return;
            }
            if (caption.equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid caption");
                alert.setHeaderText("No caption given");
                alert.setContentText("Please enter a new caption for this photo.");
                alert.showAndWait();
            }
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Rename?");
        alert.setHeaderText("Do you wish to rename this photo with the given caption?");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.get() == ButtonType.OK) {
            ArrayList<Photo> list = mainApp.saveData.currentAlbum.photos;
            int i = 0;
            while (i < list.size()) {
                if (caption.compareToIgnoreCase(list.get(i).caption) == 0) {
                    Alert alreadyExists = new Alert(AlertType.WARNING);
                    alreadyExists.initOwner(mainApp.getPrimaryStage());
                    alreadyExists.setTitle("Invalid caption");
                    alreadyExists.setHeaderText("Photo with identical caption already exists");
                    alreadyExists.setContentText("There is already an photo with this caption.");
                    alreadyExists.showAndWait();
                    return;
                }
                i++;
            }
            photoTable.getItems().get(0).caption = caption;
            photoTable.getColumns().get(0).setVisible(false);
            photoTable.getColumns().get(0).setVisible(true);
        }
    }

    /**
     *Removes a tag from the current photo.
     *
     *Asks the user to remove a tag type from the current photo. If there is no tag of that type for
     *the current photo, it shows an error and asks the user to input another tag type.
     */
    @FXML private void removeTag() {
        TextInputDialog dialogBox = new TextInputDialog();
        dialogBox.setTitle("Remove Photo Tag");
        dialogBox.setHeaderText("Remove a photo tag type");
        dialogBox.setContentText("Please enter a tag type to remove for this photo:");
        Optional<String> tagType = dialogBox.showAndWait();
        while (tagType.isPresent()){
        	if (tagType.get().equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid tag type");
                alert.setHeaderText("No tag type given");
                alert.setContentText("Please enter a tag type to remove for this photo.");
                alert.showAndWait();
            } else {
            	if (photoTable.getItems().get(0).tags.containsKey(tagType.get().toLowerCase())) {
            		Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.initOwner(mainApp.getPrimaryStage());
                    alert.setTitle("Remove tag?");
                    alert.setHeaderText("Do you wish to remove the specified tag?");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    if (confirmation.get() == ButtonType.OK) {
                    	photoTable.getItems().get(0).tags.remove(tagType.get().toLowerCase());
                        photoTable.getColumns().get(0).setVisible(false);
                        photoTable.getColumns().get(0).setVisible(true);
                    }
                    return;
                }
            	Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Tag Doesn't Exist");
                alert.setHeaderText("Specified tag does not exist");
                alert.setContentText("This photo does not contain the specified tag type. Please enter a different tag type.");
                alert.showAndWait();
            }
            dialogBox = new TextInputDialog(tagType.get());
            dialogBox.setTitle("Remove Photo Tag");
            dialogBox.setHeaderText("Remove a photo tag type");
            dialogBox.setContentText("Please enter a tag type to remove for this photo:");
            tagType = dialogBox.showAndWait();
        }
    }

    /**
     *Adds a tag to the current photo.
     *
     *Asks the user to input a tag type and tag name for the current photo. If the given tag type already
     *exists for the given photo, then an error is shown and the user is asked to input a new tag type.
     */
    @FXML private void addTag() {
        TextInputDialog dialogBox = new TextInputDialog();
        dialogBox.setTitle("Add Photo Tag");
        dialogBox.setHeaderText("Add a photo tag type");
        dialogBox.setContentText("Please enter a tag type for this photo:");
        Optional<String> tagType = dialogBox.showAndWait();
        while (tagType.isPresent()){
        	if (tagType.get().equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid tag type");
                alert.setHeaderText("No tag type given");
                alert.setContentText("Please enter a new tag type for this photo.");
                alert.showAndWait();
            } else {
            	if (!photoTable.getItems().get(0).tags.containsKey(tagType.get().toLowerCase())) {
                	dialogBox = new TextInputDialog();
                    dialogBox.setTitle("Add Photo Tag");
                    dialogBox.setHeaderText("Add a photo tag name");
                    dialogBox.setContentText("Please enter a tag name for that type for this photo:");
                    Optional<String> tagName = dialogBox.showAndWait();
                    while (tagName.isPresent()){
                    	if (tagType.get().equals("")) {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.initOwner(mainApp.getPrimaryStage());
                            alert.setTitle("Invalid tag name");
                            alert.setHeaderText("No tag name given");
                            alert.setContentText("Please enter a new tag name for this photo.");
                            alert.showAndWait();
                    	} else {
                    		Alert alert = new Alert(AlertType.CONFIRMATION);
                            alert.initOwner(mainApp.getPrimaryStage());
                            alert.setTitle("Add tag?");
                            alert.setHeaderText("Do you wish to add the specified tag?");
                            Optional<ButtonType> confirmation = alert.showAndWait();
                            if (confirmation.get() == ButtonType.OK) {
                                photoTable.getItems().get(0).tags.put(tagType.get().toLowerCase(), tagName.get().toLowerCase());
                                photoTable.getColumns().get(0).setVisible(false);
                                photoTable.getColumns().get(0).setVisible(true);
                            }
                            return;
                    	}
                    	dialogBox = new TextInputDialog(tagType.get());
                        dialogBox.setTitle("Add Photo Tag");
                        dialogBox.setHeaderText("Add a photo tag name");
                        dialogBox.setContentText("Please enter a tag name for this photo:");
                        tagType = dialogBox.showAndWait();
                    }
                    return;
                }
            	Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Tag Already Exists");
                alert.setHeaderText("Tag type already exists");
                alert.setContentText("A tag of this type already exists. Please enter a different tag type.");
                alert.showAndWait();
            }
            dialogBox = new TextInputDialog(tagType.get());
            dialogBox.setTitle("Add Photo Tag");
            dialogBox.setHeaderText("Add a photo tag type");
            dialogBox.setContentText("Please enter a tag type for this photo:");
            tagType = dialogBox.showAndWait();
        }
    }

    /**
     *Changed the photo to the next in the album.
     *
     *Called when the right button is called, it changes the current photo to the next photo in the album.
     *If it is the last photo in the album, it loops back to the front.
     */
    @FXML private void changePhotoRight() {
    	mainApp.changePhoto(1);
    	photoTable.getItems().clear();
    	photoTable.getItems().add(mainApp.getPhoto());
    }

    /**
     *Changed the photo to the previous one in the album.
     *
     *Called when the left button is called, it changes the current photo to the previous photo in the album.
     *If it is the first photo in the album, it loops back to the last photo.
     */
    @FXML private void changePhotoLeft() {
    	mainApp.changePhoto(-1);
    	photoTable.getItems().clear();
    	photoTable.getItems().add(mainApp.getPhoto());
    }

    /**
     *Closes the photoView.
     *
     *Closes the photoView and opens up the photoAlbum again.
     */
    @FXML public void closePhoto() {
    	mainApp.closePhoto();
    }

    /**
     *Logs off the current user.
     *
     *Closes the photoView and opens the login menu again.
     */
    @FXML public void logOff() {
    	mainApp.logoff();
    }

    /**
     * Shows the help window for this page
     *
     * Shows the help window to provide the user with some advice on how to use the Photo Album
     */
    @FXML public void help(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About the Photo View");
        alert.setHeaderText(null);
        alert.setContentText("From here you can view the photo you selected from the selected Album.\n\nThis view also shows important information about the photo currently being viewed like the caption of that photo, when the photo was created, and any tags the photo may have.\n\nThe current photo being viewed can be changed by single clicking the arrows on either side of the menu, to cycle to the next and previous photo.\n\nFurther actions that can be enacted on the photos and this album are also found in the menu bar at the top of the page.\n\nFurther should you want to return to the photo album window or log out both options are provided in the file drop down menu on the menu bar at the top.");

        alert.showAndWait();
    }
}

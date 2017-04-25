package photoAlbum;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import photoAlbum.model.*;
import albumList.model.Album;
import photoAlbum.PhotoAlbum;

import javax.swing.*;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */
public class PhotoAlbumController {
    /**
     * The tableview that contains all the photos in the Photo Album
     */
    @FXML private TableView<Photo> photoTable;
    /**
     * The table column that contains all the thubnails of the photo in each row.
     */
    @FXML private TableColumn<Photo, String> thumbnailColumn;
    /**
     * The table column that contains the caption of the photo in each row.
     */
    @FXML private TableColumn<Photo, String> captionColumn;
    /**
     * The table column that contains the tags of the photo in each row.
     */
    @FXML private TableColumn<Photo, String> tagsColumn;
    /**
     * The table column that contains the date of the photo in each row.
     */
    @FXML private TableColumn<Photo, String> dateColumn;
    /**
     * The main application that uses this class as a controller
     *
     */
    @FXML private PhotoAlbum mainApp = new PhotoAlbum();

    /**
     * Desktop reference to indicate start point of file chooser
     */
    private Desktop desktop = Desktop.getDesktop();

    /**
     * A constructor.
     *
     * A constructor with no arguments.
     */
    public PhotoAlbumController() {
    }

    /**
     * Initializes the list of photos
     *
     * Sets the value of each column to the appropriate values for each photo
     */
    @FXML private void initialize() {
    	thumbnailColumn.setCellValueFactory(new PropertyValueFactory<Photo, String>("file"));
    	thumbnailColumn.setPrefWidth(200);
    	thumbnailColumn.setCellFactory(new Callback<TableColumn<Photo, String>, TableCell<Photo, String>>()
    	    {
    	        @Override
    	        public TableCell<Photo, String> call(TableColumn<Photo, String> param)
    	        {
    	            return new TableCell<Photo, String>()
    	            {
    	            	VBox vb;
        	        	ImageView img; {
        	        		vb = new VBox();
        	        		vb.setAlignment(Pos.CENTER);
        	        		img = new ImageView();
        	        		img.setFitHeight(100);
	                        img.setFitWidth(100);
	                        vb.getChildren().addAll(img);
	                        setGraphic(vb);
	                    }
    	                @Override
    	                public void updateItem(String item, boolean empty)
    	                {
    	                	super.updateItem(item, empty);
    	                    if(item != null)
    	                    {
								img.setImage(new Image(item));
    	                    } else {
    	                    	img.setImage(null);
    	                    }
    	                }
    	            };
    	        }
    	    });
        captionColumn.setCellValueFactory(cellData -> cellData.getValue().getCaption());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().getDateCreated());
        tagsColumn.setCellValueFactory(cellData ->  cellData.getValue().getTags());
        photoTable.setRowFactory( tv -> {
            TableRow<Photo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Photo rowData = row.getItem();
                    openPhoto();
                }
            });
            return row ;
        });
    }

    /**
     * Sets the mainApp
     *
     * Sets the main app that uses this class as a controller
     *
     * @param mainApp The main app that uses this class as a controller
     */
    public void setMainApp(PhotoAlbum mainApp) {
        this.mainApp = mainApp;
    }

    public void setPhotoList() {
    	if (mainApp.getPhotoData() != null) {
    		photoTable.setItems(mainApp.getPhotoData());
    	}
    }
    
    /**
     * Deletes the selected Photo
     *
     * Deletes the selected photo. If no Photo is selected, then an alert is shown asking to choose a
     * Photo. If a photo is selected, a confirmation alert will open asking whether the user really wants
     * to delete the selected photo. If done, then the selected photo is removed and the next photo in the
     * list is chosen. If there are no more photos, then no photo is selected.
     */
    @FXML private void deletePhoto() {
        int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Photo Selected");
            alert.setContentText("Please select a Photo in the table.");
            alert.showAndWait();
            return;
        }
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.initOwner(mainApp.getPrimaryStage());
        confirm.setTitle("Delete?");
        confirm.setHeaderText("Do you wish to delete the selected Photo?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.get() == ButtonType.OK) {
            photoTable.getItems().remove(selectedIndex);
            if (selectedIndex < photoTable.getItems().size()) {
                photoTable.getSelectionModel().select(selectedIndex);
                photoTable.getFocusModel().focus(selectedIndex);
            } else {
                if (photoTable.getItems().size() == 0) {
                } else {
                    photoTable.getSelectionModel().select(selectedIndex - 1);
                    photoTable.getFocusModel().focus(selectedIndex - 1);
                }
            }
        }
    }

    /**
     * Adds a photo to the photo list for this album
     *
     * Adds a photo to the end of the current list of photos. First, a linear search is performed on the
     * list to see if there are any other photos with the same filepath. If so, then return -1. Otherwise,
     * add the photo and return the position of that photo in the list.
     *
     * @param list The ObservableList that the photos are stored in
     * @param photo The photo that is being added to the list of photos
     * @return The position of the added photo in the list. Returns -1 if a photo with that filepath already
     * exists.
     */
    private static int addPhoto(ObservableList<Photo> list, Photo photo) {
        if (list.size() == 0) {
            list.add(photo);
            return 0;
        }
        int i = 0;
        while (i < list.size()) {
            if (photo.img.toURI().toString().compareToIgnoreCase(list.get(i).img.toURI().toString()) == 0) {
                return -1;
            }
            i++;
        }
        list.add(photo);
        return list.size() - 1;
    }

    /**
     * Shows an error when adding a photo
     *
     * If the admin tries to add a photo with the same filepath as a preexisting photo, then an
     * alert is opened that a photo with the given filepath already exists.
     */
    @FXML private void additionError() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Invalid Photo");
        alert.setHeaderText("Same photo is already present");
        alert.setContentText("This photo already exists in this album.");

        alert.showAndWait();
    }

    /**
     * Checks the output of addPhoto
     *
     * If the output of addPhoto is -1, then that means that there is already a photo with the same
     * filepath as the photo that was attempted to be added. If the output is not -1, then the photo
     * at that index is selected, which is the photo that was just added.
     *
     * @param check The output of addPhoto, which is checked for errors.
     */
    private void photoCheck(int check) {
        if (check != -1) {
            photoTable.getSelectionModel().select(check);
            photoTable.getFocusModel().focus(check);
        } else {
            additionError();
        }
    }

    /**
     * Adds a photos to the list
     *
     * This function prompts the user with a FileChooser from the OS to pick a file of a photo. After confirming the addition of a photo, it
     * is tested to see whether there is already a photo with that filepath. If not, then it is selected.
     */
    @FXML private void addPhoto() {
    	final FileChooser fileChooser = new FileChooser();
       // fileChooser.setFileFilter(new ImageFilter());
    	configureFileChooser(fileChooser);
    	File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
    	if (file == null) {
    		return;
    	}
        String caption = "";
        while (caption.equals("")) {
            TextInputDialog dialogBox = new TextInputDialog("");
            dialogBox.setTitle("Create a new Photo Caption");
            dialogBox.setHeaderText("Create a new caption");
            dialogBox.setContentText("Please enter the new caption:");
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
                alert.setContentText("Please enter a caption for this photo.");
                alert.showAndWait();
            }
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle("Add?");
        alert.setHeaderText("Do you wish to add a photo with the given caption?");
        Optional<ButtonType> confirmation = alert.showAndWait();

        //NOT DONE HERE DONT LET THIS STAY LIKE THIS IT IS NOT DONE OBVIOUSLY
        if (confirmation.get() == ButtonType.OK) {
            photoCheck(addPhoto(photoTable.getItems(), new Photo(caption, file)));
        }
    }

    /**
     * Configure the FileChooser to not allow non-Photo file types
     *
     * Configuration for FileChooser prepare the FileChooser of addPhoto to select a photo file
     * by limiting FileChooser display to only files of type .jpg, .png, .bmp, .tiff
     *
     * @param fileChooser
     */
    private static void configureFileChooser(
            final FileChooser fileChooser) {      
                fileChooser.setTitle("View Pictures");
                fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
                );                 
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.jpg","*.png","*.bmp","*.tiff"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
                );
        }

    /**
     * Edits the caption of the currently selected Photo,
     *
     * Edits the caption of the currently selected photo by prompting the user with a input dialog to fill in a new caption.
     * If no photo is selected then the user is prompted with a alert telling them to pick a photo.
     * If the caption is empty the user is prompted with an alert telling them to input a valid caption.
     * After the User is prompted for confirmation of this action.
     */
    @FXML private void editCaption() {
        int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Album Selected");
            alert.setContentText("Please select an album in the table.");
            alert.showAndWait();
            return;
        }
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
            photoTable.getItems().get(selectedIndex).caption = caption;
            photoTable.getColumns().get(0).setVisible(false);
            photoTable.getColumns().get(0).setVisible(true);
        }
    }

    /**
     * Remove the tag of the currently selected Photo,
     *
     * removes the tag of the currently selected photo by prompting the user with a input dialog to select the tag type and value to be removed.
     * If no photo is selected then the user is prompted with a alert telling them to pick a photo.
     * If the selected photo does not have an example of the selected tag to be removed then it prompts the user with an alert requesting an input of a valid tag.
     * After the User is prompted for confirmation of this action.
     */
    @FXML private void removeTag() {
        int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Photo Selected");
            alert.setContentText("Please select a photo in the table.");
            alert.showAndWait();
            return;
        }
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
            	if (photoTable.getItems().get(selectedIndex).tags.containsKey(tagType.get().toLowerCase())) {
            		Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.initOwner(mainApp.getPrimaryStage());
                    alert.setTitle("Remove tag?");
                    alert.setHeaderText("Do you wish to remove the specified tag?");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    if (confirmation.get() == ButtonType.OK) {
                    	photoTable.getItems().get(selectedIndex).tags.remove(tagType.get().toLowerCase());
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
     * Add a tag of the currently selected Photo,
     *
     * Add a tag to the currently selected photo by prompting the user with a input dialog to select the tag type and value to be added.
     * If no photo is selected then the user is prompted with a alert telling them to pick a photo.
     * If the selected photo does have an example of the selected tag to be added then it prompts the user with an alert requesting an input of a valid tag that is not already present.
     * After the User is prompted for confirmation of this action.
     */
    @FXML private void addTag() {
        int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Photo Selected");
            alert.setContentText("Please select a photo in the table.");
            alert.showAndWait();
            return;
        }
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
            	if (!photoTable.getItems().get(selectedIndex).tags.containsKey(tagType.get().toLowerCase())) {
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
                                photoTable.getItems().get(selectedIndex).tags.put(tagType.get().toLowerCase(), tagName.get().toLowerCase());
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
     * Removes the current photo and stores temporarily to be moved elsewhere.
     *
     * Saves the currently selected photo in the SaveData object and then removes it from the current album.
     * After it has been saved to SaveData it can be pasted in another album, or in the current album again if needed.
     *
     */
    @FXML private void cutPhoto() {
    	int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Album Selected");
            alert.setContentText("Please select an album in the table.");
            alert.showAndWait();
            return;
        }
        mainApp.saveData.clipboard = photoTable.getItems().remove(selectedIndex);
        if (selectedIndex < photoTable.getItems().size()) {
            photoTable.getSelectionModel().select(selectedIndex);
            photoTable.getFocusModel().focus(selectedIndex);
        } else {
            if (photoTable.getItems().size() == 0) {
            } else {
                photoTable.getSelectionModel().select(selectedIndex - 1);
                photoTable.getFocusModel().focus(selectedIndex - 1);
            }
        }
    }

    /**
     * Pastes the photo temporarily saved to the SaveData into the current album.
     *
     * Adds a copy of the previously copied or cut photo into the current album.
     *
     */
    @FXML private void pastePhoto() {
    	if (mainApp.saveData.clipboard == null) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("Nothin in Clipboard");
            alert.setHeaderText("No photo in clipboard");
            alert.setContentText("Please cut or copy a photo before pasting.");
            alert.showAndWait();
            return;
        }
    	photoCheck(addPhoto(photoTable.getItems(), mainApp.saveData.clipboard));
    }

    /**
     * Stores temporarily a copy of the currently selected photo to be moved elsewhere.
     *
     * Saves the currently selected photo in the SaveData object.
     * After it has been saved to SaveData it can be pasted in another album.
     *
     */
    @FXML private void copyPhoto() {
    	int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Album Selected");
            alert.setContentText("Please select an album in the table.");
            alert.showAndWait();
            return;
        }
        mainApp.saveData.clipboard = photoTable.getItems().get(selectedIndex);
    }

    /**
     * Filters the list of photos in the current album by a given tag
     *
     * Prompts the user for a Tag type and value to filter the photo list by.
     * If no tag, value, or and invalid one of either of those, then the application prompts the user to provide a valid tag type and value combo.
     * Adds a filter to the current Album list to only show the photos that match the given search parameters.
     * Repopulates current Photo Album view with only the items that match the parameters, and stores all others back in the album.
     *
     */
    @FXML private void searchPhotoTags() {
    	TextInputDialog dialogBox = new TextInputDialog();
        dialogBox.setTitle("Search by Tag");
        dialogBox.setHeaderText("Search by tag type");
        dialogBox.setContentText("Please enter the tag type to search by:");
        Optional<String> tagType = dialogBox.showAndWait();
        while (tagType.isPresent()){
        	if (tagType.get().equals("")) {
        		Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid tag type");
                alert.setHeaderText("No tag type given");
                alert.setContentText("Please enter a tag type to search by.");
                alert.showAndWait();
        	} else {
        		dialogBox = new TextInputDialog();
                dialogBox.setTitle("Search by Tag");
                dialogBox.setHeaderText("Search by tag name");
                dialogBox.setContentText("Please enter the tag name to search by:");
                Optional<String> tagName = dialogBox.showAndWait();
                while (tagName.isPresent()){
                	if (tagName.get().equals("")) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.initOwner(mainApp.getPrimaryStage());
                        alert.setTitle("Invalid tag name");
                        alert.setHeaderText("No tag name given");
                        alert.setContentText("Please enter a new tag name for this photo.");
                        alert.showAndWait();
                	} else {
                		ObservableList<Photo> matchingPhotos = FXCollections.observableArrayList();
                		for (Photo p : photoTable.getItems()) {
                			if (p.tags.containsKey(tagType.get().toLowerCase())) {
                				if (p.tags.get(tagType.get().toLowerCase()).equals(tagName.get().toLowerCase())) {
                        			matchingPhotos.add(p);
                        		}
                			}
                    	}
                		if (mainApp.saveData.searchedAlbum == null) {
                    		mainApp.saveData.searchedAlbum = new ArrayList<Photo>();
                        	for (Photo p : photoTable.getItems()) {
                        		mainApp.saveData.searchedAlbum.add(p);
                        	}
                    	}
                		photoTable.getItems().clear();
                    	for (Photo p : matchingPhotos) {
                    		photoTable.getItems().add(p);
                    		photoTable.getColumns().get(0).setVisible(false);
                        	photoTable.getColumns().get(0).setVisible(true);
                    	}
                        mainApp.getPrimaryStage().setTitle("Album - {results: "+ tagType.get() + "=" + tagName.get() + "}");
                        return;
                	}
                	dialogBox = new TextInputDialog(tagName.get());
                    dialogBox.setTitle("Search by Tag");
                    dialogBox.setHeaderText("Search by tag name");
                    dialogBox.setContentText("Please enter the tag name to search by:");
                    tagName = dialogBox.showAndWait();
                }
                return;
        	}
        	dialogBox = new TextInputDialog(tagType.get());
            dialogBox.setTitle("Search by Tag");
            dialogBox.setHeaderText("Search by tag type");
            dialogBox.setContentText("Please enter the tag type to search by:");
            tagType = dialogBox.showAndWait();
        }
    }

    /**
     * Checks to see whether a given Date is valid as per the bounds of the LocalDate Class functionality
     *
     * LocalDate uses a DateTimeFormatter to check if a provided date is in the right format, if it is then this function returns a TRUE.
     * If not False is returned to where this method is called.
     *
     * @param dateString The user's given date string to be checked for validity
     * @param dtf The applications prefered Date Time Format, MM/DD/YYYY, to be used to check the dateString for validity.
     * @return Boolean indicating whether or not the given date String is valid or not as per the specifications of this application.
     */
    public boolean isValidDate(String dateString, DateTimeFormatter dtf) {
        try {
            LocalDate.parse(dateString, dtf);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Filters the list of photos in the current album by a given pair of Dates
     *
     * Prompts the user for a Start date and End date to filter the photo list by.
     * If either one is left empty or are invalid, then the application prompts the user to provide a valid verison of either the Start date of End Date.
     * Adds a filter to the current Album list to only show the photos that match the given search parameters.
     * Repopulates current Photo Album view with only the items that match the parameters, and stores all others back in the album.
     *
     */
    @FXML private void searchPhotoDate() {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    	TextInputDialog dialogBox = new TextInputDialog();
        dialogBox.setTitle("Search by Date");
        dialogBox.setHeaderText("Start Date");
        dialogBox.setContentText("Please enter the start date in the format MM/DD/YYYY to search:");
        Optional<String> startDate = dialogBox.showAndWait();
        Optional<String> endDate = null;
        while (startDate.isPresent()){
        	if (this.isValidDate(startDate.get(), dtf)) {
        		dialogBox = new TextInputDialog();
                dialogBox.setTitle("Search by Date");
                dialogBox.setHeaderText("End Date");
                dialogBox.setContentText("Please enter the end date in the format MM/DD/YYYY to search:");
                endDate = dialogBox.showAndWait();
                while (endDate.isPresent()){
                	if (this.isValidDate(endDate.get(), dtf)) {
                		ObservableList<Photo> matchingPhotos = FXCollections.observableArrayList();
                		for (Photo p : photoTable.getItems()) {
                			checkPhotoDate(startDate, endDate, dtf, p, matchingPhotos);
                		}
                		if (mainApp.saveData.searchedAlbum == null) {
                    		mainApp.saveData.searchedAlbum = new ArrayList<Photo>();
                        	for (Photo p : photoTable.getItems()) {
                        		mainApp.saveData.searchedAlbum.add(p);
                        	}
                    	}
                		photoTable.getItems().clear();
                    	for (Photo p : matchingPhotos) {
                    		photoTable.getItems().add(p);
                    		photoTable.getColumns().get(0).setVisible(false);
                        	photoTable.getColumns().get(0).setVisible(true);
                    	}

                        if(endDate != null) {
                            mainApp.getPrimaryStage().setTitle("Album - {results: " + startDate.get() + " - " + endDate.get() + " }");
                        }
                    	return;
                	} else {
                		Alert alert = new Alert(AlertType.WARNING);
                        alert.initOwner(mainApp.getPrimaryStage());
                        alert.setTitle("Invalid Date");
                        alert.setHeaderText("Invalid date format");
                        alert.setContentText("Please enter a date in the format MM/DD/YYYY.");
                        alert.showAndWait();
                	}
                	endDate = dialogBox.showAndWait();
                }
                return;
        	} else {
        		Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid Date");
                alert.setHeaderText("Invalid date format");
                alert.setContentText("Please enter a date in the format MM/DD/YYYY.");
                alert.showAndWait();
        	}
            startDate = dialogBox.showAndWait();
        }
    }

    /**
     * Checks if the a given photo was created between the given Start date and End date parameters
     *
     * Takes the given Start Date and End Date and checks if the given photo was created between those dates.
     * If it was it is added to the provided Observable List of photos to repopulate the photo album view with.
     * @param startDate the Start Date for valid photos to be added to the new Observable list.
     * @param endDate   the End date for valid photos to be added to the new Observable list.
     * @param dtf       A Date Time Formatter to be used to parse the requisite information to compare dates
     * @param p         The photo to be check for matching parameters
     * @param matchingPhotos The observable list to add the photo to if it matched the parameters.
     */
    private void checkPhotoDate(Optional<String> startDate, Optional<String> endDate, 
    		DateTimeFormatter dtf, Photo p, ObservableList<Photo> matchingPhotos) {
    	LocalDate start = LocalDate.parse(startDate.get(), dtf);
    	LocalDate end = LocalDate.parse(endDate.get(), dtf);
    	if (p.dateCreated.compareTo(start) >= 0 && p.dateCreated.compareTo(end) <= 0) {
    		matchingPhotos.add(p);
    	}
    }

    /**
     *  Removes the currently selected filter
     *
     *  Removes all of the currently provided filters and repopulates the album view with all of the photos in the album.
     *  If no filter is active then an alert prompt the user that no filter is present on the given photo album view.
     *
     */
    @FXML public void clearSearchResults() {
    	if (mainApp.saveData.searchedAlbum == null) {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Search Results");
            alert.setHeaderText("Nothing searched");
            alert.setContentText("Please enter search terms before you clear the search results.");
            alert.showAndWait();
            return;
        }
    	photoTable.getItems().clear();
    	for (Photo p : mainApp.saveData.searchedAlbum) {
    		photoTable.getItems().add(p);
    		photoTable.getColumns().get(0).setVisible(false);
        	photoTable.getColumns().get(0).setVisible(true);
    	}
    	mainApp.saveData.searchedAlbum = null;
    	mainApp.saveData.filteredPhotos = null;
    	mainApp.getPrimaryStage().setTitle("Album");
    }

    /**
     *  Takes the current list of Photos (filtered or not) and creates a copy album.
     *
     *  Prompts the user to input a new Album name, if that album name is not already present in the list of albums the user has, then it is accepted.
     *  If the album name is already present then the user is prompted for a valid album name.
     *  After the User is prompted for confirmation of this action.
     *
     */
    @FXML public void createNewAlbum() {
    	String name = "";
        while (name.equals("")) {
            TextInputDialog dialogBox = new TextInputDialog("");
            dialogBox.setTitle("Create a New Album");
            dialogBox.setHeaderText("Create a new album");
            dialogBox.setContentText("Please enter the name of the new album:");
            Optional<String> result = dialogBox.showAndWait();
            if (result.isPresent()){
            	name = result.get();
            } else {
                return;
            }
            if (name.equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Invalid name");
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
        	for (Album a : mainApp.saveData.currentUser.albums) {
        		if (name.compareTo(a.name) == 0) {
        			Alert alreadyExists = new Alert(AlertType.WARNING);
                	alreadyExists.initOwner(mainApp.getPrimaryStage());
                	alreadyExists.setTitle("Invalid Name");
                	alreadyExists.setHeaderText("Name already exists");
                	alreadyExists.setContentText("There is already an album with this name.");
                	alreadyExists.showAndWait();
                	return;
        		}
        	}
        	Album searchedAlbum = new Album(name);
        	searchedAlbum.photos = new ArrayList<Photo>();
        	for (Photo p : photoTable.getItems()) {
        		searchedAlbum.photos.add(p);
        		searchedAlbum.photoCount++;
        	}
        	mainApp.saveData.currentUser.albums.add(searchedAlbum);
        }
    }

    /**
     * Logs off the current user.
     *
     * Logs off the current user by closing PhotoAlbum and opening Photos, calling its login function.
     */
    @FXML public void logOff() {
    	mainApp.logoff();
    }

    /**
     * Closes the current window of the Album and returns to the Album list window
     *
     * Calls the Close Album function in the mainApp.
     */
    @FXML public void closeAlbum() {
    	mainApp.closeAlbum();
    }

    /**
     * Saves all of the current data
     * Saves all of the current (non-filtered) photos to the Album.
     * Then saves that album and all other albums to the User.
     * All before saving all Users to the Serializable SaveData Object.
     *
     */
    public void savePhotos() {
    	mainApp.saveData.currentAlbum.photos = new ArrayList<Photo>();
    	if (mainApp.saveData.searchedAlbum == null) {
    		for (Photo p : photoTable.getItems()) {
        		mainApp.saveData.currentAlbum.photos.add(p);
        	}
    		mainApp.saveData.currentAlbum.photoCount = photoTable.getItems().size();
    	} else {
    		mainApp.saveData.filteredPhotos = new ArrayList<Photo>();
    		for (Photo p : photoTable.getItems()) {
        		mainApp.saveData.filteredPhotos.add(p);
        	}
    		for (Photo p : mainApp.saveData.searchedAlbum) {
        		mainApp.saveData.currentAlbum.photos.add(p);
        	}
    		mainApp.saveData.currentAlbum.photoCount = mainApp.saveData.searchedAlbum.size();
    	}
    	mainApp.saveData.searchedAlbum = null;
    }

    /**
     * Opens the selected photo.
     *
     * Opens the selected Photo. If none is selected, an error is shown.
     * Otherwise, the currently selected photo is opened in PhotoView application.
     */
    @FXML public void openPhoto() {
    	int selectedIndex = photoTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= photoTable.getItems().size()) {
        	// Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Photo Selected");
            alert.setContentText("Please select an photo in the table.");
            alert.showAndWait();
            return;
        }
        mainApp.saveData.photoNumber = selectedIndex;
    	mainApp.openPhoto();
    }
    
    /**
     * Returns a list of all users
     *
     * Returns an ObservableList of all users currently registered in the admin subsystem
     *
     * @return An ObservableList of all users
     */
    @FXML public ObservableList<Photo> returnUserList(){
        return photoTable.getItems();
    }

    /**
     * Shows the help window for this page
     *
     * Shows the help window to provide the user with some advice on how to use the Photo Album
     */
    @FXML public void help(){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About the Photo Album");
        alert.setHeaderText(null);
        alert.setContentText("From here you can view the photos within your selected Album.\n\nThis system also shows important information about the photos in the album like the caption of that photo, when the photo was created, and any tags the photo may have.\n\nPhotos can be selected by single clicking on the list. Photos can be viewed by either double clicking the photo in question or by selecting the photo and then using the view photo option in the view drop down menu on the menu bar.\n\nFurther actions that can be enacted on the photos and this album are also found in the menu bar at the top of the page. Actions such as copy and cut act on the currently selected photo.\n\nPhotos can be filtered or searched by either the date of their creation or a specified tag from the search drop down menu from the menu bar at the top of the window. Doing so will repopulate the list with photos that match the given specification.\nThe results of the current search can be made into a new album from the search drop down menu or otherwise the current search can be removed and all items in the album should be repopulated.\n\nFurther should you want to return to the user's album list window or log out both options are provided in the file drop down menu on the menu bar at the top.");

        alert.showAndWait();
    }
}

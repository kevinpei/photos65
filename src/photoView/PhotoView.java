package photoView;

import java.io.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import adminSystem.model.User;
import albumList.AlbumList;
import javafx.collections.*;
import javafx.event.EventHandler;
import photoAlbum.PhotoAlbum;
import photos65.Photos;
import photoAlbum.model.Photo;
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


public class PhotoView extends Application {

	/**
	 *The primary stage that will be used to display PhotoView.
	 */
    private Stage primaryStage;
    /**
     * The current photo being shown in PhotoView.
     */
    public Photo currentPhoto;
    /**
     * The current number of the photo being shown in PhotoView.
     */
    public int photoNumber;
    /**
     * The save data tracking everything being stored in Photos.
     */
    public SaveData saveData;
    /**
     * The FXML loader that's loading the PhotoView.fxml file.
     */
    FXMLLoader loader = new FXMLLoader();
    /**
     * The controller for this main App and fxml file.
     */
    PhotoViewController controller;
    
    /**
     * A constructor.
     * 
     * A constructor with no arguments.
     */
    public PhotoView() {
    }

    /**
     *A constructor with saveData.
     *
     *Initializes a new PhotoView according to the saveData. It will set the current photo on
     *display and set which photos are in the view depending on the search results.
     *
     * @param saveData The save data to read from.
     */
    public PhotoView(SaveData saveData) {
    	this.saveData = saveData;
    	this.photoNumber = saveData.photoNumber;
    	if (saveData.filteredPhotos == null) {
    		this.currentPhoto = saveData.currentAlbum.photos.get(photoNumber);
    	} else {
    		this.currentPhoto = saveData.filteredPhotos.get(photoNumber);
    	}
    }

    /**
     *Returns the current photo.
     *
     *Returns the current photo. If there is no current photo, return null.
     *
     * @return The current photo or null if there is none.
     */
    public Photo getPhoto() {
    	if (currentPhoto != null) {
    		 return currentPhoto;
    	} else {
    		return null;
    	}
    }

    /**
     *Overridden start method
     *
     *Sets the stage and title, then shows the fxml file. It also makes sure the main window
     *is not resizable to prevent formatting errors.
     *
     * @param primaryStage The primary stage to show everything on.
     */
    @Override public void start(Stage primaryStage) {
    	
        this.primaryStage = primaryStage;
        //We call the window "Song Library".
        this.primaryStage.setTitle("Photo View");
        showPhotoOverview();
        //We set the window to not be resizable to avoid formatting errors.
        primaryStage.setResizable(false);
    }

    /**
     *Sets the appearance of PhotoView.
     *
     *Sets the controller, fxml file, and main App for the controller. It also sets the current
     *photo on display and automatically saves when closed.
     */
    public void showPhotoOverview() {
        try {
            // Load the song library xml file.
            
            loader.setLocation(PhotoView.class.getResource("view/PhotoView.fxml"));
            AnchorPane userOverview = (AnchorPane) loader.load();
            Scene scene = new Scene(userOverview);
            primaryStage.setScene(scene);
            primaryStage.show();
            // Give the controller access to the main app.
            controller = loader.getController();
            controller.setMainApp(this);
            controller.setPhoto();
            primaryStage.setOnCloseRequest(event -> {
                save();
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * 
     * Returns the primary stage.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     *Changes the current photo.
     *
     *Changes the current photo by the amount indicated by step. This will always be 1 or -1, indicating
     *going forward or backward a photo in the current album. It changes the current photo according to
     *the step and the current album.
     *
     * @param step Amount to go forward or backward in the album.
     */
    public void changePhoto (int step) {
    	photoNumber = saveData.photoNumber + step;
    	if (saveData.filteredPhotos == null) {
    		if (photoNumber < 0) {
        		photoNumber = saveData.currentAlbum.photos.size() - 1;
        	} else  if (photoNumber > saveData.currentAlbum.photos.size() - 1) {
        		photoNumber = 0;
        	}
    		saveData.photoNumber = photoNumber;
        	currentPhoto = saveData.currentAlbum.photos.get(photoNumber);
    	} else {
    		if (photoNumber < 0) {
        		photoNumber = saveData.filteredPhotos.size() - 1;
        	} else  if (photoNumber > saveData.filteredPhotos.size() - 1) {
        		photoNumber = 0;
        	}
    		saveData.photoNumber = photoNumber;
        	currentPhoto = saveData.filteredPhotos.get(photoNumber);
    	}
    	controller.setPhoto();
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
    @Override public void stop() {
    	saveData.searchedAlbum = null;
    	saveData.filteredPhotos = null;
    	save();
    }

    /**
     *Closes PhotoView and goes to PhotoAlbum.
     *
     *Closes the current stage after saving, then opens a new photoalbum using the newly saved data.
     */
    public void closePhoto() {
        save();
        primaryStage.close();
        PhotoAlbum photoAlbum = new PhotoAlbum(this.saveData);
        photoAlbum.start(primaryStage);
    }

    /**
     *Logs off the current user.
     *
     *Closes the current stage after saving, then opens a new Photos using the newly saved data and
     *opens up the login menu.
     */
    public void logoff() {
    	save();
    	primaryStage.close();
    	Photos photoLibrary = new Photos(this.saveData);
    	photoLibrary.login(this.saveData);
    }
}
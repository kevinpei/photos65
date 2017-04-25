package photoAlbum;

import java.io.*;

import photos65.model.SaveData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import photoView.PhotoView;
import photoAlbum.model.Photo;
import javafx.collections.*;
import javax.swing.JFileChooser;
import java.io.File;
import java.time.LocalDate;

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
import photos65.*;
import albumList.AlbumList;
import albumList.model.Album;


public class PhotoAlbum extends Application implements Serializable{

    /**
     * Store the primary Stage of the application
     */
    private Stage primaryStage;
    /**
     * List of Photos for the given PhotoAlbum in an ObservableList structure
     */
    private ObservableList<Photo> photos = FXCollections.observableArrayList();
    /**
     * SaveData object to store and retrieve saved data of users
     */
    public SaveData saveData = new SaveData();

    FXMLLoader loader = new FXMLLoader();
    PhotoAlbumController controller;

    /**
     * A constructor.
     *
     * A constructor with no arguments.
     */
    public PhotoAlbum(){

    }
    /**
     * Photo Album Constructor
     *<p>
     * Creates a PhotoAlbum object from the provided SaveData.
     *</p>
     *
     * @param saveData Description: The provided SaveData from which to generate the PhotoAlbum.
     */
    public PhotoAlbum(SaveData saveData){
    	this.saveData = saveData;
    	if (saveData.filteredPhotos != null) {
    		for (Photo p : saveData.filteredPhotos) {
        		photos.add(p);
        	}
    	} else if (saveData.currentAlbum.photos != null) {
    		for (Photo p : saveData.currentAlbum.photos) {
        		photos.add(p);
        	}
    	}
    }

    /**
     * Gets the List of Photos for this Album.
     *
     * <p>
     *     Returns a Observable List of photos in this Album.
     * </p>
     *
     * @return
     */
    public ObservableList<Photo> getPhotoData() {
    	if (photos != null) {
    		 return photos;
    	} else {
    		return null;
    	}
    }

    /**
     * This method overrides the default start method.
     * <p>
     *     Overrides the default start method of application, to add key information specific to this app, and to call the  showPhotoAlbum() method.
     * </p>
     *
     * @param primaryStage Description: the primary Stage for which this application will run on.
     */

    @Override public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        //We call the window "Song Library".
        this.primaryStage.setTitle("Album");
        showPhotoAlbum();
        //We set the window to not be resizable to avoid formatting errors.
        primaryStage.setResizable(false);
    }

    /**
     * This shows the main window.
     *
     * <p>
     *     Loads the controller for the PhotoAlbum and the fxml related to it, then shows the primary stage as that window
     * </p>
     */

    public void showPhotoAlbum() {
        try {
            // Load the song library xml file.
            
            loader.setLocation(PhotoAlbum.class.getResource("view/PhotoAlbum.fxml"));
            AnchorPane userOverview = (AnchorPane) loader.load();
            Scene scene = new Scene(userOverview);
            primaryStage.setScene(scene);
            primaryStage.show();
            // Give the controller access to the main app.
            controller = loader.getController();
            controller.setMainApp(this);
            controller.setPhotoList();
            primaryStage.setOnCloseRequest(event -> {
            	saveData.filteredPhotos = null;
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
    	controller.savePhotos();
        try {
            SaveData.writeData(saveData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Logs out of the Photo Album
     *
     * <p>
     *     Saves the current User data and then logs out of the Photo Album system.
     *     Then prompts the user with the main login menu again.
     * </p>
     */
    public void logoff() {
        save();
        saveData.searchedAlbum = null;
    	saveData.filteredPhotos = null;
        primaryStage.close();
        Photos photoLibrary = new Photos(this.saveData);
        photoLibrary.login(this.saveData);
    }

    /**
     * Closes out of the current Photo Album
     *
     * <p>
     *     Saves the current User data and then closes out of the current Photo Album.
     *     Then prompts the user with the Album List page again.
     * </p>
     */
    public void closeAlbum() {
        save();
        saveData.searchedAlbum = null;
    	saveData.filteredPhotos = null;
        primaryStage.close();
        AlbumList albumList = new AlbumList(this.saveData);
        albumList.start(primaryStage);
    }

    /**
     * Opens the currently selected photo
     * <p>
     *     Opens the currently selected photo (either by double click or menu option) and shows it in the photo view application.
     * </p>
     */
    public void openPhoto() {
        save();
        primaryStage.close();
        PhotoView photoView = new PhotoView(this.saveData);
        photoView.start(primaryStage);
    }

}
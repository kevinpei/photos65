package albumList;

import java.io.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import albumList.model.Album;
import javafx.collections.*;

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

import photos65.Photos;
import photos65.model.SaveData;
import adminSystem.model.User;
import photoAlbum.PhotoAlbum;
import photoView.PhotoViewController;


public class AlbumList extends Application {
	
	/**
	 *The primary stage that will be used to display AlbumList.
	 */
    private Stage primaryStage;
    /**
     * A list of all albums currently stored in the current user.
     */
    public ObservableList<Album> albums = FXCollections.observableArrayList();
    /**
     * The save data tracking everything being stored in Photos.
     */
    public SaveData saveData;
    /**
     * The FXML loader that's loading the AlbumList.fxml file.
     */
    FXMLLoader loader = new FXMLLoader();
    /**
     * The controller for this main App and fxml file.
     */
    AlbumListController controller;

    
    /**
     * A constructor.
     * 
     * A constructor with no arguments.
     */
    public AlbumList() {
    }

    
    /**
     *A constructor with saveData.
     *
     *Initializes a new AlbumList according to the saveData. It will set the album list equal to
     *the list of albums stored in the current user.
     *
     * @param saveData The save data to read from.
     */
    public AlbumList(SaveData saveData) {
    	this.saveData = saveData;
    	if (saveData.currentUser.albums != null) {
    		for (Album a : saveData.currentUser.albums) {
        		albums.add(a);
        	}
    	}
    }

    /**
     *Returns the list of albums.
     *
     *Returns the list of albums of the current user. If there are no albums, return null.
     *
     * @return The current album list or null if there is none.
     */
    public ObservableList<Album> getAlbumData() {
    	if (albums != null) {
    		 return albums;
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
        this.primaryStage.setTitle("Album List");
        showAlbumOverview();
        //We set the window to not be resizable to avoid formatting errors.
        primaryStage.setResizable(false);
    }

    /**
     *Sets the appearance of AlbumList.
     *
     *Sets the controller, fxml file, and main App for the controller. It also sets the current
     *album list and automatically saves when closed.
     */
    public void showAlbumOverview() {
        try {
            // Load the song library xml file.
            
            loader.setLocation(AlbumList.class.getResource("view/AlbumList.fxml"));
            AnchorPane albumOverview = (AnchorPane) loader.load();
            Scene scene = new Scene(albumOverview);
            primaryStage.setScene(scene);
            primaryStage.show();
            // Give the controller access to the main app.
            controller = loader.getController();
            controller.setMainApp(this);
            controller.setAlbumList();
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
     *Saves data.
     *
     *Saves the current data into save.dat, located withn the dat folder in this workspace.
     */
    public void save() {
    	controller.saveAlbums();
    	try {
			SaveData.writeData(saveData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

    /**
     *Closes AlbumList and goes to PhotoAlbum.
     *
     *Closes the current stage after saving, then opens a new PhotoAlbum using the newly saved data.
     */
    public void openAlbum() {
    	save();
    	primaryStage.close();
    	PhotoAlbum album = new PhotoAlbum(this.saveData);
    	album.start(primaryStage);
    }
}
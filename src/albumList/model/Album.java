
package albumList.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import photoAlbum.model.Photo;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */

public class Album implements Serializable{
	/**
	 * A String containing the name of the album.
	 */
	public String name;
	/**
	 * An integer representing how many photos are in this album.
	 */
	public int photoCount;
	/**
	 * A LocalDate object representing the date this album was created.
	 */
	public LocalDate dateCreated;
	
	public ArrayList<Photo> photos;
	
	/** 
	 * A constructor.
	 * 
	 * This constructor creates an Album object with the given name. It automatically sets the 
	 * photoCount to 0 and the dateCreated to the current date.
	 * 
	 * @param username The username this user object has.
	 */
	public Album (String name) {
		this.name = name;
		this.photoCount = 0;
		dateCreated = LocalDate.now();
	}
	
	/**
	 * Changes how many photos this photo album has
	 * 
	 * This function changes the photoCount of this album.
	 * 
	 * @param albumNumber The number to set the number of photos to.
	 */
	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}
	/**
	 * Returns name
	 * 
	 * This function returns the name as a StringProperty.
	 * 
	 * @return Name as a StringProperty
	 */
	public StringProperty getName() {
		return new SimpleStringProperty(name);
	}
	
	/**
	 * Returns number of photos
	 * 
	 * This function returns the number of photos in this album as an IntegerProperty.
	 * 
	 * @return AlbumNumber as a IntegerProperty
	 */
	public IntegerProperty getPhotoCount() {
		return new SimpleIntegerProperty(photoCount);
	}
	
	/**
	 * Returns date created
	 * 
	 * This function returns the local date as a StringProperty. First, the date is formatted as
	 * MM/dd/yyyy, then is returned as a StringProperty object.
	 * 
	 * @return Date created as a StringProperty
	 */
	public StringProperty getEarliestDate() {
		if (photos == null) {
			return new SimpleStringProperty("N/A");
		}
		if (photos.size() == 0) {
			return new SimpleStringProperty("N/A");
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate earliestDate = photos.get(0).dateCreated;
		for (Photo p: photos) {
			if (earliestDate.compareTo(p.dateCreated) > 0) {
				earliestDate = p.dateCreated;
			}
		}
		return new SimpleStringProperty(dtf.format(earliestDate));
	}
	
	public StringProperty getLatestDate() {
		if (photos == null) {
			return new SimpleStringProperty("N/A");
		}
		if (photos.size() == 0) {
			return new SimpleStringProperty("N/A");
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDate latestDate = photos.get(0).dateCreated;
		for (Photo p: photos) {
			if (latestDate.compareTo(p.dateCreated) < 0) {
				latestDate = p.dateCreated;
			}
		}
		return new SimpleStringProperty(dtf.format(latestDate));
	}
}

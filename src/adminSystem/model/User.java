package adminSystem.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import albumList.model.Album;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */

public class User implements Serializable{
	/**
	 * A String containing the username of the given user.
	 */
	public String username;
	/**
	 * A String containing the password of the given user. If it is not assigned, then that means that
	 * this user has no password.
	 */
	public String password;
	/**
	 * An integer representing how many albums this user has.
	 */
	public int albumNumber;
	/**
	 * A LocalDate object representing the date this user was created.
	 */
	public LocalDate dateCreated;
	/**
	 * A list of all albums the user currently has.
	 */
	public ArrayList<Album> albums;
	
	/** 
	 * A constructor.
	 * 
	 * This constructor only includes a username. Use the next constructor to include both username
	 * and password. This constructor automatically sets the date created to the local date on the
	 * machine.
	 * 
	 * @param username The username this user object has.
	 */
	public User (String username) {
		this.username = username;
		this.password = "";
		dateCreated = LocalDate.now();
	}
	/**
	 * A constructor.
	 * 
	 * This constructor includes both username and password and sets them accordingly. This constructor
	 * automatically sets the date created to the local date on the machine.
	 * 
	 * @param username The username this user object has.
	 * @param password The password this user object has.
	 */
	public User (String username, String password) {
		this(username);
		this.password = password;
	}
	/**
	 * Changes how many albums a user currently has created.
	 * 
	 * This function changes how many albums a user currently has created.
	 * 
	 * @param albumNumber The number to set the number of albums created to.
	 */
	public void setAlbumNumber(int albumNumber) {
		this.albumNumber = albumNumber;
	}
	/**
	 * Returns username
	 * 
	 * This function returns username as a StringProperty.
	 * 
	 * @return Username as a StringProperty
	 */
	public StringProperty getUsername() {
		return new SimpleStringProperty(username);
	}
	
	/**
	 * Returns number of albums created
	 * 
	 * This function returns the number of created albums as an IntegerProperty.
	 * 
	 * @return AlbumNumber as a IntegerProperty
	 */
	public IntegerProperty getAlbumNumber() {
		return new SimpleIntegerProperty(albumNumber);
	}
	
	/**
	 * Returns date created
	 * 
	 * This function returns the local date as a StringProperty. First, the date is formatted as
	 * MM/dd/yyyy, then is returned as a StringProperty object.
	 * 
	 * @return Date created as a StringProperty
	 */
	public StringProperty getDateCreated() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		return new SimpleStringProperty(dtf.format(dateCreated));
	}
}

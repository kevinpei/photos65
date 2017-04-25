package photoAlbum.model;

import javafx.beans.property.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 * @author Kevin Pei and Andrew Dos Reis
 * @version 1.0
 */

public class Photo implements Serializable{
    /**
     * A Buffered img containing the name of the album.
     */
    public File img;
    /**
     * An integer representing how many photos are in this album.
     */
    public String caption;
    /**
     * A LocalDate object representing the date this album was created.
     */
    public LocalDate dateCreated;

    /**
     * A ArrayList of Strings to represent Tag values
     */
    public Hashtable<String, String> tags = new Hashtable<String, String>();
    /**
     * A constructor.
     *
     * This constructor creates an Album object with the given name. It automatically sets the
     * photoCount to 0 and the dateCreated to the current date.
     *
     * @param caption The username this user object has.
     * @param file The string of a filename for a file of a photo this user object has.
     */
    public Photo (String caption, File file) {
        this.caption = caption;
        this.dateCreated = LocalDate.ofEpochDay(file.lastModified() / 86400000L);
        this.img = file;
    }

    /**
     * Returns Caption of this photo
     *
     * This function returns caption currently attached to this photo as a StringProperty.
     *
     * @return Caption created as a StringProperty
     */
    public StringProperty getCaption() {
        return new SimpleStringProperty(caption);
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
    /**
     * Returns file path
     *
     * This function returns the file path as a StringProperty. It appends "file:" to the front
     * so that it can be used to create images.
     *
     * @return Date created as a StringProperty
     */
    public StringProperty fileProperty(){ 
    	return new SimpleStringProperty("file:" + img.getPath()); 
    	}
    
    /**
     * Adds a String tag to this photo's tag Hashtable.
     *
     * This function adds a given tag to the Photo's Hashtable of Tags.
     * This is done after the photo is created from the menu.
     * 
     * @param tagName Tag type to be added to the photo
     * @param tagValue Tag name to be added to the photo
     */
    public void addTag(String tagName, String tagValue){
        this.tags.put(tagName, tagValue);
    }
    
    /**
     * Removes a String tag from this photo's tag ArrayList.
     *
     * This function removes a given tag from the Photo's Hashtable of Tags.
     * This is done after the photo is created from the menu.
     * 
     * @param tagName Tag type to be removed from the photo
     */
    public void removeTag(String tagName) {
    	this.tags.remove(tagName);
    }
    
    /**
     * Returns all tags of this photo.
     * 
     * Turns the ArrayList of tags into a StringProperty so that it can be used in javafx table columns.
     * All tags are separated by commas. If there are no tags, then an empty StringProperty is returned.
     * 
     * @return List of tags as a StringProperty
     */
    public StringProperty getTags() {
    	if (tags.size() == 0) {
    		return new SimpleStringProperty("");
    	}
    	SimpleStringProperty tagsList = null;
    	for (String key : tags.keySet()) {
    		if (tagsList == null) {
    			tagsList = new SimpleStringProperty(key + "=" + tags.get(key) + "\n");
    		} else {
    			tagsList = new SimpleStringProperty(tagsList.get() + key + "=" + tags.get(key) + "\n");
    		}
    	}
    	return tagsList;
    }

    /**
     * Returns the list of tags as a single string.
     *
     * Returns all of the tags of the photo with proper formating for use in the tagColumn.
     *
     * @return Formatted String of all of the tags of the photo.
     */
    public String getStringTags() {
    	if (tags.size() == 0) {
    		return new String("");
    	}
    	String tagsList = null;
    	for (String key : tags.keySet()) {
    		if (tagsList == null) {
    			tagsList = new String(key + "=" + tags.get(key) + "\n");
    		} else {
    			tagsList = new String(tagsList + ", " + key + "=" + tags.get(key) + "\n");
    		}
    	}
    	return tagsList;
    }
}

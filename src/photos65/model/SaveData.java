package photos65.model;

import java.io.*;
import java.util.ArrayList;
import photoAlbum.model.Photo;

//import Library.PhotoAlbum;
import adminSystem.model.User;
import albumList.model.Album;

public class SaveData implements Serializable{
	
	/**
	 * Serial ID used to keep track of the current version of SaveData.
	 */
	private static final long serialVersionUID = 8697123432L;
	/**
	 * The directory to save the SaveData in.
	 */
	public static final String storeDir = "dat";
	/**
	 * The file name to save SaveData as.
	 */
	public static final String storeFile = "photos.dat";
	/**
	 * A list of all users currently saved.
	 */
	public ArrayList<User> userlist;
	/**
	 * The current user being accessed.
	 */
	public User currentUser;
	/**
	 * The current album being accessed.
	 */
	public Album currentAlbum;
	/**
	 * The number in the album of the current photo being accessed
	 */
	public int photoNumber;
	/**
	 * The current photo being cut or copied.
	 */
	public Photo clipboard;
	/**
	 * The original album before it was searched by tag or date.
	 */
	public ArrayList<Photo> searchedAlbum;
	/**
	 * The list resulting from the search, used in photo view to view only searched photos.
	 */
	public ArrayList<Photo> filteredPhotos;
	
	/**
	 * A constructor.
	 * 
	 * A constructor that initializes an empty userlist.
	 */
	public SaveData() {
		userlist = new ArrayList<User>();
	}
	
	/**
	 * Saves the data to file.
	 * 
	 * Serializes SaveData and saves it to storeDir as storeFile. 
	 * 
	 * @param saveData The SaveData to save.
	 * @throws IOException Exception thrown if the given file does not exist.
	 */
	public static void writeData(SaveData saveData) throws IOException{
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(storeDir + File.separator + storeFile));
		oos.writeObject(saveData);
		oos.close();
	}
	
	/**
	 * Reads from file.
	 * 
	 * Reads SaveData from the file in storeDir with name storeFile and return it. 
	 * If there is no such file, then return null.
	 * 
	 * @return Whatever SaveData is stored at the given location.
	 * @throws ClassNotFoundException Throws an error if a class is not found when loaded by a given name.
	 */
	public static SaveData readData() throws ClassNotFoundException {
    	ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(
			new FileInputStream(storeDir + File.separator + storeFile));
			SaveData saveData = (SaveData)ois.readObject();
	    	ois.close();
	    	return saveData;
		} catch (IOException e) {
			return null;
		}
    	
    }
}

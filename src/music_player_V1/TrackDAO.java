package music_player_V1;

import java.io.File;
import java.util.ArrayList;

public interface TrackDAO {

	public ArrayList<Track> readTracks(String folder, final String suffix);

	public Track readDetails(File file);

	/**
	 * retrieves all the books from the database. A list of size 0 is returned if no
	 * books are stored.
	 * 
	 * @return ArrayList<Track>
	 */
	public ArrayList<Track> getTracks();

	/**
	 * persists a book to the database
	 * 
	 * @param book
	 */
	public void writeTrack(Track track);

	/**
	 * Print a list of al books to the console
	 */
	public void showAllTracks();

	/**
	 * Open the connection to the database
	 * 
	 */

	public void openConnection();

	/**
	 * Close the connection to the database
	 *
	 * When you're finished with the db its very important that you close the
	 * connections so that data gets persisted.
	 */
	public void closeConnection();

}

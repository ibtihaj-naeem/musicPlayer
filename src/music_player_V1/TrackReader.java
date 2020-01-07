package music_player_V1;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

public class TrackReader implements TrackDAO {

	private ArrayList<Track> tracks = null;
	private Connection connect = null;
	private Statement statement = null;

	public TrackReader() {
		tracks = new ArrayList<Track>();
	}

	@Override
	public ArrayList<Track> readTracks(String audiofilepath, final String suffix) {
		File audioFile = new File(audiofilepath);
		File[] audioFiles = audioFile.listFiles(new FilenameFilter() {
			/**
			 * Accept files with matching suffix.
			 * 
			 * @param dir  The directory containing the file.
			 * @param name The name of the file.
			 * @return true if the name ends with the suffix.
			 */
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(suffix);
			}
		});

		// Put all the matching files into the organizer.
		// for(File file : audioFiles) {
		Track trackDetails = readDetails(audioFile);
		tracks.add(trackDetails);
		writeTrack(trackDetails);
		// }
		return tracks;
	}

	@Override
	public Track readDetails(File file) {
		{
			// The information needed.
			String filepath = file.getPath();
			String title = "unknown";
			String artist = "unknown";
			String album = "unknown";
			double duration = 0;

			// Look for artist and title in the name of the file.
			String details = file.getName();
			String[] parts = details.split("-");

			if (parts.length == 2) {
				artist = parts[0];
				String titlePart = parts[1];
				// Remove a file-type suffix.
				parts = titlePart.split("\\.");
				if (parts.length >= 1) {
					title = parts[0];
				} else {
					title = titlePart;
				}
			}
			return new Track(filepath, title, artist, album, duration);
		}
	}

	@Override
	public ArrayList<Track> getTracks() {
		ArrayList<Track> readTracks = new ArrayList<Track>();
		try {
			// This is our prepared query, that selects everything from book
			// table
			String query = "SELECT * FROM Tracks";

			// Executes the query and stores the results.
			ResultSet results = this.statement.executeQuery(query);

			while (results.next()) {

				/*
				 * Assign results from query to their own variable. We can reference columns by
				 * their name of index value e.g. 0
				 */
				String filepath = results.getString("Filepath");
				String title = results.getString("Title");
				String artist = results.getString("Artist");
				String album = results.getString("Album");
				double duration = results.getDouble("Duration");
				readTracks.add(new Track(filepath, title, artist, album, duration));
			}

		} catch (SQLException e) {
			System.out.println("SQLException happened while retrieving records- abort programmme");
			throw new RuntimeException(e);
		}
		return readTracks;
	}

	@Override
	public void writeTrack(Track track) {
		try {

			// Prepared statements allow us to use variables in them more
			// efficiently
			PreparedStatement preparedStatement = this.connect.prepareStatement(
					"INSERT INTO Tracks (Filepath, Title, Artist, Album, Duration) VALUES (?, ?, ?, ?, ?)");

			preparedStatement.setString(1, track.getFilepath());
			preparedStatement.setString(2, track.getTitle());
			preparedStatement.setString(3, track.getArtist());
			preparedStatement.setString(4, track.getAlbum());
			preparedStatement.setDouble(5, track.getDuration());

			// This executes the query. Please note there are different execute
			// types.
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("SQLException happened while writing a track- abort programmme");
			throw new RuntimeException(e);
		}

	}

	@Override
	public void showAllTracks() {

		ArrayList<Track> dbTracks = getTracks();
		Iterator<Track> iter = dbTracks.iterator();

		// While there are still results...
		Track tmpTrack;
		while (iter.hasNext()) {
			tmpTrack = iter.next();
			// Prints results to console
			System.out.println("Filepath: " + tmpTrack.getFilepath() + " ----");
			System.out.println("Track Title: " + tmpTrack.getTitle() + " ----");
			System.out.println("Artist: " + tmpTrack.getArtist());
			System.out.println("Album: " + tmpTrack.getAlbum());
			System.out.println("Duration:" + tmpTrack.getDuration() + "\n");
		}

	}

	@Override
	public void openConnection() {
		try {
			// recreate the connection if needed
			if (this.connect == null || this.connect.isClosed()) {
				// change the DB Path
				//
				try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				this.connect = DriverManager.getConnection(
						// jdbc:hsqldb:file:myDBfilestore
						"jdbc:hsqldb:file:db_data/myDBfilestore;ifexists=true;shutdown=true", "SA", "");
			}
			// recreate the statement if needed
			if (this.statement == null || this.statement.isClosed()) {
				this.statement = this.connect.createStatement();
			}

		} catch (SQLException e) {
			System.out.println("ERROR - Failed to create a connection to the database");
			throw new RuntimeException(e);
		}
	}

	@Override
	public void closeConnection() {
		try {

			if (this.statement != null) {
				this.statement.close();
			}
			if (this.connect != null) {
				this.connect.close();
			}
			System.out.println("Closed the connection to the database");
		} catch (Exception e) {
			System.out.print("ERROR-Failed to close the connection to the database");
			throw new RuntimeException(e);
		}

	}

	public void createDatabase() {
		try {
			statement = connect.createStatement();

			String query = "CREATE TABLE TRACKS " + "( filepath VARCHAR(255), " + " title VARCHAR(255), "
					+ " artist VARCHAR(255), " + " album VARCHAR(255), " + " duration DOUBLE, "
					+ "id   int   GENERATED   BY   DEFAULT   AS   IDENTITY(start   with   1,INCREMENT BY 1) NOT NULL,"
					+ " PRIMARY KEY ( id ))";

			statement.executeUpdate(query);
		}

		catch (SQLException e) {
			System.out.println("SQLException happened while creating a table- abort programmme");
			throw new RuntimeException(e);

		}
	}

	public void dropDatabase() {
		try {
			statement = connect.createStatement();

			String query = "DROP TABLE TRACKS ";

			statement.executeUpdate(query);
		}

		catch (SQLException e) {
			System.out.println("SQLException happened while deleting a table- abort programmme");
			throw new RuntimeException(e);

		}

	}
}

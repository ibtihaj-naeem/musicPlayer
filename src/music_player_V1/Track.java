package music_player_V1;

public class Track {

	private String filepath = null;
	private String title = null;
	private String artist = null;
	private String album = null;
	private double duration = 0;

	public Track(String filepath, String title, String artist, String album, double duration) {
		// Super here for convention
		super();
		this.filepath = filepath;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.duration = duration;

	}

	public String getFilepath() {
		return filepath;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public double getDuration() {
		return duration;
	}

}

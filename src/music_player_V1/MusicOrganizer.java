package music_player_V1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JTable;

import net.proteanit.sql.DbUtils;

import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;
import java.awt.Font;

public class MusicOrganizer {

	public JFrame frame;
	private JTextField textFieldTitle;
	private JTextField textFieldArtist;
	private JTextField textFieldAlbum;
	private JTextField textFieldDuration;
	TrackReader reader = new TrackReader();
	private Connection connect = null;
	private JTable table;
	private JTextField textFieldTrackID;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MusicOrganizer window = new MusicOrganizer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MusicOrganizer() {
		initialize();
		reader.openConnection();
		reader.dropDatabase();
		reader.createDatabase();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(1280, 720));
		frame.setPreferredSize(new Dimension(1280, 720));
		frame.setSize(new Dimension(1280, 720));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 1262, 600);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		textFieldTitle = new JTextField();
		textFieldTitle.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		textFieldTitle.setBounds(130, 73, 141, 22);
		panel.add(textFieldTitle);
		textFieldTitle.setColumns(10);

		textFieldArtist = new JTextField();
		textFieldArtist.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		textFieldArtist.setBounds(130, 108, 141, 22);
		panel.add(textFieldArtist);
		textFieldArtist.setColumns(10);

		textFieldAlbum = new JTextField();
		textFieldAlbum.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		textFieldAlbum.setBounds(130, 143, 141, 22);
		panel.add(textFieldAlbum);
		textFieldAlbum.setColumns(10);

		textFieldDuration = new JTextField();
		textFieldDuration.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		textFieldDuration.setBounds(130, 178, 141, 22);
		panel.add(textFieldDuration);
		textFieldDuration.setColumns(10);

		JLabel lblArtist = new JLabel("Title");
		lblArtist.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		lblArtist.setBounds(12, 76, 56, 16);
		panel.add(lblArtist);

		JLabel lblArtist_1 = new JLabel("Artist");
		lblArtist_1.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		lblArtist_1.setBounds(12, 111, 56, 16);
		panel.add(lblArtist_1);

		JLabel lblAlbum = new JLabel("Album");
		lblAlbum.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		lblAlbum.setBounds(12, 146, 56, 16);
		panel.add(lblAlbum);

		JLabel lblDuration = new JLabel("Duration");
		lblDuration.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		lblDuration.setBounds(12, 181, 82, 16);
		panel.add(lblDuration);

		JButton btnUpdateButton = new JButton("Update Track Details");
		btnUpdateButton.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		btnUpdateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					connect = DriverManager.getConnection(
							// jdbc:hsqldb:file:myDBfilestore
							"jdbc:hsqldb:file:db_data/myDBfilestore;ifexists=true;shutdown=true", "SA", "");
					String query = "Update Tracks set title='" + textFieldTitle.getText() + "',artist='"
							+ textFieldArtist.getText() + "'" + ",album='" + textFieldAlbum.getText() + "',duration='"
							+ Double.parseDouble(textFieldDuration.getText()) + "'where id='"
							+ textFieldTrackID.getText() + "'";

					PreparedStatement preparedStatement = connect.prepareStatement(query);
					preparedStatement.execute();
					JOptionPane.showMessageDialog(null, "Track Details Update Successfully");
					PreparedStatement preparedStatement1 = connect
							.prepareStatement("SELECT Filepath, Title, Artist, Album, Duration FROM Tracks");
					ResultSet results = preparedStatement1.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(results));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnUpdateButton.setBounds(30, 239, 220, 25);
		panel.add(btnUpdateButton);

		JButton btnAddTrack = new JButton("Add Track");
		btnAddTrack.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		btnAddTrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("MP3 File", "MP3");
				jfc.setMultiSelectionEnabled(true);
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(filter);
				int valid = jfc.showOpenDialog(frame);
				if (valid == javax.swing.JFileChooser.CANCEL_OPTION) {
					return;
				} else if (valid == javax.swing.JFileChooser.APPROVE_OPTION) {
					File[] files = jfc.getSelectedFiles();
					for (File file : files) {
						reader.readTracks(file.getPath(), ".mp3");
						JOptionPane.showMessageDialog(null, "Track Added to Library Successfully");
					}

				}
			}
		});
		btnAddTrack.setBounds(12, 315, 115, 25);
		panel.add(btnAddTrack);

		JButton btnDeleteTrack = new JButton("Delete Track");
		btnDeleteTrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					connect = DriverManager.getConnection(
							// jdbc:hsqldb:file:myDBfilestore
							"jdbc:hsqldb:file:db_data/myDBfilestore;ifexists=true;shutdown=true", "SA", "");
					String query = "Delete from Tracks where id=" + textFieldTrackID.getText();
					PreparedStatement preparedStatement = connect.prepareStatement(query);
					preparedStatement.execute();
					JOptionPane.showMessageDialog(null, "Track Deleted Successfully");
					PreparedStatement preparedStatement1 = connect.prepareStatement("SELECT * FROM Tracks");
					ResultSet results = preparedStatement1.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(results));
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			}
		});
		btnDeleteTrack.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		btnDeleteTrack.setBounds(156, 315, 132, 25);
		panel.add(btnDeleteTrack);

		JButton btnLoadTracks = new JButton("Load Library");
		btnLoadTracks.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		btnLoadTracks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					reader.openConnection();
					connect = DriverManager.getConnection(
							// jdbc:hsqldb:file:myDBfilestore
							"jdbc:hsqldb:file:db_data/myDBfilestore;ifexists=true;shutdown=true", "SA", "");
					PreparedStatement preparedStatement = connect.prepareStatement("SELECT * FROM Tracks");
					ResultSet results = preparedStatement.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(results));
					JOptionPane.showMessageDialog(null, "Library Loaded Successfully");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnLoadTracks.setBounds(75, 277, 134, 25);
		panel.add(btnLoadTracks);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(300, 13, 950, 564);
		panel.add(scrollPane);

		table = new JTable();
		table.setFont(new Font("Gill Sans Nova", Font.PLAIN, 20));
		scrollPane.setViewportView(table);

		JLabel lblTrackId = new JLabel("Track ID");
		lblTrackId.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		lblTrackId.setBounds(12, 47, 65, 16);
		panel.add(lblTrackId);

		textFieldTrackID = new JTextField();
		textFieldTrackID.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		textFieldTrackID.setColumns(10);
		textFieldTrackID.setBounds(130, 38, 141, 22);
		panel.add(textFieldTrackID);

		JButton btnOpenMusicPlayer = new JButton("Open Music Player");
		btnOpenMusicPlayer.setFont(new Font("Gill Sans Nova", Font.PLAIN, 24));
		btnOpenMusicPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				MusicPlayer frame = new MusicPlayer();
				frame.setVisible(true);
			}
		});
		btnOpenMusicPlayer.setBounds(51, 383, 220, 114);
		panel.add(btnOpenMusicPlayer);
	}
}

package music_player_V1;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

public class MusicPlayer extends JFrame {

	private static final long serialVersionUID = 2758306190571144936L;
	private JPanel contentPane;
	JList<String> list = new JList<String>();
	JLabel lblCurrentlyPlaying = new JLabel("Choose a song to play");

	private PlaybackQueue queue = new PlaybackQueue();
	private ArrayList<File> updatelist = new ArrayList<File>();
	private Player player = null;
	private File currentlyPlaying = null;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private long pauselocation = 0;
	private long songlength = 0;
	private static int songsPlayed = 0;
	private boolean pause = false;
	private boolean loop = false;
	private static int songIndex = 0;
	private static boolean completed = false;

	public void updateList() {
		updatelist = queue.getQueue();
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (int i = 0; i < updatelist.size(); i++) {
			model.add(i, ((File) updatelist.get(i)).getName().replaceAll(".mp3", ""));
		}
		list.setModel(model);
	}

	public void add() {
		queue.add(this);
		updateList();
	}

	public void play() {
		if (songsPlayed == 0) {
			try {
				songIndex = list.getSelectedIndex();
				if (songIndex == -1) {
					songIndex = 0;
				}
				list.setSelectedIndex(songIndex);
				currentlyPlaying = (File) this.updatelist.get(songIndex);
				fis = new FileInputStream(currentlyPlaying);
				bis = new BufferedInputStream(fis);
				lblCurrentlyPlaying.setText(currentlyPlaying.getName().replaceAll(".mp3", ""));
				player = new Player(bis);
				songlength = fis.available();
				songIndex = 1;
				if (pause == true) {
					fis.skip(songlength - pauselocation);
					pause = false;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			new Thread() {
				public void run() {
					try {
						player.play();
						if (player.isComplete()) {
							next();
						}
					} catch (JavaLayerException ex) {
						System.out.println(ex.getMessage());
					}
				}
			}.start();
		} else {
			player.close();
			songIndex = 0;
			play();
		}
	}

	public void next() {
		try {
			player.close();
			++songIndex;
			if (songIndex > list.getLastVisibleIndex()) {
				--songIndex;
				JOptionPane.showMessageDialog(rootPane, "No more songs in queue");
				return;
			} else {
				loop = true;
				list.setSelectedIndex(songIndex);
				currentlyPlaying = (File) this.updatelist.get(songIndex);
				fis = new FileInputStream(currentlyPlaying);
				lblCurrentlyPlaying.setText(currentlyPlaying.getName().replaceAll(".mp3", ""));
				bis = new BufferedInputStream(fis);
				player = new Player(bis);
				songlength = fis.available();
				songsPlayed = 1;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		new Thread() {
			public void run() {
				try {
					player.play();
					// Automatically Play Next Song if current song is completed
					if (player.isComplete()) {
						next();
					}
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		}.start();
	}

	public void prev() {
		try {
			player.close();
			--songIndex;
			if (songIndex == -1) {
				++songIndex;
				JOptionPane.showMessageDialog(rootPane, "No previous songs in queue");
			} else {
				list.setSelectedIndex(songIndex);
				currentlyPlaying = (File) this.updatelist.get(songIndex);
				lblCurrentlyPlaying.setText(currentlyPlaying.getName().replaceAll(".mp3", ""));
				fis = new FileInputStream(currentlyPlaying);
				bis = new BufferedInputStream(fis);
				player = new Player(bis);
				songlength = fis.available();
				songsPlayed = 1;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		new Thread() {
			public void run() {
				try {
					player.play();
					// Automatically Play Next Song if current song is completed
					if (player.isComplete()) {
						next();
					}

				} catch (JavaLayerException ex) {
					System.out.println(ex.getMessage());
				}
			}
		}.start();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MusicPlayer frame = new MusicPlayer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MusicPlayer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JButton btnOpenMusicPlayer = new JButton("Open Music Organizer");
		btnOpenMusicPlayer.setBounds(41, 267, 264, 33);
		btnOpenMusicPlayer.setFont(new Font("Gill Sans Nova", Font.PLAIN, 24));
		btnOpenMusicPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MusicOrganizer window = new MusicOrganizer();
				window.frame.setVisible(true);
			}
		});
		contentPane.setLayout(null);
		contentPane.add(btnOpenMusicPlayer);
		lblCurrentlyPlaying.setBounds(31, 13, 389, 43);

		lblCurrentlyPlaying.setMaximumSize(new Dimension(900, 16));
		lblCurrentlyPlaying.setFont(new Font("Gill Sans Nova", Font.PLAIN, 20));
		contentPane.add(lblCurrentlyPlaying);

		JButton btnAddFiles = new JButton("Add Files");
		btnAddFiles.setBounds(41, 79, 97, 25);
		btnAddFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getContentPane();
				add();
			}
		});
		contentPane.add(btnAddFiles);
		;

		JButton btnPrev = new JButton("");
		btnPrev.setBounds(150, 69, 48, 35);
		btnPrev.setBorderPainted(false);
		btnPrev.setContentAreaFilled(false);
		Image img = new ImageIcon(this.getClass().getResource("/prev.png")).getImage();
		btnPrev.setIcon(new ImageIcon(img));
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prev();
			}
		});
		contentPane.add(btnPrev);

		JButton btnNext = new JButton("");
		btnNext.setBounds(345, 69, 48, 35);
		btnNext.setPreferredSize(new Dimension(35, 10));
		btnNext.setBorderPainted(false);
		btnNext.setContentAreaFilled(false);
		Image img3 = new ImageIcon(this.getClass().getResource("/next.png")).getImage();
		btnNext.setIcon(new ImageIcon(img3));
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				next();
			}
		});
		contentPane.add(btnNext);

		JButton btnPause = new JButton("");
		btnPause.setBounds(187, 69, 57, 35);
		btnPause.setBorderPainted(false);
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					pauselocation = fis.available();
					player.close();
					pause = true;
				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		btnPause.setContentAreaFilled(false);
		btnPause.setBorder(null);
		Image img1 = new ImageIcon(this.getClass().getResource("/pause.png")).getImage();
		btnPause.setIcon(new ImageIcon(img1));
		contentPane.add(btnPause);

		JButton btnStop = new JButton("");
		btnStop.setBounds(291, 69, 57, 35);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.close();
				pauselocation = 0;
				songlength = 0;
			}
		});
		btnStop.setBorderPainted(false);
		btnStop.setContentAreaFilled(false);
		Image img2 = new ImageIcon(this.getClass().getResource("/stop.png")).getImage();
		btnStop.setIcon(new ImageIcon(img2));
		contentPane.add(btnStop);

		JButton btnPlay = new JButton("");
		btnPlay.setBounds(228, 61, 73, 43);
		btnPlay.setBorderPainted(false);
		btnPlay.setContentAreaFilled(false);
		Image img4 = new ImageIcon(this.getClass().getResource("/play1.png")).getImage();
		btnPlay.setIcon(new ImageIcon(img4));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				play();
			}
		});
		contentPane.add(btnPlay);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(41, 117, 352, 141);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(list);
		list.setFont(new Font("Gill Sans Nova", Font.PLAIN, 18));
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					player.close();
					pauselocation = 0;
					songlength = 0;
					play();
				}
			}
		});

	}
}

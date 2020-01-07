package music_player_V1;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PlaybackQueue {

	JFileChooser jfc = new JFileChooser();
	private ArrayList<File> queue = new ArrayList<File>();
	FileFilter filter = new FileNameExtensionFilter("MP3 File", "MP3");

	public void add(JFrame frame) {
		jfc.setMultiSelectionEnabled(true);
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(filter);
		int valid = jfc.showOpenDialog(frame);
		if (valid == javax.swing.JFileChooser.CANCEL_OPTION) {
			return;
		} else if (valid == javax.swing.JFileChooser.APPROVE_OPTION) {
			File[] file = jfc.getSelectedFiles();
			queue.addAll(Arrays.asList(file));
		}
	}

	ArrayList<File> getQueue() {
		return queue;
	}

}

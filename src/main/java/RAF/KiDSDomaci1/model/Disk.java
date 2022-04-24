package RAF.KiDSDomaci1.model;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Disk {
	
	private final File directory;
	private final BlockingQueue<File> diskQueue = new LinkedBlockingQueue<>();
	
	public Disk(File directory) {
		this.directory = directory;
	}
	
	public File getDirectory() {
		return directory;
	}

	public BlockingQueue<File> getDiskQueue() {
		return diskQueue;
	}

	@Override
	public String toString() {
		return directory.toPath().toString();
	}
}


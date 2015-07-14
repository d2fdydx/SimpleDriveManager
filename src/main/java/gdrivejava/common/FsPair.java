package gdrivejava.common;

import java.io.Serializable;

public class FsPair implements Serializable {
	FileSystem localFileSystem=null;
	FileSystem remoteFileSystem=null;
	FsPair(FileSystem local, FileSystem remote){
		localFileSystem=local; remoteFileSystem=remote;
	}
	public FileSystem getLocalFileSystem() {
		return localFileSystem;
	}
	public void setLocalFileSystem(FileSystem localFileSystem) {
		this.localFileSystem = localFileSystem;
	}
	public FileSystem getRemoteFileSystem() {
		return remoteFileSystem;
	}
	public void setRemoteFileSystem(FileSystem remoteFileSystem) {
		this.remoteFileSystem = remoteFileSystem;
	}
	
	
}

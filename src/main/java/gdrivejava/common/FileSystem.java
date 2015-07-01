package gdrivejava.common;

import gdrivejava.event.listener.SyncEventListener;

import java.util.List;

public interface FileSystem <Listener extends SyncEventListener<?>>{

	

	public List<INode> listFiles();
	public void addEventListener(Listener listener);
	
	//public void sync (File file, String path);
	
}

package gdrivejava.common;

import gdrivejava.event.listener.SyncEventListener;

import java.util.List;
import java.util.Map;

public interface FileSystem <Listener extends SyncEventListener<?>>{

	

	public List<INode> listFiles();
	public Map<String , INode> getFilesMap();
	public void addEventListener(Listener listener);
	
	//public void sync (File file, String path);
	
}

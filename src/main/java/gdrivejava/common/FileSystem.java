package gdrivejava.common;

import java.util.List;
import java.util.Map;

import gdrivejava.event.SyncEvent;
import gdrivejava.event.listener.SyncEventListener;

public interface FileSystem {


	public void addSyncEvent(SyncEvent e);
	public void addSyncEvent(List<SyncEvent> es);
	public String getRootPath();
	public void setRootPath(String path);
	public Map<String , INode> getFilesMap();
	public void addEventListener(SyncEventListener listener);
	
	
	
}

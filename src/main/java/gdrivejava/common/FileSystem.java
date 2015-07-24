package gdrivejava.common;

import java.util.List;
import java.util.Map;

import gdrivejava.event.SyncEvent;
import gdrivejava.event.listener.SyncEventListener;

public interface FileSystem<F> {


	public void addSyncEvent(SyncEvent e);
	public void startIndependentSync();
	public void addSyncEvent(List<SyncEvent> es);
	public String getRootPath();
	public void setRootPath(String path);
	public Map<String, INode<F>> getFilesMap();
	public void addEventListener(SyncEventListener listener);
	public void destoryThis();
	public void criticalDestory();
	public void join();
	public void refresh(INode<F> node);
	
}

package gdrivejava.event;

import gdrivejava.common.INode;
import gdrivejava.common.SyncAction;

public class SyncEvent {

	String path;
	SyncAction action;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public SyncAction getAction() {
		return action;
	}
	public void setAction(SyncAction action) {
		this.action = action;
	}
	
}

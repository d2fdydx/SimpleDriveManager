package gdrivejava.event;

import gdrivejava.common.INode;

public class SyncEvent {
	boolean mDir;
	String mPath;
	String UUID;
	INode node;
	public boolean ismDir() {
		return mDir;
	}
	public void setmDir(boolean mDir) {
		this.mDir = mDir;
	}
	public String getmPath() {
		return mPath;
	}
	public void setmPath(String mPath) {
		this.mPath = mPath;
	}
	public INode getNode() {
		return node;
	}
	public void setNode(INode node) {
		this.node = node;
	}
	public boolean isDir() {
		return mDir;
	}
	public void setDir(boolean dir) {
		mDir = dir;
	}
	public String getPath() {
		return mPath;
	}
	public void setPath(String path) {
		mPath = path;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
}

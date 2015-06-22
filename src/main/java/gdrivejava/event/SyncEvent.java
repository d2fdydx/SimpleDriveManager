package gdrivejava.event;

public class SyncEvent {
	boolean mDir;
	String mPath;
	String UUID;
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

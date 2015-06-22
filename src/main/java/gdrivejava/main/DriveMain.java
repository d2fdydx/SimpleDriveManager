package gdrivejava.main;

import gdrivejava.common.FileSystem;
import gdrivejava.event.LocalSyncEvent;
import gdrivejava.event.RemoteSyncEvent;
import gdrivejava.event.listener.LocalSyncEventListener;
import gdrivejava.event.listener.RemoteSyncEventListener;
import gdrivejava.google.GoogleFileSystem;

public class DriveMain implements RemoteSyncEventListener, LocalSyncEventListener{

	static FileSystem sGoogleFS;
	static DriveMain sMain=null;
	public static FileSystem getGoogleFS() {
		return sGoogleFS;
	}
	public static void setGoogleFS(FileSystem googleFS) {
		sGoogleFS = googleFS;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		sMain = new DriveMain();
		sGoogleFS = new GoogleFileSystem();
		sGoogleFS.addEventListener(sMain);
		
		
		
	}
	
	
	//================
	@Override
	public void handleLocalSync(LocalSyncEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handleRemoteSync(RemoteSyncEvent e) {
		// TODO Auto-generated method stub
		
	}

}

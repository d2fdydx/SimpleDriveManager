package gdrivejava.main;

import gdrivejava.common.FileSystem;
import gdrivejava.event.LocalSyncEvent;
import gdrivejava.event.RemoteSyncEvent;
import gdrivejava.event.listener.LocalSyncEventListener;
import gdrivejava.event.listener.RemoteSyncEventListener;
import gdrivejava.google.GoogleFileSystem;

public class DriveMain {

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
		sGoogleFS.addEventListener(new RemoteSyncEventListener() {
			
			@Override
			public void handle(RemoteSyncEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}
	
	
	

}

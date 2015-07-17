package gdrivejava.google;

import java.io.IOException;
import java.util.Map;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import gdrivejava.auth.GoogleAuth;
import gdrivejava.common.AbstractFileSystem;
import gdrivejava.common.INode;
import gdrivejava.common.SyncAction;
import gdrivejava.event.SyncEvent;

public class GoogleFileSystem extends AbstractFileSystem<File> {
	
	Drive mDrive=null;
	GoogleFileStore mStore =null;
	GoogleFileSystem me =null;
	String rootPath=null;
	public GoogleFileSystem() {
		super("Google FS");
		me =this;
		// TODO Auto-generated constructor stub
		try {
			mDrive =  GoogleAuth.getDriveService();
			
			mStore = new GoogleFileStore(this);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	

	


	public Drive getDrive() {
		return mDrive;
	}





	@Override
	public Map<String, INode<File>> getFilesMap() {
		// TODO Auto-generated method stub
		return mStore.getPathMap();
	}



	@Override
	protected void sync(String path, SyncAction action) {
		// TODO Auto-generated method stub
		System.out.println("event:" + path  + " "+ action);
		switch(action){
		case Pull:
			mStore.downloadFile(path);
			break;
		case Push:
			mStore.uploadFile(path);
			break;
		case Update:
			mStore.updateFile(path);
			break;
		}
	}






	@Override
	protected Runnable getRunnable() {
		// TODO Auto-generated method stub
		return new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true){
					//do event first
					if (eventList.size()>0){
						
						SyncEvent e = null;
						synchronized(eventList){
							e= eventList.remove(0);

						}
						System.out.println("hv event " + e.getPath());
						sync(e.getPath(),e.getAction());



						continue;
					}

					//
					System.out.println(me.thread.toString());

					synchronized (me) {
						try {
							me.wait(5000l);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}


			}
		};

	}






	public String getRootPath() {
		return rootPath;
	}






	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}












	


	



}

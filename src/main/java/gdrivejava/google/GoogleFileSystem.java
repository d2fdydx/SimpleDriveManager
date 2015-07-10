package gdrivejava.google;

import gdrivejava.auth.GoogleAuth;
import gdrivejava.common.FileSystem;
import gdrivejava.common.INode;
import gdrivejava.common.SyncAction;
import gdrivejava.event.listener.RemoteSyncEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class GoogleFileSystem implements FileSystem<RemoteSyncEventListener> {
	
	Drive mDrive=null;
	GoogleFileStore mStore =null;
	
	RemoteSyncEventListener mListener=null;
	public GoogleFileSystem() {
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
	
	

	@Override
	public List<INode> listFiles() {
		// TODO Auto-generated method stub
		return null;
	}



	public Drive getDrive() {
		return mDrive;
	}



	@Override
	public void addEventListener(RemoteSyncEventListener listener) {
		// TODO Auto-generated method stub
		mListener = listener; // only one listener only, actually
		
	}



	@Override
	public Map<String, INode> getFilesMap() {
		// TODO Auto-generated method stub
		return mStore.getPathMap();
	}



	@Override
	public void sync(String path, SyncAction action) {
		// TODO Auto-generated method stub
		if (action == SyncAction.Pull){
			mStore.downloadFile(path);
		}
	}



}

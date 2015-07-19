package gdrivejava.google;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import gdrivejava.auth.GoogleAuth;
import gdrivejava.common.AbstractFileSystem;
import gdrivejava.common.INode;
import gdrivejava.common.ObjectStoreUtil;
import gdrivejava.common.SyncAction;
import gdrivejava.event.SyncEvent;
import gdrivejava.main.DriveMain;

public class GoogleFileSystem extends AbstractFileSystem<File> {
	
	final static String fileDb =".googldIndex";
	Drive mDrive=null;
	GoogleFileStore mStore =null;
	
	String rootPath=null;
	public GoogleFileSystem(String rp) {
		super("Google FS");
		rootPath=rp;
		// TODO Auto-generated constructor stub
		try {
			mDrive =  GoogleAuth.getDriveService();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Problem with Google File System " + rootPath);
			return ;
		} 
		try {
			mStore = (GoogleFileStore) ObjectStoreUtil.readIndex(FilenameUtils.concat(rootPath, fileDb));
			return ;
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			mStore = new GoogleFileStore(this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			mStore = new GoogleFileStore(this);
		}
		
		
		try {
			
			
			ObjectStoreUtil.saveIndex(FilenameUtils.concat(rootPath, fileDb), mStore);
			System.out.println("created Index");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("cannot create Index");
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
	protected void sync(String path, SyncAction action) throws Exception {
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






	





	public String getRootPath() {
		return rootPath;
	}






	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}






	@Override
	protected String getIndexName() {
		// TODO Auto-generated method stub
		return FilenameUtils.concat(rootPath, fileDb);
	}






	@Override
	protected Serializable getStore() {
		// TODO Auto-generated method stub
		return  mStore;
	}






	@Override
	public void refresh(INode<File> node) {
		// TODO Auto-generated method stub
		
		
	}












	


	



}

package gdrivejava.google;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
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

public class GoogleFileSystem extends AbstractFileSystem<File> {
	
	final static String fileDb =".googleIndex";
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
	
			mStore = (GoogleFileStore) ObjectStoreUtil.readIndex(FilenameUtils.concat(rootPath, fileDb));
			if (mStore!=null){
				mStore.setDrive(mDrive);
				mStore.setGoogleFs(this);
				return ;
			}
		
			mStore = new GoogleFileStore(this);
		
		
		
		
			
			
			if (ObjectStoreUtil.saveIndex(FilenameUtils.concat(rootPath, fileDb), mStore)){
				System.out.println("created Index");
			}else{
				System.err.println("fail to create Index");
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






	@Override
	protected void refreshIndex() {
		// TODO Auto-generated method stub
		try {
			List <String> t =this.mStore.refreshIndex();
			for (String p : t){
				System.out.println("remote: modified file"+ p);
				SyncEvent e = new SyncEvent();
				e.setAction(SyncAction.Pull);
				e.setPath(p);
				this.publishSyncEvent(e);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}






	@Override
	public void startIndependentSync() {
		// TODO Auto-generated method stub
		setInitflag(false);
	}






	











	


	



}

package gdrivejava.google;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.mortbay.jetty.servlet.PathMap;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.sun.crypto.provider.ARCFOURCipher;

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
	public GoogleFileSystem(String rp) throws Exception {
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
				setNewIndex(false);
				return ;
			}
			System.out.println("Google FS start build index");
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
	
		System.out.println("event:" + path  + " "+ action);
		switch(action){
		case Pull:
			try{
				
				mStore.downloadFile(path);
				SyncEvent e = new SyncEvent();
				e.setAction(SyncAction.CreateIndex);
				e.setPath(path);
				this.publishSyncEvent(e);

			}catch (Exception e){
				e.printStackTrace();
				throw e;
			}
			break;
		case Push:
			mStore.uploadFile(path);
			break;
		case Update:
			mStore.updateFile(path);
			break;
		case RemoveIndex:
			mStore.deleteIndex(path);
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
			List <String> t =this.mStore.getUpdatedAndCreated();
			for (String p : t){
				System.out.println("remote: modified file"+ p);
				SyncEvent e = new SyncEvent();
				e.setAction(SyncAction.Pull);
				e.setPath(p);
				this.publishSyncEvent(e);
			}
			List<INode<com.google.api.services.drive.model.File>> removed =new ArrayList<>(); 
			this.mStore.getChanges(removed,null);
			for (INode<com.google.api.services.drive.model.File> p :removed){
				SyncEvent e = new SyncEvent();
				e.setAction(SyncAction.Deleted);
				e.setPath(p.getFullPathName());
				e.setNode(p);
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






	@Override
	protected boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}






	@Override
	protected void clearDirty() {
		// TODO Auto-generated method stub
		mStore.setDirty(false);
	}






	@Override
	protected boolean saveStore() {
		// TODO Auto-generated method stub
		
		if (ObjectStoreUtil.saveIndex(FilenameUtils.concat(rootPath, fileDb), mStore)){
			System.out.println("save Index");
			return true;
		}else{
			System.err.println("fail to save Index");
			return false;
		}
	}






	











	


	



}

package gdrivejava.local;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import gdrivejava.common.AbstractFileSystem;
import gdrivejava.common.INode;
import gdrivejava.common.ObjectStoreUtil;
import gdrivejava.common.SyncAction;
import gdrivejava.event.SyncEvent;

public class LocalFileSystem extends AbstractFileSystem<File>{

	LocalFileStore store=null;
	LocalFileSystem me=null;
	String rootPath=null;
	final static String fileDb =".localIndex";


	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public LocalFileSystem(String rp){
		super("Local FS");
		rootPath=rp;
		me =this;
		
	
			store = (LocalFileStore) ObjectStoreUtil.readIndex(FilenameUtils.concat(rootPath, fileDb));
		if (store !=null){	
			return;
		}
		
		
	
		
			
			store= new LocalFileStore(this);
			
		
		
			if (ObjectStoreUtil.saveIndex(FilenameUtils.concat(rootPath, fileDb), store)){
				
			}else{
				
			}
		
		
	}

	@Override
	public Map<String, INode<File>> getFilesMap() {
		// TODO Auto-generated method stub

		return store.getNodeMap();
	}

	@Override
	protected void sync(String path, SyncAction action) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getIndexName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Serializable getStore() {
		// TODO Auto-generated method stub
		return (Serializable) store;
	}

	@Override
	public void refresh(INode<File> node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void refreshIndex() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startIndependentSync() {
		// TODO Auto-generated method stub
		setInitflag(false);
	}


	


}

package gdrivejava.local;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gdrivejava.common.AbstractFileSystem;
import gdrivejava.common.INode;
import gdrivejava.common.SyncAction;
import gdrivejava.event.SyncEvent;
import gdrivejava.google.GoogleINode;

public class LocalFileSystem extends AbstractFileSystem<File>{

	LocalFileStore store=null;
	LocalFileSystem me=null;
	String rootPath=null;



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
		try {
			store= new LocalFileStore(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.destoryThis();
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


	


}

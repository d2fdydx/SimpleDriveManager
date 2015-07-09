package gdrivejava.local;

import gdrivejava.common.FileSystem;
import gdrivejava.common.INode;
import gdrivejava.event.listener.LocalSyncEventListener;
import gdrivejava.main.DriveMain;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LocalFileSystem implements FileSystem<LocalSyncEventListener>{

	LocalFileStore store=null;
	
	@Override
	public List<INode> listFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addEventListener(LocalSyncEventListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	public LocalFileSystem(){
		store= new LocalFileStore();
	}

	@Override
	public Map<String, INode> getFilesMap() {
		// TODO Auto-generated method stub
		return store.getNodeMap();
	}
	

}

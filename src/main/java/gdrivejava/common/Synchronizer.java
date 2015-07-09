package gdrivejava.common;

import gdrivejava.event.listener.LocalSyncEventListener;
import gdrivejava.event.listener.RemoteSyncEventListener;

import java.util.Map;

public class Synchronizer {

	
	public boolean syncAll(FileSystem<LocalSyncEventListener> localFs , FileSystem<RemoteSyncEventListener> remoteFs){
		//down
		Map<String, INode> localMap = localFs.getFilesMap(), 
				remoteMap = remoteFs.getFilesMap();
		for (String localId : localMap.keySet()){
			//do sth
		}
		
		return true;
	}
	
}

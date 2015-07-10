package gdrivejava.common;

import gdrivejava.event.listener.LocalSyncEventListener;
import gdrivejava.event.listener.RemoteSyncEventListener;

import java.util.Map;

public class Synchronizer {

	
	public boolean syncAll(FileSystem<LocalSyncEventListener> localFs , FileSystem<RemoteSyncEventListener> remoteFs){
		//down
		Map<String, INode> localMap = localFs.getFilesMap(), 
				remoteMap = remoteFs.getFilesMap();
		for (String remoteId : remoteMap.keySet()){
			//do sth
			if(!localMap.containsKey(remoteId)){
				INode remoteNode = remoteMap.get(remoteId);
				if (!remoteNode.isDir()){
					String tpath =remoteMap.get(remoteId).getFullPathName();
					System.out.println(remoteMap.get(remoteId).getFullPathName());
					remoteFs.sync(tpath, SyncAction.Pull);
				}
				
			}
		}
		
		return true;
	}
	
}

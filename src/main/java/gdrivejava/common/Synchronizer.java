package gdrivejava.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gdrivejava.event.SyncEvent;
import gdrivejava.google.GoogleFileSystem;
import gdrivejava.local.LocalFileSystem;
import gdrivejava.main.DriveMain;

public class Synchronizer {

	Map <String,FsPair> FsMap= null;

	public void start(){
		FsMap= new HashMap<>();
		
		FileSystem remote  = new GoogleFileSystem();
		remote.setRootPath(DriveMain.LocationPath);
		FileSystem local = new LocalFileSystem();
		local.setRootPath(DriveMain.LocationPath);
		FsPair pair = new FsPair(local, remote);
		FsMap.put(DriveMain.LocationPath,pair);
		syncAll();
		
	}


	void syncAll(){
		
		for (String root : FsMap.keySet()){
			FileSystem localFs = FsMap.get(root).getLocalFileSystem();
			Map<String, INode> localMap = localFs.getFilesMap();
			FileSystem remoteFs =FsMap.get(root).getRemoteFileSystem();
			


				Map<String,INode> remoteMap = remoteFs.getFilesMap();
				List<SyncEvent> remoteEvents = new ArrayList<>();
				List<SyncEvent> localEvents = new ArrayList<>();
				//pull
				for (String remoteId : remoteMap.keySet()){
					//do sth



					if(!localMap.containsKey(remoteId)){
						INode remoteNode = remoteMap.get(remoteId);
						if (!remoteNode.isDir()){
							String tpath = remoteNode.getFullPathName();
							System.out.println( remoteNode.getFullPathName());
							SyncEvent e= new SyncEvent();
							e.setAction(SyncAction.Pull);
							e.setPath(tpath);
						//	remoteEvents.add(e);

							
						}else{
							//do nth( meaningless)
							//localFs.sync(tpath, SyncAction.Mkdir);
						}

					}else{
						 //check modified time .etc
						
					}
					
					
					
				}
				//push
				for (String localId: localMap.keySet()){
					if (!remoteMap.containsKey(localId)){
						INode localNode = localMap.get(localId);
						//push new file
						if (!localNode.isDir()){
							String tpath = localNode.getFullPathName();
							SyncEvent e= new SyncEvent();
							e.setAction(SyncAction.Push);
							e.setPath(tpath);
							//System.out.println("adder :" + tpath);
							remoteEvents.add(e);
						}
					}
				}
				remoteFs.addSyncEvent(remoteEvents);
				localFs.addSyncEvent(localEvents);
			
		}
		
//down
		
	}

}

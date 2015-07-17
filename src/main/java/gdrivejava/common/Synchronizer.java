package gdrivejava.common;

import java.io.File;
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
		
		FileSystem<?> remote  = new GoogleFileSystem();
		remote.setRootPath(DriveMain.LocationPath);
		FileSystem<?> local = new LocalFileSystem();
		local.setRootPath(DriveMain.LocationPath);
		FsPair pair = new FsPair(local, remote);
		FsMap.put(DriveMain.LocationPath,pair);
		syncAll();
		
	}


	void syncAll(){
		
		for (String root : FsMap.keySet()){
			FileSystem<File> localFs = FsMap.get(root).getLocalFileSystem();
			Map<String, INode<File>> localMap = localFs.getFilesMap();
			 
			FileSystem<Object> remoteFs =FsMap.get(root).getRemoteFileSystem();
		


				Map<String,INode<Object>> remoteMap = remoteFs.getFilesMap();
				List<SyncEvent> remoteEvents = new ArrayList<>();
				List<SyncEvent> localEvents = new ArrayList<>();
				//pull
				for (String remotePath : remoteMap.keySet()){
					//do sth



					if(!localMap.containsKey(remotePath)){
						INode<Object> remoteNode = remoteMap.get(remotePath);
						
						if (!remoteNode.isDir()){
							String tpath = remoteNode.getFullPathName();
							System.out.println( remoteNode.getFullPathName());
							SyncEvent e= new SyncEvent();
							e.setAction(SyncAction.Pull);
							e.setPath(tpath);
							remoteEvents.add(e);

							
						}else{
							//do nth( meaningless)
							//localFs.sync(tpath, SyncAction.Mkdir);
						}

					}else{
						 //check modified time .etc
						INode<File> localNode = localMap.get(remotePath);
						long localTime =localNode.getModifiedTime();
						INode<Object> remoteNode = remoteMap.get(remotePath);
						if (remoteNode.isDir())
							continue;
						long remoteTime = remoteNode.getModifiedTime();
						if (remoteTime > localTime){ // remote newer?
						
							
								if (!remoteNode.getMd5CheckSum().equals(localNode.getMd5CheckSum())){
									//download
									String tpath = remoteNode.getFullPathName();
									SyncEvent e= new SyncEvent();
									e.setAction(SyncAction.Pull);
									e.setPath(tpath);
									remoteEvents.add(e);
								}
							
						}else{
							
								
									if (!remoteNode.getMd5CheckSum().equals(localNode.getMd5CheckSum())){
										//download
										String tpath = remoteNode.getFullPathName();
										SyncEvent e= new SyncEvent();
										e.setAction(SyncAction.Update);
										e.setPath(tpath);
										remoteEvents.add(e);
									}
								
						}
					
					}
					
					
					
				}
				//push
				for (String localId: localMap.keySet()){
					if (!remoteMap.containsKey(localId)){
						INode<File> localNode = localMap.get(localId);
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

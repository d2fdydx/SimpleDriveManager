package gdrivejava.common;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gdrivejava.event.SyncEvent;
import gdrivejava.event.listener.SyncEventListener;
import gdrivejava.google.GoogleFileSystem;
import gdrivejava.local.LocalFileSystem;
import gdrivejava.main.DriveMain;

public class Synchronizer {

	Map <String,FsPair> FsMap= null;

	public void start(){
		FsMap= new HashMap<>();
		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable exc) {
				// TODO Auto-generated method stub
				exc.printStackTrace();
				for (String p : FsMap.keySet()){
					FsPair t = FsMap.get(p);
					t.getLocalFileSystem().criticalDestory();
					t.getRemoteFileSystem().criticalDestory();
				}
				for (String p : FsMap.keySet()){
					FsPair t = FsMap.get(p);
					t.getLocalFileSystem().join();
					t.getRemoteFileSystem().join();
				}
			}
		});
		FileSystem<?> remote  = new GoogleFileSystem(DriveMain.LocationPath);
	
		FileSystem<?> local = new LocalFileSystem(DriveMain.LocationPath);

		FsPair pair = new FsPair(local, remote);
		FsMap.put(DriveMain.LocationPath,pair);
		try {
			startSync();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//wtf !!
			throw new RuntimeException();
			
		}
		
	}


	void startSync() throws Exception {
		
		for (String root : FsMap.keySet()){
			FileSystem<File> localFs = FsMap.get(root).getLocalFileSystem();
			Map<String, INode<File>> localMap = localFs.getFilesMap();
		
			FileSystem<Object> remoteFs =FsMap.get(root).getRemoteFileSystem();
			Map<String,INode<Object>> remoteMap = remoteFs.getFilesMap();
			localFs.addEventListener(new LocalListener(remoteFs, localFs));
			remoteFs.addEventListener(new RemoteListener(remoteFs, localFs));


				
				List<SyncEvent> remoteEvents = new ArrayList<>();
				List<SyncEvent> localEvents = new ArrayList<>();
				//pull
				for (String remotePath : remoteMap.keySet()){
					//do sth



					if(!localMap.containsKey(remotePath)){
						INode<Object> remoteNode = remoteMap.get(remotePath);
						
						if (!remoteNode.isDir()){
							String tpath = null;
						
							try {
								tpath = remoteNode.getFullPathName();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
						
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
						long localTime = 0;
						
						localTime = localNode.getLastModifiedTime();
						
						INode<Object> remoteNode = remoteMap.get(remotePath);
						if (remoteNode.isDir())
							continue;
						long remoteTime;
						
						remoteTime = remoteNode.getLastModifiedTime();
						
						if (remoteTime > localTime){ // remote newer?
						
							
								if (!remoteNode.getCheckSum().equals(localNode.getCheckSum())){
									//download
									String tpath = remoteNode.getFullPathName();
									SyncEvent e= new SyncEvent();
									e.setAction(SyncAction.Pull);
									e.setPath(tpath);
									remoteEvents.add(e);
								}
							
						}else{
							
								
									if (!remoteNode.getCheckSum().equals(localNode.getCheckSum())){
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
			remoteFs.startIndependentSync();
			localFs.startIndependentSync();
		}
		
//down
		
	}

	class LocalListener implements SyncEventListener{
		FileSystem<Object> remoteFs;
		FileSystem<File> localFs;
		public LocalListener (FileSystem<Object> remoteFs, FileSystem<File> localFs) {
			// TODO Auto-generated constructor stub
			super ();
			this.remoteFs = remoteFs;
			this.localFs=localFs;
		}
		@Override
		public void handle(SyncEvent event) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class RemoteListener implements SyncEventListener{

		FileSystem<Object> remoteFs;
		FileSystem<File> localFs;
		Map<String,INode<Object>> remoteMap =null;
		Map<String,INode<File>> localMap = null;
		public RemoteListener(FileSystem<Object> remoteFs, FileSystem<File> localFs) {
			// TODO Auto-generated constructor stub
			super ();
			this.remoteFs = remoteFs;
			this.localFs=localFs;
			this.remoteMap = remoteFs.getFilesMap();
			this.localMap = localFs.getFilesMap();
		}
		
		@Override
		public void handle(SyncEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case Pull:
				INode<?> rf = remoteMap.get(event.getPath()),
					lf = localMap.get(event.getPath());
				if (lf ==null)
					remoteFs.addSyncEvent(event);
				//should check conflict ( by fs last modified time
				//
				//sth sth
				//==================
				if (!rf.getCheckSum().equals(lf.getCheckSum())){
					remoteFs.addSyncEvent(event);
				}
				
				
				break;
			default:
				break;
			}
			
		}
		
	}
	public void cleanUp(){
		
	}
}

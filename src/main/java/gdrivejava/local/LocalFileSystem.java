package gdrivejava.local;

import java.io.File;
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

	public LocalFileSystem(){
		super("Local FS");
		me =this;
		store= new LocalFileStore(this);
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
	protected Runnable getRunnable() {
		// TODO Auto-generated method stub
		return new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true){
					//do event first
					if (eventList.size()>0){
						SyncEvent e = null;
						synchronized(eventList){
							e= eventList.remove(0);

						}
						sync(e.getPath(),e.getAction());

						continue;
					}

					//

					synchronized (me) {
						try {
							me.wait(5000l);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}


			}
		};

	}


}

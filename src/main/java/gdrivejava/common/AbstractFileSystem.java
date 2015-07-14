package gdrivejava.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gdrivejava.event.SyncEvent;
import gdrivejava.event.listener.SyncEventListener;

public abstract class AbstractFileSystem implements FileSystem {
	
	protected Thread thread =null;
	protected List<SyncEvent> eventList = new LinkedList<>() ;
	protected List<SyncEventListener> listeners = new ArrayList<>();

	abstract protected void sync(String path, SyncAction action);
	abstract protected Runnable getRunnable();
	
	public AbstractFileSystem(String name) {
		// TODO Auto-generated constructor stub
	
		thread = new Thread(getRunnable());
		thread.setName(name);
		thread.start();
	}
	
	
	@Override
	public void addEventListener(SyncEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}
	
	

	

	@Override
	public void addSyncEvent(SyncEvent e) {
		// TODO Auto-generated method stub
		synchronized (eventList) {
			eventList.add(e);
		}
	
	}

	@Override
	public void addSyncEvent(List<SyncEvent> es) {
		// TODO Auto-generated method stub
		System.out.println(thread.getName()+ " addEvents " + es.size());
		synchronized (eventList) {
			eventList.addAll(es);
		}
	
	}
}

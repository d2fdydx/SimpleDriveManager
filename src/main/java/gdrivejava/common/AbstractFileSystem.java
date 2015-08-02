package gdrivejava.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gdrivejava.event.SyncEvent;
import gdrivejava.event.listener.SyncEventListener;

public abstract class AbstractFileSystem<F> implements FileSystem<F> {

	protected Thread thread =null;
	protected List<SyncEvent> eventList = new LinkedList<>() ;
	protected List<SyncEventListener> listeners = new ArrayList<>();
	private boolean runningflag=true;
	private boolean initflag=true;
	boolean newIndex=true;
	
	public boolean isNewIndex() {
		return newIndex;
	}
	public void setNewIndex(boolean newIndex) {
		this.newIndex = newIndex;
	}
	public boolean isInitflag() {
		return initflag;
	}
	public void setInitflag(boolean initflag) {
		this.initflag = initflag;
	}
	abstract protected void sync(String path, SyncAction action) throws Exception;
	abstract protected String getIndexName();
	abstract protected Serializable getStore();
	abstract protected boolean isDirty();
	abstract protected void clearDirty();
	abstract protected boolean saveStore() ;
	protected AbstractFileSystem<F> me ;

	public AbstractFileSystem(String name) {
		// TODO Auto-generated constructor stub
		me = this;
		thread = new Thread(getRunnable());
		thread.setUncaughtExceptionHandler(new Handler());
		thread.setName(name);
		runningflag=true;
		thread.start();
	}


	@Override
	public void addEventListener(SyncEventListener listener) {
		// TODO Auto-generated method stub
		listeners.add(listener);
	}



	
	protected void publishSyncEvent(SyncEvent e) {
		// TODO Auto-generated method stub
		for (SyncEventListener l : listeners){
			l.handle(e);
		}
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

	public void destoryThis(){
		synchronized (me) {
			runningflag=false;
		}

		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ;
	}
	public void criticalDestory(){
		synchronized (me) {
			thread.interrupt();
		}


	}
	@Override
	public void join() {
		// TODO Auto-generated method stub
		if (thread.isAlive()){
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	protected abstract void refreshIndex();
	protected Runnable getRunnable() {
		// TODO Auto-generated method stub
		return new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (runningflag){
					//do event first
					if (eventList.size()>0){

						SyncEvent e = null;
						synchronized(eventList){
							e= eventList.remove(0);

						}
						System.out.println("hv event " + e.getPath());
						try {
							sync(e.getPath(),e.getAction());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							//
							System.err.println( thread.toString() + " fail on event " + e.getAction()  );
						}


						synchronized (me) {
							if (thread.isInterrupted()){

								return ;
							}
						}
						continue;
					}

					//
					System.out.println(me.thread.toString());
					if (!initflag){
					if (isDirty()){
						if (saveStore()){
							clearDirty();
						}else{
							
						}
					}
					}
					synchronized (me) {
						if (thread.isInterrupted()){

							return ;
						}
						if (!runningflag){
							break;
						}
						if (!initflag){
							refreshIndex();
						}
						try {
							me.wait(5000l);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block

							return;
						}
						
					}

				}
				onThreadClose();


			}
		};

	}

	protected void onThreadClose(){
		
		ObjectStoreUtil.saveIndex(getIndexName(), getStore());
		
	}

	class Handler implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			System.out.println(t.toString() +" Throwable: ");
			e.printStackTrace();
			synchronized (me) {
				thread.interrupt();
			}

		}
	}
	
	
	
}

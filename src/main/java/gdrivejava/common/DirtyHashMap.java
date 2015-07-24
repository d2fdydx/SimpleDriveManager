package gdrivejava.common;

import java.util.HashMap;

import gdrivejava.event.listener.DirtyListener;

public class DirtyHashMap<F> extends HashMap<String, INode<F>>{
	boolean dirtyFlag=false;
	DirtyListener listener = null;
	
	
	public boolean isDirtyFlag() {
		return dirtyFlag;
	}


	public void setDirtyFlag(boolean dirtyFlag) {
		this.dirtyFlag = dirtyFlag;
	}


	public DirtyListener getListener() {
		return listener;
	}


	public void setListener(DirtyListener listener) {
		this.listener = listener;
	}


	@Override
	public INode<F> put(String key, INode<F> value) {
	// TODO Auto-generated method stub
		dirtyFlag=true;
		return super.put(key, value);
	}

	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		dirtyFlag=true;
		super.clear();
	}
	
	@Override
	public INode<F> remove(Object key) {
		// TODO Auto-generated method stub
		dirtyFlag=true;
		return super.remove(key);
	}


}

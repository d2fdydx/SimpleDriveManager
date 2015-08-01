package gdrivejava.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import gdrivejava.event.listener.ModifiedListener;

public class ListenerMap<K,V> implements Map<K, V>, Serializable{
	Map<K,V> map =null;
	ModifiedListener<V> listener  = null;
	public ListenerMap(Map<K,V> m, ModifiedListener<V> listener) {
		// TODO Auto-generated constructor stub
		map =m;
		this.listener = listener;
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return map.entrySet();
	}

	@Override
	public synchronized V get(Object key) {
		// TODO Auto-generated method stub
		
			return map.get(key);
		
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return map.keySet();
	}

	@Override
	public synchronized V put(K key, V value) {
		// TODO Auto-generated method stub
		listener.notifyModified(value);
		return map.put(key, value);
	}

	@Override
	public synchronized void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		for (V value : m.values()){
			listener.notifyModified(value);
		}
		map.putAll(m);
	}

	@Override
	public synchronized V remove(Object key) {
		// TODO Auto-generated method stub
		return map.remove(key);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return map.values();
	}
	
	
	
/*
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
*/

}

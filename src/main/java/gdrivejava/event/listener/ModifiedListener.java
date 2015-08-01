package gdrivejava.event.listener;

import java.io.Serializable;

import gdrivejava.common.INode;

public interface ModifiedListener<V> extends Serializable {
	void notifyModified(V node);
		
		
	
}

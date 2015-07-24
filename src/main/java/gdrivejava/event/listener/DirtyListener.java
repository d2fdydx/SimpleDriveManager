package gdrivejava.event.listener;

import gdrivejava.common.INode;

public interface DirtyListener {
	void notifyDirty(INode<?> node);
		
		
	
}

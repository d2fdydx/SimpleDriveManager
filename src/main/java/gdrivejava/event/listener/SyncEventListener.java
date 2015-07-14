package gdrivejava.event.listener;

import gdrivejava.event.SyncEvent;

public interface SyncEventListener {
	void handle (SyncEvent event);
}

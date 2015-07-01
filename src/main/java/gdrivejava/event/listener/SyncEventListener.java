package gdrivejava.event.listener;

import gdrivejava.event.SyncEvent;

public interface SyncEventListener<T extends SyncEvent> {
	void handle (T event);
}

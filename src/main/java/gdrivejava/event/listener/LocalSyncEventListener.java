package gdrivejava.event.listener;

import gdrivejava.event.LocalSyncEvent;

public interface LocalSyncEventListener {

	void handleLocalSync(LocalSyncEvent e);
}

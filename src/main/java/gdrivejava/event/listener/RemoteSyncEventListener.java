package gdrivejava.event.listener;

import gdrivejava.event.RemoteSyncEvent;

public interface RemoteSyncEventListener {

	void handleRemoteSync (RemoteSyncEvent e);
}

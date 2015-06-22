package gdrivejava.common;

import java.util.List;

public interface FileSystem <Listener>{

	

	public List<INode> listFiles();
	public void addEventListener(Listener listener);
	
	//public void sync (File file, String path);
	
}

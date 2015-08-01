package gdrivejava.local;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import gdrivejava.common.INode;
import gdrivejava.common.ListenerMap;
import gdrivejava.common.ObjectStoreUtil;
import gdrivejava.event.listener.ModifiedListener;
import gdrivejava.main.DriveMain;

public class LocalFileStore implements Serializable{
	
	Map<String,INode<File>> nodeMap = null;
	transient LocalFileSystem fileFs =null;
	long lastModifiedTime=0;
	public Map<String,INode<File>> getNodeMap() {
		return nodeMap;
	}



	public LocalFileStore(LocalFileSystem fs) {
		fileFs = fs;
		nodeMap=new HashMap<String, INode<File>>();
		nodeMap= new ListenerMap<>(nodeMap, new ModifiedListener<INode<File>>() {

			@Override
			public void notifyModified(INode<File> node) {
				// TODO Auto-generated method stub
				if (lastModifiedTime < node.getLastModifiedTime()){
					lastModifiedTime = node.getLastModifiedTime();
				}
			}
		});
		
		
		buildStore();
	}
	
	void buildStore() {
		File rootfile = new File(DriveMain.LocationPath);
		indexAllFiles(rootfile,null);
	}
	
	public void indexAllFiles(File folder,INode<File> parentNode)  {
		
		if (parentNode == null){
			//root
			parentNode = new LocalINode();
			parentNode.setFile(folder);
			parentNode.setRoot(true);
			parentNode.setDir(true);
			
			
			nodeMap.put(parentNode.getFullPathName(),parentNode);
		}
		
		
	    for (File fileEntry : folder.listFiles()) {
	    
	    	if (FilenameUtils.getExtension(fileEntry.getPath()).equals(ObjectStoreUtil.EXTENSION)){
	    		System.out.println("Extension " + FilenameUtils.getExtension(fileEntry.getPath()));
	    		continue;
	    	}
	    	INode<File> childNode = new LocalINode();
	    	childNode.setParent(parentNode);
	    	childNode.setFile(fileEntry);
	    	
	    	parentNode.addChild(childNode);
	    	nodeMap.put(childNode.getFullPathName(),childNode);
	    	
	        if (fileEntry.isDirectory()) {
	        	childNode.setDir(true);
	        	indexAllFiles(fileEntry,childNode);
	            
	        } 
	    }
	}
}

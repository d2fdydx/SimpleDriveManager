package gdrivejava.local;

import gdrivejava.common.INode;
import gdrivejava.main.DriveMain;

import java.io.File;
import java.util.HashMap;

public class LocalFileStore {
	
	HashMap<String,INode> nodeMap = null;

	public HashMap<String, INode> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(HashMap<String, INode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public LocalFileStore(){
		nodeMap=new HashMap<String, INode>();
		buildStore();
	}
	
	void buildStore(){
		File rootfile = new File(DriveMain.LocationPath);
		indexAllFiles(rootfile,null);
	}
	
	public void indexAllFiles(File folder,INode parentNode) {
		
		if (parentNode == null){
			parentNode = new INode();
			parentNode.setLocalFile(folder);
			
			parentNode.setDir(true);
			
			System.out.println(parentNode.getFullPathName());
			nodeMap.put(parentNode.getFullPathName(),parentNode);
		}
		
		
	    for (File fileEntry : folder.listFiles()) {
	    	INode childNode = new INode();
	    	childNode.setParent(parentNode);
	    	childNode.setLocalFile(fileEntry);
	    	parentNode.addChild(childNode);
	    	nodeMap.put(childNode.getFullPathName(),childNode);
	    	System.out.println(childNode.getFullPathName());
	        if (fileEntry.isDirectory()) {
	        	childNode.setDir(true);
	        	indexAllFiles(fileEntry,childNode);
	            
	        } 
	    }
	}
}

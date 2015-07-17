package gdrivejava.local;

import gdrivejava.common.INode;
import gdrivejava.main.DriveMain;

import java.io.File;
import java.util.HashMap;

public class LocalFileStore {
	
	HashMap<String,INode<File>> nodeMap = null;
	LocalFileSystem fileFs =null;
	
	public HashMap<String,INode<File>> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(HashMap<String, INode<File>> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public LocalFileStore(LocalFileSystem fs){
		fileFs = fs;
		nodeMap=new HashMap<String, INode<File>>();
		buildStore();
	}
	
	void buildStore(){
		File rootfile = new File(DriveMain.LocationPath);
		indexAllFiles(rootfile,null);
	}
	
	public void indexAllFiles(File folder,INode<File> parentNode) {
		
		if (parentNode == null){
			//root
			parentNode = new LocalINode();
			parentNode.setFile(folder);
			parentNode.setRoot(true);
			parentNode.setDir(true);
			
			System.out.println(parentNode.getFullPathName());
			nodeMap.put(parentNode.getFullPathName(),parentNode);
		}
		
		
	    for (File fileEntry : folder.listFiles()) {
	    	INode<File> childNode = new LocalINode();
	    	childNode.setParent(parentNode);
	    	childNode.setFile(fileEntry);
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

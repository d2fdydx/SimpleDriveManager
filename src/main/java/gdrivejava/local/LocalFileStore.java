package gdrivejava.local;

import gdrivejava.common.INode;
import gdrivejava.common.ObjectStoreUtil;
import gdrivejava.main.DriveMain;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

public class LocalFileStore implements Serializable{
	
	HashMap<String,INode<File>> nodeMap = null;
	transient LocalFileSystem fileFs =null;
	
	public HashMap<String,INode<File>> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(HashMap<String, INode<File>> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public LocalFileStore(LocalFileSystem fs) throws Exception{
		fileFs = fs;
		nodeMap=new HashMap<String, INode<File>>();
		buildStore();
	}
	
	void buildStore() throws Exception{
		File rootfile = new File(DriveMain.LocationPath);
		indexAllFiles(rootfile,null);
	}
	
	public void indexAllFiles(File folder,INode<File> parentNode) throws Exception {
		
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

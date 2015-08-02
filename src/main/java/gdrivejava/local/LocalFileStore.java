package gdrivejava.local;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
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
	transient boolean dirty=false;
	public boolean isDirty() {
		return dirty;
	}



	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}



	public LocalFileSystem getFileFs() {
		return fileFs;
	}



	public void setFileFs(LocalFileSystem fileFs) {
		this.fileFs = fileFs;
	}

	long lastModifiedTime=0;
	final public static String CONFLICT_PRE = "conflict_";
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
	
	public void deleteFile(String path){
		
	
		//File file = new File(FilenameUtils.concat(fileFs.getRootPath(),path));
		System.out.println(FilenameUtils.concat(fileFs.getRootPath(),path));
		System.out.println("file delete: "+path);
		Path p = FileSystems.getDefault().getPath(FilenameUtils.concat(fileFs.getRootPath(),path));
		try {
			Files.delete(p);
			System.out.println("file delete: "+FilenameUtils.concat(fileFs.getRootPath(),path));
			this.nodeMap.remove(path);
			setDirty(true);
			
		}
		catch (NoSuchFileException e){
			this.nodeMap.remove(path);
			setDirty(true);

		}
		catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("file delete fail: "+FilenameUtils.concat(fileFs.getRootPath(),path));
		}

	}
	
	public void renameFile(String path){
		String abPath =FilenameUtils.concat(fileFs.getRootPath(),path);
		File file = new File (abPath);
		String newName =CONFLICT_PRE+FilenameUtils.getName(abPath);
		String dir = FilenameUtils.getFullPath(abPath);
		File newfile = new File(FilenameUtils.concat(dir,newName));
		INode<File> node = this.nodeMap.remove(path);
		
		file.renameTo(newfile);
		System.out.println("reset file name to " + newfile.getAbsolutePath());
		node.setFile(newfile);
		this.nodeMap.put(node.getFullPathName(),node);
		this.nodeMap.remove(path);
		setDirty(true);
		
	}



	public INode<File> createNode(String path) {
		// TODO Auto-generated method stub
		
		if (nodeMap.containsKey(path)){
			return nodeMap.get(path);
		}
		
		File file = new File(FilenameUtils.concat(this.fileFs.getRootPath(), path));
		if (file.exists()){
			INode<File> node = new LocalINode();
			node.setFile(file);
			INode<File> parent = createNode(FilenameUtils.getPath(path));
			node.setParent(parent);
			parent.addChild(node);
			setDirty(true);
			nodeMap.put(node.getFullPathName(), node);
			return node;
		}
		return null;
	}



	public void refresh(INode<File> node) {
		// TODO Auto-generated method stub
		File file = new File (FilenameUtils.concat(this.fileFs.getRootPath(), node.getFullPathName()));
		if (file.exists()){
			node.setFile(file);
		}else{
			node.setCheckSum(null);
		}
		this.setDirty(true);
	}
	
}

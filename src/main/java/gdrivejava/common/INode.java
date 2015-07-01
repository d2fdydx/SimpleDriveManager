package gdrivejava.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.model.File;

public class INode implements Serializable {

	String Id=null;
	String fullPathName ="/";
	List <INode> children =new ArrayList<INode>();
	INode parent = null;
	boolean root=false;
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean root) {
		this.root = root;
	}
	public String getFullPathName() {
		return fullPathName;
	}
	public void setFullPathName(String fullPathName) {
		this.fullPathName = fullPathName;
	}
	boolean dir =false;
	
	
	public boolean isDir() {
		return dir;
	}
	public void setDir(boolean dir) {
		this.dir = dir;
	}
	public INode getParent() {
		return parent;
	}
	public void setParent(INode parent) {
		this.parent = parent;
	}
	File file =null;
	public INode (){
	
	}
	public INode (File f){
		file = f;
	}
	public String getId() {
		return file.getId();
	}


	public void addChild (INode n){
		this.children.add(n);
	}
	public List<INode> getChildren() {
		return children;
	}
	public void setChildren(List<INode> children) {
		this.children = children;
	}
	public String getName(){
		if (file !=null)
			return file.getTitle();
		else return "/";
	}
	
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
}

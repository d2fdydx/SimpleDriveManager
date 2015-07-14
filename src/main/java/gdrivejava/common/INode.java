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
	boolean dir =false;
	File remoteFile =null;
	java.io.File localFile =null;


	public java.io.File getLocalFile() {
		return localFile;
	}
	public void setLocalFile(java.io.File localFile) {
		this.localFile = localFile;
	}
	public File getRemoteFile() {
		return remoteFile;
	}
	public void setRemoteFile(File remoteFile) {
		this.remoteFile = remoteFile;
	}
	public void setId(String id) {
		Id = id;
	}
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean root) {
		this.root = root;
	}
	public String getFullPathName() {

		if (parent !=null){
			if (!parent.isRoot()){
				if (localFile!=null){
					return parent.getFullPathName()+"/"+localFile.getName();
				}else if (remoteFile !=null){
					return parent.getFullPathName()+"/"+remoteFile.getTitle();
				}
				return parent.getFullPathName()+"/"+"noName";
			}else{
				if (localFile!=null){
					return parent.getFullPathName()+localFile.getName();
				}else if (remoteFile !=null){
					return parent.getFullPathName()+remoteFile.getTitle();
				}
				return parent.getFullPathName()+"noName";
			}
		
		}
		return "/";
	}
	public void setFullPathName(String fullPathName) {
		this.fullPathName = fullPathName;
	}



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

	public INode (){

	}
	public INode (File f){
		remoteFile = f;
	}
	public String getId() {

		return Id;
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
		if (remoteFile !=null)
			return remoteFile.getTitle();
		else if (root){
			return "";
		}else return "noName";
	}

}

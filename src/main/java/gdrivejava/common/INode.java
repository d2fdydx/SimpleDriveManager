package gdrivejava.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class INode<E> implements Serializable {

	String Id=null;
	String fullPathName ="/";
	List <INode<E>> children =new ArrayList<INode<E>>();
	INode<E> parent = null;
	boolean root=false;
	boolean dir =false;
	E file=null;

	
	protected abstract String getName();
	
	protected abstract long getModifiedTime();
	protected abstract String getMd5CheckSum();
	public E getFile() {
		return file;
	}
	public void setFile(E file) {
		this.file = file;
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
				
				if (file !=null){
					return parent.getFullPathName()+"/"+getName();
				}
				return parent.getFullPathName()+"/"+"noName";
			}else{
				
				if (file!=null){
					return parent.getFullPathName()+getName();
				}
				return parent.getFullPathName()+"noName";
			}
		
		}
		return "/";
	}
	



	public boolean isDir() {
		return dir;
	}
	public void setDir(boolean dir) {
		this.dir = dir;
	}
	public INode<E> getParent() {
		return parent;
	}
	public void setParent(INode<E> parent) {
		this.parent = parent;
	}

	public INode (){

	}
	
	public String getId() {

		return Id;
	}


	public void addChild (INode<E> n){
		this.children.add(n);
	}
	public List<INode<E>> getChildren() {
		return children;
	}
	public void setChildren(List<INode<E>> children) {
		this.children = children;
	}
	

}

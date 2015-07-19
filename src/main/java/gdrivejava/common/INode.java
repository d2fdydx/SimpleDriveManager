package gdrivejava.common;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

public abstract class INode<E> implements Serializable {

	protected String name =null;
	protected long lastModifiedTime =0;
	String Id=null;
	String fullPathName =null;
	protected String checkSum =null;

	List <INode<E>> children =new ArrayList<INode<E>>();
	INode<E> parent = null;
	boolean root=false;
	boolean dir =false;
	transient protected E file=null;

	protected String fileStr =null;
	

	
	
	public String getName() {
		return name;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFullPathName(String fullPathName) {
		this.fullPathName = fullPathName;
	}

	public String getFileStr() {
		return fileStr;
	}

	protected void setFileStr(String fileStr) {
		this.fileStr = fileStr;
	}

	
	
	public E getFile() throws Exception {
		
		return file;
	}
	public void setFile(E file) {
	

		this.file = file;
		this.fileStr = file.toString();
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
	public String getFullPathName() throws Exception {

		if (parent !=null){
			if (!parent.isRoot()){
				
				if (file !=null){
					//parent.getFullPathName()+"/"+getName();
					return parent.getFullPathName()+"/"+getName();
				}
				fullPathName=parent.getFullPathName()+"/"+"noName";
				return parent.getFullPathName()+"/"+"noName";
			}else{
				
				if (file!=null){
					fullPathName = parent.getFullPathName()+getName();
					return parent.getFullPathName()+getName();
				}
				fullPathName =parent.getFullPathName()+"noName";
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

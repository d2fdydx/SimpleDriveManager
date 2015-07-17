package gdrivejava.common;

public class INodeWalker {

	/*
	public void printStructure(INode root){
		printNode(root,0);
	}
	
	
	void printNode(INode node, int depth){
		String s = "";
		for (int i=0; i<depth; i++){
			s += "-";
		}
		s += node.getName();
		System.out.println(s);
		for (INode child : node.getChildren()){
			printNode(child,depth +1);
		}
	}
	public void setFullPathName(INode root){
		
		setName(root,"");
	}
	void setName(INode current, String parentStr){
		current.setFullPathName(parentStr+current.getName());
		System.out.println(current.getFullPathName());
		for (INode child : current.getChildren()){
			setName(child,current.getFullPathName()+"/");
		}
		return ;
	}
	*/
	
}

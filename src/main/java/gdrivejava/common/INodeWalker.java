package gdrivejava.common;

public class INodeWalker {

	
	public void printStructure(INode<?> root){
		printNode(root,0);
	}
	
	
	void printNode(INode<?> node, int depth){
		String s = "";
		for (int i=0; i<depth; i++){
			s += "-";
		}
		s += node.getName();
		System.out.println(s);
		for (INode<?> child : node.getChildren()){
			printNode(child,depth +1);
		}
	}
	

	
	
}

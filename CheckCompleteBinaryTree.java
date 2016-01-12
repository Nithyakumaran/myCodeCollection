public class CheckCompleteBinaryTree{
	public static void main(String[] args){
		System.out.println("Program to check if a tree is complete binary tree");
		Tree t= new Tree(4);
		t.addNode(2);
		t.addNode(1);
		/*t.addNode(3);
		t.addNode(6);
		t.addNode(5);
		t.addNode(8); */
		System.out.println("The tree " + t+" \nis complete " + checkIfCompleteBinaryTree(t));
	}

public static boolean checkIfCompleteBinaryTree(Tree t){
	if(t==null) return true;
	if(t.left==null && t.right==null) return true;
	if(t.left==null && t.right!=null) return false;
	else return checkIfCompleteBinaryTree(t.left)&& checkIfCompleteBinaryTree(t.right);
}


}

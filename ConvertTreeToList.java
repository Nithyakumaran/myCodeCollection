import com.Tree;
import com.LinkedList;

class ConvertTreeToList{
	public static void main(String [] args){
		System.out.println("Program to convert Tree to List");
		Tree root= new Tree(1);
		root.left= new Tree(2);
		root.left.left= new Tree(3);
		root.left.right= new Tree(4);
		root.right=new Tree(5);
		root.right.right= new Tree(6);
		System.out.println("Printing init Tree \n"+root);
		System.out.println(convertToList(root));

	}
	public static Tree convertToList(Tree root){
		if(root==null) return root;
		Tree left=convertToList(root.left);
		Tree right=convertToList(root.right);
		if(right==null && left==null)
			return root;
		else if(left==null){
			root.right=right;
			return root;
		}
		else if(right==null){
			root.right=left;
			return root;
		}
		else{
			Tree temp=left;
			while(temp!=null && temp.right!=null){
				temp= temp.right;
			}
			temp.right=right;
			root.right=left;
			root.left=null;	
			return root;	
		}
	}
}
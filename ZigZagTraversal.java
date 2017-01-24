class ZigZagTraversal{
	public static void main(String[] args){
		System.out.println("Program to ZigZag Traverse a tree");
		Tree t= new Tree(4);
		t.addNode(2);
		t.addNode(1);
		t.addNode(3);
		t.addNode(6);
		t.addNode(8);
		t.addNode(7);
		t.addNode(9);
		ZigZag(t);
	}
	public static void ZigZag(Tree t,Stack<Tree> stk){
		if(t==null) return;
		if(stk==null || !stk.isEmpty()){
			System.out.println("["+t.getVal+"]");
		}
	}
}
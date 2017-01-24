package com;

public class Tree{

	public int val;
	public Tree right;
	public Tree left;

	public Tree(int val){
		this.val=val;
		this.right=null;
		this.left=null;
	}
	public void add(int val){
		if(this.val>val)
			if(this.right==null) this.right=new Tree(val);
			else this.right.add(val);
		else 
			if(this.left==null) this.left=new Tree(val);
			else this.left.add(val);

	}
	public String toString(){
		
		return this.toString("\t");
	}
	public String toString(String inden){
		StringBuffer str= new StringBuffer();
		str.append(inden).append(this.val+"\n");
		if(this.left!=null)  str.append(this.left.toString(inden+"\t"));
		if(this.right!=null) str.append(this.right.toString(inden+"\t"));
		return str.toString();
	}
}
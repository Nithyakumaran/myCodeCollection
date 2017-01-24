class AddTwoList{
	public static void main(String[] args){
		System.out.println("Program to add two LinkedList");
		AddTwoList a= new AddTwoList();
		System.out.println("R  ::"+a.add());

	}

	LinkedList add(){
		LinkedList l= this.new LinkedList(2);
		l.next=this.new LinkedList(14);
		l.next.next=this.new LinkedList(3);

		LinkedList l1= this.new LinkedList(5);
		l1.next=this.new LinkedList(6);
		l1.next.next=this.new LinkedList(4);
		System.out.println("L1 ::"+l);
		System.out.println("L2 ::"+l1);
		return this.add(l,l1,0);
	}
	LinkedList add(LinkedList l1, LinkedList l2,int carry){
		int result=0;
		if(l1==null && l2 ==null) return null;
		if(l1==null ^ l2==null) return l1==null? l2: l1;
		else{
			result = l1.getVal()+l2.getVal()+carry;
			carry=result/10;
			result= result%10;
			LinkedList r= this.new LinkedList(result);
			r.next = add(l1.next,l2.next,carry);
			return r;
		}
	}

	public class LinkedList{
		int val;
		public LinkedList next;
		
		public LinkedList(int val){
			this.val=val;
			next=null;
		}
		public int getVal(){
			return this.val;
		}

		public String toString(){
			StringBuffer str= new StringBuffer();
			str.append(this.val).append("-->");
			if(this.next==null) str.append("end");
			else str.append(this.next);
			return str.toString();
		}

	}
}
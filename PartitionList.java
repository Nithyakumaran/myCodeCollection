import com.LinkedList;
class PartitionList{
	//1->4->3->2->5->2
	public static void main(String[] args){
		System.out.println("Program to reArrangebased on partiion");
		LinkedList l = new LinkedList(1);
		l.add(4);
		l.add(3);
		l.add(2);
		l.add(5);
		l.add(2);
		int partition=3;
		System.out.println("Printing List ::"+l);
		System.out.println("Partition Value::"+partition);
		reArrnge(l,partition,-1);
	}
	/**
	*	1-->4-->3-->2-->5-->2
	*	Step 1: 1< 3 and is infront so okay
	*	Step 2:	4>3  and is infront so okay
	*	Step 3: 3==3 so okay
	*	Step 4: 2< 3 and behind three, so move it front
	*	
	*	Find x position
	*	
	**/
	public static LinkedList reArrnge(LinkedList l, int part,int partPos){
		if(l==null) return l;
		if(partPos<0) partPos=pos(l,part,0);
		if(partPos<0) return l;
		else {
			System.out.println("Part Pos::"+partPos);
			int counter=1;
			LinkedList head=l;
			while(head!=null){
				if(head.getVal()<part && counter>partPos){
					move(l,head.getVal())
					head.val=head.next.val;
					head.next=head.next.next;
				}
			}
			return l;
		}

		
	}
	public static int pos(LinkedList l, int val,int pos){
		if(l==null) return -1;
		else if(l.getVal()==val) return pos+1; 
		else return pos(l.next,val,pos+1);

	}
}
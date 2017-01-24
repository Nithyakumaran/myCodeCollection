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
		public void add(int val){
			if(this.next==null) this.next= new LinkedList(val);
			else this.next.add(val);
		}
		public String toString(){
			StringBuffer str= new StringBuffer();
			str.append(this.val).append("-->");
			if(this.next==null) str.append("end");
			else str.append(this.next);
			return str.toString();
		}

	}
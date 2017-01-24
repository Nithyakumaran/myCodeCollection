public class Search {
	
	public static void main(String[] args){
		System.out.println("Program to find Range in an array");
		int[] arr= {1,1,2,2,3,3,3,4,5,6,6,6,6,7,8,8,8};
		print(arr);
		System.out.println(binarySearch(arr, 6, 0, arr.length-1));
	}
	static int binarySearch(int[] arr, int val,int start, int end){
		if(arr==null|| arr.length<1 ) return -1;
		if(end<start) return start;
		int mid=start+(end-start)/2;
		if(arr[mid]>val) return binarySearch(arr, val, start, mid-1);
		else return binarySearch(arr, val, mid+1, end);
	}
	
	static void print(int[] arr){
		if(arr==null|| arr.length<1) return;
		System.out.print("[");
		for(int i:arr) System.out.print(i+" ");
		System.out.println("]");
	}
}

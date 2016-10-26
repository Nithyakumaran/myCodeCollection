class SumIncreasingDecreaseingWindow{
	public static void main(String[] args){
		System.out.println("Program to Print find sum of decreasing and non decreasing sum in a given window size");
		int[] arr= {1,2,3,4,3,2,1,3,2,5,4};
		int k=4;
		findSum(arr,k);
	}
	static void print (int[] arr, int start, int end){
		if(arr.length<1 || start>=arr.length|| end>=arr.length) return;
		System.out.print("[");
		for(int i=start ; i<end;i++) System.out.print(arr[i]+" ");
		System.out.println("]");
	}
	public static void findSum(int[] arr, int k){
		if(arr==null || arr.length<1) return;
		if(k>=arr.length) return;
		int increasingSum=0;
		int decreasingSum=0;
		int pos=0;
		print(arr,0,k);
		for(int i=0;i<k-1;i++) {
			if(arr[i]<=arr[i+1]) increasingSum++;
			else decreasingSum++;
		} 
		System.out.println("Sum "+(increasingSum-decreasingSum));
		for(int i=1;i<arr.length-2;i++){
			print(arr,i,k+i);
			if(arr[i-1]<=arr[i]) increasingSum--;
			else if(arr[i-1]>arr[i]) decreasingSum--;
			if(arr[k+i-1]<=arr[k+i]) increasingSum++;
			else decreasingSum++;
			System.out.println("Sum "+(increasingSum-decreasingSum));
		}
	}
}
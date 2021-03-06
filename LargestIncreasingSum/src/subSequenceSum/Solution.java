package subSequenceSum;


import java.util.Arrays;
/**
 * 
 * @author Nithya
 *
 */
public class Solution {
	/**
	 * 
	 * @param intArr
	 * @param max
	 * @param counter
	 * @return Largest sum in the subsequence of increasing order
	 */
	public static int subSequenceSum(int[] intArr,int max,int counter){
		//Base Case
		if(intArr.length<=1) return intArr[0];
		
		else if(intArr[0]<intArr[1]) return subSequenceSum(Arrays.copyOfRange(intArr, 1, intArr.length), max= max+intArr[1],counter);
		else return Math.max(max,subSequenceSum(Arrays.copyOfRange(intArr, 1, intArr.length), intArr[1],counter));
		
	}
	
	public static void main(String[] args){
		int[] intArr= {1, 101, 2, 3, 100, 4, 5};
		System.out.println(subSequenceSum(intArr, intArr[0],1));
		
	}
}

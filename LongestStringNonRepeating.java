import java.util.*;
class LongestStringNonRepeating{

	static HashMap<String,Boolean> map= new HashMap<String,Boolean>();
	public static void main(String[] args){
		System.out.println("Longest Substring Without Repeating Characters");
		String str="abcabcbb";
		System.out.println("Initial String::"+str);
		String strOutput=longest(str,0,"");
		System.out.println("Longest non Repeating String::"+strOutput);
	}

	/**
	*	Recurence Relation
	*	str1=longest(str,n,prevBiggestString)
	*	str2=longest(str,n+1,"")
	*	return str1.length()<str2.length()?str2:str1;
	**/
	public static String longest(String str,int pos, String prevBiggestString){
		System.out.println("Pos::"+pos+"::prevBiggestString::"+prevBiggestString);
		if(!isValid(str,pos))return prevBiggestString;
		StringBuffer sb= new StringBuffer();
		if(!map.contains(str.charAt(pos)+"")) {
			sb.append(prevBiggestString).append(str.charAt(pos)+"");
			map.put(str.charAt(pos)+"");
		}
		
		return sb.toString();
	}
	public static boolean contains(String str, pos){
		if(!isValid(str,pos+1)) return false;

	}
	public static boolean isValid(String str, int pos){
		return str.length()>pos && pos>=0;
	}

}
class FareySequence{

	public static void main(String[] args){
		int n=100;
		printFareySequence(n);
	}
	
	static void printFareySequence(int n){
		if(n<1) return;
		for(int i=1;i<=n;i++)
			System.out.println(simplify(i,n));
		
	}
	
	static String simplify(int num, int denom){
		int gVal=gcd(num,denom);
		return (num/gval)+"/"+(denom/gVal);
	}
	
	static int gcd(int m, int n){
			if(m == n) return m;
			else if (m > n) return gcd(m-n, n);
			else return gcd(m, n-m);
	}
}

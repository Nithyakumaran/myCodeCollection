package fibo;

import java.io.*;
/**
 * This class is used to find if the given number is a fibonacci number
 * @author gnanasnn
 *
 */
public class Solution {
	/**
	 * @Desc This function provides a mathematical approximation 
	 * 			to find if the provided number is fibonacci
	 * @param input
	 * @return boolean in String format
	 */
	public static String isFibo(long input) {

		double X1 = 5 * Math.pow(input, 2) + 4;
		double X2 = 5 * Math.pow(input, 2) - 4;

		long X1_sqrt = (long) Math.sqrt(X1);
		long X2_sqrt = (long) Math.sqrt(X2);

		if ((X1_sqrt * X1_sqrt == X1) || (X2_sqrt * X2_sqrt == X2))
			return "IsFibo";
		else
			return "IsNotFibo";

	}
	/**
	 * @Desc	This is main function. It takes in number of test cases from users. 
	 * 			For every testcase it returns if the provided number is a Fibonnaci number or not
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		int testCases = 0;

		if ((input = br.readLine()) != null) {
			testCases = Integer.parseInt(input);
		}

		for (long i = 0; i < testCases; i++) {

			if ((input = br.readLine()) != null) {
				long data = Long.parseLong(input);
				System.out.println(isFibo(data));

			}

		}

	}

}

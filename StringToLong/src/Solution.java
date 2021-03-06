
import javax.naming.SizeLimitExceededException;

public class Solution {

	/**
	 * @Desc The Function takes a string and converts it to equivalent long
	 * @param String
	 *            s
	 * @return long
	 * @This function uses the ascii value 0-9 to convert string to long
	 * 
	 */
	public static long stringToLong(String s) throws Exception {
		s = s.trim();

		if (s.length() > 19)
			throw new SizeLimitExceededException(
					"String contains more number than maximum capacity of Long Data Type");
		long result = 0;
		long negative = 1;

		for (int i = 0; i < s.length(); i++) {

			// * check for negative sign
			if (i == 0 && s.charAt(i) == '-') {
				negative = -1;
				continue;
			}
			// Convert character to long. Use ASCII values
			long longChar = (long) s.charAt(i) - '0';

			// * ASCII range is between 0-9.
			// * If n> 0 denotes presence of value other than numbers in string.
			if (longChar >= 10 || longChar < 0)
				throw new NumberFormatException(
						"The String cannot contain alphabets or other special characters");

			result = result * 10 + longChar;

		}
		return negative * result;
	}

	public static void main(String[] args) {

		long i;
		try {
			// * Positive Cases
			// 1. Regualr Numbers as string
			i = stringToLong("123");
			if (i == 123)
				System.out.println("success");
			else
				System.out.println("failuer and retuned value is" + i);

			
			// * Positive Cases
			// 2. String with forward and trailing space
			i = stringToLong("   10    ");
			if (i == 10)
				System.out.println("success");
			else
				System.out.println("failuer and retuned value is" + i);

			
			// * Positive Cases
			// 2. Negative Number
			i = stringToLong("-123");
			if (i == -123)
				System.out.println("success");
			else
				System.out.println("failuer and retuned value is" + i);
			
			
			// Negative Cases Follow
			// 1. String size bigger than long Max value
			i = stringToLong("10000000000000000000000");
			if (i == -123)
				System.out.println("success");
			else
				System.out.println("failuer and retuned value is" + i);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			// Negative case
			// 2. String contains alphabets
			i = stringToLong("abc");

			if (i == -123)
				System.out.println("success");
			else
				System.out.println("failuer and retuned value is" + i);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

	}

}

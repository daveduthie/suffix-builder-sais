/**
 * 
 */
package com.ystervark;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

/**
 * @author daveduthie
 *
 */
public class StressTest {

	@Test
	public void aTest() {
		assertTrue(true);
	}

	public static int[] naiveSuffixArray(String s) {
		String[] str = new String[s.length()];
		int[] res = new int[s.length()];

		for (int i = 0; i < s.length(); ++i) {
			str[i] = s.substring(i);
		}
		Arrays.sort(str);

		for (int i = 0; i < s.length(); ++i) {
			res[i] = s.length() - str[i].length();
		}

		// for (int i = 0; i < str.length; ++i) {
		// System.out.println(res[i] + " -> " + str[i]);
		// }

		return res;
	}

	public static String makeRandomString(int len, boolean appendDollar) {
		Random r = new Random();
		StringBuilder b = new StringBuilder(len + 1);

		for (int i = 0; i < len; ++i) {
			char c = (char) (r.nextInt(4) + 65);
			b.append(c);
		}

		if (appendDollar) {
			b.append('$');
		}

		return b.toString();
	}

	public static void stressTestPatternMatching() {
		Random r = new Random();

		while (true) {
			int textLen = r.nextInt(10000);
			String text = StressTest.makeRandomString(textLen, true);

			int numPatterns = r.nextInt(1000);
			String[] patterns = new String[numPatterns];
			for (int i = 0; i < numPatterns; ++i) {
				int patternLen = r.nextInt(1000);
				String pattern = StressTest.makeRandomString(patternLen, false);
				patterns[i] = pattern;
			}

			SuffixArray.prn("text:", text);
			for (String p : patterns) {
				SuffixArray.prn("_patt:", p);
			}

			HashSet<Integer> matches = Matching.multiMatchesInText(text, patterns);
			HashSet<Integer> expect = Matching.naiveMultiPatternMatch(text, patterns);

			for (int e : expect) {
				if (!matches.contains(e)) {
					throw new IllegalStateException("Broken:\ntext: " + text + "\npatt: " + patterns.toString()
							+ "\nand " + e + " is not in matches");
				}
			}

			for (int m : matches) {
				if (!expect.contains(m)) {
					throw new IllegalStateException("Broken:\ntext:" + text + "\npatt:" + patterns.toString() + "\nand "
							+ m + " is in matches when it shouldn't be");
				} else {
					SuffixArray.prn("++++++++++++++++++++++++++++++++++++++++++++++> match:", m);
				}
			}
		}
	}

}

/**
 * 
 */
package com.ystervark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * @author daveduthie
 *
 */
public class Main {

	static class fastscanner {
		StringTokenizer tok = new StringTokenizer("");
		BufferedReader in;

		fastscanner() {
			in = new BufferedReader(new InputStreamReader(System.in));
		}

		String next() throws IOException {
			while (!tok.hasMoreElements())
				tok = new StringTokenizer(in.readLine());
			return tok.nextToken();
		}

		int nextInt() throws IOException {
			return Integer.parseInt(next());
		}
	}

	public static void main(String[] args) throws IOException {
		//
	}

}

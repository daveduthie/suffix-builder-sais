package com.ystervark;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ystervark.SuffixArray.Buckets;
import com.ystervark.SuffixArray.SuffArray;
import com.ystervark.SuffixArray.Text;

public class BucketsTest {

	Buckets b1;
	Buckets b2;
	Text text1;
	Text text2;

	@Before
	public void setUp() {
		b1 = new Buckets();
		b2 = new Buckets();
		text1 = new SuffixArray.Alphabetic("TGTGTGTGCACCG$".toCharArray());
		text2 = new SuffixArray.Alphabetic("AACGATAGCGGTAGA$".toCharArray());
		b1.build_vocab_list(text1);
		b2.build_vocab_list(text2);
	}

	@Test
	// @Ignore
	public void buildVocab() {
		assertTrue("$ in vocab list", b1.get_vocab(0) == '$');
		assertTrue("A in vocab list", b1.get_vocab(1) == 'A');
		assertTrue("C in vocab list", b1.get_vocab(2) == 'C');
		assertTrue("G in vocab list", b1.get_vocab(3) == 'G');
		assertTrue("T in vocab list", b1.get_vocab(4) == 'T');
	}

	@Test
	// @Ignore
	public void getCounts() {
		assertTrue("correct number of $'s", b1.get_count('$') == 1);
		assertTrue("correct number of A's", b1.get_count('A') == 1);
		assertTrue("correct number of C's", b1.get_count('C') == 3);
		assertTrue("correct number of G's", b1.get_count('G') == 5);
		assertTrue("correct number of T's", b1.get_count('T') == 4);
	}

	@Test
	// @Ignore
	public void getCounts2() {
		assertTrue("correct number of $'s", b2.get_count('$') == 1);
		assertTrue("correct number of A's", b2.get_count('A') == 6);
		assertTrue("correct number of C's", b2.get_count('C') == 2);
		assertTrue("correct number of G's", b2.get_count('G') == 5);
		assertTrue("correct number of T's", b2.get_count('T') == 2);
	}

	@Test
	// @Ignore
	public void getHeadPtrs() {
		b1.get_head_ptrs();

		assertTrue("correct $ head", b1.get_head_ptr('$') == 0);
		assertTrue("correct A head", b1.get_head_ptr('A') == 1);
		assertTrue("correct C head", b1.get_head_ptr('C') == 2);
		assertTrue("correct G head", b1.get_head_ptr('G') == 5);
		assertTrue("correct T head", b1.get_head_ptr('T') == 10);
	}

	@Test
	// @Ignore
	public void getHeadPtrs2() {
		b2.get_head_ptrs();

		assertTrue("correct $ head", b2.get_head_ptr('$') == 0);
		assertTrue("correct A head", b2.get_head_ptr('A') == 1);
		assertTrue("correct C head", b2.get_head_ptr('C') == 7);
		assertTrue("correct G head", b2.get_head_ptr('G') == 9);
		assertTrue("correct T head", b2.get_head_ptr('T') == 14);
	}

	@Test
	// @Ignore
	public void getTailPtrs() {
		b1.get_tail_ptrs();

		assertTrue("correct $ tail", b1.get_tail_ptr('$') == 0);
		assertTrue("correct A tail", b1.get_tail_ptr('A') == 1);
		assertTrue("correct C tail", b1.get_tail_ptr('C') == 4);
		assertTrue("correct G tail", b1.get_tail_ptr('G') == 9);
		assertTrue("correct T tail", b1.get_tail_ptr('T') == 13);
	}

	@Test
	// @Ignore
	public void getTailPtrs2() {
		b2.get_tail_ptrs();

		assertTrue("correct $ tail", b2.get_tail_ptr('$') == 0);
		assertTrue("correct A tail", b2.get_tail_ptr('A') == 6);
		assertTrue("correct C tail", b2.get_tail_ptr('C') == 8);
		assertTrue("correct G tail", b2.get_tail_ptr('G') == 13);
		assertTrue("correct T tail", b2.get_tail_ptr('T') == 15);
	}
}

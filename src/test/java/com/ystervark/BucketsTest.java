package com.ystervark;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ystervark.SuffixArray.Buckets;
import com.ystervark.SuffixArray.Text;

public class BucketsTest {

	Buckets b;
	Text text;

	@Before
	public void setUp() {
		b = new Buckets(256);
		text = new SuffixArray.Alphabetic("TGTGTGTGCACCG$".toCharArray());
		b.build_vocab_list(text);
	}

	@Test
	// @Ignore
	public void buildVocab() {
		assertTrue("$ in vocab list", b.get_vocab(0) == '$');
		assertTrue("A in vocab list", b.get_vocab(1) == 'A');
		assertTrue("C in vocab list", b.get_vocab(2) == 'C');
		assertTrue("G in vocab list", b.get_vocab(3) == 'G');
		assertTrue("T in vocab list", b.get_vocab(4) == 'T');
	}

	@Test
	// @Ignore
	public void getCounts() {
		assertTrue("correct number of $'s", b.get_count('$') == 1);
		assertTrue("correct number of A's", b.get_count('A') == 1);
		assertTrue("correct number of C's", b.get_count('C') == 3);
		assertTrue("correct number of G's", b.get_count('G') == 5);
		assertTrue("correct number of T's", b.get_count('T') == 4);
	}

	@Test
	// @Ignore
	public void getHeadPtrs() {
		b.get_head_ptrs();

		assertTrue("correct $ head", b.get_head_ptr('$') == 0);
		assertTrue("correct A head", b.get_head_ptr('A') == 1);
		assertTrue("correct C head", b.get_head_ptr('C') == 2);
		assertTrue("correct G head", b.get_head_ptr('G') == 5);
		assertTrue("correct T head", b.get_head_ptr('T') == 10);
	}

	@Test
	// @Ignore
	public void getTailPtrs() {
		b.get_tail_ptrs();

		assertTrue("correct $ tail", b.get_tail_ptr('$') == 0);
		assertTrue("correct A tail", b.get_tail_ptr('A') == 1);
		assertTrue("correct C tail", b.get_tail_ptr('C') == 4);
		assertTrue("correct G tail", b.get_tail_ptr('G') == 9);
		assertTrue("correct T tail", b.get_tail_ptr('T') == 13);
	}
}

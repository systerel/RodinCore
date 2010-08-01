package org.eventb.rubin.tests;

import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eventb.rubin.PredicateFrontEnd;
import org.eventb.rubin.Sequent;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Validation tests of newPP based on several examples from Rubin's book and
 * others.
 * 
 * @author Laurent Voisin
 */
public class NewPPTests extends AbstractPPTests {

	private static final String FILE_PATH = "formulas/";

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 1
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter01() throws Exception {
		testChapter("rubin_01");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 2
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter02() throws Exception {
		testChapter("rubin_02");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 3
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter03() throws Exception {
		testChapter("rubin_03");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 7
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter07() throws Exception {
		testChapter("rubin_07");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 8
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter08() throws Exception {
		testChapter("rubin_08");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 9
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter09() throws Exception {
		testChapter("rubin_09");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 10
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter10() throws Exception {
		testChapter("rubin_10");
	}

	/**
	 * Runs the old predicate prover on a set of sequents taken from chapter 11
	 * of Jean E. Rubin's book.
	 */
	@Test
	public void testChapter11() throws Exception {
		testChapter("rubin_11");
	}

	@Test
	public void testOthers() throws Exception {
		testChapter("others");
	}

	/*
	 * This test allows to check specifically the formulas in the special file
	 * "test". This is used only for debugging purposes.
	 */
	@Test
	@Ignore
	public void testTests() throws Exception {
		testChapter("test");
	}

	private void testChapter(String name) throws IOException {
		final String filePath = getLocalPath(new Path(FILE_PATH + name + ".txt"));
		final Sequent[] sequents = PredicateFrontEnd.parseFile(filePath);
		assertNotNull("Parser failed unexpectedly", sequents);
		for (Sequent sequent : sequents) {
			testSequent(sequent);
		}
	}

}

package org.eventb.rubin.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;
import org.eventb.rubin.PredicateFrontEnd;
import org.eventb.rubin.Sequent;
import org.junit.Test;

public class NewPPValidationTests extends AbstractPPTests {
	
	public static final String FILE_PATH = "formulas.valid/"; 
	
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
	
	private void testChapter(String chapNo) throws IOException {
		String fileName = getLocalPath(new Path(FILE_PATH + chapNo + ".txt"));
		Sequent[] sequents = PredicateFrontEnd.parseFile(fileName);
		assertNotNull("Parser failed unexpectedly", sequents);
		for (Sequent sequent: sequents) {
			testSequent(sequent);
		}
	}
	
	protected void testSequent(Sequent sequent) throws IOException {
		String name = sequent.getName();
		typeCheck(sequent);
		
//		if (!name.startsWith("VALID")) return;
		
		long start = System.currentTimeMillis();
		System.out.println("-------------------");
		System.out.println("Proving: " + name);
		

		final ISimpleSequent ss = SimpleSequents.make(sequent.getHypotheses(),
				sequent.getGoal(), ff);
		final PPProof proof = new PPProof(ss, null);
		proof.translate();
		proof.load();
		proof.prove(5000);
//		proof.prove(-1);
		PPResult ppr = proof.getResult();
		
		if (name.startsWith("VALIDPPFAILS")) {
			// Test for an valid sequent that PP fails to discharge
			assertEquals(name, ppr.getResult(), Result.valid);
		} else if (name.startsWith("VALID")) {
			// Test for a valid sequent
			assertEquals(name, ppr.getResult(), Result.valid);
		} else if (name.startsWith("INVALID")) {
			// Test for an invalid sequent
			assertTrue(name, !ppr.getResult().equals(Result.valid));
		} else {
			fail("Invalid name for sequent:\n" + sequent);
		}
		long stop = System.currentTimeMillis();
		System.out.println("Time: " + (stop-start) + "ms");
	}

}

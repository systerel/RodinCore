package org.eventb.rubin.tests;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.runtime.Path;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.rubin.PredicateFrontEnd;
import org.eventb.rubin.Sequent;
import org.junit.Test;

import com.clearsy.atelierb.provers.core.AtbProversCore;

public class OldPPTests extends AbstractPPTests {
	
	public static final String FILE_PATH = "formulas/"; 
	
	private static final long PP_DELAY = 800; 
	
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
	
//	public void testOthers() throws Exception {
//		testChapter("others");
//	}
	
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
		
//		if (!name.startsWith("VALID")) return;
		
		// Translate to a prover sequent
		ITypeEnvironment typenv = typeCheck(sequent);
		IProverSequent ps = ProverFactory.makeSequent(
				typenv,
				new HashSet<Predicate>(Arrays.asList(sequent.getHypotheses())),
				sequent.getGoal()
		);

		// Create the tactic for applying PP
		ITactic tactic = AtbProversCore.externalPP(false, PP_DELAY);

		if (name.startsWith("VALIDPPFAILS")) {
			// Test for an valid sequent that PP fails to discharge
			assertFailure("PP succeeded unexpectedly on sequent" + name, 
					ps, tactic);
		} else if (name.startsWith("VALID")) {
//			StringBuffer str = 
//				ClassicB.translateSequent(sequent.getHypotheses(),
//						sequent.getGoal());
//			writer.write("&\nSet(p"+ count++ +"|"+ str.toString()+")\n");
//			
//			System.out.println("&\nSet(p |"+ ClassicB.translateSequent(sequent.getHypotheses(),
//					sequent.getGoal()).toString()+")\n");
			
			// Test for a valid sequent
			assertSuccess("PP failed unexpectedly on sequent " + name,
					ps, tactic);
		} else if (name.startsWith("INVALID")) {
			// Test for an invalid sequent
			assertFailure("PP succeeded unexpectedly on sequent " + name, 
					ps, tactic);
		} else {
			fail("Invalid name for sequent:\n" + sequent);
		}
	}

	private void assertSuccess(String msg, IProverSequent ps, ITactic tactic) {
		IProofTree tree = ProverFactory.makeProofTree(ps,null);
		tactic.apply(tree.getRoot(), null);
		assertTrue(msg, tree.isClosed());
	}
	
	private void assertFailure(String msg, IProverSequent ps, ITactic tactic) {
		IProofTree tree = ProverFactory.makeProofTree(ps,null);
		tactic.apply(tree.getRoot(), null);
		assertFalse(msg, tree.isClosed());
	}
	
}

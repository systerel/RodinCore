package org.eventb.rubin.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;
import org.eventb.rubin.PredicateFrontEnd;
import org.eventb.rubin.Sequent;
import org.junit.Test;

public class PPValidTests extends AbstractPPTests {
	
	public static final String FILE_PATH = "formulas/rubin_"; 

	private List<Sequent> sequents = new ArrayList<Sequent>();
	
	@Test
	public void testValidTests() throws Exception {
		constructChapter("01");
		constructChapter("02");
		constructChapter("03");
		constructChapter("07");
		constructChapter("08");
		constructChapter("09");
		constructChapter("10");
		doTest();
	}
	
	private void doTest() throws Exception {
		long start = System.nanoTime();
		System.out.println("-------------------");
		
		for (Sequent sequent : sequents) {
			testSequent(sequent);
		}
		
		long stop = System.nanoTime();
		System.out.println("Time: " + (stop-start)/1000000 + "ms");
		System.out.println("-------------------");
	}

	private void constructChapter(String chapNo) throws IOException {
		String fileName = getLocalPath(new Path(FILE_PATH + chapNo + ".txt"));
		Sequent[] sequents = PredicateFrontEnd.parseFile(fileName);
		assertNotNull("Parser failed unexpectedly", sequents);
		for (Sequent sequent: sequents) {
			String name = sequent.getName();
			if (name.startsWith("VALID")) this.sequents.add(sequent);
		}
	}
	
	protected void testSequent(Sequent sequent) throws IOException {
		String name = sequent.getName();
		typeCheck(sequent);

		PPProof proof = new PPProof(sequent.getHypotheses(),sequent.getGoal(), null);
		proof.translate();
		proof.load();
		proof.prove(1000);
		PPResult ppr = proof.getResult();
		
		if (name.startsWith("VALIDPPFAILS")) {
			// Test for an valid sequent that PP fails to discharge
			assertEquals(name, ppr.getResult(), Result.valid);
		} else if (name.startsWith("VALID")) {
			// Test for a valid sequent
			assertEquals(name, ppr.getResult(), Result.valid);
		} else if (name.startsWith("INVALID")) {
			// Test for an invalid sequent
			assertEquals(name, ppr.getResult(), Result.invalid);
		} else {
			fail("Invalid name for sequent:\n" + sequent);
		}
	}

}

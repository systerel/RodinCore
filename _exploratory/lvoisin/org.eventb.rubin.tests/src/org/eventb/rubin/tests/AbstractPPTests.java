package org.eventb.rubin.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.eventb.pp.PPProof;
import org.eventb.pp.PPResult;
import org.eventb.pp.PPResult.Result;
import org.eventb.rubin.Sequent;
import org.osgi.framework.Bundle;

public class AbstractPPTests {

	public static final String PLUGIN_ID = "org.eventb.rubin.tests";

	public static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final boolean PERF = false;

	/*
	 * Returns a resolved local path for a file distributed as part of this
	 * plugin or a fragment of it.
	 */
	protected static String getLocalPath(IPath relativePath) throws IOException {
		final Bundle bundle = Platform.getBundle(PLUGIN_ID);
		final IPath path = BundledFileExtractor.extractFile(bundle,
				relativePath, false);
		return path.toOSString();
	}
	
	protected final ITypeEnvironment typeCheck(Sequent sequent) {
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		for (Predicate hyp: sequent.getHypotheses()) {
			typeCheck(hyp, typenv);
		}
		typeCheck(sequent.getGoal(), typenv);
		return typenv;
	}

	/**
	 * TypeChecks the given predicate, augmenting the given type environment
	 * with the inferred types.
	 * 
	 * @param pred
	 *            the predicate to typeCheck
	 * @param typenv
	 *            initial type environment. Will be extended with inferred types
	 */
	protected final void typeCheck(Predicate pred, ITypeEnvironmentBuilder typenv) {
		ITypeCheckResult result = pred.typeCheck(typenv);
		assertTrue("TypeChecker failed on predicate " + pred, result.isSuccess());
		typenv.addAll(result.getInferredEnvironment());
		assertTrue("PredicateFormula should be type-checked", pred.isTypeChecked());
	}

	protected void testSequent(Sequent sequent) throws IOException {
		final String name = sequent.getName();
		final long start, end;
		if (PERF) {
			start = System.currentTimeMillis();
			System.out.println("-------------------");
			System.out.println("Proving: " + name);
		}

		typeCheck(sequent);
		final ISimpleSequent ss = SimpleSequents.make(sequent.getHypotheses(),
				sequent.getGoal(), ff);
		final PPProof proof = new PPProof(ss, null);
		proof.translate();
		proof.load();
		proof.prove(400);
		final PPResult ppr = proof.getResult();

		if (name.startsWith("VALIDPPFAILS")) {
			// Test for an valid sequent that PP fails to discharge
			assertEquals(name, Result.valid, ppr.getResult());
		} else if (name.startsWith("VALID")) {
			// Test for a valid sequent
			assertEquals(name, Result.valid, ppr.getResult());
		} else if (name.startsWith("INVALID")) {
			// Test for an invalid sequent
			assertTrue(name, !ppr.getResult().equals(Result.valid));
		} else {
			fail("Invalid name for sequent:\n" + sequent);
		}
		if (PERF) {
			end = System.currentTimeMillis();
			System.out.println("Time: " + (end - start) + " ms");
		}
	}


}

package org.eventb.rubin.tests;

import static junit.framework.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.xprover.BundledFileExtractor;
import org.eventb.rubin.Sequent;
import org.osgi.framework.Bundle;

public class AbstractPPTests {

	public static final String PLUGIN_ID = "org.eventb.rubin.tests";

	public static final FormulaFactory ff = FormulaFactory.getDefault();

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
		ITypeEnvironment typenv = ff.makeTypeEnvironment();
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
	protected final void typeCheck(Predicate pred, ITypeEnvironment typenv) {
		ITypeCheckResult result = pred.typeCheck(typenv);
		assertTrue("TypeChecker failed on predicate " + pred, result.isSuccess());
		typenv.addAll(result.getInferredEnvironment());
		assertTrue("PredicateFormula should be type-checked", pred.isTypeChecked());
	}


}

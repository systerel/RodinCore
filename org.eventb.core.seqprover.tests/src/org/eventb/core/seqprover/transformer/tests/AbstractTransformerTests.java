/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer.tests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.junit.Assert.assertFalse;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.core.seqprover.transformer.SimpleSequents;

/**
 * Common stuff for tests of sequent transformations.
 * 
 * @author Laurent Voisin
 */
public class AbstractTransformerTests {

	protected static final FormulaFactory ff = TestLib.ff;

	protected static final Predicate P0 = genPred("0=0");
	protected static final Predicate P1 = genPred("1=1");
	protected static final Predicate P2 = genPred("2=2");
	protected static final Predicate P3 = genPred("3=3");

	protected static final Predicate TRUE = genPred("⊤");
	protected static final Predicate FALSE = genPred("⊥");

	protected static final ISequentTransformer identity = new ISequentTransformer() {
		@Override
		public Predicate transform(ITrackedPredicate predicate) {
			return predicate.getPredicate();
		}
	};

	protected static final ISequentTransformer fixed = new ISequentTransformer() {
		@Override
		public Predicate transform(ITrackedPredicate tpred) {
			final Predicate pred = tpred.getPredicate();
			if (pred.equals(P0)) {
				return P1;
			}
			return pred;
		}
	};

	protected static final ISequentTransformer discard = new ISequentTransformer() {
		@Override
		public Predicate transform(ITrackedPredicate predicate) {
			return null;
		}
	};

	protected static ISimpleSequent makeSequent(FormulaFactory factory,
			String goalImage, String... hypImages) {
		return makeSequent(factory.makeTypeEnvironment(), goalImage, hypImages);
	}

	protected static ISimpleSequent makeSequent(ITypeEnvironmentBuilder typenv,
			String goalImage, String... hypImages) {
		final int length = hypImages.length;
		final Predicate[] hyps = new Predicate[length];
		for (int i = 0; i < length; i++) {
			hyps[i] = genPred(typenv, hypImages[i]);
		}
		final Predicate goal = genPred(typenv, goalImage);
		return SimpleSequents.make(hyps, goal, typenv.getFormulaFactory());
	}

	protected static ISimpleSequent makeSequent(String goalImage,
			String... hypImages) {
		return makeSequent(ff.makeTypeEnvironment(), goalImage, hypImages);
	}

	protected static ISimpleSequent makeSequent(Predicate goal,
			Predicate... hyps) {
		return SimpleSequents.make(hyps, goal, ff);
	}

	protected static <T> void assertNotEquals(T left, T right) {
		assertFalse(left.equals(right));
		assertFalse(right.equals(left));
	}

}
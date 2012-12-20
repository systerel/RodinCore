/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.seqprover.tests.TestLib.genExpr;

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * @author htson
 *         <p>
 *         Abstract Unit tests for the Single Expression Input reasoner
 *         {@link SingleExprInputReasoner}
 */
public abstract class AbstractSingleExpressionInputReasonerTests extends AbstractManualReasonerTests {

	class SuccessfulTest {
		private final String sequentImage;
		private final String expressionImage;
		private final String [] results;

		public SuccessfulTest(String sequenceImage, String expressionImage,
				String... results) {
			this.sequentImage = sequenceImage;
			this.expressionImage = expressionImage;
			this.results = results;
		}

		SuccessfullReasonerApplication makeSuccessfullReasonerApplication() {
			final IProverSequent sequent = TestLib.genSeq(sequentImage);
			final ISealedTypeEnvironment te = sequent.typeEnvironment();
			final IReasonerInput input = makeInput(genExpr(te, expressionImage));
			return new SuccessfullReasonerApplication(sequent, input, results);
		}

	}
	
	protected abstract SuccessfulTest[] getSuccessfulTests();

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> apps = new ArrayList<SuccessfullReasonerApplication>();
		for (SuccessfulTest test : getSuccessfulTests()) {
			apps.add(test.makeSuccessfullReasonerApplication());
		}
		return apps.toArray(new SuccessfullReasonerApplication[apps.size()]);
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		String [] unsuccessfulTests = getUnsuccessfulTests();
		
		assert unsuccessfulTests.length % 4 == 0;
		for (int i = 0; i < unsuccessfulTests.length; i += 4) {
			unsuccessfullReasonerApps.addAll(makeIncorrectPositionApplication(
					unsuccessfulTests[i], unsuccessfulTests[i + 1],
					unsuccessfulTests[i + 2], unsuccessfulTests[i + 3]));
		}
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	protected abstract String [] getUnsuccessfulTests();
	
	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String sequentImage, String predicateImage, String expressionImage, String reason) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		Predicate predicate = null;
		if (predicateImage != null) {
			predicate = TestLib.genPred(predicateImage);
			predicate.typeCheck(ff.makeTypeEnvironment());
		}
		IReasonerInput input = makeInput(lib.parseExpression(expressionImage));
		
		IProverSequent sequent = TestLib.genSeq(sequentImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, reason));

		return unsuccessfullReasonerApps;
	}

	protected IReasonerInput makeInput(Expression expr) {
		return new SingleExprInput(expr);
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}

/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added methods rewritePred and noRewritePred
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;

//import com.b4free.rodin.core.B4freeCore;

/**
 * Abstract unit tests for the Manual Rewrites reasoners that implement the
 * {@link AbstractManualRewrites} interface. The purpose of this class is to
 * simplify the testing of such reasoners.
 * <p>
 * Implementers should define at least:
 * <ul>
 * <li>One method to test the function published in the reasoner tactic that
 * allows to compute applicable positions using method
 * {@link #assertGetPositions(String, String...).</li>
 * <li>One method to test the overall integration of the reasoner in the {@link AbstractManualRewrites} class.
 * It should test both for successful and failing reasoner applications.</li>
 * <li>Then use as many methods as appropriate to verify the rewriting performed
 * by the reasoner independently from the machinery in the abstract class.
 * This should be done using the methods
 * {@link #rewritePred(String, String, String,String)} and
 * {@link #noRewritePred(String, String, String)}.</li>
 * </ul>
 * 
 * @author htson
 */
public abstract class AbstractManualRewriterTests extends AbstractManualReasonerTests {

	private final AbstractManualRewrites rewriter;

	/**
	 * For backward compatibility with previous tests, do not use anymore.
	 */
	public AbstractManualRewriterTests() {
		this.rewriter = null;
	}

	public AbstractManualRewriterTests(AbstractManualRewrites rewriter) {
		this.rewriter = rewriter;
	}

	protected static class SuccessfulTest {
		
		String predicateImage;
		String positionImage;
		String[] results;

		public SuccessfulTest(String predicateImage, String positionImage, String... results) {
			this.predicateImage = predicateImage;
			this.positionImage = positionImage;
			this.results = results;
		}

	}

	protected Collection<SuccessfullReasonerApplication> makeSuccessfullReasonerApplication(
			 String predicateImage, String positionImage, String[] results) {
		final List<SuccessfullReasonerApplication> apps = new ArrayList<SuccessfullReasonerApplication>();		
		final ITypeEnvironmentBuilder typenv = ff.makeTypeEnvironment();
		final Predicate predicate = genPred(typenv, predicateImage);
		IPosition position = makePosition(positionImage);

		// Successful in goal
		IReasonerInput input = new AbstractManualRewrites.Input(null, position);
		final IProverSequent[] expecteds = new IProverSequent[results.length];
		for (int i = 0; i < expecteds.length; i++) {
			expecteds[i] = genFullSeq(typenv, "", "", "⊤", results[i]);
		}
		apps.add(new SuccessfullReasonerApplication(genSeq(" ⊤ |- " + predicate), input, expecteds));

		// Successful in hypothesis
		input = new AbstractManualRewrites.Input(predicate, position);
		
		String newHyps = stream(results).filter(res -> !res.trim().equals("⊤")).collect(joining(" ;; "));
		final IProverSequent expected = genFullSeq(typenv, predicate.toString(), "", newHyps, "⊤");
		apps.add(new SuccessfullReasonerApplication(genSeq(predicate + " |- ⊤"), input, expected));
		return apps;
	}
	
	protected void assertReasonerSuccess(String predicateImage, String positionImage, String... results)
			throws UntranslatableException {
		for (var app : makeSuccessfullReasonerApplication(predicateImage, positionImage, results)) {
			testSuccessfulReasonerApplications("successful rewriting expected", app);
		}
	}

	protected Collection<UnsuccessfullReasonerApplication> makeHypNotPresent() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();
		IProverSequent sequent = genSeq(" ⊤ |- ⊤ ");
		Predicate pred = genPred("⊥");
		IReasonerInput input = new AbstractManualRewrites.Input(pred,
				IPosition.ROOT);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Nonexistent hypothesis: ⊥"));
		return unsuccessfullReasonerApps;
	}

	protected Collection<UnsuccessfullReasonerApplication> makeIncorrectPositionApplication(
			String predicateImage, String positionImage) {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		Predicate predicate = genPred(predicateImage);
		IPosition position = makePosition(positionImage);
		IReasonerInput input = new AbstractManualRewrites.Input(null, position);

		IProverSequent sequent = genSeq(" ⊤ |- " + predicateImage);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Rewriter " + getReasonerID()
						+ " is inapplicable for goal " + predicate
						+ " at position " + position));

		sequent = genSeq(predicateImage + " |- ⊤");
		input = new AbstractManualRewrites.Input(predicate, position);
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input));
		unsuccessfullReasonerApps.add(new UnsuccessfullReasonerApplication(
				sequent, input, "Rewriter " + getReasonerID()
						+ " is inapplicable for hypothesis " + predicate
						+ " at position " + position));

		return unsuccessfullReasonerApps;
	}

	protected void assertReasonerFailure(String predicateImage, String positionImage) {
		for (var app : makeIncorrectPositionApplication(predicateImage, positionImage)) {
			testUnsuccessfulReasonerApplications("unsuccessful rewriting expected", app);
		}
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		Collection<SuccessfullReasonerApplication> successfullReasonerApps = new ArrayList<SuccessfullReasonerApplication>();
		SuccessfulTest [] successfulTests = getSuccessfulTests();
		for (SuccessfulTest test : successfulTests) {
			Collection<SuccessfullReasonerApplication> apps = makeSuccessfullReasonerApplication(test.predicateImage,
					test.positionImage, test.results);
			successfullReasonerApps.addAll(apps);

		}
		return successfullReasonerApps
				.toArray(new SuccessfullReasonerApplication[successfullReasonerApps
						.size()]); 
	}
	
	/**
	 * Returns successful test cases
	 *
	 * @return successful test cases
	 * @deprecated Do not override this method anymore. Call
	 *             {@link #assertReasonerSuccess(String, String, String...)}
	 *             with each test case instead.
	 */
	@Deprecated
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[0];
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		Collection<UnsuccessfullReasonerApplication> unsuccessfullReasonerApps = new ArrayList<UnsuccessfullReasonerApplication>();

		String [] unsuccessfulTests = getUnsuccessfulTests();
		
		assert unsuccessfulTests.length % 2 == 0;
		for (int i = 0; i < unsuccessfulTests.length; i += 2) {
			unsuccessfullReasonerApps.addAll(makeIncorrectPositionApplication(
					unsuccessfulTests[i], unsuccessfulTests[i+1]));
		}
		
		unsuccessfullReasonerApps.addAll(makeHypNotPresent());
		
		return unsuccessfullReasonerApps
			.toArray(new UnsuccessfullReasonerApplication[unsuccessfullReasonerApps
						.size()]);
	}

	/**
	 * Returns unsuccessful test cases
	 *
	 * @return unsuccessful test cases
	 * @deprecated Do not override this method anymore. Call
	 *             {@link #assertReasonerFailure(String, String)}
	 *             with each test case instead.
	 */
	@Deprecated
	protected String[] getUnsuccessfulTests() {
		return new String[0];
	}

//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

	/**
	 * Test the rewriter for rewriting from an input predicate (represented by its
	 * string image) at a given position to an expected predicate (represented by
	 * its string image).
	 * <p>
	 * The type environment should be in the format accepted by the method
	 * {@link TestLib#mTypeEnvironment(String, FormulaFactory)}.
	 * </p>
	 * 
	 * @param inputImage    the string image of the input predicate
	 * @param pos           the string image of the position to rewrite
	 * @param expectedImage the string image of the expected predicate
	 * @param env           the image of the type environment to be parsed by
	 *                      {@code mTypeEnvironment} and used for type-checking
	 */
	protected void rewritePred(String inputImage, String posImage,
			String expectedImage, String typenvImage) {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(typenvImage);
		final Predicate input = genPred(typenv, inputImage);
		final Predicate expected = genPred(typenv, expectedImage);
		final IPosition pos = makePosition(posImage);
		final Predicate actual = rewriter.rewrite(input, pos);
		assertNotNull("the rewrite failed", actual);
		assertEquals(expected, actual);
	}

	protected void rewritePred(String inputImage, String posImage,
			String expectedImage) {
		rewritePred(inputImage, posImage, expectedImage, "");
	}

	/**
	 * Test the rewriter for not rewriting an input predicate (represented by its
	 * string image) at a given position.
	 * <p>
	 * The type environment should be in the format accepted by the method
	 * {@link TestLib#mTypeEnvironment(String, FormulaFactory)}.
	 * </p>
	 * 
	 * @param inputImage the string image of the input predicate
	 * @param pos        the string image of the position to rewrite
	 * @param env        the image of the type environment to be parsed by
	 *                   {@code mTypeEnvironment} and used for type-checking
	 */
	protected void noRewritePred(String inputImage, String posImage,
			String typenvImage) {
		final ITypeEnvironmentBuilder typenv = mTypeEnvironment(typenvImage);
		final Predicate input = genPred(typenv, inputImage);
		final IPosition pos = makePosition(posImage);
		final Predicate actual = rewriter.rewrite(input, pos);
		assertNull("the rewrite succeeded unexpectedly", actual);
	}

	protected void noRewritePred(String inputImage, String posImage) {
		noRewritePred(inputImage, posImage, "");
	}

}

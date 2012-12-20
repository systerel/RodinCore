/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pp.sequent;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.pp.TestSequent.makeSequent;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.internal.pp.PPTranslator;
import org.eventb.pp.AbstractRodinTest;
import org.eventb.pp.TestSequent;
import org.junit.Test;

/**
 * Unit tests for class {@link PPTranslator}.
 * 
 * @author Laurent Voisin
 */
public class TestPPTranslator extends AbstractRodinTest {

	private static final ITypeEnvironmentBuilder NO_TE = ff.makeTypeEnvironment();
	private static final List<String> NO_HYP = emptyList();
	
	private static final IDatatypeExtension DT_TYPE = new IDatatypeExtension() {
		
		@Override
		public String getTypeName() {
			return "DT";
		}
		
		@Override
		public String getId() {
			return "DT.id";
		}
		
		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			// none
		}
		
		@Override
		public void addConstructors(IConstructorMediator mediator) {
			mediator.addConstructor("dt", "dt.id");
		}
	};
	
	private static final IDatatype DT = ff.makeDatatype(DT_TYPE);
	
	private static final FormulaFactory DT_FF = getInstance(DT.getExtensions());

	private static ISimpleSequent makeInputSequent(ITypeEnvironment typenv,
			List<String> hyps, String goal) {
		return makeSequent(typenv, hyps, goal);
	}

	private static TestSequent makeTestSequent(ITypeEnvironment typenv,
			List<String> hyps, String goal) {
		return new TestSequent(typenv, hyps, goal);
	}
			
	/**
	 * Ensures that a closed sequent is properly normalized and translated.
	 */
	@Test
	public void closedSequent() throws Exception {
		final ISimpleSequent is = makeInputSequent(NO_TE, NO_HYP, "⊤");
		final TestSequent expected = makeTestSequent(NO_TE, NO_HYP, "⊤");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that a sequent containing only scalar variables is properly
	 * normalized and translated.
	 */
	@Test
	public void simpleSequent() throws Exception {
		final ISimpleSequent is = makeInputSequent(NO_TE, asList("a = 0"),
				"b = 1");
		final TestSequent expected = makeTestSequent(
				mTypeEnvironment("a=ℤ; b=ℤ", ff), asList("a = 0"), "b = 1");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that a sequent is properly normalized before translation.
	 */
	@Test
	public void normalization() throws Exception {
		final ISimpleSequent is = makeInputSequent(NO_TE, asList("p = 0 ↦ TRUE"),
				"q = FALSE ↦ 1");
		final TestSequent expected = makeTestSequent(
				mTypeEnvironment("p_1=ℤ; p_2=BOOL; q_1=BOOL; q_2=ℤ", ff),
				asList("p_1 = 0 ∧ p_2 = TRUE"), "¬ q_1 = TRUE ∧ q_2 = 1");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that two occurrences of the same variable are normalized in the
	 * same way before translation.
	 */
	@Test
	public void normalizationDouble() throws Exception {
		final ISimpleSequent is = makeInputSequent(NO_TE,
				asList("p = 0 ↦ 1  ↦ (2 ↦ 3)"), "p = q ↦ r");
		final TestSequent expected = makeTestSequent(NO_TE,
				asList("p_1 = 0 ∧ p_2 = 1 ∧ p_3 = 2 ∧ p_4 = 3"),
				"p_1 = q_1 ∧ p_2 = q_2 ∧ p_3 = r_1 ∧ p_4 = r_2");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that normalization takes care of name conflicts.
	 */
	@Test
	public void normalizationConflict() throws Exception {
		final ISimpleSequent is = makeInputSequent(NO_TE, asList("p = 0 ↦ 1",
				"p_1 = 2"), "p_2 = TRUE");
		final TestSequent expected = makeTestSequent(NO_TE, asList(
				"p_3 = 0 ∧ p_4 = 1", "p_1 = 2"), "p_2 = TRUE");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that normalization removes hypotheses with extended expressions
	 * while keeping those with identifiers bearing a parametric type.
	 */
	@Test
	public void testExtensionsInHyps() throws Exception {
		final ITypeEnvironment tEnv = mTypeEnvironment("p=DT; a=ℤ; x=DT", DT_FF);
		final List<String> inHyps = asList("p = dt", "a = 0", "x ∈ DT",
				"∀y⦂DT,z⦂DT·y=z");
		final List<String> transHyps = asList("a = 0");
		final String goal = "a < a";
		final ISimpleSequent is = makeInputSequent(tEnv, inHyps, goal);
		final TestSequent exp = makeTestSequent(mTypeEnvironment("a=ℤ", DT_FF),
				transHyps, goal);
		exp.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that normalization makes a false goal if it contains extended
	 * expressions.
	 */
	@Test
	public void testExtensionsInGoal() throws Exception {
		final ITypeEnvironment tEnv = mTypeEnvironment("p=DT; a=ℤ", DT_FF);
		final List<String> hyp = asList("a = 0");
		final ISimpleSequent is = makeInputSequent(tEnv, hyp, "p = dt");
		final TestSequent expected = makeTestSequent(
				mTypeEnvironment("a=ℤ", ff), hyp, "⊥");
		expected.assertTranslatedSequentOf(is);
	}

	/**
	 * Ensures that normalization succeeds if goal contains identifiers bearing
	 * a parametric type.
	 */
	@Test
	public void testExtensionsInTypeEnv() throws Exception {
		final ITypeEnvironment tEnv = mTypeEnvironment("a=ℤ; x=DT", DT_FF);
		final List<String> inHyps = asList("a = 0", "x ∈ DT");
		final List<String> transHyps = asList("a = 0");
		final String goal = "∀y⦂DT·y=x";
		final ISimpleSequent is = makeInputSequent(tEnv, inHyps, goal);
		final TestSequent exp = makeTestSequent(mTypeEnvironment("a=ℤ", DT_FF),
				transHyps, "⊥");
		exp.assertTranslatedSequentOf(is);
	}

}

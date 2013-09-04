/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer.tests;

import static java.util.Arrays.copyOf;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.eventb.core.seqprover.transformer.SimpleSequents.translateDatatypes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.tests.DatatypeParser;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;
import org.junit.Test;

/**
 * Acceptance tests for
 * {@link SimpleSequents#translateDatatypes(ISimpleSequent)}. They check the
 * specific cases of type constructor, constructor and destructor expression
 * translation in a sequent. These tests are performed on both hypotheses and
 * goal.
 * 
 * @author "Thomas Muller"
 */
public class DatatypeTranslationTests extends AbstractTransformerTests {

	private static final String msgTypeEnvironment //
	= "Agent=ℙ(Agent); Identifier=ℙ(Identifier); a=Agent; b=Agent; c=Identifier";

	private static final String msgDatatypeSpec //
	= "MESSAGES[U,V] ::=  message[sender: U; receiver: U; identifier: V]";

	private static final String msgAxioms //
	= "MESSAGES∈Agent × Identifier  MESSAGES_Type;;"
			+ " message ∈ Agent × Agent × Identifier ⤖ MESSAGES_Type ;;"
			+ " sender∈ran(message) ↠ Agent ;;"
			+ " receiver∈ran(message) ↠ Agent ;;"
			+ " identifier∈ran(message) ↠ Identifier;;"
			+ " (sender ⊗ receiver) ⊗ identifier=message∼;;"
			+ " ∀U,V·partition(MESSAGES[U × V],message[U × U × V])";

	@Test
	public void testTypeConstructorInHyp() {
		testSequentTranslation(msgTypeEnvironment,//
				"∃ x ·x ∈ MESSAGES(Agent, Identifier) |- 2 = 1",//
				msgAxioms + " ;; ∃ x · x ∈ MESSAGES_Type |- 2 = 1");
	}

	@Test
	public void testConstructorInHyp() {
		testSequentTranslation(msgTypeEnvironment,
				"sample = message(a, b, c) |- 2 = 1",//
				msgAxioms + ";; sample = message(a ↦ b ↦ c) |- 2 = 1");
	}

	@Test
	public void testDestructorInHyp() {
		testSequentTranslation(msgTypeEnvironment,
				"sender(message(a, b, c)) = a  |- 2 = 1",//
				msgAxioms + ";; sender(message(a ↦ b ↦ c)) = a |- 2 = 1");
	}

	@Test
	public void testTypeConstructorInGoal() {
		testSequentTranslation("Agent=ℙ(Agent); Identifier=ℙ(Identifier)",
				" |- ∃ x ·x ∈ MESSAGES(Agent, Identifier)",//
				msgAxioms + "|- ∃ x · x ∈ MESSAGES_Type");
	}

	@Test
	public void testConstructorInGoal() {
		testSequentTranslation(msgTypeEnvironment,
				" |- sample = message(a, b, c)",//
				msgAxioms + "|- sample = message(a ↦ b ↦ c)");
	}

	@Test
	public void testDestructorInGoal() {
		testSequentTranslation(msgTypeEnvironment,
				" |- sender(message(a, b, c)) = a",//
				msgAxioms + "|- sender(message(a ↦ b ↦ c)) = a");
	}

	private void testSequentTranslation(String typeEnvStr, String sequentImage,
			String expectedImage) {
		final IDatatype datatype = DatatypeParser.parse(ff, msgDatatypeSpec);
		final FormulaFactory srcFac = datatype.getFactory();
		final ITypeEnvironmentBuilder srcTypenv = mTypeEnvironment(typeEnvStr, srcFac);
		final ISimpleSequent srcSequent = getSimpleSequent(srcTypenv,
				sequentImage);
		final ISimpleSequent actual = translateDatatypes(srcSequent);
		final FormulaFactory trgFac = actual.getFormulaFactory();
		assertNoDatatypeExtension(trgFac);
		final ITypeEnvironmentBuilder trgTypenv = actual.getTypeEnvironment().makeBuilder();
		final ISimpleSequent expected = getSimpleSequent(trgTypenv,
				expectedImage);
		assertEquals(expected, actual);
	}

	private void assertNoDatatypeExtension(FormulaFactory fac) {
		final Set<IFormulaExtension> extensions = fac.getExtensions();
		for (final IFormulaExtension extension : extensions) {
			assertFalse(extension.getOrigin() instanceof IDatatype);
		}
	}

	private ISimpleSequent getSimpleSequent(ITypeEnvironmentBuilder typenv,
			String sequentImage) {
		final String[] split = sequentImage.split("\\s*;;\\s*|\\s*\\|-\\s*");
		final String goalStr = split[split.length - 1];
		final String[] hStrs = copyOf(split, split.length - 1);
		final String[] filtered = (hStrs[0].isEmpty()) ? new String[0] : hStrs;
		return makeSequent(typenv, goalStr, filtered);
	}

}
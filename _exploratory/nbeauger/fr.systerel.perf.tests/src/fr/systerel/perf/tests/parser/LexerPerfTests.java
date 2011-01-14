/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests.parser;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.ParseResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import fr.systerel.perf.tests.Chrono;

/**
 * @author Nicolas Beauger
 * 
 */
@SuppressWarnings("restriction")
public class LexerPerfTests {

	private static final int TIMES_ALL_TOKENS = 100000;
	public static final int TIMES_REPEAT_SCAN = 10;
	
	@Rule
	public static final TestName testName = new TestName();

	public static final FormulaFactory FACTORY = FormulaFactory.getDefault();
	private static final AbstractGrammar GRAMMAR = FACTORY.getGrammar();
	private static final String ALL_TOKENS = "≠ ≤ dom ≥ λ ⇔ finite ⇒ partition ⩤ ⩥ card prj2 prj1 succ ▷ ⇸ ¬ mod ⋃ ⋂ + ( ) , ≔ ; pred > · = < ⤀ ∥ ∧ ℕ ∣ ℙ ⊥ ⊤ → ⦂ ↔ ∨ ∩ :∈ ∪ BOOL id ◁ ⤔ union ⤖ ] ∼ TRUE ℙ1 × ^   [   min ℕ1 max ∅ ∃ ‥ ∀ ⊈ ⊆ ⊄ :∣ ⊂ ∈ ∉ inter ∗ ∖ ran − ℤ ↣ FALSE ⊗ ÷ } bool ↠ { ↦ ∘ ";

	@Test
	public void lexAllTokens() {
		final String string = makeLexString();
		final ParseResult result = new ParseResult(FACTORY,
				LanguageVersion.LATEST, null);
		final Chrono chrono = new Chrono(testName);
		final int eof = AbstractGrammar._EOF; //GRAMMAR.getEOF();
		chrono.startMeasure();
		for (int i = 0; i < TIMES_REPEAT_SCAN; i++) {
			final Scanner scanner = new Scanner(string, result, GRAMMAR);
			while (scanner.Scan().kind != eof)
				;
		}
		chrono.endMeasure();
	}

	public static String makeLexString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TIMES_ALL_TOKENS; i++) {
			sb.append(ALL_TOKENS);
		}
		return sb.toString();
	}
}

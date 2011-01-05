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

import java.util.Map.Entry;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.internal.core.lexer.Scanner;
import org.eventb.internal.core.lexer.Scanner.ScannerState;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.IndexedSet;
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
	private static final int TIMES_REPEAT_SCAN = 10;
	
	@Rule
	public static final TestName testName = new TestName();

	private static final FormulaFactory FACTORY = FormulaFactory.getDefault();
	private static final AbstractGrammar GRAMMAR = FACTORY.getGrammar();
	private static final IndexedSet<String> TOKENS = GRAMMAR.getTokens();
	private static final String ALL_TOKENS;

	static {
		final StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> entry : TOKENS.entrySet()) {
			sb.append(entry.getKey());
			sb.append(' ');
		}
		ALL_TOKENS = sb.toString();
	}

	@Test
	public void lexAllTokens() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < TIMES_ALL_TOKENS; i++) {
			sb.append(ALL_TOKENS);
		}
		final ParseResult result = new ParseResult(FACTORY,
				LanguageVersion.LATEST, null);
		final Scanner scanner = new Scanner(sb.toString(), result, GRAMMAR);
		final ScannerState init = scanner.save();
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		for (int i = 0; i < TIMES_REPEAT_SCAN; i++) {
			while (scanner.Scan().kind != AbstractGrammar._EOF)
				;
			scanner.restore(init);
		}
		chrono.endMeasure();
	}
}

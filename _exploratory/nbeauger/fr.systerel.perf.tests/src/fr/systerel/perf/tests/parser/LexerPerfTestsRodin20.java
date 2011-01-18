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

import static fr.systerel.perf.tests.parser.Common.FACTORY;
import static fr.systerel.perf.tests.parser.Common.TIMES_REPEAT_SCAN;
import static fr.systerel.perf.tests.parser.Common.makeLexString;

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
public class LexerPerfTestsRodin20 {

	@Rule
	public static final TestName testName = new TestName();

	private static final AbstractGrammar GRAMMAR = FACTORY.getGrammar();

	@Test
	public void lexAllTokens() {
		final String string = makeLexString();
		final ParseResult result = new ParseResult(FACTORY,
				LanguageVersion.LATEST, null);
		final Chrono chrono = new Chrono(testName);
		final int eof = AbstractGrammar._EOF;
		chrono.startMeasure();
		for (int i = 0; i < TIMES_REPEAT_SCAN; i++) {
			final Scanner scanner = new Scanner(string, result, GRAMMAR);
			while (scanner.Scan().kind != eof)
				;
		}
		chrono.endMeasure();
	}
}

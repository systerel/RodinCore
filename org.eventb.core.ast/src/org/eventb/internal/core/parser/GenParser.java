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
package org.eventb.internal.core.parser;

import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.EOF;


import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.lexer.Scanner;

/**
 * @author Nicolas Beauger
 *
 */
public class GenParser {

	private static final boolean DEBUG = false;
	
	public static class OverrideException extends Exception {
	
		private static final long serialVersionUID = -1281802568424261959L;
	
		public OverrideException(String reason) {
			super(reason);
		}
	}

	public static class SyntaxError extends Exception {

		private static final long serialVersionUID = -8349775303104088736L;
		private static final SyntaxError SYNTAX_ERROR = new SyntaxError();
		
		private SyntaxError() {
			// singleton
		}

		public static SyntaxError getInstance() {
			return SYNTAX_ERROR;
		}

	}

	private final Scanner scanner;
	
	// this field configures the parser to parse some kind of formula,
	// viz. Expression, Predicate, Assignment, or Type.
	private final Class<?> clazz;
	private final FormulaFactory factory;
	private final ParseResult result;
	private final boolean withPredVar;

	
	static enum ProgressDirection {
		LEFT, RIGHT
	}
	
	public GenParser(Class<?> clazz, Scanner scanner, ParseResult result,
			boolean withPredVar) {
		super();
		this.scanner = scanner;
		this.clazz = clazz;
		this.result = result;
		this.factory = result.factory;
		this.withPredVar = withPredVar;
	}

	/**
	 * Parses the tokens produced by the scanner associated to this parser.
	 * <p>
	 * The actual result of the parse is obtained via
	 * {@link #getResult() getResult()}.
	 */
	public void parse() {

		final ParserContext pc = new ParserContext(scanner, factory,
				result, withPredVar);
		try {
			pc.init();
			// separate parsed type in order to have
			// errors in case of unexpected result type
			if (clazz == Type.class) {
				final Type res = pc.subParse(MainParsers.TYPE_PARSER, false);
				result.setParsedType(res);
			} else if (clazz == Assignment.class) {
				final Assignment res = pc.subParse(MainParsers.ASSIGNMENT_PARSER, false);
				result.setParsedAssignment(res); 
			} else if (clazz == Predicate.class) {
				final Predicate res = pc.subParse(MainParsers.PRED_PARSER, false);
				result.setParsedPredicate(res); 
			} else if (clazz == Expression.class) {
				final Expression res = pc.subParse(MainParsers.EXPR_PARSER, false);
				result.setParsedExpression(res); 
			} else {
				throw new IllegalArgumentException(
						"Can only parse one of: Predicate, Expression, Assignment or Type.");
			}
			final int eof = pc.getGrammar().getKind(EOF);
			if (pc.t.kind != eof) {
				failUnmatchedTokens(pc);
			}
			if (DEBUG) {
				pc.debugEndChecks();
			}
		} catch (SyntaxError e) {
			processFailure(pc.takeProblem());
		}

	}

	private void failUnmatchedTokens(ParserContext pc) {
		final int startPos = pc.t.pos;
		pc.scanUntilEOF();
		final int endPos = pc.t.pos - 1;
		processFailure(new ASTProblem(pc.makeSourceLocation(startPos, endPos),
				ProblemKind.UnmatchedTokens, ProblemSeverities.Error));
	}

	private void processFailure(ASTProblem problem) {
		result.addProblem(problem);
		result.resetParsedFormula();
	}
	
	/**
	 * Returns the current parsing result.
	 *
	 * @return the current result.
	 */
	public ParseResult getResult() {
		return result;
	}

}

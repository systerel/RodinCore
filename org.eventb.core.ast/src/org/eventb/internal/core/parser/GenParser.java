/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;


import static org.eventb.core.ast.Formula.NO_TAG;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;

/**
 * @author Nicolas Beauger
 *
 */
public class GenParser {

	
	public static class SyntaxError extends Exception {

		private static final long serialVersionUID = -8349775303104088736L;

		public SyntaxError(String reason) {
			super(reason);
		}

	}

	private static class CycleError extends Exception {
		public CycleError(String reason) {
			super(reason);
		}
	}

	private static class SyntaxCompatibleError extends SyntaxError {
		public SyntaxCompatibleError(String reason) {
			super(reason);
		}
	}

	private static class SyntaxUnexpectedSymbol extends SyntaxError {

		public SyntaxUnexpectedSymbol(String reason) {
			super(reason);
		}

	}

	private static class UnmatchedToken extends SyntaxError {

		public UnmatchedToken(String reason) {
			super(reason);
		}

	}

	
	private final Scanner scanner;
	
	// this field configures the parser to parse some kind of formula,
	// viz. Expression, Predicate, Assignment, or Type.
	private final Class<?> clazz;
	private final FormulaFactory factory;
	private final ParseResult result;
	private final LanguageVersion version;
	private final boolean withPredVar;

	
	
	static class ParserContext {
		private final Scanner scanner;
		protected final FormulaFactory factory;
		private final AbstractGrammar grammar;
		private final Object origin;
		protected Token t;    // last recognized token
		protected Token la;   // lookahead token
		
		public ParserContext(Scanner scanner, FormulaFactory factory, Object origin) {
			this.scanner = scanner;
			this.factory = factory;
			this.grammar = factory.getGrammar();
			this.origin = origin;
		}
		
		public SourceLocation getSourceLocation(int startPos) {
			return new SourceLocation(startPos, t.getEnd(), origin);
		}
		
		public void init() {
			la = scanner.Scan();
		}
		
		public void progress() {
			t = la;
			la = scanner.Scan();
		}
		
		public void progress(int expectedKind) throws SyntaxError {
			if (la.kind != expectedKind) {
				throw new SyntaxError("expected symbol " + expectedKind
						+ " but was " + la.val);
			}
			progress();
		}
		
		public ISubParser getSubParser() {
			return grammar.getSubParser(t.kind);
		}
		
		public boolean canProgressRight(int parentTag) throws SyntaxError {
			if (la.kind == BMath._EOF) { // end of the formula
				return false;
			}
			final OperatorRegistry opRegistry = grammar.getOperatorRegistry();
			return opRegistry.hasLessPriority(parentTag, grammar.getOperatorTag(la));
		}
		
	}
	
	
	public GenParser(Class<?> clazz, Scanner scanner, ParseResult result,
			boolean withPredVar) {
		super();
		this.scanner = scanner;
		this.clazz = clazz;
		this.result = result;
		this.factory = result.factory;
		this.version = result.version;
		this.withPredVar = withPredVar;
	}

	/**
	 * Parses the tokens produced by the scanner associated to this parser.
	 * <p>
	 * The actual result of the parse is obtained via
	 * {@link #getResult() getResult()}.
	 */
	public void parse() {

		try {
			final ParserContext pc = new ParserContext(scanner, factory, result.getOrigin());
			pc.init();
			final Formula<?> res = Parsers.MainParser.parse(NO_TAG, pc, 0);
			if (clazz.isInstance(res)) {
				if (clazz == Predicate.class) {
					result.setParsedPredicate((Predicate) res); 
				} else if (clazz == Expression.class) {
					result.setParsedExpression((Expression) res); 
				} else if (clazz == Assignment.class) {
					result.setParsedAssignment((Assignment) res); 
				}
				// FIXME parsing Type
				//			else if (StartOf(1)) { 
				//				Type type = Type();
				//				result.setParsedType(type); 
				//			}
			}
		} catch (SyntaxError e) {
			result.addProblem(new ASTProblem(null, ProblemKind.SyntaxError, ProblemSeverities.Error, e.getMessage()));
			// TODO Auto-generated catch block
			result.resetParsedFormula();
		}

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

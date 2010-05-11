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
import static org.eventb.internal.core.parser.AbstractGrammar._EOF;
import static org.eventb.internal.core.parser.AbstractGrammar._LPAR;
import static org.eventb.internal.core.parser.AbstractGrammar._RPAR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.parser.GenScan.ScanState;

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
		protected final ParseResult result;
		private final Stack<Integer> startPosStack = new Stack<Integer>();
		private final Stack<Binding> bindingStack = new Stack<Binding>();
		private int startPos = -1;
		private Binding binding = new Binding();
		private boolean parsingType;
		protected Token t;    // last recognized token
		protected Token la;   // lookahead token
		
		protected ParserContext(Scanner scanner, FormulaFactory factory, ParseResult result) {
			this.scanner = scanner;
			this.factory = factory;
			this.grammar = factory.getGrammar();
			this.result = result;
		}
		
		// FIXME end position is wrong in some parsers
		// where they have progressed after last token 
		public SourceLocation getSourceLocation() {
			if (startPos < 0) {
				throw new IllegalStateException("no start position set");
			}
			return new SourceLocation(startPos, t.getEnd(), result.getOrigin());
		}
		
		public void init() {
			la = scanner.Scan();
			progress();
		}
		
		public void progress() {
			t = la;
			la = scanner.Scan();
		}
		
		public SavedContext save() {
			return new SavedContext(scanner.save(), t, la, parsingType);
		}
		
		public void restore(SavedContext sc) {
			scanner.restore(sc.scanState);
			t = sc.t;
			la = sc.la;
			parsingType = sc.parsingType;
		}
		
		static class SavedContext {
			final ScanState scanState;
			final Token t;
			final Token la;
			final boolean parsingType;
			
			SavedContext(ScanState scanState, Token t, Token la, boolean parsingType) {
				this.scanState = scanState;
				this.t = t;
				this.la = la;
				this.parsingType = parsingType;
			}
			
		}
		
		public boolean isParsingType() {
			return parsingType;
		}
		
		public void startParsingType() {
			this.parsingType = true;
		}
		
		public void stopParsingType() {
			this.parsingType = false;
		}

		/**
		 * Checks that the expected token with the given kind is ahead, then
		 * makes progress.
		 * 
		 * @param expectedKind
		 *            kind of the expected token
		 * @throws SyntaxError
		 *             in case an unexpected token is ahead
		 */
		public void progress(int expectedKind) throws SyntaxError {
			if (t.kind != expectedKind) {
				throw new SyntaxError("expected symbol \""
						+ grammar.getTokens().getValue(expectedKind)
						+ "\" but was \"" + t.val + "\"");
			}
			progress();
		}
		
		public void progressOpenParen() throws SyntaxError {
			progress(_LPAR);
		}
		
		public void progressCloseParen() throws SyntaxError {
			progress(_RPAR);
		}
		
		public List<INudParser<? extends Formula<?>>> getNudParsers() {
			return grammar.getNudParsers(t);
		}
		
		public ILedParser<? extends Formula<?>> getLedParser() {
			return grammar.getLedParser(t);
		}
		
		public int getBoundIndex(String name) {
			return binding.getBoundIndex(name);
		}
		
		public boolean canProgressRight(int parentTag) throws SyntaxError {
			if (t.kind == _EOF) { // end of the formula
				return false;
			}
			if (!grammar.isOperator(t)) {
				return false;
			}
			final OperatorRegistry opRegistry = grammar.getOperatorRegistry();
			return opRegistry.hasLessPriority(parentTag, grammar.getOperatorTag(t));
		}
		
		private void pushPos() {
			startPosStack.push(startPos);
			startPos = t.pos;
		}
		
		private void popPos() {
			startPos = startPosStack.pop();
		}
		
		public <T> T subParse(INudParser<T> parser) throws SyntaxError {
			pushPos();
			final T res = parser.nud(this);
			popPos();
			return res;
		}
		
		public <T> T subParse(int parentTag, IMainParser<T> parser) throws SyntaxError {
			pushPos();
			final T res = parser.parse(parentTag, this);
			popPos();
			return res;
		}
		
		public <T> T subParse(int parentTag, IMainParser<T> parser, List<BoundIdentDecl> newBoundIdents) throws SyntaxError {
			pushPos();
			bindingStack.push(binding);
			binding = new Binding(binding, newBoundIdents);
			final T res = parser.parse(parentTag, this);
			binding = bindingStack.pop();
			popPos();
			return res;
		}
	}
	
	static interface IMainParser<T> extends INudParser<T> {

		/**
		 * Parses the given parser context. Starts from the current token. When
		 * the method returns, the current token is the one that immediately
		 * follows the last one that belongs to the parsed formula.
		 * 
		 * @param parentTag
		 *            tag of the parent formula, {@link Formula#NO_TAG} if none
		 * @param pc
		 *            current parser context
		 * @return a formula
		 * @throws SyntaxError
		 *             if a syntax error occurs while parsing
		 */
		T parse(int parentTag, ParserContext pc) throws SyntaxError;
	}
	
    private static class Binding {
    	private Map<String, Integer> binders;
    	private int maxCount = -1;
 
    	// Creates an empty binding.
        Binding() {
        	binders = new HashMap<String, Integer>();
        }
    	
        // Creates a new binding based on <code>base</code> and extended
        // with <code>ident</code>
		Binding(Binding base, BoundIdentDecl ident) {
        	binders = new HashMap<String, Integer>(base.binders);
			maxCount = base.maxCount;
    		binders.put(ident.getName(), ++ maxCount);
    	}

        // Creates a new binding based on <code>base</code> and extended
        // with <code>idents</code>
		Binding(Binding base, List<BoundIdentDecl> idents) {
        	binders = new HashMap<String, Integer>(base.binders);
			int index = base.maxCount;
    		for (BoundIdentDecl ident: idents) {
    			binders.put(ident.getName(), ++ index);
    		}
    		maxCount = index;
    	}

		// Returns the index to use for the identifier <code>name</code>
		// or -1 if the name is free under this binding.
		int getBoundIndex(String name) {
			Integer index = binders.get(name);
			if (index == null) {
				return -1;
			}
			else {
				return maxCount - index;
			}
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
			final ParserContext pc = new ParserContext(scanner, factory, result);
			pc.init();
			final Object res;
			if (clazz == Type.class) {
				res = pc.subParse(NO_TAG, Parsers.TYPE_PARSER);
			} else if (clazz == Assignment.class) {
				// FIXME particular case required because assignment lhs
				// is not a terminal (not a formula, but a list of identifiers)
				// other possibility: introduce a notion of non terminal
				// returned by sub-parsers, then implement assignment parsing
				// with led sub-parsers
				res = pc.subParse(Parsers.ASSIGNMENT_PARSER);
			} else {
				res = pc.subParse(NO_TAG, Parsers.FORMULA_PARSER);
			}
			if (pc.t.kind != _EOF) {
				throw new UnmatchedToken("tokens have been ignored from: \""
						+ pc.t.val + "\" at position " + pc.t.pos);
			}
			if (clazz.isInstance(res)) {
				if (clazz == Predicate.class) {
					result.setParsedPredicate((Predicate) res); 
				} else if (clazz == Expression.class) {
					result.setParsedExpression((Expression) res); 
				} else if (clazz == Assignment.class) {
					result.setParsedAssignment((Assignment) res); 
				} else if (clazz == Type.class) {
					result.setParsedType((Type) res);
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

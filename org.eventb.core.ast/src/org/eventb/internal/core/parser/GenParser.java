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


import static org.eventb.internal.core.parser.AbstractGrammar.*;

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

	
	protected static class SyntaxCompatibleError extends SyntaxError {

		private static final long serialVersionUID = -6230478311681172354L;

		public SyntaxCompatibleError(String reason) {
			super(reason);
		}
	}

	public static class OverrideException extends Exception {
	
		private static final long serialVersionUID = -1281802568424261959L;
	
		public OverrideException(String reason) {
			super(reason);
		}
	}

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

	private static class StackedValue<T> {
		T val;
		private final Stack<T> stack = new Stack<T>();
		
		public StackedValue(T initVal) {
			this.val = initVal;
		}
		
		public StackedValue(StackedValue<T> toCopy) {
			this.val = toCopy.val;
			this.stack.addAll(toCopy.stack);
		}
		
		public void push(T newVal) {
			stack.push(val);
			val = newVal;
		}

		public void pop() {
			val = stack.pop();
		}
		
		@Override
		public String toString() {
			return val.toString() + " " + stack.toString();
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
		private static final Token INIT_TOKEN = new Token(IndexedSet.NOT_AN_INDEX, "", -1);

		private final Scanner scanner;
		protected final FormulaFactory factory;
		private final AbstractGrammar grammar;
		protected final ParseResult result;
		protected final boolean withPredVar;
		protected final LanguageVersion version;
		private StackedValue<Binding> binding = new StackedValue<Binding>(new Binding());
		private StackedValue<Integer> parentKind = new StackedValue<Integer>(_EOF); 
		private StackedValue<Integer> startPos = new StackedValue<Integer>(-1); 
		private int endPos = -1;
		private boolean parsingType;
		protected Token t;    // last recognized token
		protected Token la;   // lookahead token
		
		protected ParserContext(Scanner scanner, FormulaFactory factory, ParseResult result, boolean withPredVar, LanguageVersion version) {
			this.scanner = scanner;
			this.factory = factory;
			this.grammar = factory.getGrammar();
			this.result = result;
			this.withPredVar = withPredVar;
			this.version = version;
		}

		/**
		 * Makes a source location starting from the position where the latest
		 * call to a subparse() method occurred, ending at the end position of the
		 * previous token (before current token t).
		 * 
		 * @return a source location
		 */
		public SourceLocation getSourceLocation() {
			if (startPos.val < 0) {
				throw new IllegalStateException("no start position set");
			}
			return new SourceLocation(startPos.val, endPos, result.getOrigin());
		}
		
		public void init() {
			t = INIT_TOKEN;
			la = scanner.Scan();
			progress();
		}
		
		public void progress() {
			if(grammar.isOpen(t.kind)) {
				pushParentKind(_OPEN);
			}
			if (grammar.isClose(la.kind)) {
				popParentKind();
			}
			endPos = t.getEnd();
			t = la;
			la = scanner.Scan();
		}
		
		private void pushParentKind(int newParentKind) {
			parentKind.push(newParentKind);
		}
		
		public void pushParentKind() {
			parentKind.push(t.kind);
		}

		public void popParentKind() {
			parentKind.pop();
		}
		
		public SavedContext save() {
			return new SavedContext(scanner.save(), t, la, parsingType,
					startPos, binding, parentKind);
		}
		
		public void restore(SavedContext sc) {
			scanner.restore(sc.scanState);
			t = sc.t;
			la = sc.la;
			parsingType = sc.parsingType;
			startPos = new StackedValue<Integer>(sc.startPos);
			binding = new StackedValue<Binding>(sc.binding);
			parentKind = new StackedValue<Integer>(sc.parentKind);
		}
		
		static class SavedContext {
			final ScanState scanState;
			final Token t;
			final Token la;
			final boolean parsingType;
			final StackedValue<Integer> startPos;
			final StackedValue<Binding> binding;
			final StackedValue<Integer> parentKind;
			
			SavedContext(ScanState scanState, Token t, Token la, boolean parsingType, StackedValue<Integer> startPos, StackedValue<Binding> binding, StackedValue<Integer> parentKind) {
				this.scanState = scanState;
				this.t = t;
				this.la = la;
				this.parsingType = parsingType;
				this.startPos = new StackedValue<Integer>(startPos);
				this.binding = new StackedValue<Binding>(binding);
				this.parentKind = new StackedValue<Integer>(parentKind);
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
			return binding.val.getBoundIndex(name);
		}
		
		public boolean canProgressRight() throws SyntaxCompatibleError {
			if (t.kind == _EOF) { // end of the formula
				return false;
			}
			if (!grammar.isOperator(t)) {
				return false;
			}
			return grammar.hasLessPriority(parentKind.val, t.kind);
		}
		
		private void pushPos() {
			startPos.push(t.pos);
		}
		
		private void popPos() {
			startPos.pop();
		}
		
		public <T> T subParse(INudParser<T> parser) throws SyntaxError {
			pushPos();
			try {
				return parser.nud(this);
			} finally {
				popPos();
			}
		}
		
		public <T> T subParse(INudParser<T> parser,
				List<BoundIdentDecl> newBoundIdents) throws SyntaxError {
			// FIXME add a warning about identifiers bound twice
			binding.push(new Binding(binding.val, newBoundIdents));
			try {
				return subParse(parser);
			} finally {
				binding.pop();
			}
		}
		
		public <T> T subParseNoBinding(INudParser<T> parser) throws SyntaxError {
			binding.push(new Binding());
			try {
				return subParse(parser);
			} finally {
				binding.pop();
			}
		}
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
			final ParserContext pc = new ParserContext(scanner, factory,
					result, withPredVar, version);
			pc.init();
			final Object res;
			if (clazz == Type.class) {
				res = pc.subParse(MainParsers.TYPE_PARSER);
			} else if (clazz == Assignment.class) {
				// FIXME particular case required because assignment lhs
				// is not a terminal (not a formula, but a list of identifiers)
				// other possibility: introduce a notion of non terminal
				// returned by sub-parsers, then implement assignment parsing
				// with led sub-parsers
				res = pc.subParse(MainParsers.ASSIGNMENT_PARSER);
			} else {
				res = pc.subParse(MainParsers.FORMULA_PARSER);
			}
			if (pc.t.kind != _EOF) {
				throw new UnmatchedToken("tokens have been ignored from: \""
						+ pc.t.val + "\" at position " + pc.t.pos);
			}
			// TODO remove above debug check when stable
			if (pc.parentKind.val != _EOF) {
				throw new IllegalStateException("Improper parent stack: "
						+ pc.parentKind
						+ " with "
						+ pc.parentKind.val
						+ " = "
						+ factory.getGrammar().getTokens().getValue(
								pc.parentKind.val));
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

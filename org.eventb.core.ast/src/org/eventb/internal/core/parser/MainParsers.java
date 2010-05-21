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
import static org.eventb.internal.core.parser.BMath.*;
import static org.eventb.internal.core.parser.SubParsers.BOUND_IDENT_DECL_SUBPARSER;
import static org.eventb.internal.core.parser.SubParsers.FREE_IDENT_SUBPARSER;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.GenParser.ParserContext.SavedContext;

/**
 * Main parsers implement an algorithm for parsing a formula (or a part of a
 * formula) without being bound to any particular operator.
 * <p>
 * As main parsers are null-denoted, they all implement {@link INudParser}.
 * </p>
 * 
 * @author Nicolas Beauger
 * 
 */
public class MainParsers {
	
	private static String makeSynErrMessage(Formula<?> formula, Class<?> clazz) {
		return "expected a " + clazz.getCanonicalName() + ", but was "
				+ formula + " at position " + formula.getSourceLocation();
	}

	static Predicate asPredicate(Formula<?> formula) throws SyntaxError {
		if (!(formula instanceof Predicate)) {
			throw new SyntaxError(makeSynErrMessage(formula, Predicate.class));
		}
		return (Predicate) formula;
	}

	static Expression asExpression(Formula<?> formula) throws SyntaxError {
		if (!(formula instanceof Expression)) {
			throw new SyntaxError(makeSynErrMessage(formula, Expression.class));
		}
		return (Expression) formula;
	}

	private static abstract class ParserApplier<Parser> {
		
		public ParserApplier() {
			// avoid synthetic accessor method emulation
		}
		
		public Formula<?> apply(ParserContext pc, Formula<?> left) throws SyntaxError {
			final Parser parser = getParser(pc);
			pc.pushParentKind();
			try {
				return apply(pc, parser, left);
			} finally {
				pc.popParentKind();
			}
		}
		
		protected abstract Parser getParser(ParserContext pc) throws SyntaxError;
		protected abstract Formula<?> apply(ParserContext pc, Parser parser, Formula<?> left) throws SyntaxError;
		

		protected static void throwNoParserFoundFor(ParserContext pc) throws SyntaxError {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}

		// errors must be non empty 
		protected static SyntaxError newCompoundError(List<SyntaxError> errors) {
			final StringBuilder reason = new StringBuilder(
					"Parse failed because");
			if (errors.size()>=2) {
				reason.append(" either: ");
			} else {
				reason.append(": ");
			}
			final Iterator<SyntaxError> iter = errors.iterator();
			while(iter.hasNext()) {
			final SyntaxError syntaxError = iter.next();
				reason.append(syntaxError.getMessage());
				if (iter.hasNext()) {
					reason.append(" OR ");
				}
			}
			return new SyntaxError(reason.toString());
		}

	}
	
	static final ParserApplier<List<INudParser<? extends Formula<?>>>> NUD_APPLIER = new ParserApplier<List<INudParser<? extends Formula<?>>>>() {
		
		@Override
		protected List<INudParser<? extends Formula<?>>> getParser(ParserContext pc)
		throws SyntaxError {
			final List<INudParser<? extends Formula<?>>> subParsers = pc.getNudParsers();
			if (subParsers.isEmpty()) {
				throwNoParserFoundFor(pc);
			}
			return subParsers;
		}
		
		@Override
		protected Formula<?> apply(ParserContext pc,
				List<INudParser<? extends Formula<?>>> nudParsers,
				Formula<?> left) throws SyntaxError {
			final List<SyntaxError> errors = new ArrayList<SyntaxError>();
			final Iterator<INudParser<? extends Formula<?>>> iter = nudParsers.iterator();
			final SavedContext savedContext = pc.save();
			while(iter.hasNext()) {
				final INudParser<? extends Formula<?>> nudParser = iter.next();
				try {
					// FIXME the call to nud may add problems to pc.result
					// without throwing an exception
					return nudParser.nud(pc);
				} catch (SyntaxError e) {
					errors.add(e);
					pc.restore(savedContext);
				}
			}
			throw newCompoundError(errors);
		}
		
	};

	static final ParserApplier<ILedParser<? extends Formula<?>>> LED_APPLIER = new ParserApplier<ILedParser<? extends Formula<?>>>() {
		
		@Override
		protected ILedParser<? extends Formula<?>> getParser(ParserContext pc)
				throws SyntaxError {
			final ILedParser<? extends Formula<?>> subParser = pc.getLedParser();
			if (subParser == null) {
				throwNoParserFoundFor(pc);
			}
			return subParser;
		}
		
		@Override
		protected Formula<?> apply(ParserContext pc,
				ILedParser<? extends Formula<?>> parser, Formula<?> left)
				throws SyntaxError {
			// TODO implement backtracking for led parsers as well 
			pc.progress();
			return parser.led(left, pc);
		}
		
	};
	
	// Core algorithm implementation
	private static class FormulaParser implements INudParser<Formula<?>> {
		
		public FormulaParser() {
			// void constructor
		}
		
		public Formula<?> nud(ParserContext pc)
				throws SyntaxError {
		
			Formula<?> left = NUD_APPLIER.apply(pc, null);

			while (pc.canProgressRight()) {
				left = LED_APPLIER.apply(pc, left);
			}
			
			return left;
		}
	}

	static final FormulaParser FORMULA_PARSER = new FormulaParser();

	private static class TypeParser implements INudParser<Type> {
		public TypeParser() {
			// void constructor
		}
		
		public Type nud(ParserContext pc) throws SyntaxError {
			pc.startParsingType();
			final Expression expression = pc.subParse(EXPR_PARSER);
			if (!expression.isATypeExpression()) {
				throw new SyntaxError(
						"expected a type expression at position "
								+ pc.getSourceLocation());
			}
			try {
				return expression.toType(pc.factory);
			} catch (InvalidExpressionException e) {
				// TODO should not happen (already checked)
				e.printStackTrace();
				return null;
			} finally {
				pc.stopParsingType();
			}
		}
	}

	static final TypeParser TYPE_PARSER = new TypeParser();

	private static class PredicateParser implements INudParser<Predicate> {
		public PredicateParser() {
			// void constructor
		}
		
		public Predicate nud(ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.nud(pc);
			return asPredicate(formula);
		}
	}

	static final PredicateParser PRED_PARSER = new PredicateParser();

	private static class ExpressionParser implements INudParser<Expression> {
		
		public ExpressionParser() {
			// void constructor
		}
		
		public Expression nud(ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.nud(pc);
			return asExpression(formula);
		}
	}

	static final ExpressionParser EXPR_PARSER = new ExpressionParser();

	// TODO verify that all formulae inside parentheses are parsed with a GROUP0
	// parent kind
	static final INudParser<Formula<?>> CLOSED_SUGAR = new INudParser<Formula<?>> () {

		public Formula<?> nud(ParserContext pc) throws SyntaxError {
			pc.progressOpenParen();
			final Formula<?> formula = pc.subParse(FORMULA_PARSER);
			pc.progressCloseParen();
			return formula;
		}
	};

	static class PatternParser implements INudParser<Pattern> {
		
		final Pattern pattern;
		
		public PatternParser(ParseResult result) {
			this.pattern = new Pattern(result);
		}

		public Pattern nud(ParserContext pc) throws SyntaxError {
			final PatternAtomParser atomParser = new PatternAtomParser(pattern, this);
			pc.subParse(atomParser);
			while (pc.t.kind == _MAPSTO) {
				pc.progress();
				pc.subParse(atomParser);
				pattern.mapletParsed(pc.getSourceLocation());
			}
			return pattern;
		}

		private static class PatternAtomParser implements INudParser<Object> {

			private final Pattern pattern;
			private final PatternParser parser;
			
			public PatternAtomParser(Pattern pattern, PatternParser parser) {
				this.pattern = pattern;
				this.parser = parser;
			}

			public Object nud(ParserContext pc) throws SyntaxError {
				if (pc.t.kind == _LPAR) {
					pc.progressOpenParen();
					pc.subParse(parser);
					pc.progressCloseParen();
				} else {
					final BoundIdentDecl boundIdent = pc
							.subParse(BOUND_IDENT_DECL_SUBPARSER);
					pattern.declParsed(boundIdent);
				}
				return null;
			}
		}
	}

	// parses a non empty list of T
	static class AbstListParser<T> implements INudParser<List<T>> {
	
		private final INudParser<T> parser;
		
		public AbstListParser(INudParser<T> parser) {
			this.parser = parser;
		}

		public List<T> nud(ParserContext pc) throws SyntaxError {
			final List<T> list = new ArrayList<T>();
			T next = pc.subParse(parser);
			list.add(next);
			while (pc.t.kind == _COMMA) {
				pc.progress();
				next = pc.subParse(parser);
				list.add(next);
			}
			return list;
		}
		
	}

	static final AbstListParser<Expression> EXPR_LIST_PARSER = new AbstListParser<Expression>(EXPR_PARSER);
	
	static final AbstListParser<FreeIdentifier> FREE_IDENT_LIST_PARSER = new AbstListParser<FreeIdentifier>(FREE_IDENT_SUBPARSER);
	
	static final AbstListParser<BoundIdentDecl> BOUND_IDENT_DECL_LIST_PARSER = new AbstListParser<BoundIdentDecl>(BOUND_IDENT_DECL_SUBPARSER);
	
	static List<BoundIdentDecl> makePrimedDecl(List<FreeIdentifier> lhsList, FormulaFactory factory) {
		final List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>(lhsList.size());
	    for (FreeIdentifier ident: lhsList) {
			decls.add(ident.asPrimedDecl(factory));
		}
		return decls;
	}

	// used as a main parser; directly called by the general parser.
	/** @see GenParser#parse() */
	static final INudParser<Assignment> ASSIGNMENT_PARSER = new INudParser<Assignment>() {

		public Assignment nud(ParserContext pc) throws SyntaxError {
			final List<FreeIdentifier> idents = pc.subParse(FREE_IDENT_LIST_PARSER);
			final Token tokenAfterIdents = pc.t;
			final int tokenKind = tokenAfterIdents.kind;
			pc.progress();

			if (tokenKind == _LPAR) { // FUNIMAGE assignment
				if (idents.size() != 1) {
					throw new SyntaxError("Assignment to function images applies to exactly one function.");
				}
				final FreeIdentifier ident = idents.get(0);
				final Expression index = pc.subParse(EXPR_PARSER);
				pc.progressCloseParen();
				pc.progress(_BECEQ);
				final Expression value = pc.subParse(EXPR_PARSER);
				final Expression overriding = makeFunctionOverriding(ident, index, value, pc.factory);
				return pc.factory.makeBecomesEqualTo(ident, overriding, pc.getSourceLocation());
			} else if (tokenKind == _BECEQ) {
				final List<Expression> values = pc.subParse(EXPR_LIST_PARSER);
				if (idents.size() != values.size()) {
					throw new SyntaxError("incompatible size of left and right parts of assignment");
				}
				return pc.factory.makeBecomesEqualTo(idents, values, pc.getSourceLocation());
			} else if (tokenKind == _BECMO) {
				if (idents.size() != 1) {
					throw new SyntaxError("\'Becomes Member Of\' applies to only one identifier");
				}
				final Expression expr = pc.subParse(EXPR_PARSER);
				return pc.factory.makeBecomesMemberOf(idents.get(0), expr, pc.getSourceLocation());
			} else if (tokenKind == _BECST) {
				final List<BoundIdentDecl> primed = makePrimedDecl(idents, pc.factory);
				final Predicate condition = pc.subParse(PRED_PARSER, primed);
				return pc.factory.makeBecomesSuchThat(idents, primed, condition, pc.getSourceLocation());
			} else {
				throw new SyntaxError("Unknown assignment operator: "
						+ tokenAfterIdents.val);
			}
		}
		
	};
	
	static Expression makeFunctionOverriding(FreeIdentifier ident,
			Expression index, Expression value, FormulaFactory factory) {
		
		Expression pair = factory.makeBinaryExpression(Formula.MAPSTO, index, value, null);
		Expression singletonSet = factory.makeSetExtension(pair, null);
		return factory.makeAssociativeExpression(Formula.OVR, 
				new Expression[] {ident, singletonSet}, null);
	}
	
}

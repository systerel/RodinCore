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

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.*;
import static org.eventb.internal.core.parser.AbstractGrammar.*;
import static org.eventb.internal.core.parser.BMath.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.parser.GenParser.IMainParser;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.GenParser.ParserContext.SavedContext;

/**
 * @author Nicolas Beauger
 *
 */
public class Parsers {

	private static abstract class AbstractSubParser<T> implements ISubParser<T> {

		protected final int tag;

		protected AbstractSubParser(int tag) {
			this.tag = tag;
		}
		
		public int getTag() {
			return tag;
		}
		
	}

	private static abstract class DefaultNudParser<T> extends AbstractSubParser<T> implements INudParser<T> {

		protected DefaultNudParser(int tag) {
			super(tag);
		}
	}

	private static abstract class DefaultLedExprParser<T> extends AbstractSubParser<T> implements ILedParser<T> {

		protected DefaultLedExprParser(int tag) {
			super(tag);
		}
		
		public final T led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final Expression leftExpr = asExpression(left);
			final Expression right = parseRight(pc);
			return led(leftExpr, right, pc);
		}
		
		protected abstract T led(Expression left, Expression right,
				ParserContext pc) throws SyntaxError;
		
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			return pc.subParse(tag, EXPR_PARSER);
		}
	}
	
	private static abstract class DefaultLedPredParser<T> extends AbstractSubParser<T> implements ILedParser<T> {

		protected DefaultLedPredParser(int tag) {
			super(tag);
		}
		
		public final T led(Formula<?> left, ParserContext pc) throws SyntaxError {
			final Predicate leftPred = asPredicate(left);
			final Predicate right = parseRight(pc);
			return led(leftPred, right, pc);
		}
		
		protected abstract T led(Predicate left, Predicate right, ParserContext pc) throws SyntaxError;
		
		protected Predicate parseRight(ParserContext pc) throws SyntaxError {
			return pc.subParse(tag, PRED_PARSER);
		}
	}
	
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

	private static abstract class DefaultMainParser<T> extends DefaultNudParser<T> implements IMainParser<T> {

		protected DefaultMainParser() {
			super(NO_TAG);
		}

		public final T nud(ParserContext pc) throws SyntaxError {
			return parse(NO_TAG, pc);
		}
	}
	
	private static class FormulaParser extends DefaultMainParser<Formula<?>> {
		public FormulaParser() {
			// void constructor
		}
		
		public Formula<?> parse(int parentTag, ParserContext pc)
				throws SyntaxError {
		
			final List<INudParser<? extends Formula<?>>> nudParsers = getNudParsers(pc);
			Formula<?> left = nudParse(pc, nudParsers);
			while (pc.canProgressRight(parentTag)) {
				final ILedParser<? extends Formula<?>> ledParser = getLedParser(pc);
				pc.progress();
				left = ledParser.led(left, pc);
			}
			return left;
		}
		
		// TODO implement backtracking for led parsers as well 
	
		private static Formula<?> nudParse(ParserContext pc,
				List<INudParser<? extends Formula<?>>> nudParsers) throws SyntaxError {
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
	
		// errors must be non empty 
		private static SyntaxError newCompoundError(List<SyntaxError> errors) {
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

	static final FormulaParser FORMULA_PARSER = new FormulaParser();

	private static class TypeParser extends DefaultMainParser<Type> {
		public TypeParser() {
			// void constructor
		}
		
		public Type parse(int parentTag, ParserContext pc) throws SyntaxError {
			pc.startParsingType();
			final Expression expression = pc.subParse(NO_TAG, EXPR_PARSER);
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

	private static class PredicateParser extends DefaultMainParser<Predicate> {
		public PredicateParser() {
			// void constructor
		}
		
		public Predicate parse(int parentTag, ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.parse(parentTag, pc);
			return asPredicate(formula);
		}
	}

	static final PredicateParser PRED_PARSER = new PredicateParser();

	private static class ExpressionParser extends DefaultMainParser<Expression> {
		
		public ExpressionParser() {
			// void constructor
		}
		
		public Expression parse(int parentTag, ParserContext pc) throws SyntaxError {
			final Formula<?> formula = FORMULA_PARSER.parse(parentTag, pc);
			return asExpression(formula);
		}
	}

	static final ExpressionParser EXPR_PARSER = new ExpressionParser();

	// returns a non empty list
	static List<INudParser<? extends Formula<?>>> getNudParsers(ParserContext pc)
			throws SyntaxError {
		final List<INudParser<? extends Formula<?>>> subParsers = pc.getNudParsers();
		if (subParsers.isEmpty()) {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}
		return subParsers;
	}

	static ILedParser<? extends Formula<?>> getLedParser(ParserContext pc)
	throws SyntaxError {
		final ILedParser<? extends Formula<?>> subParser = pc.getLedParser();
		if (subParser == null) {
			throw new SyntaxError("don't know how to parse: " + pc.t.val);
		}
		return subParser;
	}

	static class IdentSubParser extends DefaultNudParser<Identifier> {

		protected IdentSubParser(int tag) {
			super(tag);
		}

		public Identifier nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = makeIdent(pc);
			pc.progress();
			return ident;
		}
		
		// Creates an identifier for the given token.
		// Takes care of the bindings.
		private Identifier makeIdent(ParserContext pc) throws SyntaxError {
			final String name = pc.t.val;
			final int index = pc.getBoundIndex(name);
			final SourceLocation loc = pc.getSourceLocation();
			if (index == -1) {
				final Type type;
				if (pc.isParsingType()) {
					type = pc.factory.makePowerSetType(pc.factory.makeGivenType(name));
				} else {
					type = null;
				}
				return pc.factory.makeFreeIdentifier(name, loc, type);
			} else {
				return pc.factory.makeBoundIdentifier(index, loc);
			}
		}

		protected FreeIdentifier parseFreeIdent(ParserContext pc, String name,
				SourceLocation loc) throws SyntaxError {
			final Type type;
			if (pc.isParsingType()) {
				type = pc.factory.makePowerSetType(pc.factory
						.makeGivenType(name));
			} else {
				type = null;
			}
			return pc.factory.makeFreeIdentifier(name, loc, type);
		}

		protected BoundIdentifier parseBoundIdent(ParserContext pc,
				String name, int index, SourceLocation loc) throws SyntaxError {
			return pc.factory.makeBoundIdentifier(index, loc);
		}

	}
	
	static final IdentSubParser IDENT_SUBPARSER = new IdentSubParser(NO_TAG);
	
	static final INudParser<FreeIdentifier> FREE_IDENT_SUBPARSER = new DefaultNudParser<FreeIdentifier>(
			FREE_IDENT) {

		public FreeIdentifier nud(ParserContext pc) throws SyntaxError {
			final Identifier ident = IDENT_SUBPARSER.nud(pc);
			if (!(ident instanceof FreeIdentifier)) {
				throw new SyntaxError("expected a free identifier at position "
						+ pc.getSourceLocation());
			}
			return (FreeIdentifier) ident;
		}
	};

	static final INudParser<IntegerLiteral> INTLIT_SUBPARSER = new DefaultNudParser<IntegerLiteral>(INTLIT) {
	
		public IntegerLiteral nud(ParserContext pc) throws SyntaxError {
			final BigInteger value = BigInteger.valueOf((Integer // FIXME NumberFormatException
					.valueOf(pc.t.val)));
			pc.progress();
			return pc.factory.makeIntegerLiteral(value, pc.getSourceLocation());
		}
	};

	// TODO verify that all parser calls are made with NO_TAG when parsing
	// formulae inside parentheses
	static final INudParser<Formula<?>> CLOSED_SUGAR = new DefaultNudParser<Formula<?>> (NO_TAG) {

		public Formula<?> nud(ParserContext pc) throws SyntaxError {
			pc.progressOpenParen();
			final Formula<?> formula = pc.subParse(NO_TAG, FORMULA_PARSER);
			pc.progressCloseParen();
			return formula;
		}
	};

	// FIXME current design does not allow subparsers with no tag
	static final DefaultLedExprParser<Expression> OFTYPE = new DefaultLedExprParser<Expression>(OFTYPE_TAG) {
		
		@Override
		public Expression led(Expression left, Expression right, ParserContext pc)
				throws SyntaxError {
			final Type type = pc.subParse(NO_TAG, TYPE_PARSER);
			switch (left.getTag()) {
			case Formula.EMPTYSET:
				return pc.factory.makeEmptySet(type, pc.getSourceLocation());
			default:
				throw new SyntaxError("Unexpected oftype");
			}
		}
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			// do not parse right
			return null;
		}
	};
	
	static class AtomicExpressionParser extends DefaultNudParser<AtomicExpression> {
	
		protected AtomicExpressionParser(int tag) {
			super(tag);
		}
	
		public AtomicExpression nud(ParserContext pc)
				throws SyntaxError {
			final AtomicExpression atomExpr = pc.factory.makeAtomicExpression(
					tag, pc.getSourceLocation());
			pc.progress();
			return atomExpr;
		}

	}

	static class BinaryExpressionInfix extends DefaultLedExprParser<Expression> {

		public BinaryExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		public Expression led(Expression left, Expression right, ParserContext pc)
				throws SyntaxError {
			final SourceLocation srcLoc = pc.getSourceLocation();
			return makeResult(pc.factory, left,
					right, srcLoc);
		}
		
		protected Expression makeResult(FormulaFactory factory,
				Expression left, Expression right, SourceLocation srcLoc) {
			return factory.makeBinaryExpression(tag, left, right, srcLoc);
		}

	}
	
	static class ExtendedBinaryExpressionInfix extends BinaryExpressionInfix {

		public ExtendedBinaryExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		protected Expression makeResult(FormulaFactory factory,
				Expression left, Expression right, SourceLocation srcLoc) {
			final IExpressionExtension extension = (IExpressionExtension) factory
					.getExtension(tag);

			return factory.makeExtendedExpression(extension,
					asList(left, right), Collections.<Predicate> emptySet(),
					srcLoc);
		}

	}

	static class AssociativeExpressionInfix extends DefaultLedExprParser<Expression> {

		public AssociativeExpressionInfix(int tag) {
			super(tag);
		}

		@Override
		public Expression led(Expression left, Expression right, ParserContext pc)
				throws SyntaxError {
			final List<Expression> children = new ArrayList<Expression>();
			if (left.getTag() == tag) {
				children.addAll(asList(getChildren(left)));
			} else {
				children.add(left);
			}
			children.add(right);
			final SourceLocation srcLoc = pc.getSourceLocation();
			return makeResult(pc.factory, children, srcLoc);
		}
		
		protected Expression[] getChildren(Formula<?> exprWithSameTag) {
			return ((AssociativeExpression) exprWithSameTag).getChildren();
		}
		
		protected Expression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation srcLoc) {
			return factory.makeAssociativeExpression(tag, children, srcLoc);
		}
		
	}

	static class ExtendedAssociativeExpressionInfix extends AssociativeExpressionInfix {

		public ExtendedAssociativeExpressionInfix(int tag) {
			super(tag);
		}
		
		@Override
		protected Expression[] getChildren(Formula<?> exprWithSameTag) {
			return ((ExtendedExpression) exprWithSameTag).getChildExpressions();
		}
		
		@Override
		protected ExtendedExpression makeResult(FormulaFactory factory,
				List<Expression> children, SourceLocation srcLoc) {
			final IExpressionExtension extension = (IExpressionExtension) factory
					.getExtension(tag);
			return factory.makeExtendedExpression(extension, children, Collections
					.<Predicate> emptyList(), srcLoc);
		}
	}
	
	static class AssociativePredicateInfix extends DefaultLedPredParser<AssociativePredicate> {

		public AssociativePredicateInfix(int tag) {
			super(tag);
		}

		@Override
		public AssociativePredicate led(Predicate left, Predicate right, ParserContext pc)
				throws SyntaxError {
			final List<Predicate> children = new ArrayList<Predicate>();
			if (left.getTag() == tag) {
				children.addAll(asList(((AssociativePredicate) left)
						.getChildren()));
			} else {
				children.add(left);
			}
			children.add(right);
			return pc.factory.makeAssociativePredicate(tag, children, pc
					.getSourceLocation());
		}
	}

	static class RelationalPredicateInfix extends DefaultLedExprParser<RelationalPredicate> {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		@Override
		public RelationalPredicate led(Expression left, Expression right, ParserContext pc) throws SyntaxError {
			return pc.factory.makeRelationalPredicate(tag, left,
					right, pc.getSourceLocation());
		}
	}

	static final ILedParser<BinaryExpression> FUN_IMAGE = new DefaultLedExprParser<BinaryExpression>(FUNIMAGE) {

		@Override
		public BinaryExpression led(Expression left, Expression right, ParserContext pc) throws SyntaxError {
			pc.progressCloseParen();
			return pc.factory.makeBinaryExpression(tag, left,
					right, pc.getSourceLocation());
		}
		
		@Override
		protected Expression parseRight(ParserContext pc) throws SyntaxError {
			return pc.subParse(NO_TAG, EXPR_PARSER);
		}
		
	};

	static class LiteralPredicateParser extends DefaultNudParser<LiteralPredicate> {

		public LiteralPredicateParser(int tag) {
			super(tag);
		}

		public LiteralPredicate nud(ParserContext pc) throws SyntaxError {
			final LiteralPredicate litPred = pc.factory.makeLiteralPredicate(
					tag, pc.getSourceLocation());
			pc.progress();
			return litPred;
		}
	}

	static class BinaryPredicateParser extends DefaultLedPredParser<BinaryPredicate> {

		public BinaryPredicateParser(int tag) {
			super(tag);
		}

		@Override
		public BinaryPredicate led(Predicate left, Predicate right, ParserContext pc)
				throws SyntaxError {
			return pc.factory.makeBinaryPredicate(tag, left,
					right, pc.getSourceLocation());
		}
	}

	static class QuantifiedPredicateParser extends DefaultNudParser<QuantifiedPredicate> {

		public QuantifiedPredicateParser(int tag) {
			super(tag);
		}

		public QuantifiedPredicate nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final List<FreeIdentifier> identList = pc.subParse(FREE_IDENT_LIST_PARSER);
			pc.progress(_DOT);
			final List<BoundIdentDecl> boundIdentifiers = makeBoundIdentDeclList(
					pc.factory, identList);
			final Predicate pred = pc.subParse(tag, PRED_PARSER, boundIdentifiers);

			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					pred, null);
		}
	}

	static <T extends Identifier>List<BoundIdentDecl> makeBoundIdentDeclList(FormulaFactory factory,
			final List<FreeIdentifier> identList) {
		final List<BoundIdentDecl> boundIdentifiers = new ArrayList<BoundIdentDecl>(identList.size());
		// TODO use Formula.bindTheseIdents instead ?
		for (FreeIdentifier ident: identList) {
			boundIdentifiers.add(ident.asDecl(factory));
		}
		return boundIdentifiers;
	}

	static class UnaryExpressionParser extends DefaultNudParser<UnaryExpression> {

		public UnaryExpressionParser(int tag) {
			super(tag);
		}

		public UnaryExpression nud(ParserContext pc)
				throws SyntaxError {
			pc.progress();
			pc.progressOpenParen();
			final Expression child = pc.subParse(EXPR_PARSER);
			pc.progressCloseParen();
			return pc.factory.makeUnaryExpression(tag, child, pc.getSourceLocation());
		}

	}

	static class PatternParser extends DefaultMainParser<Pattern> {
		
		final Pattern pattern;
		
		public PatternParser(ParseResult result) {
			this.pattern = new Pattern(result);
		}

		public Pattern parse(int parentTag, ParserContext pc) throws SyntaxError {
			final PatternAtomParser atomParser = new PatternAtomParser(pattern, this);
			pc.subParse(atomParser);
			while (pc.t.kind == _MAPSTO) {
				pc.progress();
				pc.subParse(atomParser);
				pattern.mapletParsed(pc.getSourceLocation());
			}
			return pattern;
		}

		private static class PatternAtomParser extends DefaultMainParser<Object> {

			private final Pattern pattern;
			private final PatternParser parser;
			
			public PatternAtomParser(Pattern pattern, PatternParser parser) {
				this.pattern = pattern;
				this.parser = parser;
			}

			public Object parse(int parentTag, ParserContext pc) throws SyntaxError {
				if (pc.t.kind == _LPAR) {
					pc.progressOpenParen();
					pc.subParse(parser);
					pc.progressCloseParen();
				} else {
					final FreeIdentifier freeIdentifier = pc.subParse(
							FREE_IDENT_SUBPARSER);
					pattern.declParsed(freeIdentifier.asDecl(pc.factory));
				}
				return null;
			}
		}
	}

	static class AbstListParser<T extends Expression> extends DefaultNudParser<List<T>> {
	
		private final INudParser<T> parser;
		
		public AbstListParser(INudParser<T> parser) {
			super(NO_TAG);
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
	
	static final AbstListParser<Identifier> IDENT_LIST_PARSER = new AbstListParser<Identifier>(IDENT_SUBPARSER);

	static final AbstListParser<FreeIdentifier> FREE_IDENT_LIST_PARSER = new AbstListParser<FreeIdentifier>(FREE_IDENT_SUBPARSER);
	
	static final INudParser<SetExtension> SETEXT_PARSER = new DefaultNudParser<SetExtension>(SETEXT) {
		
		public SetExtension nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final List<Expression> exprs = pc.subParse(EXPR_LIST_PARSER);
			pc.progress(_RBRACE);
			
			return pc.factory.makeSetExtension(exprs, pc.getSourceLocation());
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_EXPLICIT = new DefaultNudParser<QuantifiedExpression>(CSET) {
		
		public QuantifiedExpression nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final List<FreeIdentifier> idents = pc.subParse(FREE_IDENT_LIST_PARSER);
			pc.progress(_DOT);
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, idents);
			final Predicate pred = pc.subParse(tag, PRED_PARSER, boundIdents);
			pc.progress(_MID);
			final Expression expr = pc.subParse(tag, EXPR_PARSER, boundIdents);
			pc.progress(_RBRACE);
			
			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					expr, pc.getSourceLocation(), Form.Explicit);
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_IMPLICIT = new DefaultNudParser<QuantifiedExpression>(CSET) {
		
		public QuantifiedExpression nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final Expression expr = pc.subParse(EXPR_PARSER);
			pc.progress(_MID);
			final List<FreeIdentifier> idents = asList(expr.getFreeIdentifiers());
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, idents);
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);
			
			final Predicate pred = pc.subParse(tag, PRED_PARSER, boundIdents);
			pc.progress(_RBRACE);
			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					boundExpr, pc.getSourceLocation(), Form.Implicit);
		}
	};
	
	static final INudParser<QuantifiedExpression> CSET_LAMBDA = new DefaultNudParser<QuantifiedExpression>(CSET) {
		
		public QuantifiedExpression nud(ParserContext pc) throws SyntaxError {
			pc.progress();
			final PatternParser pattParser = new PatternParser(pc.result);
			final Pattern pattern = pc.subParse(pattParser);
			pc.progress(_DOT);
			final List<BoundIdentDecl> boundDecls = pattern.getDecls();
			final Predicate pred = pc.subParse(tag, PRED_PARSER, boundDecls);
			pc.progress(_MID);
			final Expression expr = pc.subParse(tag, EXPR_PARSER, boundDecls);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), expr, null);
			return pc.factory.makeQuantifiedExpression(tag, boundDecls, pred,
					pair, pc.getSourceLocation(), Form.Lambda);
		}
	};

	static final IMainParser<Assignment> ASSIGNMENT_PARSER = new DefaultMainParser<Assignment>() {

		public Assignment parse(int parentTag,
				ParserContext pc) throws SyntaxError {
			final List<FreeIdentifier> idents = pc.subParse(FREE_IDENT_LIST_PARSER);
			pc.progress(_BECEQ);
			final List<Expression> values = pc.subParse(EXPR_LIST_PARSER);
			return pc.factory.makeBecomesEqualTo(idents, values, pc.getSourceLocation());
		}
		
	};
	
}

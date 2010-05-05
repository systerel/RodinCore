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
import static org.eventb.internal.core.parser.BMath.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.GenParser.ParserContext.SavedContext;

/**
 * @author Nicolas Beauger
 *
 */
public class Parsers {

	private static abstract class AbstractSubParser implements ISubParser {

		protected final int tag;

		protected AbstractSubParser(int tag) {
			this.tag = tag;
		}
		
		public int getTag() {
			return tag;
		}
		
	}

	private static abstract class DefaultNudParser extends AbstractSubParser implements INudParser {

		protected DefaultNudParser(int tag) {
			super(tag);
		}

	}

	private static abstract class DefaultLedParser extends AbstractSubParser implements ILedParser {

		protected DefaultLedParser(int tag) {
			super(tag);
		}

	}
	
	// TODO verify that all parser calls are made with NO_TAG when parsing
	// formulae inside parentheses
	static final INudParser CLOSED_SUGAR = new DefaultNudParser(NO_TAG) {

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progressOpenParen();
			final Formula<?> formula = MainParser.parse(NO_TAG, pc);
			pc.progressCloseParen();
			return formula;
		}
	};

	// FIXME current design does not allow subparsers with no tag
	static final ILedParser OFTYPE = new DefaultLedParser(OFTYPE_TAG) {
		
		public Formula<?> led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Type type = MainParser.parseType(pc);
			switch (left.getTag()) {
			case Formula.EMPTYSET:
				return pc.factory.makeEmptySet(type, pc.getSourceLocation(left
						.getSourceLocation().getStart()));
			default:
				throw new SyntaxError("Unexpected oftype");
			}
		}
	};
	
	static class AtomicExpressionParser extends DefaultNudParser {
	
		protected AtomicExpressionParser(int tag) {
			super(tag);
		}
	
		public Formula<?> nud(ParserContext pc, int startPos)
				throws SyntaxError {
			final AtomicExpression atomExpr = pc.factory.makeAtomicExpression(
					tag, pc.getSourceLocation(startPos));
			pc.progress();
			return atomExpr;
		}

	}

	static class BinaryExpressionInfix extends DefaultLedParser {

		public BinaryExpressionInfix(int tag) {
			super(tag);
		}
		
		public Expression led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			final SourceLocation srcLoc = pc.getSourceLocation(startPos);
			return makeResult(pc.factory, (Expression) left,
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
	
	static class AssociativeExpressionInfix extends DefaultLedParser {

		public AssociativeExpressionInfix(int tag) {
			super(tag);
		}

		public Expression led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			final List<Expression> children = new ArrayList<Expression>();
			if (left.getTag() == tag) {
				children.addAll(asList(getChildren(left)));
			} else {
				children.add((Expression) left);
			}
			children.add(right);
			final SourceLocation srcLoc = pc.getSourceLocation(startPos);
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
	
	static class AssociativePredicateInfix extends DefaultLedParser {

		public AssociativePredicateInfix(int tag) {
			super(tag);
		}

		public AssociativePredicate led(Formula<?> left, ParserContext pc, int startPos)
				throws SyntaxError {
			final Predicate right = MainParser.parsePredicate(tag, pc);
			if (!(left instanceof Predicate)) {
				throw new SyntaxError("expected predicates");
			}
			final List<Predicate> children = new ArrayList<Predicate>();
			if (left.getTag() == tag) {
				children.addAll(asList(((AssociativePredicate) left)
						.getChildren()));
			} else {
				children.add((Predicate) left);
			}
			children.add(right);
			return pc.factory.makeAssociativePredicate(tag, children, pc
					.getSourceLocation(startPos));
		}
	}

	static class RelationalPredicateInfix extends DefaultLedParser {

		public RelationalPredicateInfix(int tag) {
			super(tag);
		}

		public RelationalPredicate led(Formula<?> left, ParserContext pc,
				int startPos) throws SyntaxError {
			final Expression right = MainParser.parseExpression(tag, pc);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			return pc.factory.makeRelationalPredicate(tag, (Expression) left,
					right, pc.getSourceLocation(startPos));
		}
	}

	static final ILedParser FUN_IMAGE = new DefaultLedParser(FUNIMAGE) {

		public BinaryExpression led(Formula<?> left, ParserContext pc,
				int startPos) throws SyntaxError {
			final Expression right = MainParser.parseExpression(NO_TAG, pc);
			if (!(left instanceof Expression)) {
				throw new SyntaxError("expected expressions");
			}
			pc.progressCloseParen();
			return pc.factory.makeBinaryExpression(tag, (Expression) left,
					right, pc.getSourceLocation(startPos));
		}
	};

	static class LiteralPredicateParser extends DefaultNudParser {

		public LiteralPredicateParser(int tag) {
			super(tag);
		}

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			final LiteralPredicate litPred = pc.factory.makeLiteralPredicate(
					tag, pc.getSourceLocation(startPos));
			pc.progress();
			return litPred;
		}
	}

	static class QuantifiedPredicateParser extends DefaultNudParser {

		public QuantifiedPredicateParser(int tag) {
			super(tag);
		}

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progress();
			final List<FreeIdentifier> identList = IDENT_LIST_PARSER.parse(pc, pc.t.pos);
			pc.progress(_DOT);
			final Predicate pred = MainParser.parsePredicate(tag, pc);

			final Predicate boundPred = pred.bindTheseIdents(identList, pc.factory);
			final List<BoundIdentDecl> boundIdentifiers = makeBoundIdentDeclList(
					pc.factory, identList);
			return pc.factory.makeQuantifiedPredicate(tag, boundIdentifiers,
					boundPred, null);
		}
	}

	static List<BoundIdentDecl> makeBoundIdentDeclList(FormulaFactory factory,
			final List<FreeIdentifier> identList) {
		final List<BoundIdentDecl> boundIdentifiers = new ArrayList<BoundIdentDecl>(identList.size());
		// TODO use Formula.bindTheseIdents instead ?
		for (FreeIdentifier ident: identList) {
			boundIdentifiers.add(ident.asDecl(factory));
		}
		return boundIdentifiers;
	}

	static class UnaryExpressionParser extends DefaultNudParser {

		public UnaryExpressionParser(int tag) {
			super(tag);
		}

		public Formula<?> nud(ParserContext pc, int startPos)
				throws SyntaxError {
			pc.progress();
			pc.progressOpenParen();
			final Expression child = MainParser.parseExpression(NO_TAG, pc);
			pc.progressCloseParen();
			return pc.factory.makeUnaryExpression(tag, child, pc.getSourceLocation(startPos));
		}

	}

	static class PatternParser {
		
		public static void parse(ParserContext pc, int startPos, Pattern pattern) throws SyntaxError {
			parsePatternAtom(pc, pc.t.pos, pattern);
			while (pc.t.kind == _MAPSTO) {
				pc.progress();
				parsePatternAtom(pc, pc.t.pos, pattern);
				pattern.mapletParsed(pc.getSourceLocation(startPos));
			}
		}

		private static void parsePatternAtom(ParserContext pc, int startPos,
				Pattern pattern) throws SyntaxError {
			if (pc.t.kind == _LPAR) {
				pc.progressOpenParen();
				parse(pc, pc.t.pos, pattern);
				pc.progressCloseParen();
			} else {
				final FreeIdentifier ident = (FreeIdentifier) FREE_IDENT_SUBPARSER.nud(pc, pc.t.pos);
				pattern.declParsed(ident.asDecl(pc.factory));
			}
		}

	}
	
	static abstract class AbstListParser<T extends Expression> {
	
		public List<T> parse(ParserContext pc, int startPos) throws SyntaxError {
			final List<T> list = new ArrayList<T>();
			T next = getItem(pc, startPos);
			list.add(next);
			while (pc.t.kind == _COMMA) {
				pc.progress();
				next = getItem(pc, pc.t.pos);
				list.add(next);
			}
			return list;
		}
		
		protected abstract T getItem(ParserContext pc, int startPos) throws SyntaxError;
		
	}

	static final AbstListParser<Expression> EXPR_LIST_PARSER = new AbstListParser<Expression>() {
		@Override
		protected Expression getItem(ParserContext pc, int startPos)
				throws SyntaxError {
			return MainParser.parseExpression(NO_TAG, pc);
		}
	};
	
	static final AbstListParser<FreeIdentifier> IDENT_LIST_PARSER = new AbstListParser<FreeIdentifier>() {
		@Override
		protected FreeIdentifier getItem(ParserContext pc, int startPos)
				throws SyntaxError {
			final FreeIdentifier ident = (FreeIdentifier) FREE_IDENT_SUBPARSER.nud(pc, pc.t.pos);
			return ident;
		}
	};

	static final INudParser SETEXT_PARSER = new DefaultNudParser(SETEXT) {
		
		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progress();
			final List<Expression> exprs = EXPR_LIST_PARSER.parse(pc, pc.t.pos);
			pc.progress(_RBRACE);
			
			return pc.factory.makeSetExtension(exprs, pc.getSourceLocation(startPos));
		}
	};
	
	static final INudParser CSET_EXPLICIT = new DefaultNudParser(CSET) {
		
		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progress();
			final List<FreeIdentifier> idents = IDENT_LIST_PARSER.parse(pc, pc.t.pos);
			pc.progress(_DOT);
			final Predicate pred = MainParser.parsePredicate(NO_TAG, pc);
			pc.progress(_MID);
			final Expression expr = MainParser.parseExpression(NO_TAG, pc);
			pc.progress(_RBRACE);
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, idents);
			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					boundExpr, pc.getSourceLocation(startPos), Form.Explicit);
		}
	};
	
	static final INudParser CSET_IMPLICIT = new DefaultNudParser(CSET) {
		
		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progress();
			final Expression expr = MainParser.parseExpression(NO_TAG, pc);
			pc.progress(_MID);
			final Predicate pred = MainParser.parsePredicate(NO_TAG, pc);
			pc.progress(_RBRACE);
			final List<FreeIdentifier> idents = asList(expr.getFreeIdentifiers());
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);
			final List<BoundIdentDecl> boundIdents = makeBoundIdentDeclList(pc.factory, idents);
			return pc.factory.makeQuantifiedExpression(tag, boundIdents, pred,
					boundExpr, pc.getSourceLocation(startPos), Form.Implicit);
		}
	};
	
	static final INudParser CSET_LAMBDA = new DefaultNudParser(CSET) {
		
		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			pc.progress();
			final Pattern pattern = new Pattern(pc.result);
			PatternParser.parse(pc, pc.t.pos, pattern);
			final List<FreeIdentifier> idents = getFreeIdents(pc, pattern.getDecls());
			pc.progress(_DOT);
			final Predicate pred = MainParser.parsePredicate(NO_TAG, pc);
			final Predicate boundPred = pred.bindTheseIdents(idents, pc.factory);
			pc.progress(_MID);
			final Expression expr = MainParser.parseExpression(NO_TAG, pc);
			final Expression boundExpr = expr.bindTheseIdents(idents, pc.factory);
			
			final Expression pair = pc.factory.makeBinaryExpression(MAPSTO,
					pattern.getPattern(), boundExpr, null);
			return pc.factory.makeQuantifiedExpression(tag, pattern.getDecls(), boundPred,
					pair, pc.getSourceLocation(startPos), Form.Lambda);
		}

		// TODO avoid binding identifiers afterwards, better pass them
		// as arguments everywhere; reuse Binding from bmath.atg
		private List<FreeIdentifier> getFreeIdents(ParserContext pc,
				final List<BoundIdentDecl> decls) {
			final List<FreeIdentifier> idents = new ArrayList<FreeIdentifier>(decls.size());
			for (BoundIdentDecl bid : decls) {
				idents.add(pc.factory.makeFreeIdentifier(bid.getName(), bid
						.getSourceLocation()));
			}
			return idents;
		}
	};
	
	static class MainParser {

		/**
		 * Parses the formula from the given parser context. Starts from the
		 * current token. When the method returns, the current token is the one
		 * that immediately follows the last one that belongs to the parsed
		 * formula.
		 * 
		 * @param parentTag
		 *            tag of the parent formula, {@link Formula#NO_TAG} if none
		 * @param pc
		 *            current parser context
		 * @return a formula
		 * @throws SyntaxError
		 *             if a syntax error occurs while parsing
		 */
		public static Formula<?> parse(int parentTag, ParserContext pc)
				throws SyntaxError {
			final int startPos = pc.t.pos;
			final List<INudParser> nudParsers = getNudParsers(pc);
			Formula<?> left = nudParse(pc, startPos, nudParsers);
			while (pc.canProgressRight(parentTag)) {
				final ILedParser ledParser = getLedParser(pc);
				pc.progress();
				left = ledParser.led(left, pc, startPos);
			}
			return left;
		}
		
		// TODO implement backtracking for led parsers as well 

		private static Formula<?> nudParse(ParserContext pc, int startPos,
				List<INudParser> parsers) throws SyntaxError {
			// FIXME store current context and restore it for each sub-parser
			final List<SyntaxError> errors = new ArrayList<SyntaxError>();
			final Iterator<INudParser> iter = parsers.iterator();
			final SavedContext savedContext = pc.save();
			while(iter.hasNext()) {
				final INudParser nudParser = iter.next();
				try {
					return nudParser.nud(pc, startPos);
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

		public static Type parseType(ParserContext pc) throws SyntaxError {
			final int startPos = pc.t.pos;
			pc.startParsingType();
			final Expression expression = parseExpression(NO_TAG, pc);
			if (!expression.isATypeExpression()) {
				final int endPos = pc.t.pos;
				throw new SyntaxError(
						"expected a type expression between positions "
								+ startPos + " and " + endPos);
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

		public static Predicate parsePredicate(int parentTag, ParserContext pc) throws SyntaxError {
			final Formula<?> formula = parse(parentTag, pc);
			if (!(formula instanceof Predicate)) {
				throw new SyntaxError("expected predicate");
			}
			return (Predicate) formula;
		}
		
		public static Expression parseExpression(int parentTag, ParserContext pc) throws SyntaxError {
			final Formula<?> formula = parse(parentTag, pc);
			if (!(formula instanceof Expression)) {
				throw new SyntaxError("expected expression");
			}
			return (Expression) formula;
		}
		
		// returns a non empty list
		private static List<INudParser> getNudParsers(ParserContext pc)
				throws SyntaxError {
			final List<INudParser> subParsers = pc.getNudParsers();
			if (subParsers.isEmpty()) {
				throw new SyntaxError("don't know how to parse: " + pc.t.val);
			}
			return subParsers;
		}
		
		private static ILedParser getLedParser(ParserContext pc)
		throws SyntaxError {
			final ILedParser subParser = pc.getLedParser();
			if (subParser == null) {
				throw new SyntaxError("don't know how to parse: " + pc.t.val);
			}
			return subParser;
		}
	}

	static final INudParser FREE_IDENT_SUBPARSER = new DefaultNudParser(FREE_IDENT) {

		public FreeIdentifier nud(ParserContext pc, int startPos) throws SyntaxError {
			final String name = pc.t.val;
			final Type type;
			if (pc.isParsingType()) {
				type = pc.factory.makePowerSetType(pc.factory.makeGivenType(name));
			} else {
				type = null;
			}
			final FreeIdentifier ident = pc.factory.makeFreeIdentifier(name, pc
					.getSourceLocation(startPos), type);
			pc.progress();
			return ident;
		}
	};

	static final INudParser INTLIT_SUBPARSER = new DefaultNudParser(INTLIT) {

		public Formula<?> nud(ParserContext pc, int startPos) throws SyntaxError {
			final BigInteger value = BigInteger.valueOf((Integer // FIXME NumberFormatException
					.valueOf(pc.t.val)));
			pc.progress();
			return pc.factory.makeIntegerLiteral(value, pc.getSourceLocation(startPos));
		}
	};

}

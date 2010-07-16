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
import static org.eventb.core.ast.BecomesEqualTo.OP_BECEQ;
import static org.eventb.core.ast.BecomesMemberOf.OP_BECMO;
import static org.eventb.core.ast.BecomesSuchThat.OP_BECST;
import static org.eventb.core.ast.Formula.BECOMES_EQUAL_TO;
import static org.eventb.core.ast.Formula.BECOMES_MEMBER_OF;
import static org.eventb.core.ast.Formula.BECOMES_SUCH_THAT;
import static org.eventb.core.ast.ProblemKind.PrematureEOF;
import static org.eventb.internal.core.parser.AbstractGrammar._COMMA;
import static org.eventb.internal.core.parser.AbstractGrammar._EOF;
import static org.eventb.internal.core.parser.AbstractGrammar._LPAR;
import static org.eventb.internal.core.parser.AbstractGrammar._NOOP;
import static org.eventb.internal.core.parser.BMath._MAPSTO;
import static org.eventb.internal.core.parser.GenParser.ProgressDirection.RIGHT;
import static org.eventb.internal.core.parser.SubParsers.BOUND_IDENT_DECL_SUBPARSER;
import static org.eventb.internal.core.parser.SubParsers.FREE_IDENT_SUBPARSER;
import static org.eventb.internal.core.parser.SubParsers.NO_DECL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.lexer.Token;
import org.eventb.internal.core.parser.GenParser.ParserContext;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.GenParser.ParserContext.SavedContext;
import org.eventb.internal.core.parser.IParserPrinter.SubParseResult;
import org.eventb.internal.core.parser.SubParsers.AbstractNudParser;
import org.eventb.internal.core.parser.SubParsers.OftypeParser;

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
	
	private static final String AN_EXPRESSION = "an expression";

	private static final String A_PREDICATE = "a predicate";

	private static ASTProblem makeUnexpectedKindProblem(Formula<?> formula,
			String expectedKind, String actualKind) {
		return new ASTProblem(formula.getSourceLocation(),
				ProblemKind.UnexpectedSubFormulaKind,
				ProblemSeverities.Error, expectedKind, actualKind);
	}

	static Predicate asPredicate(Formula<?> formula) throws SyntaxError {
		if (!(formula instanceof Predicate)) {
			throw new SyntaxError(makeUnexpectedKindProblem(formula, A_PREDICATE, AN_EXPRESSION));
		}
		return (Predicate) formula;
	}

	static Expression asExpression(Formula<?> formula) throws SyntaxError {
		if (!(formula instanceof Expression)) {
			throw new SyntaxError(makeUnexpectedKindProblem(formula, AN_EXPRESSION, A_PREDICATE));
		}
		return (Expression) formula;
	}

	private static abstract class ParserApplier<Parser> {
		
		public ParserApplier() {
			// avoid synthetic accessor method emulation
		}
		
		public SubParseResult<Formula<?>> apply(ParserContext pc, Formula<?> left) throws SyntaxError {
			final Parser parser = getParser(pc);
			pc.pushParentKind();
			try {
				return apply(pc, parser, left);
			} finally {
				pc.popParentKind();
			}
		}
		
		protected abstract Parser getParser(ParserContext pc) throws SyntaxError;
		protected abstract SubParseResult<Formula<?>> apply(ParserContext pc, Parser parser, Formula<?> left) throws SyntaxError;
		
		protected static SyntaxError newOperatorError(ParserContext pc,
				ProblemKind problemKind) {
			final SourceLocation srcLoc = pc.makeSourceLocation(pc.t);
			if (pc.t.kind == _EOF) {
				return new SyntaxError(new ASTProblem(srcLoc, PrematureEOF,
						ProblemSeverities.Error));
			}
			return new SyntaxError(new ASTProblem(srcLoc, problemKind,
					ProblemSeverities.Error, pc.t.val));
		}

		// errors must be non empty 
		protected static SyntaxError newCompoundError(SourceLocation loc, Set<ASTProblem> errors) {
			final List<String> messages = new ArrayList<String>(errors.size());
			for (ASTProblem astProblem : errors) {
				messages.add(astProblem.toString());
			}
			return new SyntaxError(new ASTProblem(loc,
					ProblemKind.VariousPossibleErrors, ProblemSeverities.Error,
					ProblemKind.makeCompoundMessage(errors)));
		}

	}
	
	static final ParserApplier<List<INudParser<? extends Formula<?>>>> NUD_APPLIER = new ParserApplier<List<INudParser<? extends Formula<?>>>>() {
		
		@Override
		protected List<INudParser<? extends Formula<?>>> getParser(ParserContext pc)
		throws SyntaxError {
			final List<INudParser<? extends Formula<?>>> subParsers = pc.getNudParsers();
			if (subParsers.isEmpty()) {
				final ILedParser<? extends Formula<?>> ledParser = pc.getLedParser();
				if (ledParser == null) { // no parser exists for current token
					throw newOperatorError(pc, ProblemKind.UnknownOperator);
				} else { // operator is misplaced
					throw newOperatorError(pc, ProblemKind.MisplacedLedOperator);
				}
			}
			return subParsers;
		}
		
		@Override
		protected SubParseResult<Formula<?>> apply(ParserContext pc,
				List<INudParser<? extends Formula<?>>> nudParsers,
				Formula<?> left) throws SyntaxError {
			final Set<ASTProblem> errors = new LinkedHashSet<ASTProblem>();
			final Iterator<INudParser<? extends Formula<?>>> iter = nudParsers.iterator();
			final SavedContext savedContext = pc.save();
			while(iter.hasNext()) {
				final INudParser<? extends Formula<?>> nudParser = iter.next();
				try {
					// FIXME the call to nud may add problems to pc.result
					// without throwing an exception
					// => convention: exception + problem if not recoverable
					//                problem only if recoverable
					final SubParseResult<? extends Formula<?>> nudResult = nudParser
							.nud(pc);

					return new SubParseResult<Formula<?>>(
							nudResult.getParsed(), nudResult.getKind(),
							nudResult.isClosed());
					// FIXME check for ambiguities (several succeeding parsers)
				} catch (SyntaxError e) {
					errors.add(e.getProblem());
					pc.restore(savedContext);
				}
			}
			if (errors.size() == 1) {
				throw new SyntaxError(errors.iterator().next());
			} else {
				throw newCompoundError(pc.makeSourceLocation(pc.t), errors);
			}
		}
		
	};

	static final ParserApplier<ILedParser<? extends Formula<?>>> LED_APPLIER = new ParserApplier<ILedParser<? extends Formula<?>>>() {
		
		@Override
		protected ILedParser<? extends Formula<?>> getParser(ParserContext pc)
				throws SyntaxError {
			final ILedParser<? extends Formula<?>> subParser = pc.getLedParser();
			if (subParser == null) {
				final List<INudParser<? extends Formula<?>>> nudParsers = pc.getNudParsers();
				if (nudParsers.isEmpty()) { // no parser exists for current token
					throw newOperatorError(pc, ProblemKind.UnknownOperator);
				} else { // operator is misplaced
					throw newOperatorError(pc, ProblemKind.MisplacedNudOperator);
				}
			}
			return subParser;
		}
		
		@Override
		protected SubParseResult<Formula<?>> apply(ParserContext pc,
				ILedParser<? extends Formula<?>> parser, Formula<?> left)
				throws SyntaxError {
			final SubParseResult<? extends Formula<?>> ledResult = parser.led(left, pc);
			return new SubParseResult<Formula<?>>(ledResult.getParsed(),
					ledResult.getKind(), ledResult.isClosed());
		}
		
	};
	
	static final int[] NO_TAGS = new int[0];
	
	private static abstract class AbstractMainParser<T> implements INudParser<T> {
		public AbstractMainParser() {
			// avoid synthetic accessors
		}
		
	}
	
	// Core algorithm implementation
	static final INudParser<? extends Formula<?>> FORMULA_PARSER = new AbstractMainParser<Formula<?>>() {
		
		public SubParseResult<Formula<?>> nud(ParserContext pc)
				throws SyntaxError {
		
			SubParseResult<Formula<?>> left = NUD_APPLIER.apply(pc, null);

			while (pc.giveProgressDirection() == RIGHT) {
				left = LED_APPLIER.apply(pc, left.getParsed());
			}
			
			return left;
		}

		public void toString(IToStringMediator mediator, Formula<?> toPrint) {
			mediator.forward(toPrint);			
		}

	};

	static final INudParser<Type> TYPE_PARSER = new AbstractMainParser<Type>() {
		
		public SubParseResult<Type> nud(ParserContext pc) throws SyntaxError {
			pc.startParsingType();
			try {
				final SubParseResult<Expression> exprResult = pc.subParseRes(EXPR_PARSER, false);
				final Expression expression = exprResult.getParsed();
				if (!expression.isATypeExpression()) {
					throw newInvalidTypeExpr(pc);
				}
				final Type type = expression.toType(pc.factory);
				return new SubParseResult<Type>(type, exprResult.getKind(),
						exprResult.isClosed());
			} catch (InvalidExpressionException e) {
				// cannot happen (already checked)
				throw new IllegalStateException(
						"The expression has been reckoned as a valid type expression, but could not be translated into a type.",
						e);
			} finally {
				pc.stopParsingType();
			}
		}

		private SyntaxError newInvalidTypeExpr(ParserContext pc) {
			return new SyntaxError(new ASTProblem(pc.getSourceLocation(), ProblemKind.InvalidTypeExpression, ProblemSeverities.Error));
		}

		public void toString(IToStringMediator mediator, Type toPrint) {
			final Expression expression = toPrint.toExpression(mediator.getFactory());
			mediator.forward(expression);
		}
	};

	static final INudParser<Predicate> PRED_PARSER = new AbstractMainParser<Predicate>() {
		
		public SubParseResult<Predicate> nud(ParserContext pc) throws SyntaxError {
			final SubParseResult<? extends Formula<?>> formulaResult = FORMULA_PARSER.nud(pc);
			final Predicate predicate = asPredicate(formulaResult.getParsed());
			return new SubParseResult<Predicate>(predicate, formulaResult
					.getKind(), formulaResult.isClosed());
		}

		public void toString(IToStringMediator mediator, Predicate toPrint) {
			mediator.forward(toPrint);
		}
	};

	static final INudParser<Expression> EXPR_PARSER = new AbstractMainParser<Expression>() {
		
		public SubParseResult<Expression> nud(ParserContext pc) throws SyntaxError {
			final SubParseResult<? extends Formula<?>> formulaResult = FORMULA_PARSER.nud(pc);
			final Expression expression = asExpression(formulaResult.getParsed());
			return new SubParseResult<Expression>(expression, formulaResult
					.getKind(), formulaResult.isClosed());
		}
		
		public void toString(IToStringMediator mediator, Expression toPrint) {
			mediator.forward(toPrint);
		}
	};

	static final INudParser<Formula<?>> CLOSED_SUGAR = new AbstractMainParser<Formula<?>> () {

		public SubParseResult<Formula<?>> nud(ParserContext pc) throws SyntaxError {
			pc.progressOpenParen();
			final SubParseResult<? extends Formula<?>> formula = pc.subParseNoCheckRes(FORMULA_PARSER);
			pc.progressCloseParen();
			return new SubParseResult<Formula<?>>(formula.getParsed(), formula
					.getKind(), true);
		}

		public void toString(IToStringMediator mediator, Formula<?> toPrint) {
			// should never be called
			assert false;
		}
		
	};

	static class PatternParser extends AbstractMainParser<Pattern> {
		
		final Pattern pattern;
		
		public PatternParser(ParseResult result) {
			this.pattern = new Pattern(result);
		}

		public SubParseResult<Pattern> nud(ParserContext pc) throws SyntaxError {
			final PatternAtomParser atomParser = new PatternAtomParser(pattern, this);
			pc.subParseNoCheck(atomParser);
			while (pc.t.kind == _MAPSTO) {
				pc.progress(_MAPSTO);
				pc.subParseNoCheck(atomParser);
				pattern.mapletParsed(pc.getSourceLocation());
			}
			return new SubParseResult<Pattern>(pattern, _MAPSTO);
		}

		// needed as a parser in order to be passed to pc.subParse
		// so as to get correct source locations
		private static class PatternAtomParser extends AbstractMainParser<Object> {

			private static final SubParseResult<Object> NULL_SUB_PARSE_RESULT = new SubParseResult<Object>(null, _NOOP);
			private final Pattern pattern;
			private final PatternParser parser;
			
			public PatternAtomParser(Pattern pattern, PatternParser parser) {
				this.pattern = pattern;
				this.parser = parser;
			}

			public SubParseResult<Object> nud(ParserContext pc) throws SyntaxError {
				if (pc.t.kind == _LPAR) {
					pc.progressOpenParen();
					pc.subParse(parser, false);
					pc.progressCloseParen();
				} else {
					final BoundIdentDecl boundIdent = pc
							.subParse(BOUND_IDENT_DECL_SUBPARSER, false);
					pattern.declParsed(boundIdent);
				}
				return NULL_SUB_PARSE_RESULT;
			}

			public void toString(IToStringMediator mediator,
					Object toPrint) {
				// should never happen
				assert false;
			}
		}

		public void toString(IToStringMediator mediator,
				Pattern toPrint) {
			// should never happen
			assert false;
		}
	}

	// parses a non empty list of T
	static class AbstListParser<T extends Formula<?>> extends AbstractMainParser<List<T>> {
	
		private final INudParser<T> parser;
		
		public AbstListParser(INudParser<T> parser) {
			this.parser = parser;
		}

		public SubParseResult<List<T>> nud(ParserContext pc) throws SyntaxError {
			final List<T> list = new ArrayList<T>();
			final T first = pc.subParseNoCheck(parser);
			list.add(first);
			while (pc.t.kind == _COMMA) {
				pc.progress(_COMMA);
				final T next = pc.subParseNoCheck(parser);
				list.add(next);
			}
			// FIXME not an operator kind => must be called with compatibility checks disabled
			return new SubParseResult<List<T>>(list, _NOOP); 
		}

		public void toString(IToStringMediator mediator,
				List<T> toPrint) {
			final Iterator<T> iter = toPrint.iterator();
			final T first = iter.next();
			mediator.subPrintNoPar(first, false, NO_DECL);
			while(iter.hasNext()) {
				appendSeparator(mediator);
				final T next = iter.next();
				mediator.subPrintNoPar(next, false, NO_DECL);
			}
		}

		protected void appendSeparator(IToStringMediator mediator) {
			mediator.append(",");
		}
		
	}

	static final AbstListParser<Expression> EXPR_LIST_PARSER = new AbstListParser<Expression>(EXPR_PARSER);
	
	static final AbstListParser<Expression> SPACED_EXPR_LIST_PARSER = new AbstListParser<Expression>(EXPR_PARSER) {
		@Override
		protected void appendSeparator(IToStringMediator mediator) {
			super.appendSeparator(mediator);
			mediator.append(SubParsers.SPACE);
		}
	};
	
	static final AbstListParser<FreeIdentifier> FREE_IDENT_LIST_PARSER = new AbstListParser<FreeIdentifier>(FREE_IDENT_SUBPARSER);

	public static class BoundIdentDeclListParser extends AbstListParser<BoundIdentDecl> {

		public BoundIdentDeclListParser() {
			super(BOUND_IDENT_DECL_SUBPARSER);
		}
		
		public void toString(IToStringMediator mediator, BoundIdentDecl[] decls, String[] localNames) {
			printIdent(mediator, decls, localNames, 0);
			for(int i=1;i<localNames.length;i++) {
				mediator.append(",");
				printIdent(mediator, decls, localNames, i);			
			}
		}

		private static void printIdent(IToStringMediator mediator,
				final BoundIdentDecl[] decls, final String[] resolvedIdents,
				int index) {
			mediator.append(resolvedIdents[index]);
			if (mediator.isWithTypes() && decls[index].isTypeChecked()) {
				OftypeParser.appendOftype(mediator, decls[index].getType(), false);
			}
		}
		
	}
	
	static final BoundIdentDeclListParser BOUND_IDENT_DECL_LIST_PARSER = new BoundIdentDeclListParser();
	
	static List<BoundIdentDecl> makePrimedDecl(List<FreeIdentifier> lhsList, FormulaFactory factory) {
		final List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>(lhsList.size());
	    for (FreeIdentifier ident: lhsList) {
			if (ident.isPrimed()) {
				decls.add(ident.asDecl(factory));
			} else {
				decls.add(ident.asPrimedDecl(factory));
			}
		}
		return decls;
	}
	
	public static class BecomesEqualToParser extends AbstractNudParser<BecomesEqualTo> {

		public BecomesEqualToParser(int kind) {
			super(kind, BECOMES_EQUAL_TO);
		}

		public SubParseResult<BecomesEqualTo> nud(ParserContext pc) throws SyntaxError {
			final List<FreeIdentifier> idents = pc.subParseNoCheck(FREE_IDENT_LIST_PARSER);
			// the list is guaranteed to be non empty
			assert !idents.isEmpty();
			
			final Token tokenAfterIdents = pc.t;
			final int tokenKind = tokenAfterIdents.kind;
			pc.progress(tokenKind);

			if (tokenKind == _LPAR) { // FUNIMAGE assignment
				if (idents.size() != 1) {
					throw new SyntaxError(new ASTProblem(
							pc.getSourceLocation(),
							ProblemKind.InvalidAssignmentToImage,
							ProblemSeverities.Error));
				}
				final FreeIdentifier ident = idents.get(0);
				final Expression index = pc.subParse(EXPR_PARSER, false);
				pc.progressCloseParen();
				pc.progress(kind);
				final Expression value = pc.subParse(EXPR_PARSER, true);
				final Expression overriding = makeFunctionOverriding(ident, index, value, pc.factory);
				final BecomesEqualTo bet = pc.factory.makeBecomesEqualTo(ident, overriding, pc.getSourceLocation());
				return new SubParseResult<BecomesEqualTo>(bet, kind);
			} else if (tokenKind == kind) {
				final List<Expression> values = pc.subParseNoCheck(EXPR_LIST_PARSER);
				if (idents.size() != values.size()) {
					throw new SyntaxError(new ASTProblem(
							pc.makeSourceLocation(tokenAfterIdents),
							ProblemKind.IncompatibleIdentExprNumbers,
							ProblemSeverities.Error, idents.size(), values
									.size()));
				}
				final BecomesEqualTo bet = pc.factory.makeBecomesEqualTo(idents, values, pc.getSourceLocation());
				return new SubParseResult<BecomesEqualTo>(bet, kind);
			} else {
				throw new SyntaxError(new ASTProblem(pc
						.makeSourceLocation(tokenAfterIdents),
						ProblemKind.UnknownOperator, ProblemSeverities.Error,
						tokenAfterIdents + " (as assignment operator)"));
				// FIXME when switching to led parsing, this disappears
			}
		}

		public void toString(IToStringMediator mediator, BecomesEqualTo toPrint) {
			final FreeIdentifier[] idents = toPrint.getAssignedIdentifiers();
			FREE_IDENT_LIST_PARSER.toString(mediator, asList(idents));
			mediator.appendImage(kind);
			final Expression[] expressions = toPrint.getExpressions();
			SPACED_EXPR_LIST_PARSER.toString(mediator, asList(expressions));
		}
		
	}

	public static class BecomesMemberOfParser extends AbstractNudParser<BecomesMemberOf> {

		public BecomesMemberOfParser(int kind) {
			super(kind, BECOMES_MEMBER_OF);
		}

		public SubParseResult<BecomesMemberOf> nud(ParserContext pc) throws SyntaxError {
			final FreeIdentifier ident = pc.subParse(FREE_IDENT_SUBPARSER, false);
			pc.progress(kind);
			final Expression expr = pc.subParseNoParentNoCheck(EXPR_PARSER, Collections.<BoundIdentDecl>emptyList());
			final BecomesMemberOf bmo = pc.factory.makeBecomesMemberOf(ident, expr, pc.getSourceLocation());
			return new SubParseResult<BecomesMemberOf>(bmo, kind);
		}

		public void toString(IToStringMediator mediator, BecomesMemberOf toPrint) {
			final FreeIdentifier[] idents = toPrint.getAssignedIdentifiers();
			FREE_IDENT_LIST_PARSER.toString(mediator, asList(idents));
			mediator.appendImage(kind);
			final Expression set = toPrint.getSet();
			EXPR_PARSER.toString(mediator, set);
		}

	}

	public static class BecomesSuchThatParser extends AbstractNudParser<BecomesSuchThat> {

		public BecomesSuchThatParser(int kind) {
			super(kind, BECOMES_SUCH_THAT);
		}

		public SubParseResult<BecomesSuchThat> nud(ParserContext pc) throws SyntaxError {
			final List<FreeIdentifier> idents = pc.subParseNoCheck(FREE_IDENT_LIST_PARSER);
			// the list is guaranteed to be non empty
			assert !idents.isEmpty();
			
			pc.progress(kind);
			
			final List<BoundIdentDecl> primed = makePrimedDecl(idents, pc.factory);
			final Predicate condition = pc.subParse(PRED_PARSER, primed, true);
			final BecomesSuchThat bst = pc.factory.makeBecomesSuchThat(idents, primed, condition, pc.getSourceLocation());
			return new SubParseResult<BecomesSuchThat>(bst, kind);
		}

		public void toString(IToStringMediator mediator, BecomesSuchThat toPrint) {
			final FreeIdentifier[] idents = toPrint.getAssignedIdentifiers();
			FREE_IDENT_LIST_PARSER.toString(mediator, asList(idents));
			mediator.appendImage(kind);
		
			final Predicate condition = toPrint.getCondition();
			final BoundIdentDecl[] primedIdents = toPrint.getPrimedIdents();
			
			mediator.subPrintNoPar(condition, true, toNames(primedIdents));
		}

		private static String[] toNames(BoundIdentDecl[] idents) {
			final String[] names = new String[idents.length];
			for (int i = 0; i < idents.length; i++) {
				names[i] = idents[i].getName();
			}
			return names;
		}

	}

	// used as a main parser; directly called by the general parser.
	// FIXME particular case required because assignment lhs
	// is not a terminal (not a formula, but a list of identifiers)
	// other possibility: introduce a notion of non terminal
	// returned by sub-parsers, then implement assignment parsing
	// with led sub-parsers
	/** @see GenParser#parse() */
	public static final INudParser<Assignment> ASSIGNMENT_PARSER = 
		new AbstractMainParser<Assignment>() {

		public SubParseResult<Assignment> nud(ParserContext pc) throws SyntaxError {
			final INudParser<? extends Assignment> parser = getAssignmentParser(pc);
			final SubParseResult<? extends Assignment> assignResult = pc.subParseRes(parser, false);
			return new SubParseResult<Assignment>(assignResult.getParsed(),
					assignResult.getKind());
		}

		private INudParser<? extends Assignment> getAssignmentParser(ParserContext pc) throws SyntaxError {
			final int becEqKind = pc.getKind(OP_BECEQ.getImage());
			if (pc.lookAheadFor(becEqKind)) {
				return new BecomesEqualToParser(becEqKind);
			}
			final int becMoKind = pc.getKind(OP_BECMO.getImage());
			if (pc.lookAheadFor(becMoKind)) {
				return new BecomesMemberOfParser(becMoKind);
			}
			final int becStKind = pc.getKind(OP_BECST.getImage());
			if (pc.lookAheadFor(becStKind)) {
				return new BecomesSuchThatParser(becStKind);
			}

			// FIXME when switching to led parsing, this disappears
			throw new SyntaxError(new ASTProblem(pc
					.getEnclosingSourceLocation(),
					ProblemKind.UnknownOperator, ProblemSeverities.Error,
					" (expected to find an assignment operator)"));
		}

		public void toString(IToStringMediator mediator, Assignment toPrint) {
			mediator.forward(toPrint);
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

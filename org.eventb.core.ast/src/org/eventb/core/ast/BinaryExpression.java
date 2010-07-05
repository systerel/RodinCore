/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.internal.core.parser.BMath.ARITHMETIC;
import static org.eventb.internal.core.parser.BMath.BINOP;
import static org.eventb.internal.core.parser.BMath.FUNCTIONAL;
import static org.eventb.internal.core.parser.BMath.INTERVAL;
import static org.eventb.internal.core.parser.BMath.PAIR;
import static org.eventb.internal.core.parser.BMath.RELATION;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.SubParsers.BinaryExpressionInfix;
import org.eventb.internal.core.parser.SubParsers.LedImage;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * BinaryExpression is the class for all binary expressions in an event-B
 * formula.
 * <p>
 * It has 2 children and can only accept {MAPSTO, REL, TREL, SREL, STREL, PFUN,
 * TFUN, PINJ, TINJ, PSUR, TSUR, TBIJ, SETMINUS, CPROD, DPROD, PPROD, DOMRES,
 * DOMSUB, RANRES, RANSUB, UPTO, MINUS, DIV, MOD, EXPN, FUNIMAGE, RELIMAGE}
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class BinaryExpression extends Expression {

	// Left and right children.
	// Are never null by construction.
	private final Expression left;
	private final Expression right;
	
	// offset of the corresponding tag-interval in Formula
	protected final static int firstTag = FIRST_BINARY_EXPRESSION;
	protected final static String[] tags = {
		"\u21a6",  // MAPSTO
		"\u2194",  // REL
		"\ue100",  // TREL
		"\ue101",  // SREL
		"\ue102",  // STREL
		"\u21f8",  // PFUN
		"\u2192",  // TFUN
		"\u2914",  // PINJ
		"\u21a3",  // TINJ
		"\u2900",  // PSUR
		"\u21a0",  // TSUR
		"\u2916",  // TBIJ
		"\u2216",  // SETMINUS
		"\u00d7",  // CPROD
		"\u2297",  // DPROD
		"\u2225",  // PPROD
		"\u25c1",  // DOMRES
		"\u2a64",  // DOMSUB
		"\u25b7",  // RANRES
		"\u2a65",  // RANSUB
		"\u2025",  // UPTO
		"\u2212",  // MINUS
		"\u00f7",  // DIV
		"mod",     // MOD
		"\u005e",  // EXPN
		"FUNIMAGE",// FUNIMAGE
		"RELIMAGE" // RELIMAGE
	};
	// For testing purposes
	public static final int TAGS_LENGTH = tags.length;

	/**
	 * @since 2.0
	 */
	public static final String PPROD_ID = "Parallel Product";
	/**
	 * @since 2.0
	 */
	public static final String REL_ID = "Relation";
	/**
	 * @since 2.0
	 */
	public static final String TREL_ID = "Total Relation";
	/**
	 * @since 2.0
	 */
	public static final String SREL_ID = "Surjective Relation";
	/**
	 * @since 2.0
	 */
	public static final String STREL_ID = "Surjective Total Relation";
	/**
	 * @since 2.0
	 */
	public static final String PFUN_ID = "Partial Function";
	/**
	 * @since 2.0
	 */
	public static final String PINJ_ID = "Partial Injection";
	/**
	 * @since 2.0
	 */
	public static final String TINJ_ID = "Total Injection";
	/**
	 * @since 2.0
	 */
	public static final String PSUR_ID = "Partial Surjection";
	/**
	 * @since 2.0
	 */
	public static final String TSUR_ID = "Total Surjection";
	/**
	 * @since 2.0
	 */
	public static final String TBIJ_ID = "Total Bijection";
	/**
	 * @since 2.0
	 */
	public static final String SETMINUS_ID = "Set Minus";
	/**
	 * @since 2.0
	 */
	public static final String DPROD_ID = "Direct Product";
	/**
	 * @since 2.0
	 */
	public static final String DOMRES_ID = "Domain Restriction";
	/**
	 * @since 2.0
	 */
	public static final String DOMSUB_ID = "Domain Subtraction";
	/**
	 * @since 2.0
	 */
	public static final String RANRES_ID = "Range Restriction";
	/**
	 * @since 2.0
	 */
	public static final String RANSUB_ID = "Range Subtraction";
	/**
	 * @since 2.0
	 */
	public static final String MINUS_ID = "Minus";
	/**
	 * @since 2.0
	 */
	public static final String DIV_ID = "Integer Division";
	/**
	 * @since 2.0
	 */
	public static final String MOD_ID = "Modulo";
	/**
	 * @since 2.0
	 */
	public static final String EXPN_ID = "Integer Exponentiation";
	/**
	 * @since 2.0
	 */
	public static final String TFUN_ID = "Total Function";
	/**
	 * @since 2.0
	 */
	public static final String UPTO_ID = "Up To";
	/**
	 * @since 2.0
	 */
	public static final String MAPSTO_ID = "Maps to";
	/**
	 * @since 2.0
	 */
	public static final String CPROD_ID = "Cartesian Product";
	/**
	 * @since 2.0
	 */
	public static final String FUNIMAGE_ID = "Fun Image";
	/**
	 * @since 2.0
	 */
	public static final String RELIMAGE_ID = "Relational Image";

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {
			grammar.addOperator("\u21a6", MAPSTO_ID, PAIR, new BinaryExpressionInfix(MAPSTO));
			grammar.addOperator("\u2194", REL_ID, RELATION, new BinaryExpressionInfix(REL));
			grammar.addOperator("\ue100", TREL_ID, RELATION, new BinaryExpressionInfix(TREL));
			grammar.addOperator("\ue101", SREL_ID, RELATION, new BinaryExpressionInfix(SREL));
			grammar.addOperator("\ue102", STREL_ID, RELATION, new BinaryExpressionInfix(STREL));
			grammar.addOperator("\u21f8", PFUN_ID, RELATION, new BinaryExpressionInfix(PFUN));
			grammar.addOperator("\u2192", TFUN_ID, RELATION, new BinaryExpressionInfix(TFUN));
			grammar.addOperator("\u2914", PINJ_ID, RELATION, new BinaryExpressionInfix(PINJ));
			grammar.addOperator("\u21a3", TINJ_ID, RELATION, new BinaryExpressionInfix(TINJ));
			grammar.addOperator("\u2900", PSUR_ID, RELATION, new BinaryExpressionInfix(PSUR));
			grammar.addOperator("\u21a0", TSUR_ID, RELATION, new BinaryExpressionInfix(TSUR));
			grammar.addOperator("\u2916", TBIJ_ID, RELATION, new BinaryExpressionInfix(TBIJ));
			grammar.addOperator("\u2216", SETMINUS_ID, BINOP, new BinaryExpressionInfix(SETMINUS));
			grammar.addOperator("\u00d7", CPROD_ID, BINOP, new BinaryExpressionInfix(CPROD));
			grammar.addOperator("\u2297", DPROD_ID, BINOP, new BinaryExpressionInfix(DPROD));
			grammar.addOperator("\u2225", PPROD_ID, BINOP, new BinaryExpressionInfix(PPROD));
			grammar.addOperator("\u25c1", DOMRES_ID, BINOP, new BinaryExpressionInfix(DOMRES));
			grammar.addOperator("\u2a64", DOMSUB_ID, BINOP, new BinaryExpressionInfix(DOMSUB));
			grammar.addOperator("\u25b7", RANRES_ID, BINOP, new BinaryExpressionInfix(RANRES));
			grammar.addOperator("\u2a65", RANSUB_ID, BINOP, new BinaryExpressionInfix(RANSUB));
			grammar.addOperator("\u2025", UPTO_ID, INTERVAL, new BinaryExpressionInfix(UPTO));
			grammar.addOperator("\u2212", MINUS_ID, ARITHMETIC, new BinaryExpressionInfix(MINUS));
			grammar.addOperator("\u00f7", DIV_ID, ARITHMETIC, new BinaryExpressionInfix(DIV));
			grammar.addOperator("mod", MOD_ID, ARITHMETIC, new BinaryExpressionInfix(MOD));
			grammar.addOperator("\u005e", EXPN_ID, ARITHMETIC, new BinaryExpressionInfix(EXPN));
			grammar.addOperator("(", FUNIMAGE_ID, FUNCTIONAL, new LedImage(FUNIMAGE, BMath._RPAR));
			grammar.addOperator("[", RELIMAGE_ID, FUNCTIONAL, new LedImage(RELIMAGE, BMath._RBRACKET));
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected BinaryExpression(Expression left, Expression right, int tag,
			SourceLocation location, FormulaFactory factory) {
		super (tag, location, 
				combineHashCodes(left.hashCode(), right.hashCode()));
		this.left = left;
		this.right = right;
		
		assert tag >= firstTag && tag < firstTag+tags.length;
		assert left != null;
		assert right != null;
		
		setPredicateVariableCache(this.left, this.right);
		synthesizeType(factory, null);
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		IdentListMerger freeIdentMerger = 
			IdentListMerger.makeMerger(left.freeIdents, right.freeIdents);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = 
			IdentListMerger.makeMerger(left.boundIdents, right.boundIdents);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}

		// Fast exit if children are not typed
		// (the most common case where type synthesis can't be done)
		if (! left.isTypeChecked() || ! right.isTypeChecked()) {
			return;
		}
		Type leftType = left.getType();
		Type rightType = right.getType();
		
		final Type resultType;
		Type alpha, beta, gamma, delta;
		switch (getTag()) {
		case Formula.FUNIMAGE:
			alpha = leftType.getSource();
			if (alpha != null && alpha.equals(rightType)) {
				resultType = leftType.getTarget();
			} else {
				resultType = null;
			}
			break;
		case Formula.RELIMAGE:
			alpha = leftType.getSource();
			beta = leftType.getTarget();
			if (alpha != null && alpha.equals(rightType.getBaseType())) {
					resultType = ff.makePowerSetType(beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.MAPSTO:
			resultType = ff.makeProductType(leftType, rightType);
			break;
		case Formula.REL:
		case Formula.TREL:
		case Formula.SREL:
		case Formula.STREL:
		case Formula.PFUN:
		case Formula.TFUN:
		case Formula.PINJ:
		case Formula.TINJ:
		case Formula.PSUR:
		case Formula.TSUR:
		case Formula.TBIJ:
			alpha = leftType.getBaseType();
			beta = rightType.getBaseType();
			if (alpha != null && beta != null) {
				resultType = ff.makePowerSetType(
						ff.makeRelationalType(alpha, beta)
				);
			} else {
				resultType = null;
			}
			break;
		case Formula.SETMINUS:
			alpha = leftType.getBaseType();
			if (alpha != null && leftType.equals(rightType)) {
				resultType = leftType;
			} else {
				resultType = null;
			}
			break;
		case Formula.CPROD:
			alpha = leftType.getBaseType();
			beta = rightType.getBaseType();
			if (alpha != null && beta != null) {
				resultType =ff.makeRelationalType(alpha, beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.DPROD:
			alpha = leftType.getSource();
			beta = leftType.getTarget();
			gamma = rightType.getTarget();
			if (alpha != null && beta != null && gamma != null
					&& alpha.equals(rightType.getSource())) {
				resultType = ff.makeRelationalType(alpha, ff.makeProductType(beta, gamma));
			} else {
				resultType = null;
			}
			break;
		case Formula.PPROD:
			alpha = leftType.getSource();
			beta = rightType.getSource();
			gamma = leftType.getTarget();
			delta = rightType.getTarget();
			if (alpha != null && beta != null && gamma != null && delta != null) {
				resultType = ff.makeRelationalType(
						ff.makeProductType(alpha, beta),
						ff.makeProductType(gamma, delta));
			} else {
				resultType = null;
			}
			break;
		case Formula.DOMRES:
		case Formula.DOMSUB:
			alpha = leftType.getBaseType();
			if (alpha != null && alpha.equals(rightType.getSource())) {
				resultType = rightType;
			} else {
				resultType = null;
			}
			break;
		case Formula.RANRES:
		case Formula.RANSUB:
			beta = rightType.getBaseType();
			if (beta != null && beta.equals(leftType.getTarget())) {
				resultType = leftType;
			} else {
				resultType = null;
			}
			break;
		case Formula.UPTO:
			if (leftType instanceof IntegerType && rightType instanceof IntegerType) {
				resultType = ff.makePowerSetType(leftType);
			} else {
				resultType = null;
			}
			break;
		case Formula.MINUS:
		case Formula.DIV:
		case Formula.MOD:
		case Formula.EXPN:
			if (leftType instanceof IntegerType && rightType instanceof IntegerType) {
				resultType = leftType;
			} else {
				resultType = null;
			}
			break;
		default:
			assert false;
			resultType = null;
		}
		
		if (resultType == null) {
			return;
		}
		setFinalType(resultType, givenType);
	}
	
	// Tag operator
	protected String getTagOperator() {
		return tags[getTag()-firstTag];
	}

	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		BinaryExpression otherExpr = (BinaryExpression) other;
		return hasSameType(other)
				&& left.equals(otherExpr.left, withAlphaConversion)
				&& right.equals(otherExpr.right, withAlphaConversion);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		final TypeVariable alpha, beta, gamma, delta;
		final Type resultType;
		
		left.typeCheck(result, quantifiedIdentifiers);
		right.typeCheck(result, quantifiedIdentifiers);
		
		switch (getTag()) {
		case Formula.FUNIMAGE:
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makeRelationalType(right.getType(), beta), this);
			resultType = beta;
			break;
		case Formula.RELIMAGE:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makeRelationalType(alpha, beta), this);
			result.unify(right.getType(), result.makePowerSetType(alpha), this);
			resultType = result.makePowerSetType(beta);
			break;
		case Formula.MAPSTO:
			resultType = result.makeProductType(left.getType(), right.getType());
			break;
		case Formula.REL:
		case Formula.TREL:
		case Formula.SREL:
		case Formula.STREL:
		case Formula.PFUN:
		case Formula.TFUN:
		case Formula.PINJ:
		case Formula.TINJ:
		case Formula.PSUR:
		case Formula.TSUR:
		case Formula.TBIJ:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makePowerSetType(alpha), this);
			result.unify(right.getType(), result.makePowerSetType(beta), this);
			resultType = result.makePowerSetType(result.makeRelationalType(alpha, beta));
			break;
		case Formula.SETMINUS:
			alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			result.unify(left.getType(), resultType, this);
			result.unify(right.getType(), resultType, this);
			break;
		case Formula.CPROD:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makePowerSetType(alpha), this);
			result.unify(right.getType(), result.makePowerSetType(beta), this);
			resultType = result.makeRelationalType(alpha, beta);
			break;
		case Formula.DPROD:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			gamma = result.newFreshVariable(null);
			result.unify(left.getType(),result.makeRelationalType(alpha, beta), this);
			result.unify(right.getType(),result. makeRelationalType(alpha, gamma), this);
			resultType = result.makeRelationalType(alpha, result.makeProductType(beta, gamma));
			break;
		case Formula.PPROD:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			gamma = result.newFreshVariable(null);
			delta = result.newFreshVariable(null);
			result.unify(left.getType(), result.makeRelationalType(alpha, gamma), this);
			result.unify(right.getType(), result.makeRelationalType(beta, delta), this);
			resultType = result.makeRelationalType(
					result.makeProductType(alpha, beta),
					result.makeProductType(gamma, delta));
			break;
		case Formula.DOMRES:
		case Formula.DOMSUB:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			resultType = result.makeRelationalType(alpha, beta);
			result.unify(left.getType(), result.makePowerSetType(alpha), this);
			result.unify(right.getType(), resultType, this);
			break;
		case Formula.RANRES:
		case Formula.RANSUB:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			resultType = result.makeRelationalType(alpha, beta);
			result.unify(left.getType(), resultType, this);
			result.unify(right.getType(), result.makePowerSetType(beta), this);
			break;
		case Formula.UPTO:
			final Type intType = result.makeIntegerType();
			result.unify(left.getType(), intType, this);
			result.unify(right.getType(), intType, this);
			resultType = result.makePowerSetType(intType);
			break;
		case Formula.MINUS:
		case Formula.DIV:
		case Formula.MOD:
		case Formula.EXPN:
			resultType = result.makeIntegerType();
			result.unify(left.getType(), resultType, this);
			result.unify(right.getType(), resultType, this);
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return left.solveType(unifier) & right.solveType(unifier);
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final IParserPrinter<BinaryExpression> parser;
		switch (getTag()) {
		case FUNIMAGE:
			parser = new LedImage(getTag(), BMath._RPAR);
			break;
		case RELIMAGE:
			parser = new LedImage(getTag(), BMath._RBRACKET);
			break;
		default:
			parser = new BinaryExpressionInfix(getTag());
			break;
		}
		parser.toString(mediator, this);
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getTagOperator() + "]" 
				+ getTypeName() + "\n"
				+ left.getSyntaxTree(boundNames, tabs + "\t")
				+ right.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		left.isLegible(result, quantifiedIdents);
		if (result.isSuccess()) {
			right.isLegible(result, quantifiedIdents);
		}
	}

	/**
	 * Returns the expression on the left-hand side of this node.
	 * 
	 * @return the expression on the left-hand side
	 */
	public Expression getLeft() {
		return left;
	}

	/**
	 * Returns the expression on the right-hand side of this node.
	 * 
	 * @return the expression on the right-hand side
	 */
	public Expression getRight() {
		return right;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		left.collectFreeIdentifiers(freeIdentSet);
		right.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		left.collectNamesAbove(names, boundNames, offset);
		right.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Expression newLeft = left.bindTheseIdents(binding, offset, factory);
		Expression newRight = right.bindTheseIdents(binding, offset, factory);
		if (newLeft == left && newRight == right) {
			return this;
		}
		return factory.makeBinaryExpression(getTag(), newLeft, newRight, getSourceLocation());
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case MAPSTO:   goOn = visitor.enterMAPSTO(this);   break;
		case REL:      goOn = visitor.enterREL(this);      break;
		case TREL:     goOn = visitor.enterTREL(this);     break;
		case SREL:     goOn = visitor.enterSREL(this);     break;
		case STREL:    goOn = visitor.enterSTREL(this);    break;
		case PFUN:     goOn = visitor.enterPFUN(this);     break;
		case TFUN:     goOn = visitor.enterTFUN(this);     break;
		case PINJ:     goOn = visitor.enterPINJ(this);     break;
		case TINJ:     goOn = visitor.enterTINJ(this);     break;
		case PSUR:     goOn = visitor.enterPSUR(this);     break;
		case TSUR:     goOn = visitor.enterTSUR(this);     break;
		case TBIJ:     goOn = visitor.enterTBIJ(this);     break;
		case SETMINUS: goOn = visitor.enterSETMINUS(this); break;
		case CPROD:    goOn = visitor.enterCPROD(this);    break;
		case DPROD:    goOn = visitor.enterDPROD(this);    break;
		case PPROD:    goOn = visitor.enterPPROD(this);    break;
		case DOMRES:   goOn = visitor.enterDOMRES(this);   break;
		case DOMSUB:   goOn = visitor.enterDOMSUB(this);   break;
		case RANRES:   goOn = visitor.enterRANRES(this);   break;
		case RANSUB:   goOn = visitor.enterRANSUB(this);   break;
		case UPTO:     goOn = visitor.enterUPTO(this);     break;
		case MINUS:    goOn = visitor.enterMINUS(this);    break;
		case DIV:      goOn = visitor.enterDIV(this);      break;
		case MOD:      goOn = visitor.enterMOD(this);      break;
		case EXPN:     goOn = visitor.enterEXPN(this);     break;
		case FUNIMAGE: goOn = visitor.enterFUNIMAGE(this); break;
		case RELIMAGE: goOn = visitor.enterRELIMAGE(this); break;
		default:       assert false;
		}

		if (goOn) goOn = left.accept(visitor);
		
		if (goOn) {
			switch (getTag()) {
			case MAPSTO:   goOn = visitor.continueMAPSTO(this);   break;
			case REL:      goOn = visitor.continueREL(this);      break;
			case TREL:     goOn = visitor.continueTREL(this);     break;
			case SREL:     goOn = visitor.continueSREL(this);     break;
			case STREL:    goOn = visitor.continueSTREL(this);    break;
			case PFUN:     goOn = visitor.continuePFUN(this);     break;
			case TFUN:     goOn = visitor.continueTFUN(this);     break;
			case PINJ:     goOn = visitor.continuePINJ(this);     break;
			case TINJ:     goOn = visitor.continueTINJ(this);     break;
			case PSUR:     goOn = visitor.continuePSUR(this);     break;
			case TSUR:     goOn = visitor.continueTSUR(this);     break;
			case TBIJ:     goOn = visitor.continueTBIJ(this);     break;
			case SETMINUS: goOn = visitor.continueSETMINUS(this); break;
			case CPROD:    goOn = visitor.continueCPROD(this);    break;
			case DPROD:    goOn = visitor.continueDPROD(this);    break;
			case PPROD:    goOn = visitor.continuePPROD(this);    break;
			case DOMRES:   goOn = visitor.continueDOMRES(this);   break;
			case DOMSUB:   goOn = visitor.continueDOMSUB(this);   break;
			case RANRES:   goOn = visitor.continueRANRES(this);   break;
			case RANSUB:   goOn = visitor.continueRANSUB(this);   break;
			case UPTO:     goOn = visitor.continueUPTO(this);     break;
			case MINUS:    goOn = visitor.continueMINUS(this);    break;
			case DIV:      goOn = visitor.continueDIV(this);      break;
			case MOD:      goOn = visitor.continueMOD(this);      break;
			case EXPN:     goOn = visitor.continueEXPN(this);     break;
			case FUNIMAGE: goOn = visitor.continueFUNIMAGE(this); break;
			case RELIMAGE: goOn = visitor.continueRELIMAGE(this); break;
			default:       assert false;
			}
		}
		
		if (goOn) goOn = right.accept(visitor);
		
		switch (getTag()) {
		case MAPSTO:   return visitor.exitMAPSTO(this);
		case REL:      return visitor.exitREL(this);
		case TREL:     return visitor.exitTREL(this);
		case SREL:     return visitor.exitSREL(this);
		case STREL:    return visitor.exitSTREL(this);
		case PFUN:     return visitor.exitPFUN(this);
		case TFUN:     return visitor.exitTFUN(this);
		case PINJ:     return visitor.exitPINJ(this);
		case TINJ:     return visitor.exitTINJ(this);
		case PSUR:     return visitor.exitPSUR(this);
		case TSUR:     return visitor.exitTSUR(this);
		case TBIJ:     return visitor.exitTBIJ(this);
		case SETMINUS: return visitor.exitSETMINUS(this);
		case CPROD:    return visitor.exitCPROD(this);
		case DPROD:    return visitor.exitDPROD(this);
		case PPROD:    return visitor.exitPPROD(this);
		case DOMRES:   return visitor.exitDOMRES(this);
		case DOMSUB:   return visitor.exitDOMSUB(this);
		case RANRES:   return visitor.exitRANRES(this);
		case RANSUB:   return visitor.exitRANSUB(this);
		case UPTO:     return visitor.exitUPTO(this);
		case MINUS:    return visitor.exitMINUS(this);
		case DIV:      return visitor.exitDIV(this);
		case MOD:      return visitor.exitMOD(this);
		case EXPN:     return visitor.exitEXPN(this);
		case FUNIMAGE: return visitor.exitFUNIMAGE(this);
		case RELIMAGE: return visitor.exitRELIMAGE(this);
		default:       return true;
		}
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitBinaryExpression(this);		
	}
	
	private Predicate getWDPredicateDIV(FormulaFactory formulaFactory) {
		Predicate leftConjunct = left.getWDPredicateRaw(formulaFactory);
		Predicate rightConjunct = right.getWDPredicateRaw(formulaFactory);
		Expression zero =  formulaFactory.makeIntegerLiteral(BigInteger.ZERO, null);
		Predicate extraConjunct = formulaFactory.makeRelationalPredicate(NOTEQUAL, right, zero, null);
		return 
		getWDSimplifyC(formulaFactory,
				getWDSimplifyC(formulaFactory, leftConjunct, rightConjunct),
				extraConjunct);
	}

	private Predicate getWDPredicateMOD(FormulaFactory ff) {
		Predicate leftConjunct = left.getWDPredicateRaw(ff);
		Predicate rightConjunct = right.getWDPredicateRaw(ff);
		
		Expression zero =  ff.makeIntegerLiteral(BigInteger.ZERO, null);
		Predicate leftWD = ff.makeRelationalPredicate(LE, zero, left, null);
		Predicate rightWD = ff.makeRelationalPredicate(LT, zero, right, null);
		return getWDSimplifyC(ff, getWDSimplifyC(ff, getWDSimplifyC(ff,
				leftConjunct, rightConjunct), leftWD), rightWD);
	}

	private Predicate getWDPredicateEXPN(FormulaFactory ff) {
		Predicate leftConjunct = left.getWDPredicateRaw(ff);
		Predicate rightConjunct = right.getWDPredicateRaw(ff);
		Expression zero = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		Predicate leftNotZero = ff.makeRelationalPredicate(LE, zero, left, null);
		Predicate rightNotZero = ff.makeRelationalPredicate(LE, zero, right, null);
		return
		getWDSimplifyC(ff,
				getWDSimplifyC(ff,
						getWDSimplifyC(ff, leftConjunct, rightConjunct),
						leftNotZero),
						rightNotZero);
	}

	private Predicate getWDPredicateFUNIMAGE(FormulaFactory ff) {
		final Predicate leftConjunct = left.getWDPredicateRaw(ff);
		final Predicate rightConjunct = right.getWDPredicateRaw(ff);
		final Expression dom = ff.makeUnaryExpression(KDOM, left, null);
		final Predicate inDom = ff.makeRelationalPredicate(IN, right, dom, null);
		
		final Expression src = left.getType().getSource().toExpression(ff);
		final Expression trg = left.getType().getTarget().toExpression(ff);
		final Expression pfun = ff.makeBinaryExpression(PFUN, src, trg, null);
		final Predicate isPFun = ff.makeRelationalPredicate(IN, left, pfun, null);
		
		return getWDSimplifyC(ff,
				getWDSimplifyC(ff,
						getWDSimplifyC(ff, leftConjunct, rightConjunct),
						inDom),
						isPFun);
	}

	@Override
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		switch (getTag()) {
		case DIV:      return getWDPredicateDIV(formulaFactory);
		case MOD:      return getWDPredicateMOD(formulaFactory);
		case EXPN:     return getWDPredicateEXPN(formulaFactory);
		case FUNIMAGE: return getWDPredicateFUNIMAGE(formulaFactory);
		default:
			Predicate leftConjunct = left.getWDPredicateRaw(formulaFactory);
			Predicate rightConjunct = right.getWDPredicateRaw(formulaFactory);
			return getWDSimplifyC(formulaFactory, leftConjunct, rightConjunct);
		}
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final Expression newLeft = left.rewrite(rewriter);
		final Expression newRight = right.rewrite(rewriter);
		final BinaryExpression before;
		if (newLeft == left && newRight == right) {
			before = this;
		} else {
			before = rewriter.getFactory().makeBinaryExpression(getTag(),
					newLeft, newRight, getSourceLocation());
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	public boolean isATypeExpression() {
		int tag = getTag();
		return (tag == CPROD || tag == REL)
				&& left.isATypeExpression()
				&& right.isATypeExpression();
	}

	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		Type leftAsType = left.toType(factory);
		Type rightAsType = right.toType(factory);
		Type result = factory.makeProductType(leftAsType, rightAsType);
		switch (getTag()) {
		case CPROD:
			return result;
		case REL:
			return factory.makePowerSetType(result);
		default:
			throw new InvalidExpressionException();
		}
	}
	
	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		left.addGivenTypes(set);
		right.addGivenTypes(set);
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		left.getPositions(filter, indexes, positions);
		indexes.incrementTop();
		right.getPositions(filter, indexes, positions);
		indexes.pop();
	}

	@Override
	protected Formula<Expression> getChild(int index) {
		switch (index) {
		case 0:
			return left;
		case 1:
			return right;
		default:
			return null;
		}
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		IPosition pos;
		indexes.push(0);
		pos = left.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.incrementTop();
		pos = right.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		Expression newLeft = left;
		Expression newRight = right;
		switch (index) {
		case 0:
			newLeft = rewriter.rewrite(left);
			break;
		case 1:
			newRight = rewriter.rewrite(right);
			break;
		default:
			throw new IllegalArgumentException("Position is outside the formula");
		}
		return rewriter.factory.makeBinaryExpression(getTag(), newLeft, newRight,
				getSourceLocation());
	}

}

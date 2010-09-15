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
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.BinaryExpression.MINUS_ID;
import static org.eventb.internal.core.parser.BMath.ARITHMETIC;
import static org.eventb.internal.core.parser.BMath.BOUND_UNARY;
import static org.eventb.internal.core.parser.BMath.UNARY_RELATION;

import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers;
import org.eventb.internal.core.parser.SubParsers.ConverseParser;
import org.eventb.internal.core.parser.SubParsers.UnaryExpressionParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * UnaryExpression is the base class for all unary expressions in an event-B
 * formula.
 * <p>
 * It can accept tags {KCARD, POW, POW1, KUNION, KINTER, KDOM, KRAN, KPRJ1,
 * KPRJ2, KID, KMIN, KMAX, CONVERSE, UNMINUS}.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class UnaryExpression extends Expression {

	
	protected final Expression child;

	// offset in the corresponding tag interval
	private static final int FIRST_TAG = FIRST_UNARY_EXPRESSION;
	
	private static final String KCARD_ID = "Cardinal";
	private static final String POW_ID = "Power Set";
	private static final String POW1_ID = "Powerset 1";
	private static final String KUNION_ID = "Unary Union";
	private static final String KINTER_ID = "Unary Intersection";
	private static final String KDOM_ID = "Domain";
	private static final String KRAN_ID = "Range";
	private static final String KPRJ1_ID = "Old Projection 1";
	private static final String KPRJ2_ID = "Old Projection 2";
	private static final String KID_ID = "Old Identity";
	private static final String KMIN_ID = "Min";
	private static final String KMAX_ID = "Max";

	/**
	 * @since 2.0
	 */
	public static final String CONVERSE_ID = "Converse";

	@SuppressWarnings("deprecation")
	private static enum Operators implements IOperatorInfo<UnaryExpression> {
		OP_KCARD("card", KCARD_ID, BOUND_UNARY, KCARD),
		OP_POW("\u2119", POW_ID, BOUND_UNARY, POW),
		OP_POW1("\u21191", POW1_ID, BOUND_UNARY, POW1),
		OP_KUNION("union", KUNION_ID, BOUND_UNARY, KUNION),
		OP_KINTER("inter", KINTER_ID, BOUND_UNARY, KINTER),
		OP_KDOM("dom", KDOM_ID, BOUND_UNARY, KDOM),
		OP_KRAN("ran", KRAN_ID, BOUND_UNARY, KRAN),
		OP_KPRJ1("prj1", KPRJ1_ID, BOUND_UNARY, KPRJ1),
		OP_KPRJ2("prj2", KPRJ2_ID, BOUND_UNARY, KPRJ2),
		OP_KID("id", KID_ID, BOUND_UNARY, KID),
		OP_KMIN("min", KMIN_ID, BOUND_UNARY, KMIN),
		OP_KMAX("max", KMAX_ID, BOUND_UNARY, KMAX),
		OP_CONVERSE("\u223c", CONVERSE_ID, UNARY_RELATION, CONVERSE) {
			@Override
			public IParserPrinter<UnaryExpression> makeParser(int kind) {
				return new ConverseParser(kind);
			}
		},
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		private final int tag;
		
		private Operators(String image, String id, String groupId, int tag) {
			this.image = image;
			this.id = id;
			this.groupId = groupId;
			this.tag = tag;
		}

		@Override
		public String getImage() {
			return image;
		}
		
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public String getGroupId() {
			return groupId;
		}

		@Override
		public IParserPrinter<UnaryExpression> makeParser(int kind) {
			return new UnaryExpressionParser(kind, tag);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
	}

	private static final IOperatorInfo<Expression> OP_MINUS = new IOperatorInfo<Expression>() {
		
		@Override
		public IParserPrinter<Expression> makeParser(int kind) {
			return new SubParsers.UnminusParser(kind);
		}

		@Override
		public String getImage() {
			return "\u2212";
		}
		
		@Override
		public String getId() {
			return MINUS_ID;
		}
		
		@Override
		public String getGroupId() {
			return ARITHMETIC;
		}

		@Override
		public boolean isSpaced() {
			return false;
		}
	};
	
	// For testing purposes
	public static final int TAGS_LENGTH = Operators.values().length + 1;

	private static void initCommon(BMath grammar) {
		try {
			grammar.addOperator(Operators.OP_KCARD);
			grammar.addOperator(Operators.OP_POW);
			grammar.addOperator(Operators.OP_POW1);
			grammar.addOperator(Operators.OP_KUNION);
			grammar.addOperator(Operators.OP_KINTER);
			grammar.addOperator(Operators.OP_KDOM);
			grammar.addOperator(Operators.OP_KRAN);
			grammar.addOperator(Operators.OP_KMIN);
			grammar.addOperator(Operators.OP_KMAX);
			grammar.addOperator(Operators.OP_CONVERSE);
			grammar.addOperator(OP_MINUS);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public static void initV1(BMath grammar) {
		try {		
			initCommon(grammar);
			grammar.addOperator(Operators.OP_KPRJ1);
			grammar.addOperator(Operators.OP_KPRJ2);
			grammar.addOperator(Operators.OP_KID);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public static void initV2(BMath grammar) {
		initCommon(grammar);
	}

	protected UnaryExpression(Expression child, int tag, SourceLocation location,
			FormulaFactory factory) {
		
		super(tag, location, child.hashCode());
		this.child = child;

		assert tag >= FIRST_TAG && tag < FIRST_TAG+TAGS_LENGTH;
		assert child != null;
		
		setPredicateVariableCache(this.child);
		synthesizeType(factory, null);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		this.freeIdents = child.freeIdents;
		this.boundIdents = child.boundIdents;

		// Fast exit if children are not typed
		// (the most common case where type synthesis can't be done)
		if (! child.isTypeChecked()) {
			return;
		}
		
		Type childType = child.getType();
		final Type resultType;
		final Type alpha, beta;
		switch (getTag()) {
		case Formula.UNMINUS:
			if (childType instanceof IntegerType) {
				resultType = ff.makeIntegerType();
			} else {
				resultType = null;
			}
			break;
		case Formula.CONVERSE:
			alpha = childType.getSource();
			beta = childType.getTarget();
			if (alpha != null) {
				resultType = ff.makeRelationalType(beta, alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KCARD:
			alpha = childType.getBaseType();
			if (alpha != null) {
				resultType = ff.makeIntegerType();
			} else {
				resultType = null;
			}
			break;
		case Formula.POW:
		case Formula.POW1:
			alpha = childType.getBaseType();
			if (alpha != null) {
				resultType = ff.makePowerSetType(childType);
			} else {
				resultType = null;
			}
			break;
		case Formula.KUNION:
		case Formula.KINTER:
			final Type baseType = childType.getBaseType();
			if (baseType != null && baseType.getBaseType() != null) {
				resultType = baseType;
			} else {
				resultType = null;
			}
			break;
		case Formula.KDOM:
			alpha = childType.getSource();
			if (alpha != null) {
				resultType = ff.makePowerSetType(alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KRAN:
			beta = childType.getTarget();
			if (beta != null) {
				resultType = ff.makePowerSetType(beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.KPRJ1:
			alpha = childType.getSource();
			beta = childType.getTarget();
			if (alpha != null) {
				resultType = ff.makeRelationalType(
						ff.makeProductType(alpha, beta),
						alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KPRJ2:
			alpha = childType.getSource();
			beta = childType.getTarget();
			if (alpha != null) {
				resultType = ff.makeRelationalType(
						ff.makeProductType(alpha, beta),
						beta);
			} else {
				resultType = null;
			}
			break;
		case Formula.KID:
			alpha = childType.getBaseType();
			if (alpha != null) {
				resultType = ff.makeRelationalType(alpha, alpha);
			} else {
				resultType = null;
			}
			break;
		case Formula.KMIN:
		case Formula.KMAX:
			alpha = childType.getBaseType();
			if (alpha instanceof IntegerType) {
				resultType = alpha;
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


	/**
	 * Returns the unique child of this node.
	 * 
	 * @return child of this node.
	 */
	public Expression getChild() {
		return child;
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		UnaryExpression otherExpr = (UnaryExpression) other;
		return hasSameType(other)
				&& child.equals(otherExpr.child, withAlphaConversion);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {
		child.typeCheck(result,quantifiedIdentifiers);
		
		final TypeVariable alpha, beta;
		final Type resultType;
		switch (getTag()) {
		case Formula.UNMINUS:
			resultType = result.makeIntegerType();
			result.unify(child.getType(), resultType, this);
			break;
		case Formula.CONVERSE:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makeRelationalType(beta, alpha);
			break;
		case Formula.KCARD:
			alpha = result.newFreshVariable(null);
			result.unify(child.getType(), result.makePowerSetType(alpha), this);
			resultType = result.makeIntegerType();
			break;
		case Formula.POW:
		case Formula.POW1:
			alpha = result.newFreshVariable(null);
			Type childPattern = result.makePowerSetType(alpha);
			result.unify(child.getType(), childPattern, this);
			resultType = result.makePowerSetType(childPattern);
			break;
		case Formula.KUNION:
		case Formula.KINTER:
			alpha = result.newFreshVariable(null);
			resultType = result.makePowerSetType(alpha);
			result.unify(child.getType(), result.makePowerSetType(resultType), this);
			break;
		case Formula.KDOM:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makePowerSetType(alpha);
			break;
		case Formula.KRAN:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makePowerSetType(beta);
			break;
		case Formula.KPRJ1:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makeRelationalType(result.makeProductType(alpha, beta), alpha);
			break;
		case Formula.KPRJ2:
			alpha = result.newFreshVariable(null);
			beta = result.newFreshVariable(null);
			result.unify(child.getType(), result.makeRelationalType(alpha, beta), this);
			resultType = result.makeRelationalType(result.makeProductType(alpha, beta), beta);
			break;
		case Formula.KID:
			alpha = result.newFreshVariable(null);
			result.unify(child.getType(), result.makePowerSetType(alpha), this);
			resultType = result.makeRelationalType(alpha, alpha);
			break;
		case Formula.KMIN:
		case Formula.KMAX:
			resultType = result.makeIntegerType();
			result.unify(child.getType(), result.makePowerSetType(resultType), this);
			break;
		default:
			assert false;
			return;
		}
		setTemporaryType(resultType, result);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		return child.solveType(unifier);
	}
	
	private String getOperatorImage() {
		if (getTag() == UNMINUS) {
			return OP_MINUS.getImage();
		}
		return getOperator().getImage();
	}

	private Operators getOperator() {
		assert	getTag() != UNMINUS;
		return Operators.values()[getTag() - FIRST_TAG];
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final int kind = mediator.getKind();
		if (getTag() == UNMINUS) {
			OP_MINUS.makeParser(kind).toString(mediator, this);
			return;
		}
		final Operators operator = getOperator();
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		return tabs + this.getClass().getSimpleName() + " [" + getOperatorImage()
				+ "]" + getTypeName() + "\n"
				+ child.getSyntaxTree(boundNames, tabs + "\t");
	}

	@Override
	protected void isLegible(LegibilityResult result) {
		child.isLegible(result);
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		child.collectFreeIdentifiers(freeIdentSet);
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		child.collectNamesAbove(names, boundNames, offset);
	}
	
	@Override
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		Expression newChild = child.bindTheseIdents(binding, offset, factory);
		if (newChild == child) {
			return this;
		}
		return factory.makeUnaryExpression(getTag(), newChild, getSourceLocation());
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean accept(IVisitor visitor) {
		boolean goOn = true;

		switch (getTag()) {
		case KCARD:    goOn = visitor.enterKCARD(this);    break;
		case POW:      goOn = visitor.enterPOW(this);      break;
		case POW1:     goOn = visitor.enterPOW1(this);     break;
		case KUNION:   goOn = visitor.enterKUNION(this);   break;
		case KINTER:   goOn = visitor.enterKINTER(this);   break;
		case KDOM:     goOn = visitor.enterKDOM(this);     break;
		case KRAN:     goOn = visitor.enterKRAN(this);     break;
		case KPRJ1:    goOn = visitor.enterKPRJ1(this);    break;
		case KPRJ2:    goOn = visitor.enterKPRJ2(this);    break;
		case KID:      goOn = visitor.enterKID(this);      break;
		case KMIN:     goOn = visitor.enterKMIN(this);     break;
		case KMAX:     goOn = visitor.enterKMAX(this);     break;
		case CONVERSE: goOn = visitor.enterCONVERSE(this); break;
		case UNMINUS:  goOn = visitor.enterUNMINUS(this);  break;
		default:       assert false;
		}

		if (goOn) goOn = child.accept(visitor);
		
		switch (getTag()) {
		case KCARD:    return visitor.exitKCARD(this);
		case POW:      return visitor.exitPOW(this);
		case POW1:     return visitor.exitPOW1(this);
		case KUNION:   return visitor.exitKUNION(this);
		case KINTER:   return visitor.exitKINTER(this);
		case KDOM:     return visitor.exitKDOM(this);
		case KRAN:     return visitor.exitKRAN(this);
		case KPRJ1:    return visitor.exitKPRJ1(this);
		case KPRJ2:    return visitor.exitKPRJ2(this);
		case KID:      return visitor.exitKID(this);
		case KMIN:     return visitor.exitKMIN(this);
		case KMAX:     return visitor.exitKMAX(this);
		case CONVERSE: return visitor.exitCONVERSE(this);
		case UNMINUS:  return visitor.exitUNMINUS(this);
		default:       return true;
		}
	}
	
	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitUnaryExpression(this);
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final Expression newChild = child.rewrite(rewriter);

		final FormulaFactory ff = rewriter.getFactory();
		final SourceLocation sloc = getSourceLocation();
		if (getTag() == Formula.UNMINUS && newChild.getTag() == Formula.INTLIT
				&& rewriter.autoFlatteningMode()) {
			BigInteger value = ((IntegerLiteral) newChild).getValue();
			IntegerLiteral before = ff.makeIntegerLiteral(value.negate(), sloc);
			return checkReplacement(rewriter.rewrite(before));
		}

		final UnaryExpression before;
		if (newChild == child) {
			before = this;
		} else {
			before = ff.makeUnaryExpression(getTag(), newChild, sloc);
		}
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	public boolean isATypeExpression() {
		return getTag() == POW && child.isATypeExpression();
	}

	@Deprecated
	@Override
	public Type toType(FormulaFactory factory) throws InvalidExpressionException {
		if (getTag() != POW)
			throw new InvalidExpressionException();
		Type childAsType = child.toType(factory);
		return factory.makePowerSetType(childAsType);
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		child.addGivenTypes(set);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		acc.enterChildren();
		child.inspect(acc);
		acc.leaveChildren();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index == 0) {
			return child;
		}
		return null;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		IPosition pos = child.getPosition(sloc, indexes);
		if (pos != null)
			return pos;
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index != 0) 
			throw new IllegalArgumentException("Position is outside the formula");
		Expression newChild = rewriter.rewrite(child);
		return rewriter.factory.makeUnaryExpression(getTag(), newChild,
				getSourceLocation());
	}

}

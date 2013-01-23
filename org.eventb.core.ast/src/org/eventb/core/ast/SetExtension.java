/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *     Systerel - added support for predicate variables
 *     Systerel - generalised getPositions() into inspect()
 *     Systerel - externalized wd lemmas generation
 *     Systerel - added child indexes
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.extension.StandardGroup.BRACE_SETS;
import static org.eventb.internal.core.parser.AbstractGrammar.DefaultToken.LBRACE;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.StandardGroup;
import org.eventb.internal.core.ast.FindingAccumulator;
import org.eventb.internal.core.ast.ITypeCheckingRewriter;
import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.ast.extension.IToStringMediator;
import org.eventb.internal.core.ast.extension.KindMediator;
import org.eventb.internal.core.parser.BMath;
import org.eventb.internal.core.parser.GenParser.OverrideException;
import org.eventb.internal.core.parser.IOperatorInfo;
import org.eventb.internal.core.parser.IParserPrinter;
import org.eventb.internal.core.parser.SubParsers.SetExtParser;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeUnifier;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * SetExtension is the base class for a set extension in an event-B formula.
 * <p>
 * It can accept tag {SETEXT}. It has an array of expressions for its child.
 * </p>
 * 
 * @author François Terrier
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SetExtension extends Expression {

	private static final String SETEXT_ID = "Set Extension";
	private static enum Operators implements IOperatorInfo<SetExtension> {
		OP_SETEXT(LBRACE.getImage(), SETEXT_ID, BRACE_SETS),
		;
		
		private final String image;
		private final String id;
		private final String groupId;
		
		private Operators(String image, String id, StandardGroup group) {
			this.image = image;
			this.id = id;
			this.groupId = group.getId();
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
		public IParserPrinter<SetExtension> makeParser(int kind) {
			return new SetExtParser(kind);
		}

		@Override
		public boolean isSpaced() {
			return false;
		}

	}

	/**
	 * @since 2.0
	 */
	public static void init(BMath grammar) {
		try {		
			grammar.addOperator(Operators.OP_SETEXT);
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// children
	private final Expression[] members;
	
	/**
	 * @since 3.0
	 */
	protected SetExtension(Expression[] expressions, SourceLocation location,
			FormulaFactory factory, Type type) {
		super(SETEXT, location, combineHashCodes(expressions));
		this.members = expressions;

		checkPreconditions();
		setPredicateVariableCache(this.members);
		synthesizeType(factory, type);

		// ensures that type was coherent (final type cannot be null if given
		// type was not)
		assert type == null || type == this.getType();
	}

	// Common initialization.
	private void checkPreconditions() {
		assert getTag() == SETEXT;
		assert members != null;
	}
	
	@Override
	protected void synthesizeType(FormulaFactory ff, Type givenType) {
		IdentListMerger freeIdentMerger = mergeFreeIdentifiers(members);
		this.freeIdents = freeIdentMerger.getFreeMergedArray();

		IdentListMerger boundIdentMerger = mergeBoundIdentifiers(members);
		this.boundIdents = boundIdentMerger.getBoundMergedArray();

		if (freeIdentMerger.containsError() || boundIdentMerger.containsError()) {
			// Incompatible type environments, don't bother going further.
			return;
		}
		
		final int length = members.length;
		final Type resultType;
		if (length == 0) {
			// Empty set, no way to synthesize its type.
			if (givenType == null) {
				return;
			}
			assert givenType instanceof PowerSetType;
			resultType = givenType;
			if (!mergeGivenTypes(resultType, ff)) {
				// Incompatible type environments, don't set the type
				return;
			}
		} else {
			final Type memberType = members[0].getType();
			if (memberType == null) {
				return;
			}
			for (int i = 1; i < length; i++) {
				if (! memberType.equals(members[i].getType())) {
					return;
				}
			}
			resultType = ff.makePowerSetType(memberType);
		}
		setFinalType(resultType, givenType);
	}

	/**
	 * Returns the members of this set.
	 * 
	 * @return an array of expressions. It can be empty but not <code>null</code>.
	 */
	public Expression[] getMembers() {
		return members.clone();
	}

	private Operators getOperator() {
		return Operators.OP_SETEXT;
	}

	private String getOperatorImage() {
		return getOperator().getImage();
	}

	@Override
	protected void toString(IToStringMediator mediator) {
		final Operators operator = getOperator();
		final int kind = mediator.getKind();
		
		operator.makeParser(kind).toString(mediator, this);
	}

	@Override
	protected int getKind(KindMediator mediator) {
		return mediator.getKind(getOperatorImage());
	}

	@Override
	protected String getSyntaxTree(String[] boundNames, String tabs) {
		StringBuffer str = new StringBuffer();
		final String typeName = getType()!=null?" [type: "+getType().toString()+"]":"";
		str.append(tabs + this.getClass().getSimpleName() + " [SETEXT]"  + typeName + "\n");
		
		for (int i=0;i<members.length;i++) {
			str.append(members[i].getSyntaxTree(boundNames, tabs+"\t"));
		}
		
		return str.toString();
	}
	
	/**
	 * @since 2.0
	 */
	@Override
	protected void isLegible(LegibilityResult result) {
		for (int i=0; i<members.length;i++) {
			members[i].isLegible(result);
		}
	}
	
	@Override
	boolean equalsInternalExpr(Expression expr) {
		final SetExtension other = (SetExtension) expr;
		return Arrays.equals(members, other.members);
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {

		// Build the type variable for the elements of this set
		// Give a source location only if this set is empty.
		final SourceLocation alphaLoc = (members.length == 0) ? getSourceLocation() : null;
		final TypeVariable alpha = result.newFreshVariable(alphaLoc);
		
		for (Expression member : members) {
			member.typeCheck(result, quantifiedIdentifiers);
			result.unify(member.getType(), alpha, this);
		}
		setTemporaryType(result.makePowerSetType(alpha), result);
		result.analyzeExpression(this);
	}
	
	@Override
	protected boolean solveChildrenTypes(TypeUnifier unifier) {
		boolean success = true;
		for (Expression member : members) {
			success &= member.solveType(unifier);
		}
		return success;
	}

	@Override
	protected void collectFreeIdentifiers(LinkedHashSet<FreeIdentifier> freeIdentSet) {
		for (Expression child: members) {
			child.collectFreeIdentifiers(freeIdentSet);
		}
	}

	@Override
	protected void collectNamesAbove(Set<String> names, String[] boundNames, int offset) {
		for (Expression member: members) {
			member.collectNamesAbove(names, boundNames, offset);
		}
	}

	@Override
	public boolean accept(IVisitor visitor) {
		boolean goOn = visitor.enterSETEXT(this);

		for (int i = 0; goOn && i < members.length; i++) {
			if (i != 0) {
				goOn = visitor.continueSETEXT(this);
			}
			if (goOn) { 
				goOn = members[i].accept(visitor);
			}
		}
		
		return visitor.exitSETEXT(this);
	}

	@Override
	public void accept(ISimpleVisitor visitor) {
		visitor.visitSetExtension(this);
	}

	@Override
	protected Expression rewrite(ITypeCheckingRewriter rewriter) {
		final int length = members.length;
		final FormulaFactory ff = rewriter.getFactory();
		final SourceLocation sloc = getSourceLocation();

		if (length == 0 && rewriter.autoFlatteningMode()) {
			final AtomicExpression before = ff.makeEmptySet(getType(), sloc);
			return rewriter.rewrite(this, before);
		}
		boolean changed = false;
		final Expression[] newMembers = new Expression[length];
		for (int i = 0; i < length; i++) {
			final Expression member = members[i];
			final Expression newMember = member.rewrite(rewriter);
			newMembers[i] = newMember;
			changed |= newMember != member;
		}
		final SetExtension before;
		if (!changed) {
			before = this;
		} else {
			before = ff.makeSetExtension(newMembers, sloc);
		}
		return rewriter.rewrite(this, before);
	}

	@Override
	protected final <F> void inspect(FindingAccumulator<F> acc) {
		acc.inspect(this);
		if (acc.childrenSkipped()) {
			return;
		}
		acc.enterChildren();
		for (Expression member: members) {
			member.inspect(acc);
			if (acc.allSkipped()) {
				break;
			}
			acc.nextChild();
		}
		acc.leaveChildren();
	}

	@Override
	public Expression getChild(int index) {
		checkChildIndex(index);
		return members[index];
	}

	@Override
	public int getChildCount() {
		return members.length;
	}

	@Override
	protected IPosition getDescendantPos(SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		for (Expression member: members) {
			IPosition pos = member.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		indexes.pop();
		return new Position(indexes);
	}

	@Override
	protected Expression rewriteChild(int index, SingleRewriter rewriter) {
		if (index < 0 || members.length <= index) 
			throw new IllegalArgumentException("Position is outside the formula");
		Expression[] newMembers = members.clone();
		newMembers[index] = rewriter.rewrite(members[index]);
		return rewriter.factory.makeSetExtension(newMembers,
				getSourceLocation());
	}

	@Override
	public boolean isWDStrict() {
		return true;
	}

}

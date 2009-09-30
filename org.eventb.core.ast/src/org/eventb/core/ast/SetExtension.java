/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added accept for ISimpleVisitor
 *******************************************************************************/
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
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
 */
public class SetExtension extends Expression {

	// children
	private final Expression[] members;
	
	protected SetExtension(Expression expression, SourceLocation location,
			FormulaFactory factory) {
		super(SETEXT, location, expression.hashCode());
		this.members = new Expression[] {expression};

		checkPreconditions();
		synthesizeType(factory, null);
	}

	protected SetExtension(Expression[] expressions, SourceLocation location,
			FormulaFactory factory) {
		super(SETEXT, location, combineHashCodes(expressions));
		this.members = expressions.clone();

		checkPreconditions();
		synthesizeType(factory, null);
	}

	protected SetExtension(Collection<? extends Expression> expressions,
			SourceLocation location, FormulaFactory factory) {
		super(SETEXT, location, combineHashCodes(expressions));
		Expression[] temp = new Expression[expressions.size()];
		this.members = expressions.toArray(temp);

		checkPreconditions();
		synthesizeType(factory, null);
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

	@Override
	protected void toString(StringBuilder builder, boolean isRightChild,
			int parentTag, String[] boundNames, boolean withTypes) {

		// Might be a typed empty set
		if (withTypes && members.length == 0 && isTypeChecked()) {
			builder.append("(\u2205 \u2982 ");
			builder.append(getType());
			builder.append(')');
		} else {
			builder.append('{');
			String sep = "";
			for (Expression member : members) {
				builder.append(sep);
				sep = ",";
				member.toString(builder, false, getTag(), boundNames, withTypes);
			}
			builder.append('}');
		}
	}

	@Override
	protected void toStringFullyParenthesized(StringBuilder builder,
			String[] boundNames) {

		builder.append('{');
		String sep = "";
		for (Expression member : members) {
			builder.append(sep);
			sep = ",";
			member.toStringFullyParenthesized(builder, boundNames);
		}
		builder.append('}');
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
	
	@Override
	protected void isLegible(LegibilityResult result, BoundIdentDecl[] quantifiedIdents) {
		for (int i=0; i<members.length;i++) {
			members[i].isLegible(result, quantifiedIdents);
			if (!result.isSuccess()) {
				return;
			}
		}
	}
	
	@Override
	protected boolean equals(Formula<?> other, boolean withAlphaConversion) {
		if (this.getTag() != other.getTag()) {
			return false;
		}
		return hasSameType(other)
				&& equalsHelper(members, ((SetExtension) other).members,
						withAlphaConversion);
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
	protected Expression bindTheseIdents(Map<String, Integer> binding, int offset, FormulaFactory factory) {
		boolean changed = false;
		Expression[] newMembers = new Expression[members.length];
		for (int i = 0; i < members.length; i++) {
			newMembers[i] = members[i].bindTheseIdents(binding, offset, factory);
			changed |= newMembers[i] != members[i];
		}
		if (! changed) {
			return this;
		}
		return factory.makeSetExtension(newMembers, getSourceLocation());
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
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, members);
	}

	@Override
	public Expression rewrite(IFormulaRewriter rewriter) {
		final int length = members.length;
		final FormulaFactory ff = rewriter.getFactory();
		final SourceLocation sloc = getSourceLocation();

		if (length == 0 && rewriter.autoFlatteningMode()) {
			AtomicExpression before = ff.makeEmptySet(getType(), sloc);
			return checkReplacement(rewriter.rewrite(before));
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
		return checkReplacement(rewriter.rewrite(before));
	}

	@Override
	protected void addGivenTypes(Set<GivenType> set) {
		if (members.length == 0) {
			// Special case when the set is actually empty
			getType().addGivenTypes(set);
		} else {
			for (Expression member: members) {
				member.addGivenTypes(set);
			}
		}
	}

	@Override
	protected void getPositions(IFormulaFilter filter, IntStack indexes,
			List<IPosition> positions) {
		
		if (filter.select(this)) {
			positions.add(new Position(indexes));
		}

		indexes.push(0);
		for (Expression member: members) {
			member.getPositions(filter, indexes, positions);
			indexes.incrementTop();
		}
		indexes.pop();
	}

	@Override
	protected Formula<?> getChild(int index) {
		if (index < members.length) {
			return members[index];
		}
		return null;
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

}

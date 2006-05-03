/*
 * Created on 20-may-2005
 *
 */
package org.eventb.core.ast;

import static org.eventb.core.ast.AssociativeHelper.equalsHelper;
import static org.eventb.core.ast.AssociativeHelper.getSubstitutedList;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.core.ast.IdentListMerger;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Substitution;
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
		this.members = new Expression[expressions.length];
		System.arraycopy(expressions, 0, this.members, 0, expressions.length);

		checkPreconditions();
		synthesizeType(factory, null);
	}

	protected SetExtension(List<? extends Expression> expressions,
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
		Expression[] temp = new Expression[members.length];
		System.arraycopy(members, 0, temp, 0, members.length);
		return temp;
	}

	@Override
	protected String toString(boolean isRightChild, int parentTag,
			String[] boundNames, boolean withTypes) {

		// Might be a typed empty set
		if (withTypes && members.length == 0 && isTypeChecked()) {
			return "(\u2205 \u2982 " + getType() + ")";
		}
		
		StringBuffer str = new StringBuffer();
		str.append("{");
		if (members.length > 0) {
			str.append(members[0].toString(false, getTag(), boundNames, withTypes));
			for (int i=1; i<members.length;i++) {
				str.append(",");
				str.append(members[i].toString(false, getTag(), boundNames, withTypes));
			}
		}
		str.append("}");
		return str.toString();
	}

	@Override
	protected String toStringFullyParenthesized(String[] boundNames) {
		StringBuffer str = new StringBuffer();
		str.append("{");
		if (members.length > 0) {
			str.append(members[0].toStringFullyParenthesized(boundNames));
			for (int i=1; i<members.length;i++) {
				str.append(","+members[i].toStringFullyParenthesized(boundNames));
			}
		}
		str.append("}");
		return str.toString();
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
	protected boolean equals(Formula other, boolean withAlphaConversion) {
		return hasSameType(other)
				&& equalsHelper(members, ((SetExtension) other).members,
						withAlphaConversion);
	}

	@Override
	public Expression flatten(FormulaFactory factory) {
		if (members.length == 0) {
			return factory.makeEmptySet(getType(), getSourceLocation());
		}
		Expression[] newChildren = new Expression[members.length];
		boolean changed = false;
		for (int i=0;i<members.length;i++) {
			newChildren[i] = members[i].flatten(factory);
			changed |= (newChildren[i] != members[i]);
		}
		if (! changed) {
			return this;
		}
		return factory.makeSetExtension(newChildren, getSourceLocation());
	}

	@Override
	protected void typeCheck(TypeCheckResult result, BoundIdentDecl[] quantifiedIdentifiers) {

		// Build the type variable for the elements of this set
		// Give a source location only if this set is empty.
		final SourceLocation alphaLoc = (members.length == 0) ? getSourceLocation() : null;
		final TypeVariable alpha = result.newFreshVariable(alphaLoc);
		
		for (Expression member : members) {
			member.typeCheck(result, quantifiedIdentifiers);
			result.unify(member.getType(), alpha, getSourceLocation());
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
	protected Predicate getWDPredicateRaw(FormulaFactory formulaFactory) {
		return getWDConjunction(formulaFactory, members);
	}

	@Override
	protected boolean isWellFormed(int noOfBoundVars) {
		for(int i=0; i<members.length; i++)
			if(!members[i].isWellFormed(noOfBoundVars))
				return false;
		return true;
	}

	@Override
	public SetExtension applySubstitution(Substitution subst) {
		final FormulaFactory ff = subst.getFactory();
		Expression[] newMembers = new Expression[members.length];
		boolean equal = getSubstitutedList(members, subst, newMembers, ff);
		if (equal)
			return this;
		return ff.makeSetExtension(newMembers, getSourceLocation());
	}

}

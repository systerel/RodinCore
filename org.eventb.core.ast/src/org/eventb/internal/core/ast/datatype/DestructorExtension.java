/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static org.eventb.internal.core.ast.datatype.ConstructorPredicateBuilder.makeInConstructorDomain;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeGroup;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeId;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeKind;
import static org.eventb.internal.core.ast.datatype.TypeSubstitution.makeSubstitution;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * This class represents an extension used as destructor for a datatype. The
 * destructor child represents the datatype to destruct, then its child type
 * represent the datatype type in the
 * {@link #verifyType(Type, Expression[], Predicate[])},
 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} and
 * {@link #typeCheck(ExtendedExpression, ITypeCheckMediator)} methods.
 * <p>
 * This class must <strong>not</strong> override <code>equals</code> as this
 * would wreak havoc in formula factories. We rely on object identity for
 * identifying identical destructors.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class DestructorExtension extends ConstructorArgument implements
		IDestructorExtension {

	private final String name;
	private final String id;
	private final IExtensionKind kind;
	private final String groupId;

	public DestructorExtension(Datatype origin,
			ConstructorExtension constructor, String name, Type type) {
		super(constructor, type);
		this.name = name;
		this.id = computeId(name);
		int nbArgs = 1; // one argument (of type datatype)
		this.kind = computeKind(nbArgs);
		this.groupId = computeGroup(nbArgs);
	}

	@Override
	public boolean isDestructor() {
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	// formula is "destr(dt)"
	// WD is "# args . dt = constructor(args)"
	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		if (getOrigin().hasSingleConstructor()) {
			return wdMediator.makeTrueWD();
		}
		final Expression dtValue = formula.getChildExpressions()[0];
		return makeInConstructorDomain(dtValue, constructor);
	}

	@Override
	public String getSyntaxSymbol() {
		return name;
	}

	@Override
	public IExtensionKind getKind() {
		return kind;
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
	public void addPriorities(IPriorityMediator mediator) {
		// no priority
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// no compatibility
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		final Type childType = childExprs[0].getType();
		final TypeSubstitution subst = makeSubstitution(getOrigin(), childType);
		if (subst == null) {
			return null;
		}
		return getType(subst);
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		assert childExprs.length == 1;
		assert childPreds.length == 0;
		final Type childType = childExprs[0].getType();
		final TypeSubstitution subst = makeSubstitution(getOrigin(), childType);
		if (subst == null) {
			return false;
		}
		final Type expected = getType(subst);
		return expected.equals(proposedType);
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		final Type childType = expression.getChildExpressions()[0].getType();
		final TypeSubstitution subst = makeSubstitution(getOrigin(), tcMediator);
		tcMediator.sameType(childType, subst.getInstanceType());
		return getType(subst);
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * formalType.hashCode() + name.hashCode();
	}

	@Override
	public boolean isSimilarTo(ConstructorArgument other) {
		return super.isSimilarTo(other)
				&& this.name.equals(other.asDestructor().name);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	@Override
	public void toString(StringBuilder sb) {
		sb.append(name);
		sb.append(": ");
		super.toString(sb);
	}

}

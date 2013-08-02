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

import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeGroup;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeId;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeKind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.ITypeConstructorExtension;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * This class represents an extension used as type constructor for a datatype.
 * <p>
 * The type constructor type represents the datatype type and its children
 * corresponds to the datatype type parameters in the
 * {@link #verifyType(Type, Expression[], Predicate[])},
 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} and {
 * {@link #typeCheck(ExtendedExpression, ITypeCheckMediator)} methods.
 * </p>
 * <p>
 * This class must <strong>not</strong> override <code>equals</code> as this
 * would wreak havoc in formula factories. We rely on object identity for
 * identifying identical type constructors.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class TypeConstructorExtension implements ITypeConstructorExtension {

	private Datatype origin;
	private final String name;
	private final String[] formalNames;

	private final String id;
	private final String groupId;
	private final IExtensionKind kind;

	public TypeConstructorExtension(Datatype origin, DatatypeBuilder dtBuilder) {
		this.origin = origin;
		this.name = dtBuilder.getName();
		final GivenType[] typeParams = dtBuilder.getTypeParameters();
		this.formalNames = new String[typeParams.length];
		for (int i = 0; i < typeParams.length; i++) {
			formalNames[i] = typeParams[i].getName();
		}
		this.id = computeId(name);
		this.groupId = computeGroup(typeParams.length);
		this.kind = computeKind(typeParams.length);
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return wdMediator.makeTrueWD();
	}

	public String getName() {
		return name;
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
		// no priority
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		final List<Type> prmTypes = new ArrayList<Type>();
		for (Expression child : expression.getChildExpressions()) {
			final Type alpha = tcMediator.newTypeVariable();
			final PowerSetType prmType = tcMediator.makePowerSetType(alpha);
			tcMediator.sameType(prmType, child.getType());
			prmTypes.add(alpha);
		}
		return tcMediator.makePowerSetType(tcMediator.makeParametricType(
				prmTypes, this));
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		final List<Type> prmTypes = new ArrayList<Type>();
		for (Expression child : childExprs) {
			final Type childType = child.getType();
			if (childType == null) {
				return null;
			}
			final Type baseType = childType.getBaseType();
			if (baseType == null) {
				return null;
			}
			prmTypes.add(baseType);
		}
		return mediator.makePowerSetType(mediator.makeParametricType(prmTypes,
				this));
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		final Type baseType = proposedType.getBaseType();
		if (!(baseType instanceof ParametricType)) {
			return false;
		}
		final ParametricType genType = (ParametricType) baseType;
		if (genType.getExprExtension() != this) {
			return false;
		}
		final Type[] typeParameters = genType.getTypeParameters();
		assert childExprs.length == typeParameters.length;
		for (int i = 0; i < childExprs.length; i++) {
			final Type childType = childExprs[i].getType();
			if (!typeParameters[i].equals(childType.getBaseType())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isATypeConstructor() {
		return true;
	}

	@Override
	public Datatype getOrigin() {
		return origin;
	}

	@Override
	public String[] getFormalNames() {
		return formalNames.clone();
	}

	public int getNbParams() {
		return formalNames.length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * name.hashCode() + Arrays.hashCode(formalNames);
	}

	/*
	 * Implements pseudo-equality, that is equality up to datatype identity.
	 */
	public boolean isSimilarTo(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final TypeConstructorExtension other = (TypeConstructorExtension) obj;
		return this.name.equals(other.name)
				&& Arrays.equals(this.formalNames, other.formalNames);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		sb.append(name);
		String sep = "[";
		for (final String formalName : formalNames) {
			sb.append(sep);
			sep = ", ";
			sb.append(formalName);
		}
		if (formalNames.length != 0) {
			sb.append("]");
		}
	}

}

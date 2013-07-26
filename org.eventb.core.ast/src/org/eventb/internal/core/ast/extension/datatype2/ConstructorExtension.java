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
package org.eventb.internal.core.ast.extension.datatype2;

import static org.eventb.internal.core.ast.extension.datatype2.DatatypeHelper.computeGroup;
import static org.eventb.internal.core.ast.extension.datatype2.DatatypeHelper.computeId;
import static org.eventb.internal.core.ast.extension.datatype2.DatatypeHelper.computeKind;
import static org.eventb.internal.core.ast.extension.datatype2.SetSubstitution.makeSubstitution;
import static org.eventb.internal.core.ast.extension.datatype2.TypeSubstitution.makeSubstitution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype2.IConstructorExtension;
import org.eventb.core.ast.extension.datatype2.IDestructorExtension;

/**
 * This class represents an extension used as constructor for a datatype.
 * 
 * <p>
 * The constructor type represents the datatype type and its children correspond
 * to its arguments in the {@link #verifyType(Type, Expression[], Predicate[])},
 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} and {
 * {@link #typeCheck(ExtendedExpression, ITypeCheckMediator)} methods.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class ConstructorExtension implements IConstructorExtension {

	private final Datatype2 origin;
	private final String name;
	private final List<DatatypeArgument> arguments;
	private final String id;
	private final IExtensionKind kind;
	private final String groupId;

	private final List<IDestructorExtension> argumentsExt;
	private Set<IFormulaExtension> extensions;
	private HashMap<String, DestructorExtension> destructors;

	public ConstructorExtension(Datatype2 origin, GivenType datatypeType,
			List<GivenType> typeParams, String name,
			List<DatatypeArgument> arguments) {
		this.origin = origin;
		this.name = name;
		this.id = computeId(name);
		this.arguments = arguments;
		int nbArgs = arguments.size();
		this.groupId = computeGroup(nbArgs);
		this.kind = computeKind(nbArgs);
		this.argumentsExt = new ArrayList<IDestructorExtension>(nbArgs);
		this.extensions = new HashSet<IFormulaExtension>(nbArgs);
		this.destructors = new HashMap<String, DestructorExtension>(nbArgs);
		for (DatatypeArgument arg : arguments) {
			DestructorExtension destrExt = arg.finalizeConstructorArgument(
					origin, this);
			argumentsExt.add(destrExt);
			if (arg.hasDestructor()) {
				extensions.add(destrExt);
				destructors.put(destrExt.getName(), destrExt);
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type[] getArgumentTypes(Type returnType) {
		final TypeSubstitution subst = makeSubstitution(origin, returnType);
		if (subst == null) {
			throw new IllegalArgumentException("The return type: " + returnType
					+ " is not compatible with the constructor: " + this
					+ " of the datatype: " + origin.getTypeConstructor());
		}
		final int length = arguments.size();
		final Type[] argTypes = new Type[length];
		for (int i = 0; i < length; i++) {
			argTypes[i] = subst.rewrite(arguments.get(i).getType());
		}
		return argTypes;
	}

	@Override
	public boolean hasArguments() {
		return arguments.size() != 0;
	}

	@Override
	public IDestructorExtension[] getArguments() {
		return argumentsExt.toArray(new IDestructorExtension[argumentsExt
				.size()]);
	}

	@Override
	public IDestructorExtension getDestructor(String destName) {
		if (!destructors.containsKey(destName)) {
			throw new IllegalArgumentException("The current constructor: "
					+ this + " does not have a destructor named: " + destName);
		}
		return destructors.get(destName);
	}

	@Override
	public Expression[] getArgumentSets(Expression set) {
		final SetSubstitution subst = makeSubstitution(origin, set);
		if (subst == null) {
			throw new IllegalArgumentException("Constructor: " + this
					+ " is not compatible with set: " + set);
		}
		final Type[] argTypes = new Type[arguments.size()];
		for (int i = 0; i < argTypes.length; i++) {
			argTypes[i] = arguments.get(i).getType();
		}
		return subst.substitute(argTypes);
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return wdMediator.makeTrueWD();
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
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
		// either a proposed type or typechecking is required
		return null;
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		final TypeSubstitution subst = makeSubstitution(origin, proposedType);
		if (subst == null) {
			return false;
		}
		assert childExprs.length == arguments.size();
		for (int i = 0; i < childExprs.length; i++) {
			final Type argType = subst.rewrite(arguments.get(i).getType());
			final Type childType = childExprs[i].getType();
			if (!argType.equals(childType)) {
				return false;
			}
		}
		assert childPreds.length == 0;
		return true;
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		final TypeSubstitution subst = makeSubstitution(origin, tcMediator);
		final Expression[] children = expression.getChildExpressions();
		for (int i = 0; i < children.length; i++) {
			final Type childType = children[i].getType();
			final Type argType = subst.rewrite(arguments.get(i).getType());
			tcMediator.sameType(childType, argType);
		}
		return subst.getInstance();
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

	@Override
	public Datatype2 getOrigin() {
		return origin;
	}

	@Override
	public String toString() {
		return name;
	}

	public Set<IFormulaExtension> getExtensions() {
		return extensions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * name.hashCode() + arguments.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final ConstructorExtension other = (ConstructorExtension) obj;
		return this.name.equals(other.name)
				&& this.arguments.equals(other.arguments);
	}

}

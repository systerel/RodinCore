/*******************************************************************************
 * Copyright (c) 2013, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static java.util.Collections.emptyList;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeGroup;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeId;
import static org.eventb.internal.core.ast.datatype.DatatypeHelper.computeKind;
import static org.eventb.internal.core.ast.datatype.TypeSubstitution.makeSubstitution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * This class represents an extension used as constructor for a datatype.
 * 
 * <p>
 * The constructor type represents the datatype type and its children correspond
 * to its arguments in the {@link #verifyType(Type, Expression[], Predicate[])},
 * {@link #synthesizeType(Expression[], Predicate[], ITypeMediator)} and {
 * {@link #typeCheck(ExtendedExpression, ITypeCheckMediator)} methods.
 * </p>
 * <p>
 * This class must <strong>not</strong> override <code>equals</code> as this
 * would wreak havoc in formula factories. We rely on object identity for
 * identifying identical constructors.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class ConstructorExtension implements IConstructorExtension {

	private final Datatype origin;
	private final String name;
	private final boolean _needsTypeAnnotation;
	private final ConstructorArgument[] arguments;
	private final int sameTypeIndex;

	private final String id;
	private final IExtensionKind kind;
	private final String groupId;

	private HashMap<String, DestructorExtension> destructors;

	public ConstructorExtension(Datatype origin, ConstructorBuilder builder) {
		this.origin = origin;
		this.name = builder.getName();
		this.id = computeId(name);
		this._needsTypeAnnotation = builder.needsTypeAnnotation();
		final List<DatatypeArgument> builderArgs = builder.getArguments();
		final int nbArgs = builderArgs.size();
		this.groupId = computeGroup(nbArgs);
		this.kind = computeKind(nbArgs);
		this.arguments = new ConstructorArgument[nbArgs];
		this.sameTypeIndex = builder.getSameTypeIndex();
		this.destructors = new HashMap<String, DestructorExtension>(nbArgs);
		int count = 0;
		for (final DatatypeArgument builderArg : builderArgs) {
			final ConstructorArgument arg = builderArg.finalize(origin, this);
			if (arg.isDestructor()) {
				final DestructorExtension destr = arg.asDestructor();
				destructors.put(destr.getName(), destr);
			}
			arguments[count] = arg;
			++count;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasArguments() {
		return arguments.length != 0;
	}

	@Override
	public ConstructorArgument[] getArguments() {
		return arguments.clone();
	}

	/**
	 * Returns the map of destructors for this constructor. The resulting map
	 * must be used only for reading and must not be leaked to clients.
	 * 
	 * @return the map of destructors
	 */
	public Map<String, DestructorExtension> getDestructorMap() {
		return destructors;
	}

	@Override
	public IDestructorExtension getDestructor(String destName) {
		return destructors.get(destName);
	}

	@Override
	public int getArgumentIndex(IConstructorArgument argument) {
		for (int i = 0; i < arguments.length; i++) {
			if (argument == arguments[i]) {
				return i;
			}
		}
		return -1;
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
		final Type resultType = inferResultType(childExprs, mediator);
		return verifyType(resultType, childExprs, childPreds) ? resultType : null;
	}

	/*
	 * Tries to infer the result type by various means, except full pattern
	 * matching.
	 */
	protected Type inferResultType(Expression[] childExprs, ITypeMediator mediator) {
		if (sameTypeIndex >= 0) {
			return childExprs[sameTypeIndex].getType();
		}
		
		var typeConstr = origin.getTypeConstructor();
		if (typeConstr.getNbParams() != 0) {
			// either a proposed type or typechecking is required
			return null;
		}

		// Not a generic datatype, it is easy to create the result type
		final Type resultType = mediator.makeParametricType(typeConstr, emptyList());
		return resultType;
	}

	@Override
	public boolean needsTypeAnnotation() {
		return _needsTypeAnnotation;
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		final TypeSubstitution subst = makeSubstitution(origin, proposedType);
		if (subst == null) {
			return false;
		}
		assert childExprs.length == arguments.length;
		for (int i = 0; i < childExprs.length; i++) {
			final Type argType = arguments[i].getType(subst);
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
			final Type argType = arguments[i].getType(subst);
			tcMediator.sameType(childType, argType);
		}
		return subst.getInstanceType();
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

	@Override
	public boolean isBasic() {
		return Arrays.stream(arguments).allMatch(ConstructorArgument::isBasic);
	}

	@Override
	public Datatype getOrigin() {
		return origin;
	}

	/*
	 * Even though equals() is not implemented, we provide a hash code that will be
	 * used with isSimilarTo() to implement Datatype's hashCode and equals.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * name.hashCode() + Arrays.hashCode(arguments);
	}

	/*
	 * Implements pseudo-equality, that is equality up to datatype identity.
	 */
	public boolean isSimilarTo(ConstructorExtension other) {
		if (this == other) {
			return true;
		}
		if (this.getClass() != other.getClass()) {
			return false;
		}
		return this.name.equals(other.name)
				&& areSimilarArguments(this.arguments, other.arguments);
	}

	private static boolean areSimilarArguments(ConstructorArgument[] arguments1,
			ConstructorArgument[] arguments2) {
		if (arguments1.length != arguments2.length) {
			return false;
		}
		for (int i = 0; i < arguments1.length; i++) {
			if (!arguments1[i].isSimilarTo(arguments2[i])) {
				return false;
			}
		}
		return true;
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
		for (final ConstructorArgument arg : arguments) {
			sb.append(sep);
			sep = "; ";
			arg.toString(sb);
		}
		if (arguments.length != 0) {
			sb.append("]");
		}
	}

}

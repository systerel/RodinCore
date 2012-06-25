/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.internal.core.ast.extension.datatype.DatatypeExtensionComputer.computeGroup;
import static org.eventb.internal.core.ast.extension.datatype.DatatypeExtensionComputer.computeKind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IArgumentType;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 * 
 */
public class ConstructorMediator extends ArgumentMediator implements
		IConstructorMediator {

	private static class ConstructorExtension implements IExpressionExtension {

		private final String name;
		private final String id;
		private final String groupId;
		private final IExtensionKind kind;
		private final IExpressionExtension typeCons;
		private final List<ITypeParameter> typeParams;
		private final List<IArgument> arguments;
		private final IDatatype origin;

		public ConstructorExtension(String name, String id, String groupId,
				IExtensionKind kind, IExpressionExtension typeCons,
				List<ITypeParameter> typeParams, List<IArgument> arguments,
				IDatatype origin) {
			this.name = name;
			this.id = id;
			this.groupId = groupId;
			this.kind = kind;
			this.typeCons = typeCons;
			this.typeParams = typeParams;
			this.arguments = arguments;
			this.origin = origin;
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
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			// either a proposed type or typechecking is required
			return null;
		}

		@Override
		public boolean verifyType(Type proposedType, Expression[] childExprs,
				Predicate[] childPreds) {
			final TypeInstantiation instantiation = checkAndGetInst(proposedType);
			if (instantiation == null) {
				return false;
			}

			assert childExprs.length == arguments.size();
			for (int i = 0; i < childExprs.length; i++) {
				final Type childType = childExprs[i].getType();
				final IArgumentType argType = arguments.get(i).getType();
				if (!argType.verifyType(childType, instantiation)) {
					return false;
				}
			}
			return true;
		}

		private TypeInstantiation checkAndGetInst(Type proposedType) {
			if (!(proposedType instanceof ParametricType)) {
				return null;
			}
			final ParametricType genType = (ParametricType) proposedType;
			if (genType.getExprExtension() != typeCons) {
				return null;
			}

			// instantiate type parameters with those of proposed type
			return Datatype.makeTypeInst(genType, typeParams);
		}

		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final TypeInstantiation instantiation = new TypeInstantiation();

			final List<Type> typeParamVars = new ArrayList<Type>();
			for (ITypeParameter prm : typeParams) {
				final Type alpha = tcMediator.newTypeVariable();
				typeParamVars.add(alpha);
				instantiation.put(prm, alpha);
			}
			final Expression[] children = expression.getChildExpressions();
			assert children.length == arguments.size();
			for (int i = 0; i < children.length; i++) {
				final Type childType = children[i].getType();
				final IArgumentType argType = arguments.get(i).getType();
				final Type type = argType.toType(tcMediator, instantiation);
				tcMediator.sameType(childType, type);
			}
			return tcMediator.makeParametricType(typeParamVars, typeCons);
		}

		@Override
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public IDatatype getOrigin() {
			return origin;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((arguments == null) ? 0 : arguments.hashCode());
			result = prime * result
					+ ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((typeCons == null) ? 0 : typeCons.hashCode());
			result = prime * result
					+ ((typeParams == null) ? 0 : typeParams.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ConstructorExtension)) {
				return false;
			}
			ConstructorExtension other = (ConstructorExtension) obj;
			if (arguments == null) {
				if (other.arguments != null) {
					return false;
				}
			} else if (!arguments.equals(other.arguments)) {
				return false;
			}
			if (groupId == null) {
				if (other.groupId != null) {
					return false;
				}
			} else if (!groupId.equals(other.groupId)) {
				return false;
			}
			if (id == null) {
				if (other.id != null) {
					return false;
				}
			} else if (!id.equals(other.id)) {
				return false;
			}
			if (kind == null) {
				if (other.kind != null) {
					return false;
				}
			} else if (!kind.equals(other.kind)) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (typeCons == null) {
				if (other.typeCons != null) {
					return false;
				}
			} else if (!typeCons.equals(other.typeCons)) {
				return false;
			}
			if (typeParams == null) {
				if (other.typeParams != null) {
					return false;
				}
			} else if (!typeParams.equals(other.typeParams)) {
				return false;
			}
			return true;
		}
		
	}

	private static class DestructorExtension implements IExpressionExtension {
	
		private static final String PARAM_PREFIX = "p";

		private static final Predicate[] NO_PRED = new Predicate[0];

		private final String name;
		private final String id;
		private final String groupId;
		private final IExtensionKind kind;
		private final IExpressionExtension typeCons;
		private final List<ITypeParameter> typeParams;
		private final IArgumentType returnType;
		private final IDatatype origin;
		private final IExpressionExtension constructor;
	
		public DestructorExtension(String name, String id, String groupId,
				IExtensionKind kind, IExpressionExtension typeCons,
				List<ITypeParameter> typeParams, IArgumentType returnType,
				IDatatype origin, IExpressionExtension constructor) {
			this.name = name;
			this.id = id;
			this.groupId = groupId;
			this.kind = kind;
			this.typeCons = typeCons;
			this.typeParams = typeParams;
			this.returnType = returnType;
			this.origin = origin;
			this.constructor = constructor;
		}
	
		// formula is "destr(dt)"
		// WD is "# params . dt = constr(params)" 
		@Override
		public Predicate getWDPredicate(IExtendedFormula formula,
				IWDMediator wdMediator) {
			
			if (origin.getConstructors().size() == 1) {
				// if there is only one constructor the WD is true
				return wdMediator.makeTrueWD();
			}
			
			final FormulaFactory ff = wdMediator.getFormulaFactory();
			final List<IArgument> constrArgs = origin.getArguments(constructor);
			final int argSize = constrArgs.size();
			final Expression child = formula.getChildExpressions()[0];
			
			final ParametricType dtType = (ParametricType) child.getType();

			final List<Type> argTypes = origin.getArgumentTypes(constructor,
					dtType, ff);
			assert argTypes.size() == argSize;
			
			final List<BoundIdentDecl> bids = new ArrayList<BoundIdentDecl>(argSize);
			final Expression[] params = buildParams(bids, constrArgs, argTypes,
					ff);

			final Expression dt = child.shiftBoundIdentifiers(argSize, ff);
			return makeWD(dt, bids, params, ff);
		}

		private static Expression[] buildParams(List<BoundIdentDecl> bids,
				List<IArgument> constrArgs, List<Type> argTypes,
				FormulaFactory ff) {
			final int argSize = constrArgs.size();
			final Expression[] params = new Expression[argSize];
			for (int i = 0; i < argSize; i++) {
				final IArgument arg = constrArgs.get(i);
				final String prmName = makeParamName(arg, i);

				final Type argType = argTypes.get(i);
				bids.add(ff.makeBoundIdentDecl(prmName, null, argType));
				params[i] = ff.makeBoundIdentifier(argSize - 1 - i, null,
						argType);
			}
			return params;
		}

		private static String makeParamName(IArgument arg, int index) {
			final String prmName;
			if (arg.hasDestructor()) {
				prmName = arg.getDestructor() + index;
			} else {
				prmName = PARAM_PREFIX + index;
			}
			return prmName;
		}

		private Predicate makeWD(Expression dt, List<BoundIdentDecl> bids,
				Expression[] params, FormulaFactory ff) {
			final ExtendedExpression constr = ff.makeExtendedExpression(
					constructor, params, NO_PRED, null, dt.getType());
			final RelationalPredicate eqDtConstr = ff.makeRelationalPredicate(
					EQUAL, dt, constr, null);
			return ff.makeQuantifiedPredicate(EXISTS, bids, eqDtConstr, null);
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
		public Type synthesizeType(Expression[] childExprs,
				Predicate[] childPreds, ITypeMediator mediator) {
			final TypeInstantiation instantiation = checkAndGetInst(childExprs);
			if(instantiation == null) {
				return null;
			}
			return returnType.toType(mediator, instantiation);
		}
	
		@Override
		public boolean verifyType(Type proposedType,
				Expression[] childExprs, Predicate[] childPreds) {
			final TypeInstantiation instantiation = checkAndGetInst(childExprs);
			if (instantiation == null) {
				return false;
			}
			return returnType.verifyType(proposedType, instantiation);
		}
	
		private TypeInstantiation checkAndGetInst(Expression[] childExprs) {
			final Expression[] children = childExprs;
			assert children.length == 1;
			final Type childType = children[0].getType();
			if (!(childType instanceof ParametricType)) {
				return null;
			}
			final ParametricType genChildType = (ParametricType) childType;
			if (genChildType.getExprExtension() != typeCons) {
				return null;
			}
			return Datatype.makeTypeInst(genChildType, typeParams);
		}
	
		@Override
		public Type typeCheck(ExtendedExpression expression,
				ITypeCheckMediator tcMediator) {
			final List<Type> typePrmVars = new ArrayList<Type>();
			final TypeInstantiation instantiation = new TypeInstantiation();
			for (ITypeParameter prm : typeParams) {
				final Type alpha = tcMediator.newTypeVariable();
				instantiation.put(prm, alpha);
				typePrmVars.add(alpha);
			}
			final Type argType = tcMediator.makeParametricType(typePrmVars,
					typeCons);
			final Expression[] children = expression.getChildExpressions();
			assert children.length == 1;
			tcMediator.sameType(argType, children[0].getType());
			return returnType.toType(tcMediator, instantiation);
		}
	
		@Override
		public boolean conjoinChildrenWD() {
			return true;
		}
	
		@Override
		public boolean isATypeConstructor() {
			return false;
		}

		@Override
		public IDatatype getOrigin() {
			return origin;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((constructor == null) ? 0 : constructor.hashCode());
			result = prime * result
					+ ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result
					+ ((returnType == null) ? 0 : returnType.hashCode());
			result = prime * result
					+ ((typeCons == null) ? 0 : typeCons.hashCode());
			result = prime * result
					+ ((typeParams == null) ? 0 : typeParams.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof DestructorExtension)) {
				return false;
			}
			DestructorExtension other = (DestructorExtension) obj;
			if (constructor == null) {
				if (other.constructor != null) {
					return false;
				}
			} else if (!constructor.equals(other.constructor)) {
				return false;
			}
			if (groupId == null) {
				if (other.groupId != null) {
					return false;
				}
			} else if (!groupId.equals(other.groupId)) {
				return false;
			}
			if (id == null) {
				if (other.id != null) {
					return false;
				}
			} else if (!id.equals(other.id)) {
				return false;
			}
			if (kind == null) {
				if (other.kind != null) {
					return false;
				}
			} else if (!kind.equals(other.kind)) {
				return false;
			}
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (returnType == null) {
				if (other.returnType != null) {
					return false;
				}
			} else if (!returnType.equals(other.returnType)) {
				return false;
			}
			if (typeCons == null) {
				if (other.typeCons != null) {
					return false;
				}
			} else if (!typeCons.equals(other.typeCons)) {
				return false;
			}
			if (typeParams == null) {
				if (other.typeParams != null) {
					return false;
				}
			} else if (!typeParams.equals(other.typeParams)) {
				return false;
			}
			return true;
		}
	
	}

	// FIXME we may wish to have priorities and custom type check methods, thus
	// fully implementing methods from IExpressionExtension

	private final Datatype datatype;
	
	public ConstructorMediator(Datatype datatype, FormulaFactory factory) {
		super(factory);
		this.datatype = datatype;
	}

	@Override
	public void addConstructor(String name, String id) {
		addConstructor(name, id, Collections.<IArgument> emptyList());
	}

	@Override
	public void addConstructor(String name, String id,
			List<IArgument> arguments) {
		final IExpressionExtension typeConstructor = datatype.getTypeConstructor();
		final List<ITypeParameter> typeParams = datatype.getTypeParameters();
		final int nbArgs = arguments.size();
		final String groupId = computeGroup(nbArgs);
		final IExtensionKind kind = computeKind(nbArgs);
	
		final IExpressionExtension constructor = new ConstructorExtension(name,
				id, groupId, kind, typeConstructor, typeParams, arguments,
				datatype);

		// FIXME problem with duplicate arguments with destructors:
		// the destructor is built several times
		final List<IExpressionExtension> destructors = new ArrayList<IExpressionExtension>();
		for (int i = 0; i < arguments.size(); i++) {
			final IArgument arg = arguments.get(i);
			final IExpressionExtension destructor;
			if (arg.hasDestructor()) {
				final String destructorName = arg.getDestructor();
				final String destructorId = id + "." + i;
				final int destrNbArgs = 1; // one argument (of type datatype)
				final String destrGroupId = computeGroup(destrNbArgs);
				final IExtensionKind destrKind = computeKind(destrNbArgs);
				
				destructor = new DestructorExtension(destructorName,
						destructorId, destrGroupId, destrKind, typeConstructor,
						typeParams, arg.getType(), datatype, constructor);
			} else {
				destructor = null;
			}
			destructors.add(destructor);
		}
		final List<IArgument> argCopy = new ArrayList<IArgument>(arguments);
		datatype.addConstructor(constructor, destructors, argCopy);
	}

	@Override
	public IExpressionExtension getTypeConstructor() {
		return datatype.getTypeConstructor();
	}
	
	@Override
	public ITypeParameter getTypeParameter(String name) {
		for (ITypeParameter param : datatype.getTypeParameters()) {
			if (param.getName().equals(name)) {
				return param;
			}
		}
		return null;
	}

	@Override
	public IArgumentType newArgumentType(Type type) {
		if (type instanceof GivenType) {
			final String name = ((GivenType) type).getName();
			final ITypeParameter typeParam = getTypeParameter(name);
			if (typeParam != null) {
				return newArgumentType(typeParam);
			}
		} else if (type instanceof ParametricType) {
			final ParametricType paramType = (ParametricType) type;
			final IExpressionExtension exprExtension = paramType
					.getExprExtension();
			final IExpressionExtension typeConstr;
			final IExpressionExtension dtConstr = datatype.getTypeConstructor();
			// might have been parsed with a temp extension, use the correct one 
			if (exprExtension.getId().equals(dtConstr.getId())) {
				typeConstr = dtConstr;
			} else {
				typeConstr = exprExtension;
			}
			final List<IArgumentType> argTypes = new ArrayList<IArgumentType>();
			for (Type typePrm : paramType.getTypeParameters()) {
				final IArgumentType prmArgType = newArgumentType(typePrm);
				argTypes.add(prmArgType);
			}
			return makeParametricType(typeConstr, argTypes);
		} else if (type instanceof ProductType) {
			final ProductType prodType = (ProductType) type;
			final IArgumentType left = newArgumentType(prodType.getLeft());
			final IArgumentType right = newArgumentType(prodType.getRight());
			return makeProductType(left, right);
		} else if (type instanceof PowerSetType) {
			final Type baseType = ((PowerSetType) type).getBaseType();
			return makePowerSetType(newArgumentType(baseType));
		}
		return new ArgSimpleType(type);
	}

	public IDatatype getDatatype() {
		return datatype;
	}

}

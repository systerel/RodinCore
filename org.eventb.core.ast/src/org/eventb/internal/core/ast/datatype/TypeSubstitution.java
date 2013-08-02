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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.ITypeInstantiation;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.internal.core.ast.TypeRewriter;

/**
 * Represents an instantiation of a datatype, where type parameters are
 * associated with some arbitrary types. Instances are intended to be used for
 * performing type substitution on datatype extensions.
 * 
 * @author Laurent Voisin
 */
public class TypeSubstitution extends TypeRewriter implements
		ITypeInstantiation {

	/**
	 * Returns a substitution appropriate for type-checking. All actual type
	 * parameters are type variables.
	 * 
	 * @param datatype
	 *            a datatype
	 * @param tcMediator
	 *            a type-checking mediator
	 * @return a type rewriter that can be applied to constructor arguments
	 */
	public static TypeSubstitution makeSubstitution(Datatype datatype,
			ITypeCheckMediator tcMediator) {
		final TypeConstructorExtension tcons = datatype.getTypeConstructor();
		final int nbParams = tcons.getNbParams();
		final List<Type> actuals = new ArrayList<Type>(nbParams);
		for (int i = 0; i < nbParams; i++) {
			actuals.add(tcMediator.newTypeVariable());
		}
		final ParametricType instance = tcMediator.makeParametricType(actuals,
				tcons);
		return new TypeSubstitution(instance);
	}

	/**
	 * Returns a substitution appropriate for verifying types. The actual type
	 * parameters are inferred from the proposed type, if possible.
	 * 
	 * This method must not throw any exception as it is called during
	 * type-checking.
	 * 
	 * @param datatype
	 *            a datatype
	 * @param proposedInstance
	 *            a proposed instance of the datatype type
	 * @return a type rewriter that can be applied to constructor arguments, or
	 *         <code>null</code> if the proposed type is invalid
	 */
	public static TypeSubstitution makeSubstitution(Datatype datatype,
			Type proposedInstance) {
		if (!(proposedInstance instanceof ParametricType)) {
			return null;
		}
		final ParametricType instance = (ParametricType) proposedInstance;
		final IExpressionExtension ext = instance.getExprExtension();
		if (!(ext instanceof TypeConstructorExtension)) {
			return null;
		}
		if (!datatype.equals(ext.getOrigin())) {
			return null;
		}
		return new TypeSubstitution(instance);
	}

	// The datatype
	private final Datatype datatype;

	// The instance of the datatype
	private final ParametricType instance;

	// A map from type parameter names and datatype name to their instance
	private final Map<String, Type> map = new HashMap<String, Type>();

	private TypeSubstitution(ParametricType instance) {
		super(instance.getFactory());
		this.instance = instance;
		final TypeConstructorExtension tcons = (TypeConstructorExtension) instance
				.getExprExtension();
		this.datatype = tcons.getOrigin();
		map.put(tcons.getName(), instance);
		final String[] formalNames = tcons.getFormalNames();
		final int nbParams = formalNames.length;
		final Type[] actuals = instance.getTypeParameters();
		assert actuals.length == nbParams;
		for (int i = 0; i < nbParams; i++) {
			map.put(formalNames[i], actuals[i]);
		}
	}

	@Override
	public IDatatype getOrigin() {
		return datatype;
	}

	@Override
	public ParametricType getInstanceType() {
		return instance;
	}

	@Override
	public void visit(GivenType type) {
		result = map.get(type.getName());
		if (result == null) {
			super.visit(type);
		}
	}
}

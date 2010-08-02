/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension.datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.ITypeParameter;

/**
 * @author Nicolas Beauger
 * 
 */
public class Datatype implements IDatatype {

	private static class Constructor {
		private final IExpressionExtension constructor;
		private final List<IExpressionExtension> destructors;

		public Constructor(IExpressionExtension constructor,
				List<IExpressionExtension> destructors) {
			final IExtensionKind kind = constructor.getKind();
			assert kind.getProperties().getArity().check(destructors.size());
			this.constructor = constructor;
			this.destructors = destructors;
		}

		public IExpressionExtension getConstructor() {
			return constructor;
		}

		public IExpressionExtension getDestructor(int argNumber) {
			if (argNumber < 0 || argNumber >= destructors.size()) {
				return null;
			}
			return destructors.get(argNumber);
		}
		
		// non null destructors
		public Collection<IExpressionExtension> getDestructors() {
			final List<IExpressionExtension> destrs = new ArrayList<IExpressionExtension>();
			for (IExpressionExtension destr : destructors) {
				if (destr != null) {
					destrs.add(destr);
				}
			}
			return destrs;
		}

	}

	private final List<ITypeParameter> typeParams;
	private final IExpressionExtension typeConstructor;
	private final Map<String, Constructor> constructors = new HashMap<String, Constructor>();

	public Datatype(IExpressionExtension typeConstructor,
			List<ITypeParameter> typeParams) {
		this.typeConstructor = typeConstructor;
		this.typeParams = typeParams;
	}

	@Override
	public List<ITypeParameter> getTypeParameters() {
		return Collections.unmodifiableList(typeParams);
	}
	
	@Override
	public IExpressionExtension getTypeConstructor() {
		return typeConstructor;
	}

	@Override
	public IExpressionExtension getConstructor(String constructorId) {
		final Constructor constr = constructors.get(constructorId);
		if (constr == null) {
			return null;
		}
		return constr.getConstructor();
	}

	@Override
	public IExpressionExtension getDestructor(String constructorId,
			int argNumber) {
		final Constructor constr = constructors.get(constructorId);
		if (constr == null) {
			return null;
		}
		return constr.getDestructor(argNumber);
	}

	@Override
	public Set<IFormulaExtension> getExtensions() {
		final Set<IFormulaExtension> extensions = new HashSet<IFormulaExtension>();

		extensions.add(typeConstructor);
		for(Constructor constr: constructors.values()) {
			extensions.add(constr.getConstructor());
			extensions.addAll(constr.getDestructors());
		}
		return extensions;
	}

	// don't forget to insert null destructors for arguments with no
	// destructor, in order to have correct argument numbers
	public void addConstructor(IExpressionExtension constructor,
			List<IExpressionExtension> destructors) {
		final Constructor old = constructors.get(constructor);
		if (old != null) {
			throw new IllegalArgumentException("constructor "
					+ constructor.getId() + " already exists");
		}
		final Constructor constr = new Constructor(constructor, destructors);
		constructors.put(constructor.getId(), constr);
	}

}

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
package org.eventb.internal.core.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Type;

/**
 * Common implementation for specializations.
 * 
 * @author Laurent Voisin
 */
public class Specialization implements ISpecialization {

	private final Map<GivenType, Type> typeSubst;
	private final Map<FreeIdentifier, Expression> identSubst;

	public Specialization() {
		typeSubst = new HashMap<GivenType, Type>();
		identSubst = new HashMap<FreeIdentifier, Expression>();
	}

	@Override
	public void put(GivenType key, Type value) {
		if (key == null)
			throw new NullPointerException("Null given type");
		if (value == null)
			throw new NullPointerException("Null type");
		if (typeSubst.containsKey(key) && !typeSubst.get(key).equals(value))
			throw new IllegalArgumentException("Key " + key
					+ " is already registered");
		typeSubst.put(key, value);
		verify();
	}

	public Type get(GivenType key) {
		final Type value = typeSubst.get(key);
		if (value == null)
			return key;
		return value;
	}

	@Override
	public void put(FreeIdentifier ident, Expression value) {
		if (ident == null)
			throw new NullPointerException("Null identifier");
		if (!ident.isTypeChecked())
			throw new IllegalArgumentException("Untyped identifier");
		if (value == null)
			throw new NullPointerException("Null value");
		if (!value.isTypeChecked())
			throw new IllegalArgumentException("Untyped value");
		identSubst.put(ident, value);
		verify();
	}

	public Expression get(FreeIdentifier ident) {
		final Expression value = identSubst.get(ident);
		if (value == null)
			return ident;
		return value;
	}

	/**
	 * Verifies that both substitutions are compatible.
	 */
	private void verify() {
		for (Entry<FreeIdentifier, Expression> entry : identSubst.entrySet()) {
			final FreeIdentifier ident = entry.getKey();
			final Type newType = ident.getType().specialize(this);
			final Expression value = entry.getValue();
			if (!value.getType().equals(newType)) {
				throw new IllegalArgumentException("Incompatible types for "
						+ ident);
			}
		}
	}

}

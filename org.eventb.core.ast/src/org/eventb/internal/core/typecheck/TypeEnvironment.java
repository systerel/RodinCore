/*******************************************************************************
 * Copyright (c) 2005, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added support for specialization
 *     Systerel - immutable type environments
 *     Systerel - added support for factory translation
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IDatatypeTranslation;
import org.eventb.core.ast.IExtensionTranslation;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.GivenTypeHelper;
import org.eventb.internal.core.ast.Specialization;
import org.eventb.internal.core.ast.TypeRewriter;
import org.eventb.internal.core.ast.datatype.DatatypeTranslation;
import org.eventb.internal.core.ast.extension.ExtensionTranslation;

/**
 * Common implementation of type environments used to type check event-B
 * formulas.
 * <p>
 * A type environment is a map from names to their respective type.
 * </p>
 * <p>
 * The methods of this class <strong>must</strong> never change anything in the
 * map, nor leak the map outside, otherwise sealed implementation might become
 * mutable.
 * </p>
 * 
 * @author François Terrier
 */
public abstract class TypeEnvironment implements ITypeEnvironment{

	final class InternalIterator implements
			IIterator {

		Iterator<Map.Entry<String, Type>> iterator;

		Map.Entry<String, Type> current;

		public InternalIterator(Iterator<Map.Entry<String, Type>> iterator) {
			this.iterator = iterator;
			this.current = null;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public void advance() throws NoSuchElementException {
			current = iterator.next();
		}

		@Override
		public String getName() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.getKey();
		}

		@Override
		public Type getType() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return current.getValue();
		}

		@Override
		public boolean isGivenSet() throws NoSuchElementException {
			return GivenTypeHelper.isGivenSet(getName(), getType());
		}

		@Override
		public FreeIdentifier asFreeIdentifier() throws NoSuchElementException {
			if (current == null) {
				throw new NoSuchElementException();
			}
			return ff.makeFreeIdentifier(getName(), null, getType());
		}
	}

	// the mathematical language we're using
	protected final FormulaFactory ff;

	// implementation
	protected final Map<String, Type> map;
	
	/**
	 * Constructs an initially empty type environment.
	 */
	protected TypeEnvironment(FormulaFactory ff) {
		this.ff = ff;
		this.map = new HashMap<String, Type>();
	}
	
	/**
	 * Constructs a new type environment with a copy of the map of the given one.
	 * 
	 * @param typenv
	 *            type environment to copy
	 */
	protected TypeEnvironment(TypeEnvironment typenv) {
		this.ff = typenv.ff;
		this.map = new HashMap<String, Type>(typenv.map);
	}

	@Override
	public boolean contains(String name) {
		return map.containsKey(name);
	}

	@Override
	public boolean contains(FreeIdentifier ident) {
		final Type type = ident.getType();
		if (type == null) {
			throw new IllegalArgumentException("Identifier " + ident
					+ " has no type");
		}
		return type.equals(getType(ident.getName()));
	}

	@Override
	public boolean containsAll(ITypeEnvironment typenv) {
		if (this == typenv)
			return true;
		final TypeEnvironment other = (TypeEnvironment) typenv;
		for (Entry<String, Type> entry : other.map.entrySet()) {
			String name = entry.getKey();
			if (!entry.getValue().equals(this.getType(name)))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		final TypeEnvironment other = (TypeEnvironment) obj;
		return map.equals(other.map);
	}

	@Override
	public IDatatypeTranslation makeDatatypeTranslation() {
		return new DatatypeTranslation(this.makeSnapshot());
	}
	
	@Override
	public IExtensionTranslation makeExtensionTranslation() {
		return new ExtensionTranslation(this.makeSnapshot());
	}

	@Override
	public IExtensionTranslation makeExtensionTranslation(FormulaFactory targetFactory) {
		return new ExtensionTranslation(this.makeSnapshot(), targetFactory);
	}

	@Override
	public IIterator getIterator(){
		return new InternalIterator(map.entrySet().iterator());
	}

	@Override
	public Set<String> getNames() {
		return unmodifiableSet(map.keySet());
	}

	@Override
	public Type getType(String name) {
		return map.get(name);
	}

	@Override
	public FreeIdentifier[] getFreeIdentifiers() {
		return map.entrySet().stream().map(e -> ff.makeFreeIdentifier(e.getKey(), null, e.getValue()))
				.toArray(FreeIdentifier[]::new);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return ff;
	}

	@Override
	public ITypeEnvironmentBuilder specialize(ISpecialization specialization) {
		return ((Specialization) specialization).specialize(this);
	}
	
	@Override
	public ITypeEnvironmentBuilder makeBuilder() {
		return new TypeEnvironmentBuilder(this);
	}

	@Override
	public boolean isTranslatable(FormulaFactory factory) {
		if (factory == ff) {
			return true;
		}
		final IIterator iter = getIterator();
		while (iter.hasNext()) {
			iter.advance();
			if (!factory.isValidIdentifierName(iter.getName())) {
				return false;
			}
			if (!iter.getType().isTranslatable(factory)) {
				return false;
			}
		}
		return true;
	}

	protected ITypeEnvironmentBuilder doTranslate(FormulaFactory fac) {
		final TypeRewriter rewriter = new TypeRewriter(fac);
		final TypeEnvironmentBuilder result = new TypeEnvironmentBuilder(fac);
		final IIterator iter = getIterator();
		while (iter.hasNext()) {
			iter.advance();
			result.addName(iter.getName(), rewriter.rewrite(iter.getType()));
		}
		return result;
	}

}

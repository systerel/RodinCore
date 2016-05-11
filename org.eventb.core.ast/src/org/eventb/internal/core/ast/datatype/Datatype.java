/*******************************************************************************
 * Copyright (c) 2013, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static java.util.Collections.unmodifiableSet;
import static org.eventb.internal.core.ast.datatype.SetSubstitution.makeSubstitution;
import static org.eventb.internal.core.ast.datatype.TypeSubstitution.makeSubstitution;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Implementation of a complete datatype which references all its extensions
 * (type constructor, value constructors and their destructors).
 * <p>
 * Datatypes which have the exact same definition (except maybe the formula
 * factory used for building it) are represented by the same object. Therefore,
 * this class must <strong>not</strong> override <code>equals</code>
 * </p>
 * 
 * @author Vincent Monfort
 */
public class Datatype implements IDatatype {

	private static final Map<Datatype, Datatype> REGISTRY //
	= new HashMap<Datatype, Datatype>();

	// Ensure uniqueness of datatypes
	public static Datatype makeDatatype(DatatypeBuilder dtBuilder) {
		final Datatype candidate = new Datatype(dtBuilder);
		return registerDatatype(candidate);
	}

	private static Datatype registerDatatype(Datatype candidate) {
		final Datatype representative = REGISTRY.get(candidate);
		if (representative != null) {
			return representative;
		}
		REGISTRY.put(candidate, candidate);
		return candidate;
	}

	// The minimal factory containing all extensions that are needed to define
	// this datatype
	private final FormulaFactory baseFactory;

	// The type constructor
	private final TypeConstructorExtension typeCons;

	// The value constructors (order is mostly irrelevant, but clients might
	// rely on it)
	private final LinkedHashMap<String, ConstructorExtension> constructors;

	// The destructors
	private final Map<String, DestructorExtension> destructors;

	// Cache of the extensions provided by this datatype
	private final Set<IFormulaExtension> extensions;

	private final Object origin;

	private Datatype(DatatypeBuilder dtBuilder) {
		baseFactory = dtBuilder.getBaseFactory();
		origin = dtBuilder.getOrigin();
		typeCons = new TypeConstructorExtension(this, dtBuilder);
		constructors = new LinkedHashMap<String, ConstructorExtension>();
		destructors = new HashMap<String, DestructorExtension>();
		final List<ConstructorBuilder> dtConstrs = dtBuilder.getConstructors();
		for (final ConstructorBuilder dtCons : dtConstrs) {
			final ConstructorExtension constructor = dtCons.makeExtension(this);
			constructors.put(constructor.getName(), constructor);
			destructors.putAll(constructor.getDestructorMap());
		}
		extensions = new HashSet<IFormulaExtension>();
		extensions.add(typeCons);
		extensions.addAll(constructors.values());
		extensions.addAll(destructors.values());
	}

	@Override
	public TypeConstructorExtension getTypeConstructor() {
		return typeCons;
	}

	@Override
	public TypeSubstitution getTypeInstantiation(Type type) {
		if (type == null) {
			throw new NullPointerException("Null type");
		}
		final TypeSubstitution ti = makeSubstitution(this, type);
		if (ti == null) {
			throw new IllegalArgumentException("Type " + type
					+ " is not an instance of " + typeCons.getName());
		}
		return ti;
	}

	@Override
	public ISetInstantiation getSetInstantiation(Expression set) {
		if (set == null) {
			throw new NullPointerException("Null set");
		}
		return makeSubstitution(this, set);
	}

	public boolean hasSingleConstructor() {
		return constructors.size() == 1;
	}

	@Override
	public IConstructorExtension[] getConstructors() {
		final Collection<ConstructorExtension> cons = constructors.values();
		return cons.toArray(new IConstructorExtension[cons.size()]);
	}

	@Override
	public IConstructorExtension getConstructor(String name) {
		return constructors.get(name);
	}

	@Override
	public IDestructorExtension getDestructor(String name) {
		return destructors.get(name);
	}

	@Override
	public FormulaFactory getBaseFactory() {
		return baseFactory;
	}

	@Override
	public FormulaFactory getFactory() {
		return baseFactory.withExtensions(getExtensions());
	}

	@Override
	public Set<IFormulaExtension> getExtensions() {
		return unmodifiableSet(extensions);
	}

	/**
	 * The hash code is computed from the datatype's type constructors,
	 * constructor extensions, and the origin.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + typeCons.hashCode();
		result = prime * result + constructors.hashCode();
		result = prime * result + (origin == null ? 0 : origin.hashCode());
		return result;
	}

	/**
	 * The datatypes are the same if they have the same name and "similar" type
	 * constructors, constructor extensions, and origins.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final Datatype other = (Datatype) obj;
		return this.typeCons.isSimilarTo(other.typeCons)
				&& areSimilarConstructors(this.constructors.values(),
						other.constructors.values()) &&
						areSimilarOrigins(this.getOrigin(), other.getOrigin());
	}

	/**
	 * Utility method to compare the datatype origin.
	 * 
	 * @param mine
	 *            mine origin
	 * @param other
	 *            the other's origin
	 * @return <code>true</code> if the two origin are equal.
	 */
	private boolean areSimilarOrigins(Object mine, Object other) {
		if (mine == null)
			return (other == null);
		return mine.equals(other);
	}

	private static boolean areSimilarConstructors(
			Collection<ConstructorExtension> cons1,
			Collection<ConstructorExtension> cons2) {
		if (cons1.size() != cons2.size()) {
			return false;
		}
		final Iterator<ConstructorExtension> it1 = cons1.iterator();
		final Iterator<ConstructorExtension> it2 = cons2.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			if (!it1.next().isSimilarTo(it2.next())) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Returns the specification of this datatype as it is used in tests.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		typeCons.toString(sb);
		String sep = " ::= ";
		for (final ConstructorExtension cons : constructors.values()) {
			sb.append(sep);
			sep = " || ";
			cons.toString(sb);
		}
		return sb.toString();
	}

	@Override
	public Object getOrigin() {
		return origin;
	}

}

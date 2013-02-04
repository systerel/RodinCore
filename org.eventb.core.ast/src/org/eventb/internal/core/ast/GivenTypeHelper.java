/*******************************************************************************
 * Copyright (c) 2013, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;

/**
 * Utility class for recognizing given types, extracting their identifiers from
 * a type and sort them.
 * 
 * @author Laurent Voisin
 */
public class GivenTypeHelper {

	/**
	 * Returns whether the given type is a powerset of a <code>GivenType</code>
	 * and its name is the given name. This is equivalent to
	 * 
	 * <pre>
	 * ff.makeFreeIdentifier(name, null, type).isATypeExpression()
	 * </pre>
	 * 
	 * but avoids the creation of a needless identifier.
	 * @param name
	 *            the name to check
	 * @param type
	 *            the type to check
	 * 
	 * @return <code>true</code> iff <code>type</code> denotes the type of a
	 *         given set named <code>name</code>
	 */
	public static boolean isGivenSet(String name, Type type) {
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			final GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}

	// Natural order on free identifiers (for sorting by name)
	private static final Comparator<FreeIdentifier> comparator = //
	new Comparator<FreeIdentifier>() {

		@Override
		public int compare(FreeIdentifier o1, FreeIdentifier o2) {
			return o1.getName().compareTo(o2.getName());
		}

	};

	/**
	 * Returns a sorted array containing the free identifiers for the given
	 * types that occur in the type given as argument.
	 * 
	 * @param type
	 *            some solved type
	 * @param factory
	 *            the formula factory to use for building the identifiers
	 * @return a sorted array of given type identifiers
	 */
	public static FreeIdentifier[] getGivenTypeIdentifiers(Type type,
			FormulaFactory factory) {
		final Set<GivenType> givenTypes = type.getGivenTypes();
		final FreeIdentifier[] result = new FreeIdentifier[givenTypes.size()];
		int idx = 0;
		for (final GivenType givenType : givenTypes) {
			result[idx++] = givenType.toExpression();
		}
		Arrays.sort(result, comparator);
		return result;
	}

}

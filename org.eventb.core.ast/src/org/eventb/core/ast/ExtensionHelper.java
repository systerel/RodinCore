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
package org.eventb.core.ast;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.IntStack;
import org.eventb.internal.core.ast.LegibilityResult;
import org.eventb.internal.core.ast.Position;
import org.eventb.internal.core.typecheck.TypeUnifier;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 * 
 */
/* package */class ExtensionHelper {

	public static class ExtensionGatherer extends DefaultVisitor {
		
		private final Set<IFormulaExtension> extensions;

		public ExtensionGatherer(Set<IFormulaExtension> extensions) {
			this.extensions = extensions;
		}
		
		@Override
		public boolean enterExtendedExpression(ExtendedExpression expression) {
			extensions.add(expression.getExtension());
			return true;
		}
		
		@Override
		public boolean enterExtendedPredicate(ExtendedPredicate predicate) {
			extensions.add(predicate.getExtension());
			return true;
		}
	}
	
	public static Formula<?>[] concat(Expression[] expressions,
			Predicate[] predicates) {
		final Formula<?>[] children = new Formula<?>[expressions.length
				+ predicates.length];
		for (int i = 0; i < expressions.length; i++) {
			children[i] = expressions[i];
		}
		for (int i = 0; i < predicates.length; i++) {
			children[expressions.length + i] = predicates[i];
		}
		return children;
	}

	public static boolean solveTypes(TypeUnifier unifier,
			Expression[] expressions, Predicate[] predicates) {
		boolean success = true;
		for (Expression expression : expressions) {
			success &= expression.solveType(unifier);
		}
		for (Predicate predicate : predicates) {
			success &= predicate.solveType(unifier);
		}
		return success;
	}

	public static void addGivenTypes(Set<GivenType> set,
			Expression[] expressions, Predicate[] predicates) {
		for (Expression expression : expressions) {
			expression.addGivenTypes(set);
		}
		for (Predicate predicate : predicates) {
			predicate.addGivenTypes(set);
		}
	}

	public static void collectFreeIdentifiers(
			LinkedHashSet<FreeIdentifier> freeIdentSet,
			Expression[] expressions, Predicate[] predicates) {
		for (Expression expression : expressions) {
			expression.collectFreeIdentifiers(freeIdentSet);
		}
		for (Predicate predicate : predicates) {
			predicate.collectFreeIdentifiers(freeIdentSet);
		}
	}

	public static void collectNamesAbove(Set<String> names,
			String[] boundNames, int offset, Expression[] expressions,
			Predicate[] predicates) {
		for (Expression child : expressions) {
			child.collectNamesAbove(names, boundNames, offset);
		}
		for (Predicate child : predicates) {
			child.collectNamesAbove(names, boundNames, offset);
		}
	}

	// TODO use above the same generic types as below
	// TODO idea: implement an iterator on bi-arrays

	// returns the formula at index as if array1 and array2 were concatenated
	public static <T extends Formula<T>, U extends Formula<U>> Formula<?> getFormula(
			T[] array1, U[] array2, int index) {
		if (index < 0) {
			return null;
		}
		if (index < array1.length) {
			return array1[index];
		}
		index = index - array1.length;
		if (index < array2.length) {
			return array2[index];
		}
		return null;
	}

	public static <T extends Formula<T>, U extends Formula<U>> IPosition getDescendantPos(
			T[] children1, U[] children2, SourceLocation sloc, IntStack indexes) {
		indexes.push(0);
		for (T child : children1) {
			IPosition pos = child.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		for (U child : children2) {
			IPosition pos = child.getPosition(sloc, indexes);
			if (pos != null)
				return pos;
			indexes.incrementTop();
		}
		indexes.pop();
		return new Position(indexes);
	}

	public static <T extends Formula<T>, U extends Formula<U>> void isLegible(
			T[] array1, U[] array2, LegibilityResult result) {
		AssociativeHelper.isLegibleList(array1, result);
		AssociativeHelper.isLegibleList(array2, result);
	}

}

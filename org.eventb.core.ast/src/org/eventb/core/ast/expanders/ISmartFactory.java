/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.expanders;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * This interface provides some methods for easily creating optimized predicates
 * and expressions. Instances of this interface can be created using
 * {@link Expanders#getSmartFactory(FormulaFactory)}.
 * <p>
 * All predicates and expressions manipulated here must be well-typed.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface ISmartFactory {

	/**
	 * Returns the formula factory on which this smart factory is based. The
	 * returned formula factory is the one which is used internally to create
	 * formulas.
	 * 
	 * @return the underlying formula factory
	 */
	FormulaFactory getFormulaFactory();

	/**
	 * Returns a predicate telling that the two given expressions are disjoint
	 * sets. The predicate is optimized for the cases where one or both
	 * expressions are a singleton set.
	 * 
	 * @param left
	 *            a set expression of type ℙ(α)
	 * @param right
	 *            a set expression of type ℙ(α)
	 * @return a predicate telling that the two given expressions are disjoint
	 *         sets
	 */
	Predicate disjoint(Expression left, Expression right);

	/**
	 * Returns the only member of the given expression when it is a singleton
	 * set.
	 * 
	 * @param expr
	 *            a set expression of type ℙ(α)
	 * @return the only member of the given expression or <code>null</code> if
	 *         the given expression is not a singleton set
	 */
	Expression getSingletonMember(Expression expr);

	/**
	 * Returns the empty set of the given type.
	 * 
	 * @param type
	 *            the type of the result (must be of form ℙ(α))
	 * @return the empty set of the given type
	 */
	Expression emptySet(Type type);

	/**
	 * Returns a predicate telling that the two given expressions are equal.
	 * 
	 * @param left
	 *            an expression of type α
	 * @param right
	 *            an expression of type α
	 * @return a predicate telling that the two given expressions are equal
	 */
	Predicate equals(Expression left, Expression right);

	/**
	 * Returns an expression representing the intersection of the arguments. The
	 * returned expression is optimized for the cases where there are zero or
	 * one expression given.
	 * 
	 * @param type
	 *            a type of form ℙ(α)
	 * @param exprs
	 *            some set expressions of type ℙ(α)
	 * @return the intersection of the given expressions
	 */
	Expression inter(Type type, Expression... exprs);

	/**
	 * Returns a predicate telling that the first expression is a member of the
	 * second expression. The returned predicate is optimized for the cases
	 * where the second expression is an empty set, a singleton set or a type.
	 * 
	 * @param member
	 *            an expression of type α
	 * @param set
	 *            a set expression of type ℙ(α)
	 * @return a predicate telling that <code>member</code> is a member of
	 *         <code>set</code>
	 */
	Predicate in(Expression member, Expression set);

	/**
	 * Returns the conjunction of the given predicates. The result is optimized
	 * for the cases where the given list contains zero or one element.
	 * 
	 * @param predicates
	 *            a list of predicates
	 * @return the conjunction of the given predicates
	 */
	Predicate land(List<Predicate> predicates);

	/**
	 * Returns the disjunction of the given predicates. The result is optimized
	 * for the cases where the given list contains zero or one element.
	 * 
	 * @param predicates
	 *            a list of predicates
	 * @return the disjunction of the given predicates
	 */
	Predicate lor(List<Predicate> predicates);

	/**
	 * Returns the negation of the given predicate. The result is optimized
	 * for the case where the given predicate is already negated.
	 * 
	 * @param predicate
	 *            a predicate
	 * @return the negation of the given predicate
	 */
	Predicate not(Predicate predicate);

	/**
	 * Returns an expression representing the union of the arguments. The
	 * returned expression is optimized for the cases where there are zero or
	 * one expression given.
	 * 
	 * @param type
	 *            a type of form ℙ(α)
	 * @param exprs
	 *            some set expressions (type of form ℙ(α))
	 * @return the union of the given expressions
	 */
	Expression union(Type type, Expression... exprs);

}
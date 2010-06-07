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
package org.eventb.internal.core.ast.wd;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * Node is is the abstract base class for all nodes of the tree that would be
 * used to simplify the WD Lemma. There are four types of nodes (NODE_FORALL,
 * NODE_IMPLICATION,NODE_CONJONCTION and NODE_PREDICATE).
 */

public abstract class Node {

	/**
	 * Returns the depth of a tree (maximal number of quantifiers associated
	 * with a predicate).
	 * 
	 * Example : "x∈dom(f)∧f∈ℤ ⇸ ℤ∧y∈dom(f)". The returned depth will be 0.
	 * 
	 * Example : "∀f·f∈S ⇸ S ⇒(∀x·x∈dom(f) ∧(f(x)=f(x) ⇒ (∀y·y∈dom(f))))". The
	 * returned depth will be 3.
	 * 
	 * @return depth of the tree
	 */
	public abstract int depth();

	/**
	 * Shift the bound identifiers from an amount depending on the deepness of
	 * this predicate in the tree to allow comparison between predicates with
	 * different depth.
	 * 
	 * Example : "∀ x·(x∈dom(f)⇒(∀y·x∈dom(f)∧f∈S⇸S∧y∈dom(f)∧f∈S⇸S)". The two
	 * "x∈dom(f)" predicates have different bound identifier indexes in this WD
	 * Lemma and we need to apply an offset of 1 to the first predicate if we
	 * want an exact comparison between predicates.
	 * 
	 * @param offset
	 * @param ff 
	 */
	public abstract void quantifierShifter(int offset, FormulaFactory ff);

	/**
	 * Shift the bound identifiers from an amount depending on the deepness of
	 * this predicate in the tree to get back to the original situation.
	 * 
	 * @param offset
	 * @param ff 
	 */
	public abstract void quantifierUnShifter(int offset, FormulaFactory ff);

	/**
	 * Build a new AST using the simplified tree
	 * 
	 * @param fb
	 *            formula builder to use for building the result
	 * @return Predicate The simplified AST
	 */
	public abstract Predicate asPredicate(FormulaBuilder fb);

	/**
	 * Find isolated predicates (predicates which are not part of an
	 * implication) while traversing a tree or a subtree and simplify redundant
	 * predicates. Don't traverse implications.
	 * 
	 * @param knownPredicates
	 */
	public abstract void simplifyIsolatedPredicates(
			Set<Predicate> knownPredicates);

	/**
	 * Search implications while traversing a tree or a subtree. When an
	 * implication is found, creates a local set of knowPredicates to avoid to
	 * add a predicate which are a part of an implication to the global set of
	 * known predicates.
	 * 
	 * @param knownPredicates
	 */
	public abstract void simplifyImplications(Set<Predicate> knownPredicates);

}
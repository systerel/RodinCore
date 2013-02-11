/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.wd;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * An abstract base class for all nodes of the tree that would be used to
 * simplify the WD Lemma.
 * <p>
 * The tree will be created when the method improve()from WDImprover will be
 * called.There are four types of nodes
 * <li>NODE_FORALL</li>
 * <li>NODE_IMPLICATION</li>
 * <li>NODE_CONJONCTION</li>
 * <li>NODE_PREDICATE</li>
 * </p>
 * <p>
 * Example: The following formula
 * 
 * <pre>
 *  ∀ x, y · 
 *      x ↦ y∈p_at 
 *      	⇒ 	
 *      	     y∈dom(p_c_inv)
 *      	   ∧ p_c_inv∈ CO ⇸ CO
 *      	   ∧ x∈dom(p_c_inv) 
 *      	   ∧ p_c_inv∈ CO ⇸ CO
 * </pre>
 * 
 * will be represented as
 * 
 * <pre>
 * FORALL
 *  LIMP
 *   x ↦ y ∈p_at
 *    LAND
 *     y ∈ dom(p_c_inv)
 *     p_c_inv∈CO ⇸ CO
 *     x ∈ dom(p_c_inv)
 *     p_c_inv∈CO ⇸ CO
 * </pre>
 * 
 * </p>
 * <p>
 * In this class, when a method appears in two versions : x and internalx,
 * internalx is the method intended to be overrided in other classes and x is
 * the method that would be called externally.
 * </p>
 */
public abstract class Node {

	/**
	 * if <code>true</code>, the tree node could be substituted by the a node
	 * with true predicate because the corresponding sub-predicate is subsumed
	 * by another one. If <code>false</code>, it means that the constructed tree
	 * will be identical to the original one.
	 **/
	private boolean subsumed;

	public Node() {
		this.subsumed = false;
	}

	/**
	 * Simplifies the tree representing a formula by following these different
	 * steps :
	 * <p>
	 * <li>Finds the maximal number of universal quantifiers between the root of
	 * the formula and any leaf node.</li>
	 * <li>Uses this maximal binding depth to applies an offset to all the bound
	 * identifiers to allow the comparison between all the sub-predicates.</li>
	 * <li>Finds and simplifies subsumed implications using a fresh empty set to
	 * collect the antecedents</li>
	 * <li>Uses the simplified tree to build a new AST.</li>
	 * </p>
	 * 
	 * @param fb
	 *            formula builder to use
	 * @return Predicate The simplified AST
	 */
	public Predicate simplifyTree(FormulaBuilder fb) {

		final int depthmax = maxBindingDepth();
		boundIdentifiersEqualizer(depthmax, fb.ff);
		Set<Lemma> knownLemmas = new LinkedHashSet<Lemma>();
		simplify(knownLemmas, new HashSet<Predicate>(), fb);
		return asPredicate(fb, true).flatten();
	}

	/**
	 * Returns the depth of a tree (maximal number of quantifiers associated
	 * with a predicate).
	 * <p>
	 * Example : "x∈dom(f)∧f∈ℤ ⇸ ℤ∧y∈dom(f)". The returned depth will be 0.
	 * </p>
	 * <p>
	 * Example : "∀f·f∈S ⇸ S ⇒(∀x·x∈dom(f) ∧(f(x)=f(x) ⇒ (∀y·y∈dom(f))))". The
	 * returned depth will be 3.
	 * </p>
	 * 
	 * @return depth of the tree
	 */
	protected abstract int maxBindingDepth();

	/**
	 * Applies an offset to the bound identifiers from an amount depending on
	 * the deepness of this predicate in the tree to allow comparison between
	 * predicates with different depth.
	 * 
	 * <p>
	 * The offset is equal to 0 for predicates having the maximal number of
	 * universal quantifiers associated to them, and to the maximal binding
	 * depth for predicates with no quantifiers associated. This transformation
	 * is equivalent to move all the universal quantifiers to the root of the
	 * formula.
	 * </p>
	 * 
	 * <p>
	 * Example : "∀ x·x∈dom(f)⇒(∀y·x∈dom(f)∧f∈S⇸S∧y∈dom(f)∧f∈S⇸S)". The two
	 * "x∈dom(f)" predicates have different bound identifier indexes in this WD
	 * Lemma and we need to apply an offset of 1 to the first predicate if we
	 * want an exact comparison between predicates. The bound identifiers
	 * indexes will be equals to the indexes of ∀
	 * x,y·x∈dom(f)⇒(x∈dom(f)∧f∈S⇸S∧y∈dom(f)∧f∈S⇸S).
	 * </p>
	 * 
	 * @param offset
	 *            offset applied to the bound identifiers
	 * @param ff
	 *            formula factory used to modify the formula
	 */
	protected abstract void boundIdentifiersEqualizer(int offset,
			FormulaFactory ff);

	/**
	 * Builds a new AST using the simplified tree
	 * 
	 * @param fb
	 *            formula builder to use for building the result
	 * @param original
	 *            <code>true</code> to get the original predicate,
	 *            <code>false</code> to get the predicate with normalized bound
	 *            identifiers
	 * @return Predicate The simplified AST
	 */
	protected final Predicate asPredicate(FormulaBuilder fb, boolean original) {
		if (!subsumed)
			return internalAsPredicate(fb, original);
		else
			return fb.btrue;
	}

	protected abstract Predicate internalAsPredicate(FormulaBuilder fb,
			boolean original);

	/**
	 * Collects the antecedents for simplifying lemmas. This method should be
	 * called with a set with the previous known antecedents, on the left-hand
	 * side of implication nodes to allow comparison between each side of the
	 * implication.
	 * 
	 * @param antecedents
	 *            set of antecedents to complete
	 * @param fb
	 *            formula builder to use
	 */
	protected abstract void collectAntecedents(Set<Predicate> antecedents,
			FormulaBuilder fb);

	/**
	 * Adds the predicate for this node to the given set, if it isn't subsumed.
	 * Conversely, if this predicate was already present in the given set, this
	 * predicate becomes subsumed.
	 * 
	 * @param set
	 *            set where predicates will be added
	 * @param fb
	 *            formula builder to use
	 */
	protected final void addPredicateToSet(Set<Predicate> set, FormulaBuilder fb) {
		if (subsumed)
			return;
		if (!set.add(internalAsPredicate(fb, false))) {
			// This predicate is already in the set
			setNodeSubsumed();
		}
	}

	/**
	 * Traverses the formula, marking as subsumed leaf nodes that are subsumed.
	 * 
	 * @param knownLemmas
	 *            lemmas that are currently known to hold
	 * @param antecedents
	 *            left-hand side context of this node to be used for building
	 *            new lemmas
	 * @param fb
	 *            formula builder to use
	 */
	protected final void simplify(Set<Lemma> knownLemmas,
			Set<Predicate> antecedents, FormulaBuilder fb) {
		if (!subsumed)
			internalSimplify(knownLemmas, antecedents, fb);
	}

	protected abstract void internalSimplify(Set<Lemma> knownLemmas,
			Set<Predicate> antecedents, FormulaBuilder fb);

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();
		toString(sb, "");
		return sb.toString();
	}

	/**
	 * Creates a string representation of the tree, avoiding to represent
	 * subsumed sub-tree.
	 * 
	 * @param sb
	 *            stringBuilder to use
	 * @param indent
	 *            string that will be added to the stringBuilder
	 */
	protected final void toString(StringBuilder sb, String indent) {
		sb.append(indent);
		if (subsumed)
			sb.append("-- ");
		internalToString(sb, indent);
	}

	protected abstract void internalToString(StringBuilder sb, String indent);

	/**
	 * Indicates that the predicate corresponding to this node is subsumed by
	 * another predicate.
	 */
	protected final void setNodeSubsumed() {
		subsumed = true;
	}

}
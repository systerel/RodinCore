/*******************************************************************************
 * Copyright (c) 2006, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - move to tom-2.8
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;

/**
 * Abstract implementation of a formula rewriter. The purpose of this class is
 * to provide various utility methods that help writing reasoners that perform
 * formula rewriting.
 * 
 * Laurent Voisin
 */
public abstract class AbstractRewriterImpl extends DefaultRewriter {

	// true enables trace messages
	protected final boolean debug;
	private final String rewriterName;

	public AbstractRewriterImpl(boolean autoFlattening, boolean debug, String rewriterName) {
		super(autoFlattening);
		this.debug = debug;
		this.rewriterName = rewriterName;
	}

	protected final <T extends Formula<T>> void trace(T from, T to, String rule, String... otherRules) {
		if (!debug) {
			return;
		}
		if (from == to) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(rewriterName);
		sb.append(": ");
		sb.append(from);
		sb.append("  \u219d  ");
		sb.append(to);

		sb.append("   (");
		sb.append(rule);
		for (final String r : otherRules) {
			sb.append(" | ");
			sb.append(r);
		}
		sb.append(")");

		System.out.println(sb);
	}

	protected AssociativePredicate makeAssociativePredicate(int tag, Predicate... children) {
		final FormulaFactory ff = children[0].getFactory();
		return ff.makeAssociativePredicate(tag, children, null);
	}

	protected BinaryPredicate makeBinaryPredicate(int tag, Predicate left, Predicate right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeBinaryPredicate(tag, left, right, null);
	}

	protected QuantifiedPredicate makeQuantifiedPredicate(int tag, //
			BoundIdentDecl[] boundIdentifiers, Predicate child) {
		final FormulaFactory ff = child.getFactory();
		return ff.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
	}

}

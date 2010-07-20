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
package org.eventb.internal.core.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Core implementation for the inspection mechanism on formulas. For each
 * formula inspection (i.e., call to {@link Formula#inspect(IFormulaInspector)}
 * ), an instance of this class is created and passed through to each
 * sub-formula during traversal.
 * <p>
 * Instances of this class thus play the role of a bridge between the
 * sub-formulas and the inspector, hiding the AST internals from clients.
 * </p>
 * 
 * @param <F>
 *            type of findings to accumulate
 * 
 * @author Laurent Voisin
 */
public class FindingAccumulator<F> implements IAccumulator<F> {

	private final IFormulaInspector<F> inspector;
	private final IntStack indexes;
	private final List<F> findings;

	public FindingAccumulator(IFormulaInspector<F> inspector) {
		this.indexes = new IntStack();
		this.findings = new ArrayList<F>();
		this.inspector = inspector;
	}

	public void add(F item) {
		findings.add(item);
	}

	public void add(F... items) {
		for (F item : items) {
			findings.add(item);
		}
	}

	public void add(Collection<F> items) {
		findings.addAll(items);
	}

	public IPosition getCurrentPosition() {
		return new Position(indexes);
	}

	public void inspect(UnaryPredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(UnaryExpression expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(SimplePredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(SetExtension expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(RelationalPredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(QuantifiedPredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(QuantifiedExpression expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(PredicateVariable predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(MultiplePredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(LiteralPredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(IntegerLiteral literal) {
		inspector.inspect(literal, this);
	}

	public void inspect(ExtendedPredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(ExtendedExpression expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(FreeIdentifier identifier) {
		inspector.inspect(identifier, this);
	}

	public void inspect(BoundIdentifier identifier) {
		inspector.inspect(identifier, this);
	}

	public void inspect(BoundIdentDecl decl) {
		inspector.inspect(decl, this);
	}

	public void inspect(BoolExpression expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(BinaryPredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(BinaryExpression expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(AtomicExpression expression) {
		inspector.inspect(expression, this);
	}

	public void inspect(AssociativePredicate predicate) {
		inspector.inspect(predicate, this);
	}

	public void inspect(AssociativeExpression expression) {
		inspector.inspect(expression, this);
	}

	public List<F> getFindings() {
		return findings;
	}

	public void enterChildren() {
		indexes.push(0);
	}

	public void nextChild() {
		indexes.incrementTop();
	}

	public void leaveChildren() {
		indexes.pop();
	}

}

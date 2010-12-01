package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public abstract class FormulaSimplification<T extends Formula<T>> {

	// Original associative formula to simplify
	protected final T original;
	protected final T[] children;

	// Children of the resulting formula, so far
	protected Collection<T> newChildren;

	// If non-null, this contains the result, ignore newChildren above
	protected T knownResult;

	protected final DLib dLib;
	protected FormulaFactory ff;

	public FormulaSimplification(T original, T[] children, DLib dLib) {
		this.original = original;
		this.children = children;
		this.dLib = dLib;
		this.ff = dLib.getFormulaFactory();

		if (eliminateDuplicate()) {
			this.newChildren = new LinkedHashSet<T>();
		} else {
			this.newChildren = new ArrayList<T>();
		}

	}

	protected abstract boolean eliminateDuplicate();

	public T simplify() {
		processChildren();
		return makeResult();
	}

	protected void processChildren() {
		for (T child : children) {
			processChild(child);
			if (knownResult != null) {
				return;
			}
		}
	}

	protected void processChild(T child) {
		if (isNeutral(child)) {
			// ignore
		} else if (isDeterminant(child)) {
			// TODO use getDeterminantResult(child) instead
			knownResult = child;
		} else if (isContradicting(child)) {
			knownResult = getContradictionResult();
		} else {
			newChildren.add(child);
		}
	}

	protected abstract boolean isNeutral(T child);

	protected abstract boolean isDeterminant(T child);

	protected abstract boolean isContradicting(T child);

	protected abstract T getContradictionResult();

	private T makeResult() {
		if (knownResult != null) {
			return knownResult;
		}
		int size = newChildren.size();
		if (size == 0) {
			return getNeutral();
		} else if (size == 1) {
			return newChildren.iterator().next();
		} else if (size != children.length) {
			return makeAssociativeFormula();
		}
		return original;
	}

	protected abstract T getNeutral();

	protected abstract T makeAssociativeFormula();

}

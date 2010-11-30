package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eventb.core.ast.Formula;
import org.eventb.core.seqprover.eventbExtensions.DLib;

public abstract class FormulaSimplification<T extends Formula<T>> {

	protected final DLib dLib;

	public FormulaSimplification(DLib lib) {
		dLib = lib;
	}

	protected abstract boolean eliminateDuplicate();

	protected abstract boolean isNeutral(T formula);

	protected abstract boolean isDeterminant(T formula);
	
	protected abstract boolean isContradicting(T formula, Collection<T> formulas);

	protected abstract T getContradictionResult();

	protected abstract T getNeutral(T formula);

	protected abstract T makeAssociativeFormula(int tag, Collection<T> formulas);

	public T simplifyAssociativeFormula(T formula, T[] children) {
		final Collection<T> formulas = simplifiedAssociativeFormula(children);
		int size = formulas.size();
		if (size == 0) {
			return getNeutral(formula);
		} else if (size == 1) {
			return formulas.iterator().next();
		} else if (size != children.length) {
			return makeAssociativeFormula(formula.getTag(), formulas);
		}
		return formula;
	}

	protected Collection<T> simplifiedAssociativeFormula(T[] children) {
		final Collection<T> formulas;
		if (eliminateDuplicate()) {
			formulas = new LinkedHashSet<T>();
		} else {
			formulas = new ArrayList<T>();
		}

		for (T child : children) {
			final T result = processChild(formulas, child);
			if (result != null) {
				return singleton(result);
			}
		}
		return formulas;
	}
	
	protected T processChild(final Collection<T> formulas, T child) {
		if (isNeutral(child)) {
			// ignore
		} else if (isDeterminant(child)) {
			return child;
		} else if (isContradicting(child, formulas)) {
			return getContradictionResult();
		} else {
			formulas.add(child);
		}
		return null;
	}

}

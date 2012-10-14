/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IDatatypeTranslation;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTranslator;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * Sequent transformer that translates occurrences of datatypes in the predicates
 * of the sequent on which it is applied.
 * 
 * @author "Thomas Muller"
 */
public class SequentDatatypeTranslator implements ISequentTranslator {

	final IDatatypeTranslation translation;

	public SequentDatatypeTranslator(ITypeEnvironment environment) {
		this.translation = environment.makeDatatypeTranslation();
	}

	@Override
	public Predicate transform(ITrackedPredicate predicate) {
		return predicate.getPredicate().translateDatatype(translation);
	}

	@Override
	public FormulaFactory getTargetFormulaFactory() {
		return translation.getTargetFormulaFactory();
	}

	@Override
	public Predicate[] getAxioms() {
		final List<Predicate> result = translation.getAxioms();
		return result.toArray(new Predicate[result.size()]);
	}

}
/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IExtensionTranslation;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTranslator;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * Sequent transformer that translates occurrences of extensions in the
 * predicates of the sequent on which it is applied.
 * 
 * @author Josselin Dolhen
 */
public class SequentExtensionTranslator implements ISequentTranslator {

	final IExtensionTranslation translation;

	public SequentExtensionTranslator(ITypeEnvironment environment) {
		this.translation = environment.makeExtensionTranslation();
	}

	@Override
	public Predicate transform(ITrackedPredicate predicate) {
		return predicate.getPredicate().translateExtensions(translation);
	}

	@Override
	public FormulaFactory getTargetFormulaFactory() {
		return translation.getTargetTypeEnvironment().getFormulaFactory();
	}

	@Override
	public Predicate[] getAxioms() {
		return new Predicate[]{};
	}

 }

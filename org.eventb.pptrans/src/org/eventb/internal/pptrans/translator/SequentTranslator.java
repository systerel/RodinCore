/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.transformer.ISequentTransformer;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;
import org.eventb.internal.pptrans.translator.Translator;
import org.eventb.pptrans.Translator.Option;

/**
 * New implementation of translation implemented as a sequent transformer.
 * 
 * @author Laurent Voisin
 */
public class SequentTranslator implements ISequentTransformer {

	private final Translator translator;

	public SequentTranslator(ISimpleSequent sequent, Option... options) {
		final FormulaFactory ff = sequent.getFormulaFactory();
		this.translator = new Translator(ff, options);
	}

	@Override
	public Predicate transform(ITrackedPredicate tpred) {
		final Predicate pred = tpred.getPredicate();
		try {
			return translator.translate(pred);
		} catch (UnsupportedOperationException e) {
			return null;
		}
	}

}

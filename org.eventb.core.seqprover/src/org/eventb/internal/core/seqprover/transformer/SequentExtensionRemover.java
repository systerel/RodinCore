/*******************************************************************************
 * Copyright (c) 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.transformer;

import java.util.Iterator;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.seqprover.transformer.ISequentTranslator;
import org.eventb.core.seqprover.transformer.ITrackedPredicate;

/**
 * Sequent transformer that removes any predicate that cannot be translated to
 * the plain mathematical language, i.e., that contains non WD-strict
 * extensions.
 * 
 * @author Laurent Voisin
 */
public class SequentExtensionRemover implements ISequentTranslator {

	private static final Predicate[] NO_PREDS = new Predicate[] {};

	private final FormulaFactory srcFac;
	private final FormulaFactory trgFac;

	public SequentExtensionRemover(FormulaFactory srcFac) {
		this.srcFac = srcFac;
		this.trgFac = makeTargetFactory(srcFac);
	}

	/*
	 * Returns the same factory with all non WD-strict extensions removed.
	 */
	private FormulaFactory makeTargetFactory(FormulaFactory fac) {
		final Set<IFormulaExtension> exts = fac.getExtensions();
		final Iterator<IFormulaExtension> iter = exts.iterator();
		boolean changed = false;
		while (iter.hasNext()) {
			final IFormulaExtension ext = iter.next();
			if (!ext.conjoinChildrenWD()) {
				iter.remove();
				changed = true;
			}
		}
		if (!changed) {
			return fac;
		}
		return FormulaFactory.getInstance(exts);
	}

	// Shall we translate the sequent ?
	public boolean needsTranslation() {
		return srcFac != trgFac;
	}

	@Override
	public FormulaFactory getTargetFormulaFactory() {
		return trgFac;
	}

	/*
	 * We attempt to translate the predicate and return null if not
	 * translatable. Catching IllegalArgumentException allows to avoid
	 * traversing twice the formula (once for checking, then once for
	 * translating), at the risk of hiding a potential bug (i.e., if the
	 * exception is raised for another reason than use of a removed extension),
	 * but this is quite unlikely as this code is quite stable.
	 */
	@Override
	public Predicate transform(ITrackedPredicate tpred) {
		final Predicate pred = tpred.getPredicate();
		try {
			return pred.translate(trgFac);
		} catch (IllegalArgumentException exc) {
			return null;
		}
	}

	@Override
	public Predicate[] getAxioms() {
		return NO_PREDS;
	}

}

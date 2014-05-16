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
package org.eventb.internal.core.ast;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IDatatypeTranslation;
import org.eventb.core.ast.IExtensionTranslation;
import org.eventb.core.ast.ISealedTypeEnvironment;

/**
 * Common implementation for translations of mathematical extensions.
 * 
 * This class contains the code common to datatype and other extension
 * translations.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractTranslation {

	protected final ISealedTypeEnvironment srcTypenv;

	public AbstractTranslation(ISealedTypeEnvironment srcTypenv) {
		this.srcTypenv = srcTypenv;
	}

	public final ISealedTypeEnvironment getSourceTypeEnvironment() {
		return srcTypenv;
	}

	/**
	 * Verifies that the given formula can be translated by this instance. If
	 * this is not the case, then raise the appropriate exception, as documented
	 * in {@link Formula#translateDatatype(IDatatypeTranslation)} and
	 * {@link Formula#translateExtensions(IExtensionTranslation)}.
	 * 
	 * @param formula
	 *            some formula
	 */
	public void ensurePreconditions(Formula<?> formula) {
		final FormulaFactory fac = formula.getFactory();
		if (fac != srcTypenv.getFormulaFactory()) {
			throw new IllegalArgumentException("Formula factory " + fac
					+ " incompatible with translation " + this);
		}
		for (final FreeIdentifier ident : formula.getFreeIdentifiers()) {
			if (!srcTypenv.contains(ident)) {
				throw new IllegalArgumentException("Free identifier " + ident
						+ " is not part of the source type environment of "
						+ this);
			}
		}
	}

	public abstract ITypeCheckingRewriter getFormulaRewriter();

}

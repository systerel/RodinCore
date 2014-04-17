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
package org.eventb.internal.core.ast.extension;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.FreshNameSolver;
import org.eventb.internal.core.ast.datatype.DatatypeTranslation;

/**
 * Translation of operator extensions to function applications. We do not
 * translate extensions that come from a datatype (these are taken care by
 * {@link DatatypeTranslation}), nor extensions that are not WD-strict (as these
 * cannot be translated to mere function application).
 * 
 * We maintain an associative table from operator signatures to dedicated
 * translators.
 * 
 * @author Thomas Muller
 */
public class ExtensionTranslation {

	private final FormulaFactory trgFactory;
	private final ITypeEnvironmentBuilder trgTypenv;
	private final FreshNameSolver nameSolver;

	public ExtensionTranslation(ITypeEnvironment srcTypenv) {
		this.trgFactory = computeTargetFactory(srcTypenv.getFormulaFactory());
		this.trgTypenv = srcTypenv.translate(trgFactory).makeBuilder();
		this.nameSolver = new FreshNameSolver(trgTypenv);
	}

	private static FormulaFactory computeTargetFactory(FormulaFactory fac) {
		final Set<IFormulaExtension> keptExtensions;
		final Set<IFormulaExtension> extensions = fac.getExtensions();
		keptExtensions = new LinkedHashSet<IFormulaExtension>();
		for (final IFormulaExtension extension : extensions) {
			if (extension.getOrigin() instanceof IDatatype) {
				// Keep datatype extensions
				keptExtensions.add(extension);
			}
			if (!extension.conjoinChildrenWD()) {
				// Keep extensions that are not WD-strict
				keptExtensions.add(extension);
			}
		}
		return FormulaFactory.getInstance(keptExtensions);
	}

	public FormulaFactory getTargetFactory() {
		return trgFactory;
	}

	public ISealedTypeEnvironment getTargetTypeEnvironment() {
		return trgTypenv.makeSnapshot();
	}

	public final FreeIdentifier solveIdentifier(String symbol, Type type) {
		final String solvedIdentName = nameSolver.solveAndAdd(symbol);
		return trgFactory.makeFreeIdentifier(solvedIdentName, null, type);
	}

}

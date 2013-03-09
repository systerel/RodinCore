/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import static java.util.Collections.emptySet;

import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;

/**
 * Translates a formula to some target factory.
 * 
 * Most of the work is done by the super class. The only special case to handle
 * is a bound identifier declaration whose name becomes a reserved keyword.
 * 
 * @author Laurent Voisin
 */
public class FormulaTranslator extends DefaultTypeCheckingRewriter {

	private static final Set<String> NO_NAMES = emptySet();

	private final FreshNameSolver solver = new FreshNameSolver(NO_NAMES, ff);

	public FormulaTranslator(FormulaFactory target) {
		super(target);
	}

	@Override
	public BoundIdentDecl rewrite(BoundIdentDecl src) {
		if (ff == src.getFactory()) {
			return src;
		}
		final String name = solver.solve(src.getName());
		final Type type = typeRewriter.rewrite(src.getType());
		final SourceLocation sloc = src.getSourceLocation();
		return ff.makeBoundIdentDecl(name, sloc, type);
	}

}
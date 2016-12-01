/*******************************************************************************
 * Copyright (c) 2006, 2016 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eventb.core.ast.FormulaFactory;

/**
 * Abstract super class for all kinds of substitutions operated on formulas.
 * 
 * @author Stefan Hallerstede
 */
public abstract class Substitution extends DefaultTypeCheckingRewriter {

	public Substitution(FormulaFactory ff) {
		super(ff);
	}

	public Substitution(TypeRewriter typeRewriter) {
		super(typeRewriter);
	}

}

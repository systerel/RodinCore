/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.elements.terms;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for binary terms.
 *
 * @author François Terrier
 *
 */
public abstract class BinaryTerm extends AssociativeTerm {
	
	public BinaryTerm(Term left, Term right, int priority) {
		super(Arrays.asList(new Term[]{left, right}), priority);
		
		assert left != null && right != null;
		
	}
	
	protected BinaryTerm(List<Term> terms, int priority) {
		super(terms, priority);
	}
	
	public Term getLeft() {
		return children.get(0);
	}
	
	public Term getRight() {
		return children.get(1);
	}

}

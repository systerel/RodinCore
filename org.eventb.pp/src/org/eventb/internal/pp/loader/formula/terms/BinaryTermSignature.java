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
package org.eventb.internal.pp.loader.formula.terms;

import java.util.Arrays;
import java.util.List;


/**
 * This class represents a binary term signature. It is the abstract
 * base class for all binary terms.
 *
 * @author François Terrier
 *
 */
public abstract class BinaryTermSignature extends AssociativeTermSignature {

	public BinaryTermSignature(TermSignature left, TermSignature right) {
		super(Arrays.asList(new TermSignature[]{left,right}));
	}
	
	protected BinaryTermSignature(List<TermSignature> terms) {
		super(terms);
		assert terms.size() == 2;
	}

	/**
	 * Returns the left child.
	 * 
	 * @return the left child
	 */
	public TermSignature getLeft() {
		return terms.get(0);
	}
	
	/**
	 * Returns the right child.
	 * 
	 * @return the right child
	 */
	public TermSignature getRight() {
		return terms.get(1);
	}
	
}

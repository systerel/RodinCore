/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.expanders;

import static org.eventb.core.ast.Formula.KPARTITION;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.ast.expanders.PartitionExpander;
import org.eventb.internal.core.ast.expanders.SmartFactory;

/**
 * This class provides utility methods for expanding the definitions of some
 * mathematical operators. All provided methods are static.
 * <p>
 * This class is not intended to be sub-classed by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Expanders {

	/**
	 * Returns a smart factory based on the given formula factory.
	 * 
	 * @param factory
	 *            the formula factory to use
	 * @return a smart factory based on the given formula factory
	 */
	public static ISmartFactory getSmartFactory(FormulaFactory factory) {
		return new SmartFactory(factory);
	}

	/**
	 * Returns the expansion of a <code>partition</code> predicate, that is its
	 * definition using simpler mathematical operators.
	 * 
	 * @param pred
	 *            the partition predicate to expand
	 * @return the definition of the predicate
	 */
	public static Predicate expandPARTITION(Predicate pred, FormulaFactory ff) {
		assert pred.getTag() == KPARTITION;
		return new PartitionExpander((MultiplePredicate) pred, ff).expand();
	}

}

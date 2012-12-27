/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.transformer;

import org.eventb.core.ast.Predicate;

/**
 * Common protocol describing a predicate transformer that can be applied to
 * sequents. When applied to a sequent, method <code>transform</code> is called
 * on each predicate of the sequent and a new sequent is created with the
 * transformed predicates.
 * <p>
 * The formula factory to use during transformation should be obtained by the
 * transformer through other means (e.g., obtained from the simple sequent to
 * which this transformer is applied).
 * </p>
 * <p>
 * An alternative richer version of this interface is available:
 * {@link ISequentTranslator}.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 2.3
 * @see ISimpleSequent#apply(ISequentTransformer)
 */
public interface ISequentTransformer {

	/**
	 * Transforms a predicate into another predicate. In some context, it might
	 * be assumed that the original and transformed predicates are equivalent or
	 * equisatisfiable. However, such assumption is not part of the contract of
	 * this method and must be enforced at a higher level.
	 * <p>
	 * A <code>null</code> value can be returned to indicate that this
	 * transformer cannot handle the given predicate.
	 * </p>
	 * 
	 * @param predicate
	 *            tracked predicate to transform
	 * @return a transformed predicate or <code>null</code>
	 */
	Predicate transform(ITrackedPredicate predicate);

}

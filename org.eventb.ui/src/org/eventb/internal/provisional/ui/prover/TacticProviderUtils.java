/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.provisional.ui.prover;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.ui.prover.DefaultTacticProvider;

/**
 * Utility class intended to give convenient facilities to tactic provider
 * contributors.
 * <p>
 * <strong>EXPERIMENTAL</strong>. This interface has been added as part of a
 * work in progress. There is no guarantee that this API will work or that it
 * will remain the same. Please do not use this API without consulting with the
 * Systerel team.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.1
 * 
 */
public class TacticProviderUtils {

	private TacticProviderUtils() {
		// utility class: do not instantiate
	}

	/**
	 * A default method to get an operator position. Intended to be used by
	 * implementors of
	 * {@link IPositionApplication#getHyperlinkBounds(String, Predicate)}.
	 * 
	 * @param predicate
	 *            the predicate where a position is desired
	 * @param predStr
	 *            the string representation of the predicate
	 * @param position
	 *            the position of the operator in the predicate
	 * @return a Point with x (inclusive) and y (exclusive) as operator position
	 */
	public static Point getOperatorPosition(Predicate predicate,
			String predStr, IPosition position) {
		return new DefaultTacticProvider().getOperatorPosition(predicate,
				predStr, position);
	}
}

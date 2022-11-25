/*******************************************************************************
 * Copyright (c) 2009, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.prover;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.ui.prover.DefaultTacticProvider.DefaultPositionApplication;

/**
 * Utility class intended to give convenient facilities to tactic provider
 * contributors.
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
		return new DefaultPositionApplication(null, position)
				.getOperatorPosition(predicate, predStr);
	}

	/**
	 * Adapt a function generating a list of positions to generate tactic
	 * applications.
	 *
	 * @param hyp                hypothesis used or {@code null} if applied to the
	 *                           goal
	 * @param predicate          predicate on which positions are generated (either
	 *                           {@code hyp} or the goal)
	 * @param positionsSupplier  function generating the positions for the predicate
	 * @param applicationBuilder build a tactic application from {@code hyp} and a
	 *                           given position
	 * @return list of generated tactic applications
	 * @since 3.7
	 */
	public static List<ITacticApplication> adaptPositionsToApplications(Predicate hyp, Predicate predicate,
			Function<Predicate, List<IPosition>> positionsSupplier,
			BiFunction<Predicate, IPosition, ITacticApplication> applicationBuilder) {
		return positionsSupplier.apply(predicate).stream().map(p -> applicationBuilder.apply(hyp, p)).collect(toList());
	}

}

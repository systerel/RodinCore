/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.Collections;
import java.util.List;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;

/**
 * @author Nicolas Beauger
 * 
 */
public class RefinementRegistry {

	public static class RefinementException extends Exception {
		private static final long serialVersionUID = 3388507398186210462L;

		public RefinementException(String message) {
			super(message);
		}
	}

	private static final RefinementRegistry DEFAULT_INSTANCE = new RefinementRegistry();

	private RefinementRegistry() {
		// singleton
	}

	public static RefinementRegistry getDefault() {
		return DEFAULT_INSTANCE;
	}

	/**
	 * Returns an ordered list of refinement participants for the given root
	 * element type. The order is computed from contributions. In case a cycle
	 * occurs, <code>null</code> is returned.
	 * 
	 * @param rootType
	 *            a root element type
	 * @return a list of refinement participants, or <code>null</code>
	 */
	public List<IRefinementParticipant> getRefinementParticipants(
			IInternalElementType<?> rootType) throws RefinementException {
		return Collections.emptyList();
	}

	// TODO lazy loading, on request, for a particular root type

	public void load(IInternalElementType<?> rootType)
			throws RefinementException {

	}

	// TODO refinements could be removed and replaced with references to root types
	// but that would prevent from adding new attributes to refinements in the future
	public void addRefinement(IInternalElementType<?> rootType,
			String refinementId) throws RefinementException {

	}

	public void addParticipant(IRefinementParticipant refinementParticipant,
			String participantId, IInternalElementType<?> rootType)
			throws RefinementException {

	}

	public void addOrder(String participantId1,
			String participantId2)
			throws RefinementException {

	}

	public void clear() {

	}

}

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRefinementParticipant;
import org.rodinp.core.RodinCore;
import org.rodinp.internal.core.util.Util;

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

	private static final String REFINEMENT_EXTENSION_POINT_ID = RodinCore.PLUGIN_ID
			+ ".refinements";

	private static final String REFINEMENT = "Refinement";
	private static final String PARTICIPANT = "Participant";
	private static final String ORDER = "Order";
	private static final String ID = "id";
	private static final String ROOT_TYPE = "root-element-type";
	private static final String REFINEMENT_ID = "refinement-id";
	private static final String CLASS = "class";
	private static final String FIRST_PARTICIPANT = "first-participant-id";
	private static final String SECOND_PARTICIPANT = "second-participant-id";

	private static final RefinementRegistry DEFAULT_INSTANCE = new RefinementRegistry();

	private RefinementRegistry() {
		// singleton
	}

	public static RefinementRegistry getDefault() {
		return DEFAULT_INSTANCE;
	}

	// refinements per root type
	private final Map<IInternalElementType<?>, Refinement> refinements = new HashMap<IInternalElementType<?>, Refinement>();

	// refinements per id
	private final Map<String, Refinement> refinementIds = new HashMap<String, Refinement>();

	// all participant ids with associated refinement
	private final Map<String, Refinement> participants = new HashMap<String, Refinement>();

	/**
	 * Returns an ordered list of refinement participants for the given root
	 * element type. The order is computed from contributions. In case no
	 * refinement is defined for the given root element type, or if a cycle
	 * occurs, <code>null</code> is returned.
	 * 
	 * @param rootType
	 *            a root element type
	 * @return a list of refinement participants, or <code>null</code>
	 */
	public List<IRefinementParticipant> getRefinementParticipants(
			IInternalElementType<?> rootType) throws RefinementException {
		load(rootType);
		final Refinement refinement = refinements.get(rootType);
		if (refinement == null) {
			return null;
		}
		return refinement.getOrderedParticipants();
	}

	private void load(IInternalElementType<?> rootType)
			throws RefinementException {
		if (refinements.containsKey(rootType)) { // already loaded
			// FIXME refinements might have been programmatically added before
			// for now we suppose that, except in tests, the only contributions
			// come from the extension point
			return;
		}
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(REFINEMENT_EXTENSION_POINT_ID);
		// FIXME load all refinements first
		// then participants
		// then orders
		for (IConfigurationElement element : extensions) {
			final String extensionId = element.getDeclaringExtension()
					.getUniqueIdentifier();
			try {
				final String name = element.getName();
				if (name.equals(REFINEMENT)) {
					loadRefinement(element);
				} else if (name.equals(PARTICIPANT)) {
					loadParticipant(element);
				} else if (name.equals(ORDER)) {
					loadOrder(element);
				}
			} catch (Exception e) {
				extensionLoadException(extensionId, e);
				// continue
			}
		}

	}

	private void extensionLoadException(String extensionId, Exception e) {
		Util.log(e, "Exception while loading refinement extension "
				+ extensionId);
	}

	private void loadRefinement(IConfigurationElement element)
			throws RefinementException {
		final String bareId = element.getAttribute(ID);
		checkNoDots(bareId);
		final String namespace = element.getNamespaceIdentifier();
		final String refinementId = namespace + "." + bareId;
		final String rootElType = element.getAttribute(ROOT_TYPE);
		final IInternalElementType<IInternalElement> rootType = RodinCore
				.getInternalElementType(rootElType);
		addRefinement(rootType, refinementId);
	}

	private static void checkNoDots(final String id) throws RefinementException {
		if (id.contains(".")) {
			throw new RefinementException("id should not contain dots " + id);
		}
	}

	private void loadParticipant(IConfigurationElement element)
			throws CoreException, RefinementException {
		final String bareId = element.getAttribute(ID);
		checkNoDots(bareId);
		final String namespace = element.getNamespaceIdentifier();
		final String participantId = namespace + "." + bareId;
		final String refinementId = element.getAttribute(REFINEMENT_ID);
		final IRefinementParticipant participant = (IRefinementParticipant) element
				.createExecutableExtension(CLASS);
		addParticipant(participant, participantId, refinementId);
	}

	private void loadOrder(IConfigurationElement element)
			throws RefinementException {
		final String first = element.getAttribute(FIRST_PARTICIPANT);
		final String second = element.getAttribute(SECOND_PARTICIPANT);
		addOrder(first, second);
	}

	public void addRefinement(IInternalElementType<?> rootType,
			String refinementId) throws RefinementException {
		if (refinements.containsKey(rootType)) {
			throw new RefinementException(
					"A refinement for the same root type already exists: failed to add "
							+ refinementId);
		}
		if (refinementIds.containsKey(refinementId)) {
			throw new RefinementException(
					"A refinement with the same id already exists: failed to add "
							+ refinementId);
		}

		final Refinement refinement = new Refinement(rootType);
		refinementIds.put(refinementId, refinement);
		refinements.put(rootType, refinement);
	}

	private void addParticipant(IRefinementParticipant refinementParticipant,
			String participantId, String refinementId)
			throws RefinementException {
		final Refinement refinement = refinementIds.get(refinementId);
		if (refinement == null) {
			throw new RefinementException("unknown refinement " + refinementId);
		}
		final IInternalElementType<?> rootType = refinement.getRootType();
		addParticipant(refinementParticipant, participantId, rootType);
	}

	public void addParticipant(IRefinementParticipant refinementParticipant,
			String participantId, IInternalElementType<?> rootType)
			throws RefinementException {
		if (participants.containsKey(participantId)) {
			throw new RefinementException(
					"A refinement participant with the same id already exists: "
							+ participantId);
		}

		final Refinement refinement = refinements.get(rootType);
		if (refinement == null) {
			throw new RefinementException("failed to add participant "
					+ participantId
					+ " because no refinement is defined for root type "
					+ rootType);
		}
		refinement.add(refinementParticipant, participantId);
		participants.put(participantId, refinement);
	}

	public void addOrder(String participantId1, String participantId2)
			throws RefinementException {
		checkKnownParticipant(participantId1);
		checkKnownParticipant(participantId2);
		final Refinement ref = checkAndGetSameRefinement(participantId1,
				participantId2);
		ref.setOrder(participantId1, participantId2);
	}

	private void checkKnownParticipant(String partId)
			throws RefinementException {
		if (!participants.containsKey(partId)) {
			throw new RefinementException("Unknown participant " + partId);
		}
	}

	private Refinement checkAndGetSameRefinement(String partId1, String partId2)
			throws RefinementException {
		final Refinement ref1 = participants.get(partId1);
		final Refinement ref2 = participants.get(partId2);
		if (ref1 != ref2) {
			throw new RefinementException(
					"Cannot set an order between participants of different refinements: "
							+ partId1 + ", " + partId2);
		}
		return ref1;
	}

	public void clear() {
		refinements.clear();
		refinementIds.clear();
		participants.clear();
	}

}

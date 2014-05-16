/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved to a separate class and implemented version management
 *     Systerel - fixed versioning
 *******************************************************************************/
package org.eventb.internal.core.seqprover;

import static org.eventb.internal.core.seqprover.VersionedIdCodec.decodeId;
import static org.eventb.internal.core.seqprover.VersionedIdCodec.decodeVersion;
import static org.eventb.internal.core.seqprover.VersionedIdCodec.encodeVersionInId;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;

/**
 * Bridge class used by the reasoner registry to store data about reasoners and
 * provided to clients through its interface.
 * <p>
 * Implements lazy loading of reasoner instances.
 * </p>
 * <p>
 * Provides various facilities for reasoner version management.
 * </p>
 * 
 * @author "Farhad Mehta"
 * @author "Nicolas Beauger"
 */
public class ReasonerDesc implements IReasonerDesc {
	// TODO there are 3 types of reasoner descriptors:
	// * live (version from registry, dynamic signature)
	// * stored (version from proof file)
	// * unknown
	// => make 3 types
	
	private static final int UNKNOWN_VERSION = NO_VERSION - 1;
	
	protected static class ReasonerLoadingException extends Exception {
		private static final long serialVersionUID = -4173286853724624350L;

		public ReasonerLoadingException(String message) {
			super(message);
		}
	}

	/**
	 * 
	 * Protected helper class implementing dummy reasoners.
	 * <p>
	 * Used as a placeholder when we can't create the regular instance of a
	 * reasoner (wrong class, unknown id, ...).
	 * </p>
	 */
	protected static class DummyReasoner implements IReasoner {

		private final String reasonerID;

		private DummyReasoner(String reasonerID) {
			this.reasonerID = reasonerID;
		}

		@Override
		public String getReasonerID() {
			return reasonerID;
		}

		@Override
		public void serializeInput(IReasonerInput input,
				IReasonerInputWriter writer) {
			// Nothing to do
		}

		@Override
		public IReasonerInput deserializeInput(IReasonerInputReader reader) {

			return new EmptyInput();
		}

		@Override
		public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
				IProofMonitor pm) {

			return ProverFactory.reasonerFailure(this, input, "Reasoner "
					+ reasonerID + " is not installed");
		}

	}

	private static IReasoner getDummyInstance(String id) {
		return new DummyReasoner(id);
	}

	/**
	 * Checks if a string contains a whitespace character or a colon
	 * 
	 * @param str
	 *            String to check for.
	 * @return <code>true</code> iff the string contains a whitespace character
	 *         or a colon.
	 */
	private static boolean containsWhitespaceOrColon(String str) {
		for (int i = 0; i < str.length(); i++) {
			final char c = str.charAt(i);
			if (c == ':' || Character.isWhitespace(c))
				return true;
		}
		return false;
	}

	private static void checkId(String id) throws ReasonerLoadingException {
		if (id.indexOf('.') != -1) {
			throw new ReasonerLoadingException("Invalid id: " + id
					+ " (must not contain a dot)");
		}
		if (containsWhitespaceOrColon(id)) {
			throw new ReasonerLoadingException("Invalid id: " + id
					+ " (must not contain a whitespace or a colon)");
		}
	}

	private final IConfigurationElement configurationElement;
	private final String id;
	private final String name;
	private int version = UNKNOWN_VERSION;
	private final boolean isLive;

	private final boolean contextDependent;

	/**
	 * Reasoner instance lazily loaded using <code>configurationElement</code>
	 */
	private IReasoner instance;

	private static boolean isContextDependent(IConfigurationElement element) {
		final String ctxDepStr = element.getAttribute("contextDependent");
		if (ctxDepStr == null || ctxDepStr.isEmpty()) {
			// default
			return false;
		}
		return ctxDepStr.equalsIgnoreCase("true");
	}
	
	public ReasonerDesc(IConfigurationElement element)
			throws ReasonerLoadingException {
		this.configurationElement = element;
		final String localId = element.getAttribute("id");
		checkId(localId);
		final String nameSpace = element.getNamespaceIdentifier();
		this.id = nameSpace + "." + localId;
		this.name = element.getAttribute("name");
		this.isLive = true;
		this.contextDependent = isContextDependent(element);
	}

	private ReasonerDesc(IConfigurationElement configurationElement,
			IReasoner instance, String id, String name, int version, boolean contextDependent) {
		this.configurationElement = configurationElement;
		this.instance = instance;
		this.id = id;
		this.name = name;
		this.version = version;
		this.contextDependent = contextDependent;
		this.isLive = false;
	}

	/**
	 * Returns a new ReasonerDesc with a name indicating the reasoner is
	 * unknown, and a dummy reasoner instance bearing the given reasoner id.
	 * <p>
	 * This method is intended to be used only when the given id is not
	 * registered. Descriptor duplication must be avoided.
	 * </p>
	 * 
	 * @param id
	 *            the id of the unknown reasoner.
	 * @return a new ReasonerDesc
	 */
	static ReasonerDesc makeUnknownReasonerDesc(String id) {
		final IReasoner dummyInstance = getDummyInstance(id);
		final String unknownName = Messages.bind(Messages.reasonerDesc_unknown,
				id);
		return new ReasonerDesc(null, dummyInstance, id, unknownName, UNKNOWN_VERSION, false);
	}

	/**
	 * Returns a new ReasonerDesc for the given reasoner, with a name indicating
	 * the reasoner is unknown.
	 * <p>
	 * This method is intended to be used only when the given reasoner is not
	 * registered. Descriptor duplication must be avoided.
	 * </p>
	 * 
	 * @param reasoner
	 *            the reasoner for which a descriptor is desired
	 * @return a new ReasonerDesc
	 */
	static ReasonerDesc makeUnknownReasonerDesc(IReasoner reasoner) {
		final String reasonerID = reasoner.getReasonerID();
		String id = reasonerID;
		final int version;
		if (reasoner instanceof IVersionedReasoner) {
			version = ((IVersionedReasoner) reasoner).getVersion();
		} else {
			version = UNKNOWN_VERSION;
		}
		return new ReasonerDesc(null, reasoner, id, Messages.bind(
				Messages.reasonerDesc_unknown, reasonerID), version, false);
	}

	/**
	 * Returns a copy of this ReasonerDesc with the version modified according
	 * to the given parameter.
	 * 
	 * @param copyVersion
	 *            the desired version of the copy
	 * @return a new ReasonerDesc with the given version
	 */
	public ReasonerDesc copyWithVersion(int copyVersion) {
		final String baseId = decodeId(id);
		return new ReasonerDesc(configurationElement, instance,
				baseId, name, copyVersion, contextDependent);
	}

	@Override
	public IReasoner getInstance() {
		if (instance != null) {
			return instance;
		}

		if (configurationElement == null) {
			return instance = getDummyInstance(id);
		}

		// Try creating an instance of the specified class
		try {
			instance = (IReasoner) configurationElement
					.createExecutableExtension("class");
		} catch (Exception e) {
			final String className = configurationElement.getAttribute("class");
			Util.log(e, "Error instantiating class " + className
					+ " for reasoner " + id);
			if (ReasonerRegistry.DEBUG)
				System.out
						.println("Create a dummy instance for reasoner " + id);
			return instance = getDummyInstance(id);
		}

		// Check if the reasoner id from the extension point matches that
		// returned by the class instance.
		if (!id.equals(instance.getReasonerID())) {
			Util.log(null, "Reasoner instance says its id is "
					+ instance.getReasonerID()
					+ " while it was registered with id " + id);
			if (ReasonerRegistry.DEBUG)
				System.out.println("Created a dummy instance for reasoner "
						+ id);
			return instance = getDummyInstance(id);
		}

		if (ReasonerRegistry.DEBUG)
			System.out.println("Successfully loaded reasoner " + id);
		return instance;
	}

	@Override
	public String getId() {
		return decodeId(id);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getVersion() {
		if (version != UNKNOWN_VERSION) {
			return version;
		}
		if (isLive) {
			version = getRegisteredVersion();
		} else {
			version = decodeVersion(id);
		}
		return version;
	}

	@Override
	public int getRegisteredVersion() {
		final IReasoner inst = getInstance();
		if (inst instanceof IVersionedReasoner) {
			return ((IVersionedReasoner) inst).getVersion();
		} else {
			return NO_VERSION;
		}
	}

	@Override
	public String getVersionedId() {
		final int reasonerVersion = getVersion();
		return encodeVersionInId(decodeId(id), reasonerVersion);
	}

	@Override
	public boolean hasVersionConflict() {
		return getVersion() != getRegisteredVersion();
	}

	@Override
	public boolean isContextDependent() {
		return contextDependent;
	}
}

/*******************************************************************************
 * Copyright (c) 2005, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added as***File()
 *     Systerel - separation of file and root element
 *     Systerel - added simplifyProof()
 *     Systerel - added rebuildProof()
 *******************************************************************************/
package org.eventb.core;

import static org.rodinp.core.RodinCore.getOccurrenceKind;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pog.POGModule;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.sc.SCModule;
import org.eventb.internal.core.FormulaExtensionProviderRegistry;
import org.eventb.internal.core.autocompletion.AutoCompletion;
import org.eventb.internal.core.indexers.EventPropagator;
import org.eventb.internal.core.indexers.IdentifierPropagator;
import org.eventb.internal.core.pm.ProofManager;
import org.eventb.internal.core.pm.ProofRebuilder;
import org.eventb.internal.core.pm.ProofSimplifier;
import org.eventb.internal.core.pm.UserSupportManager;
import org.eventb.internal.core.pm.UserSupportUtils;
import org.eventb.internal.core.pog.POGUtil;
import org.eventb.internal.core.pog.modules.UtilityModule;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.core.pom.POLoader;
import org.eventb.internal.core.pom.RecalculateAutoStatus;
import org.eventb.internal.core.preferences.AutoPostTacticManager;
import org.eventb.internal.core.preferences.PreferenceUtils;
import org.eventb.internal.core.sc.SCUtil;
import org.osgi.framework.BundleContext;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.indexer.IPropagator;
import org.rodinp.core.location.IAttributeLocation;

/**
 * The Event-B core plugin class.
 * 
 * @since 1.0
 */
public class EventBPlugin extends Plugin {

	// The shared instance.
	private static EventBPlugin plugin;

	/**
	 * The plug-in identifier of the Event-B core support (value
	 * <code>"org.eventb.core"</code>).
	 */
	public static final String PLUGIN_ID = "org.eventb.core"; //$NON-NLS-1$

	/**
	 * debugging/tracing option names
	 */
	private static final String SC_TRACE = PLUGIN_ID + "/debug/sc"; //$NON-NLS-1$
	private static final String SC_TRACE_STATE = PLUGIN_ID + "/debug/sc/state"; //$NON-NLS-1$
	private static final String SC_TRACE_MODULECONF = PLUGIN_ID
			+ "/debug/sc/moduleconf"; //$NON-NLS-1$
	private static final String SC_TRACE_MODULES = PLUGIN_ID
			+ "/debug/sc/modules"; //$NON-NLS-1$
	private static final String SC_TRACE_MARKERS = PLUGIN_ID
			+ "/debug/sc/markers"; //$NON-NLS-1$
	private static final String POG_TRACE = PLUGIN_ID + "/debug/pog"; //$NON-NLS-1$
	private static final String POG_TRACE_STATE = PLUGIN_ID
			+ "/debug/pog/state"; //$NON-NLS-1$
	private static final String POG_TRACE_MODULECONF = PLUGIN_ID
			+ "/debug/pog/moduleconf"; //$NON-NLS-1$
	private static final String POG_TRACE_MODULES = PLUGIN_ID
			+ "/debug/pog/modules"; //$NON-NLS-1$
	private static final String POG_TRACE_TRIVIAL = PLUGIN_ID
			+ "/debug/pog/trivial"; //$NON-NLS-1$
	private static final String POM_TRACE = PLUGIN_ID + "/debug/pom"; //$NON-NLS-1$
	private static final String POM_TRACE_RECALCULATE = PLUGIN_ID
			+ "/debug/pom/recalculate"; //$NON-NLS-1$
	private static final String PO_LOADER_TRACE = PLUGIN_ID + "/debug/poloader"; //$NON-NLS-1$
	private static final String PM_TRACE = PLUGIN_ID + "/debug/pm"; //$NON-NLS-1$
	private static final String PERF_POM_PROOFREUSE_TRACE = PLUGIN_ID
			+ "/perf/pom/proofReuse"; //$NON-NLS-1$
	private static final String EXTENSIONPROVIDER_REGISTRY_TRACE = PLUGIN_ID
			+ "/debug/formulaExtensionProvider";//$NON-NLS-1$
	private static final String PREFERENCES_TRACE = PLUGIN_ID
			+ "/debug/preferences"; //$NON-NLS-1$

	/**
	 * Returns the name of the component whose data are stored in the file with
	 * the given name.
	 * 
	 * @param fileName
	 *            name of the file
	 * @return the name of the component corresponding to the given file
	 */
	public static String getComponentName(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot == -1) {
			return fileName;
		} else {
			return fileName.substring(0, lastDot);
		}
	}

	/**
	 * Returns the name of the Rodin file that contains the context with the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the context
	 * @return the name of the file containing that context
	 */
	public static String getContextFileName(String bareName) {
		return bareName + ".buc";
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the name of the Rodin file that contains the machine with the
	 * given name.
	 * 
	 * @param bareName
	 *            name of the machine
	 * @return the name of the file containing that machine
	 */
	public static String getMachineFileName(String bareName) {
		return bareName + ".bum";
	}

	/**
	 * Returns the shared instance.
	 */
	public static EventBPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Returns the name of the Rodin file that contains the proof obligations
	 * for the component of the given name.
	 * 
	 * @param bareName
	 *            name of the component
	 * @return the name of the file containing POs for that component
	 */
	public static String getPOFileName(String bareName) {
		return bareName + ".bpo";
	}

	/**
	 * Returns the name of the Rodin file that contains the proofs for the
	 * component of the given name.
	 * 
	 * @param bareName
	 *            name of the component
	 * @return the name of the file containing proofs for that component
	 */
	public static String getPRFileName(String bareName) {
		return bareName + ".bpr";
	}

	/**
	 * Returns the name of the Rodin file that contains the proof status for the
	 * component of the given name.
	 * 
	 * @param bareName
	 *            name of the component
	 * @return the name of the file containing proofs for that component
	 */
	public static String getPSFileName(String bareName) {
		return bareName + ".bps";
	}

	/**
	 * Returns the name of the Rodin file that contains the checked context with
	 * the given name.
	 * 
	 * @param bareName
	 *            name of the checked context
	 * @return the name of the file containing that checked context
	 */
	public static String getSCContextFileName(String bareName) {
		return bareName + ".bcc";
	}

	/**
	 * Returns the name of the Rodin file that contains the checked machine with
	 * the given name.
	 * 
	 * @param bareName
	 *            name of the checked machine
	 * @return the name of the file containing that checked machine
	 */
	public static String getSCMachineFileName(String bareName) {
		return bareName + ".bcm";
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		enableAssertions();
		if (isDebugging()) {
			configureDebugOptions();
		}
		PreferenceUtils.init();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Enable Java assertion checks for this plug-in.
	 */
	private void enableAssertions() {
		getClass().getClassLoader().setDefaultAssertionStatus(true);
	}

	/**
	 * Process debugging/tracing options coming from Eclipse.
	 */
	private void configureDebugOptions() {
		SCUtil.DEBUG = parseOption(SC_TRACE);
		SCUtil.DEBUG_STATE = SCUtil.DEBUG && parseOption(SC_TRACE_STATE);
		SCUtil.DEBUG_MODULECONF = SCUtil.DEBUG
				&& parseOption(SC_TRACE_MODULECONF);
		SCModule.DEBUG_MODULE = SCUtil.DEBUG && parseOption(SC_TRACE_MODULES);
		SCUtil.DEBUG_MARKERS = SCUtil.DEBUG && parseOption(SC_TRACE_MARKERS);

		POGUtil.DEBUG = parseOption(POG_TRACE);
		POGUtil.DEBUG_STATE = POGUtil.DEBUG && parseOption(POG_TRACE_STATE);
		POGUtil.DEBUG_MODULECONF = POGUtil.DEBUG
				&& parseOption(POG_TRACE_MODULECONF);
		POGModule.DEBUG_MODULE = POGUtil.DEBUG
				&& parseOption(POG_TRACE_MODULES);
		UtilityModule.DEBUG_TRIVIAL = POGUtil.DEBUG
				&& parseOption(POG_TRACE_TRIVIAL);

		AutoPOM.DEBUG = parseOption(POM_TRACE);
		RecalculateAutoStatus.DEBUG = parseOption(POM_TRACE_RECALCULATE);
		POLoader.DEBUG = parseOption(PO_LOADER_TRACE);
		UserSupportUtils.DEBUG = parseOption(PM_TRACE);
		AutoPOM.PERF_PROOFREUSE = parseOption(PERF_POM_PROOFREUSE_TRACE);
		FormulaExtensionProviderRegistry.DEBUG = parseOption(EXTENSIONPROVIDER_REGISTRY_TRACE);
		PreferenceUtils.DEBUG = parseOption(PREFERENCES_TRACE);
	}

	private static boolean parseOption(String key) {
		final String option = Platform.getDebugOption(key);
		return "true".equalsIgnoreCase(option); //$NON-NLS-1$
	}

	/**
	 * Return the default user support manager.
	 * <p>
	 * 
	 * @return the default user support manager
	 * @author htson
	 */
	public static IUserSupportManager getUserSupportManager() {
		return UserSupportManager.getDefault();
	}

	/**
	 * Return the Auto/Post tactic manager.
	 * 
	 * @since 2.1
	 */
	public static IAutoPostTacticManager getAutoPostTacticManager() {
		return AutoPostTacticManager.getDefault();
	}

	/**
	 * Returns the proof manager of this plug-in.
	 * 
	 * @return the proof manager
	 */
	public static IProofManager getProofManager() {
		return ProofManager.getDefault();
	}

	/**
	 * Returns the given object as an event-B file if possible,
	 * <code>null</code> otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is an
	 * event-B file or adaptable to an event-B file.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to an event-B file
	 * @return the given object as an event-B file or <code>null</code>
	 */
	public static IRodinFile asEventBFile(Object object) {
		final IRodinElement elem = RodinCore.asRodinElement(object);
		if (elem instanceof IRodinFile) {
			IRodinFile rf = (IRodinFile) elem;
			if (rf.getRoot() instanceof IEventBRoot) {
				return rf;
			}
		}
		if (elem instanceof IEventBRoot) {
			IRodinFile rf = ((IEventBRoot) elem).getRodinFile();
			return rf;
		}
		return null;
	}

	private static IEventBRoot asEventBRoot(Object object) {
		final IRodinFile elem = asEventBFile(object);
		if (elem == null) {
			return null;
		}
		return ((IEventBRoot) elem.getRoot());
	}

	/**
	 * Returns the given object as a context file if possible, <code>null</code>
	 * otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a
	 * context file or adaptable to an event-B file. In the latter case, the
	 * corresponding context file is returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a context file
	 * @return the given object as a context file or <code>null</code>
	 */
	public static IRodinFile asContextFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getContextRoot().getRodinFile();
	}

	/**
	 * Returns the given object as a machine file if possible, <code>null</code>
	 * otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a
	 * machine file or adaptable to an event-B file. In the latter case, the
	 * corresponding machine file is returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a machine file
	 * @return the given object as a machine file or <code>null</code>
	 */
	public static IRodinFile asMachineFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getMachineRoot().getRodinFile();
	}

	/**
	 * Returns the given object as a statically checked context file if
	 * possible, <code>null</code> otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a
	 * statically checked context file or adaptable to an event-B file. In the
	 * latter case, the corresponding statically checked context file is
	 * returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a statically checked context file
	 * @return the given object as a statically checked context file or
	 *         <code>null</code>
	 */
	public static IRodinFile asSCContextFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getSCContextRoot().getRodinFile();
	}

	/**
	 * Returns the given object as a statically checked machine file if
	 * possible, <code>null</code> otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a
	 * statically checked machine file or adaptable to an event-B file. In the
	 * latter case, the corresponding statically checked machine file is
	 * returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a statically checked machine file
	 * @return the given object as a statically checked machine file or
	 *         <code>null</code>
	 */
	public static IRodinFile asSCMachineFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getSCMachineRoot().getRodinFile();
	}

	/**
	 * Returns the given object as a proof obligation file if possible,
	 * <code>null</code> otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a proof
	 * obligation file or adaptable to an event-B file. In the latter case, the
	 * corresponding proof obligation file is returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a proof obligation file
	 * @return the given object as a proof obligation file or <code>null</code>
	 */
	public static IRodinFile asPOFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getPORoot().getRodinFile();
	}

	/**
	 * Returns the given object as a proof file if possible, <code>null</code>
	 * otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a proof
	 * file or adaptable to an event-B file. In the latter case, the
	 * corresponding proof file is returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a proof file
	 * @return the given object as a proof file or <code>null</code>
	 */
	public static IRodinFile asPRFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getPRRoot().getRodinFile();
	}

	/**
	 * Returns the given object as a proof status file if possible,
	 * <code>null</code> otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a proof
	 * status file or adaptable to an event-B file. In the latter case, the
	 * corresponding proof status file is returned.
	 * </p>
	 * <p>
	 * This is a handle-only method. The returned file may or may not exist.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a proof status file
	 * @return the given object as a proof status file or <code>null</code>
	 */
	public static IRodinFile asPSFile(Object object) {
		final IEventBRoot elem = asEventBRoot(object);
		if (elem == null) {
			return null;
		}
		return elem.getPSRoot().getRodinFile();
	}

	public static final IOccurrenceKind DECLARATION = getOccurrenceKind(PLUGIN_ID
			+ ".declaration");

	public static final IOccurrenceKind REFERENCE = getOccurrenceKind(PLUGIN_ID
			+ ".reference");

	public static final IOccurrenceKind MODIFICATION = getOccurrenceKind(PLUGIN_ID
			+ ".modification");

	public static final IOccurrenceKind REDECLARATION = getOccurrenceKind(PLUGIN_ID
			+ ".redeclaration");

	public static IPropagator getEventPropagator() {
		return EventPropagator.getDefault();
	}

	public static IPropagator getIdentifierPropagator() {
		return IdentifierPropagator.getDefault();
	}

	/**
	 * Attempts to simplify the given proof.
	 * 
	 * @param proof
	 *            a proof to simplify
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            cancelled.
	 * @return <code>true</code> iff the proof has been successfully simplified
	 * @throws CoreException
	 *             if there was a problem accessing the proof
	 * @since 3.0
	 */
	public static boolean simplifyProof(IPRProof proof, IProgressMonitor monitor)
			throws CoreException {
		return new ProofSimplifier(proof).perform(monitor);
	}

	/**
	 * Attempts to rebuild the given proof.
	 * 
	 * @param proof
	 *            a proof to rebuild
	 * @param applyPostTactics
	 *            <code>true</code> if post tactics shall be applied in case
	 *            rebuild does not close the proof tree and post tactics
	 *            preference is enabled
	 * @param monitor
	 *            the progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts <code>null</code>, indicating that no
	 *            progress should be reported and that the operation cannot be
	 *            cancelled.
	 * @return <code>true</code> iff the proof has been successfully rebuilt
	 * @throws CoreException
	 *             if there was a problem accessing the proof
	 * @since 2.0
	 */
	public static boolean rebuildProof(IPRProof proof,
			boolean applyPostTactics, IProgressMonitor monitor)
			throws CoreException {
		return new ProofRebuilder(proof, applyPostTactics).perform(monitor);
	}

	/**
	 * Returns a set of identifier proposals for the given location.
	 * 
	 * @param location
	 *            the location where completion is desired
	 * @param waitUpToDate
	 *            <code>true</code> iff method call shall be blocked until the
	 *            underlying indexing system is up to date
	 * @return a set of proposals
	 * @since 2.0
	 */
	public static Set<String> getProposals(IAttributeLocation location,
			boolean waitUpToDate) {
		return AutoCompletion.getProposals(location, waitUpToDate);
	}
}

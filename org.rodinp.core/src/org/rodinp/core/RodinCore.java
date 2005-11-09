/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.JavaCore.java which is
 * 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.rodinp.internal.core.Region;
import org.rodinp.internal.core.RodinDB;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.util.WeakHashSet;

/**
 * The plug-in runtime class for the Rodin core plug-in containing the core
 * (UI-free) support for Rodin projects.
 * <p>
 * Like all plug-in runtime classes (subclasses of <code>Plugin</code>), this
 * class is automatically instantiated by the platform when the plug-in gets
 * activated. Clients must not attempt to instantiate plug-in runtime classes
 * directly.
 * </p>
 * <p>
 * The single instance of this class can be accessed from any plug-in declaring
 * the Rodin core plug-in as a prerequisite via
 * <code>RodinCore.getRodinCore()</code>. The Rodin core plug-in will be
 * activated automatically if not already active.
 * </p>
 */

public class RodinCore extends Plugin {

	// The shared instance.
	private static RodinCore PLUGIN;

	/**
	 * The plug-in identifier of the Rodin core support (value
	 * <code>"org.rodinp.core"</code>).
	 */
	public static final String PLUGIN_ID = "org.rodinp.core"; //$NON-NLS-1$

	/**
	 * The identifier for the Rodin builder
	 * (value <code>"org.rodinp.core.rodinbuilder"</code>).
	 */
	public static final String BUILDER_ID = PLUGIN_ID + ".rodinbuilder" ; //$NON-NLS-1$

	/**
	 * The identifier for the Rodin database
	 * (value <code>"org.rodinp.core.rodindb"</code>).
	 */
	public static final String DATABASE_ID = PLUGIN_ID + ".rodindb" ; //$NON-NLS-1$

	/**
	 * The identifier for the Rodin nature
	 * (value <code>"org.rodinp.core.rodinnature"</code>).
	 * The presence of this nature on a project indicates that it is 
	 * Rodin-capable.
	 *
	 * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
	 */
	public static final String NATURE_ID = PLUGIN_ID + ".rodinnature" ; //$NON-NLS-1$

	/*
	 * Pools of symbols used in the Rodin database. Used as a replacement for
	 * String#intern() that could prevent garbage collection of strings on some
	 * VMs.
	 */
	// TODO use java.util.WeakHashMap here, instead of WeakHashSet
	private WeakHashSet<String> stringSymbols = new WeakHashSet<String>(5);

	/**
	 * Creates the Rodin core plug-in.
	 * <p>
	 * The plug-in instance is created automatically by the Eclipse platform.
	 * Clients must not call.
	 * </p>
	 */
	public RodinCore() {
		super();
		PLUGIN = this;
	}
	
	/**
	 * Adds the given listener for changes to Rodin elements.
	 * Has no effect if an identical listener is already registered.
	 *
	 * This listener will only be notified during the POST_CHANGE resource change notification
	 * and any reconcile operation (POST_RECONCILE).
	 * For finer control of the notification, use <code>addElementChangedListener(IElementChangedListener,int)</code>,
	 * which allows to specify a different eventMask.
	 * 
	 * @param listener the listener
	 * @see ElementChangedEvent
	 */
	public static void addElementChangedListener(IElementChangedListener listener) {
		addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE | ElementChangedEvent.POST_RECONCILE);
	}

	/**
	 * Adds the given listener for changes to Rodin elements.
	 * Has no effect if an identical listener is already registered.
	 * After completion of this method, the given listener will be registered for exactly
	 * the specified events.  If they were previously registered for other events, they
	 * will be deregistered.  
	 * <p>
	 * Once registered, a listener starts receiving notification of changes to
	 * Rodin elements in the model. The listener continues to receive 
	 * notifications until it is replaced or removed. 
	 * </p>
	 * <p>
	 * Listeners can listen for several types of event as defined in <code>ElementChangeEvent</code>.
	 * Clients are free to register for any number of event types however if they register
	 * for more than one, it is their responsibility to ensure they correctly handle the
	 * case where the same java element change shows up in multiple notifications.  
	 * Clients are guaranteed to receive only the events for which they are registered.
	 * </p>
	 * 
	 * @param listener the listener
	 * @param eventMask the bit-wise OR of all event types of interest to the listener
	 * @see IElementChangedListener
	 * @see ElementChangedEvent
	 * @see #removeElementChangedListener(IElementChangedListener)
	 * @since 2.0
	 */
	public static void addElementChangedListener(IElementChangedListener listener, int eventMask) {
		RodinDBManager.getRodinDBManager().deltaState.addElementChangedListener(listener, eventMask);
	}

	/**
	 * Returns the Rodin element corresponding to the given handle identifier
	 * generated by <code>IRodinElement.getHandleIdentifier()</code>, or
	 * <code>null</code> if unable to create the associated element.
	 * 
	 * @param handleIdentifier
	 *            the given handle identifier
	 * @return the Rodin element corresponding to the handle identifier
	 */
	public static IRodinElement create(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		}
		MementoTokenizer memento = new MementoTokenizer(handleIdentifier);
		RodinDB model = RodinDBManager.getRodinDBManager().getRodinDB();
		return model.getHandleFromMemento(memento);
	}

	/**
	 * Returns the Rodin element corresponding to the given file, or
	 * <code>null</code> if unable to associate the given file with a Rodin
	 * element.
	 * 
	 * <p>
	 * The file must be a Rodin file.
	 * <p>
	 * Creating a Rodin element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 * 
	 * @param file
	 *            the given file
	 * @return the Rodin element corresponding to the given file, or
	 *         <code>null</code> if unable to associate the given file with a
	 *         Rodin element
	 */
	public static RodinFile create(IFile file) {
		return (RodinFile) RodinDBManager.create(file, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin project corresponding to the given project.
	 * <p>
	 * Creating a Rodin Project has the side effect of creating and opening all
	 * of the project's parents if they are not yet open.
	 * <p>
	 * Note that no check is done at this time on the existence or the Rodin
	 * nature of this project.
	 * 
	 * @param project
	 *            the given project
	 * @return the Rodin project corresponding to the given project, null if the
	 *         given project is null
	 */
	public static IRodinProject create(IProject project) {
		if (project == null) {
			return null;
		}
		RodinDB javaModel = RodinDBManager.getRodinDBManager().getRodinDB();
		return javaModel.getRodinProject(project);
	}

	/**
	 * Returns the Rodin element corresponding to the given resource, or
	 * <code>null</code> if unable to associate the given resource with a
	 * Rodin element.
	 * <p>
	 * The resource must be one of:
	 * <ul>
	 * <li>a project - the element returned is the corresponding
	 * <code>IRodinProject</code></li>
	 * <li>a Rodin file - the element returned is the corresponding
	 * <code>RodinFile</code></li>
	 * <li>the workspace root resource - the element returned is the
	 * <code>IRodinDB</code></li>
	 * </ul>
	 * <p>
	 * Creating a Rodin element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 * 
	 * @param resource
	 *            the given resource
	 * @return the Rodin element corresponding to the given resource, or
	 *         <code>null</code> if unable to associate the given resource
	 *         with a Rodin element
	 */
	public static IRodinElement create(IResource resource) {
		return RodinDBManager.create(resource, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin database.
	 * 
	 * @param root
	 *            the given root
	 * @return the Rodin database, or <code>null</code> if the root is null
	 */
	public static IRodinDB create(IWorkspaceRoot root) {
		if (root == null) {
			return null;
		}
		return RodinDBManager.getRodinDBManager().getRodinDB();
	}

	/**
	 * Returns the single instance of the Rodin core plug-in runtime class.
	 * Equivalent to <code>(RodinCore) getPlugin()</code>.
	 * 
	 * @return the single instance of the Rodin core plug-in runtime class
	 */
	public static RodinCore getRodinCore() {
		return PLUGIN;
	}

	/**
	 * Returns the single instance of the Rodin core plug-in runtime class.
	 * 
	 * @return the single instance of the Rodin core plug-in runtime class
	 */
	public static Plugin getPlugin() {
		return PLUGIN;
	}

	public synchronized String intern(String s) {
		// make sure to copy the string (so that it doesn't hold on the
		// underlying char[] that might be much bigger than necessary)
		return this.stringSymbols.add(new String(s));

		// Note: String#intern() cannot be used as on some VMs this prevents the
		// string from being garbage collected
	}

	/**
	 * Returns a new empty region.
	 * 
	 * @return a new empty region
	 */
	public static IRegion newRegion() {
		return new Region();
	}

	/**
	 * Removes the given element changed listener.
	 * Has no affect if an identical listener is not registered.
	 *
	 * @param listener the listener
	 */
	public static void removeElementChangedListener(IElementChangedListener listener) {
		RodinDBManager.getRodinDBManager().deltaState.removeElementChangedListener(listener);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		RodinDBManager.getRodinDBManager().startup();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			RodinDBManager.getRodinDBManager().shutdown();
		} finally {
			super.stop(context);
			PLUGIN = null;
		}
	}

}

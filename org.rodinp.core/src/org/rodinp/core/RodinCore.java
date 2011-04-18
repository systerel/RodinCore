/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 *     		org.eclipse.jdt.core.JavaCore
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - added getRodinDB()
 *     Systerel - removed deprecated methods
 *     Systerel - added asRodinElement()
 *     Systerel - separation of file and root element
 *     Systerel - added database indexer
 *     Systerel - added refinements
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.osgi.framework.BundleContext;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrenceKind;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;
import org.rodinp.core.location.IInternalLocation;
import org.rodinp.core.location.IRodinLocation;
import org.rodinp.internal.core.BatchOperation;
import org.rodinp.internal.core.ElementTypeManager;
import org.rodinp.internal.core.RefinementProcessor;
import org.rodinp.internal.core.RefinementRegistry;
import org.rodinp.internal.core.Region;
import org.rodinp.internal.core.RodinDB;
import org.rodinp.internal.core.RodinDBManager;
import org.rodinp.internal.core.indexer.IndexQuery;
import org.rodinp.internal.core.indexer.OccurrenceKind;
import org.rodinp.internal.core.location.AttributeLocation;
import org.rodinp.internal.core.location.AttributeSubstringLocation;
import org.rodinp.internal.core.location.InternalLocation;
import org.rodinp.internal.core.location.RodinLocation;
import org.rodinp.internal.core.util.MementoTokenizer;
import org.rodinp.internal.core.version.ConversionResult;

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
 * @since 1.0
 */

public class RodinCore extends Plugin {

	// The shared instance.
	private static RodinCore plugin;

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
	 * The identifier for the Rodin nature
	 * (value <code>"org.rodinp.core.rodinnature"</code>).
	 * The presence of this nature on a project indicates that it is 
	 * Rodin-capable.
	 *
	 * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
	 */
	public static final String NATURE_ID = PLUGIN_ID + ".rodinnature" ; //$NON-NLS-1$

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
		addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE);
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
	 * Returns the internal element type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the element type
	 * @return the internal element type with the given id
	 * @throws IllegalArgumentException
	 *             if no such internal element type has been contributed
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends IInternalElement> IInternalElementType<T> getInternalElementType(
			String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final IInternalElementType result = manager.getInternalElementType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown internal element type: " + id);
	}

	/**
	 * Returns the attribute type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType getAttributeType(String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		IAttributeType result = manager.getAttributeType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown attribute type: " + id);
	}
	
	/**
	 * Returns the attribute type of kind boolean and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind boolean
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Boolean getBooleanAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Boolean) {
			return (IAttributeType.Boolean) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind boolean");
	}
	
	/**
	 * Returns the attribute type of kind handle and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind handle
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Handle getHandleAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Handle) {
			return (IAttributeType.Handle) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind handle");
	}
	
	/**
	 * Returns the attribute type of kind integer and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind integer
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Integer getIntegerAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Integer) {
			return (IAttributeType.Integer) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind integer");
	}
	
	/**
	 * Returns the attribute type of kind long and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind long
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.Long getLongAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.Long) {
			return (IAttributeType.Long) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind long");
	}
	
	/**
	 * Returns the attribute type of kind string and with the given id.
	 * 
	 * @param id
	 *            unique identifier of the attribute type
	 * @return the attribute type with the given id and kind string
	 * @throws IllegalArgumentException
	 *             if no such attribute type has been contributed
	 */
	public static IAttributeType.String getStringAttrType(String id) {
		IAttributeType type = getAttributeType(id);
		if (type instanceof IAttributeType.String) {
			return (IAttributeType.String) type;
		}
		throw new IllegalArgumentException(
				"Attribute type " + type.getId() + " is not of kind string");
	}
	
	/**
	 * Returns the element type with the given id.
	 * 
	 * @param id
	 *            unique identifier of the element type
	 * @return the element type with the given id
	 * @throws IllegalArgumentException
	 *             if no such element type has been contributed
	 */
	public static IElementType<? extends IRodinElement> getElementType(String id) {
		final ElementTypeManager manager = ElementTypeManager.getInstance();
		final IElementType<? extends IRodinElement> result = manager.getElementType(id);
		if (result != null) {
			return result;
		}
		throw new IllegalArgumentException("Unknown element type: " + id);
	}

	/**
	 * Returns the single instance of the Rodin core plug-in runtime class.
	 * Equivalent to <code>(RodinCore) getPlugin()</code>.
	 * 
	 * @return the single instance of the Rodin core plug-in runtime class
	 */
	public static RodinCore getRodinCore() {
		return plugin;
	}

	/**
	 * Returns the single instance of the Rodin core plug-in runtime class.
	 * 
	 * @return the single instance of the Rodin core plug-in runtime class
	 */
	public static Plugin getPlugin() {
		return plugin;
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

	/**
	 * Runs the given action as an atomic Rodin database operation.
	 * <p>
	 * After running a method that modifies Rodin elements,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of an element changed event.
	 * This method allows clients to call a number of
	 * methods that modify Rodin elements and only have element
	 * changed event notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * element changed event describing the net effect of all changes
	 * done to Rodin elements by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 *
	 * @param action the action to perform
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception RodinDBException if the operation failed.
	 */
	public static void run(IWorkspaceRunnable action, IProgressMonitor monitor)
			throws RodinDBException {
		run(action, ResourcesPlugin.getWorkspace().getRoot(), monitor);
	}

	/**
	 * Runs the given action as an atomic Rodin database operation.
	 * <p>
	 * After running a method that modifies Rodin elements,
	 * registered listeners receive after-the-fact notification of
	 * what just transpired, in the form of an element changed event.
	 * This method allows clients to call a number of
	 * methods that modify Rodin elements and only have element
	 * changed event notifications reported at the end of the entire
	 * batch.
	 * </p>
	 * <p>
	 * If this method is called outside the dynamic scope of another such
	 * call, this method runs the action and then reports a single
	 * element changed event describing the net effect of all changes
	 * done to Rodin elements by the action.
	 * </p>
	 * <p>
	 * If this method is called in the dynamic scope of another such
	 * call, this method simply runs the action.
	 * </p>
	 * <p>
 	 * The supplied scheduling rule is used to determine whether this operation can be
	 * run simultaneously with workspace changes in other threads. See 
	 * <code>IWorkspace.run(...)</code> for more details.
 	 * </p>
	 *
	 * @param action the action to perform
	 * @param rule the scheduling rule to use when running this operation, or
	 * <code>null</code> if there are no scheduling restrictions for this operation.
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @exception RodinDBException if the operation failed.
	 */
	public static void run(IWorkspaceRunnable action, ISchedulingRule rule,
			IProgressMonitor monitor) throws RodinDBException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace.isTreeLocked()) {
			new BatchOperation(action).run(monitor);
		} else {
			// use IWorkspace.run(...) to ensure that a build will be done in
			// autobuild mode
			try {
				workspace.run(new BatchOperation(action), rule,
						IWorkspace.AVOID_UPDATE, monitor);
			} catch (RodinDBException re) {
				throw re;
			} catch (CoreException ce) {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e = ce.getStatus().getException();
					if (e instanceof RodinDBException) {
						throw (RodinDBException) e;
					}
				}
				throw new RodinDBException(ce);
			}
		}
	}	

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		RodinDBManager.getRodinDBManager().startup();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			RodinDBManager.getRodinDBManager().shutdown();
		} finally {
			super.stop(context);
			plugin = null;
		}
	}

	/**
	 * Returns the given object as a Rodin element if possible,
	 * <code>null</code> otherwise.
	 * <p>
	 * A non-<code>null</code> value is returned iff the given object is a
	 * Rodin element or adaptable to a Rodin element.
	 * </p>
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param object
	 *            the object to adapt to a Rodin element
	 * @return the given object as a Rodin element or <code>null</code>
	 */
	public static IRodinElement asRodinElement(Object object) {
		if (object instanceof IRodinElement) {
			return (IRodinElement) object;
		}
		if (object instanceof IAdaptable) {
			final IAdaptable adaptable = (IAdaptable) object;
			return (IRodinElement) adaptable.getAdapter(IRodinElement.class);
		}
		return null;
	}

	/**
	 * Returns the Rodin element corresponding to the given handle identifier
	 * generated by <code>IRodinElement.getHandleIdentifier()</code>, or
	 * <code>null</code> if unable to create the associated element handle.
	 * 
	 * @param handleIdentifier
	 *            the given handle identifier
	 * @return the Rodin element corresponding to the handle identifier, or
	 *         <code>null</code> if the identifier can't be parsed
	 */
	public static IRodinElement valueOf(String handleIdentifier) {
		if (handleIdentifier == null) {
			return null;
		}
		final MementoTokenizer memento = new MementoTokenizer(handleIdentifier);
		final RodinDB db = RodinDBManager.getRodinDBManager().getRodinDB();
		return db.getHandleFromMemento(memento);
	}

	/**
	 * Returns the refinement registry instance.
	 * 
	 * @return the refinement registry
	 * @since 1.4
	 */
	public static IRefinementRegistry getRefinementRegistry() {
		return RefinementRegistry.getDefault();
	}

	/**
	 * Refines the given source root to the given target root. In case the
	 * refinement fails, <code>false</code> is returned and an error is logged
	 * when appropriate.
	 * <p>
	 * In the context of this plug-in, the notion of refinement is to be
	 * understood as an operation that fills a new component (the target) from
	 * an existing one (the source), without any constraint about the results.
	 * </p>
	 * <p>
	 * The given source root is not modified by this operation.
	 * </p>
	 * <p>
	 * The given target file is modified but not saved by this operation.
	 * </p>
	 * 
	 * @param sourceRoot
	 *            the source root to refine
	 * @param targetRoot
	 *            the target root, initially empty, intended to be filled by
	 *            refinement participants
	 * @see IRodinProject#getRodinFile(String)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress report is
	 *            not desired
	 * @return refined root or <code>null</code>
	 * @throws RodinDBException
	 *             if a database request fails during the operation
	 * @since 1.4
	 */
	public static boolean refine(IInternalElement sourceRoot,
			IInternalElement targetRoot, IProgressMonitor monitor)
			throws RodinDBException {
		return new RefinementProcessor(sourceRoot).refine(targetRoot, monitor);
	}

	/**
	 * Returns the Rodin file element corresponding to the given file, or
	 * <code>null</code> if unable to associate the given file with a Rodin
	 * element.
	 * 
	 * <p>
	 * Creating a Rodin element has the side effect of creating and opening all
	 * of the element's parents if they are not yet open.
	 * </p>
	 * 
	 * @param file
	 *            the given file
	 * @return the Rodin file element corresponding to the given file, or
	 *         <code>null</code> if unable to associate the given file with a
	 *         Rodin file element
	 */
	public static IRodinFile valueOf(IFile file) {
		return (IRodinFile) RodinDBManager.valueOf(file, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin project corresponding to the given project.
	 * <p>
	 * Calling this method has the side effect of creating and opening all of
	 * the project's parents if they are not yet open.
	 * </p>
	 * 
	 * @param project
	 *            the given project
	 * @return the Rodin project corresponding to the given project, or
	 *         <code>null</code> if the given project is <code>null</code>
	 */
	public static IRodinProject valueOf(IProject project) {
		if (project == null) {
			return null;
		}
		final RodinDB rodinDB = (RodinDB) getRodinDB();
		return rodinDB.getRodinProject(project);
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
	 * </p>
	 * <p>
	 * Calling this method has the side effect of creating and opening all of
	 * the element's parents if they are not yet open.
	 * </p>
	 * 
	 * @param resource
	 *            the given resource
	 * @return the Rodin element corresponding to the given resource, or
	 *         <code>null</code> if unable to associate the given resource
	 *         with a Rodin element
	 */
	public static IRodinElement valueOf(IResource resource) {
		return RodinDBManager.valueOf(resource, null/* unknown Rodin project */);
	}

	/**
	 * Returns the Rodin database.
	 * 
	 * @param root
	 *            the given root
	 * @return the Rodin database, or <code>null</code> if the root is
	 *         <code>null</code>
	 */
	public static IRodinDB valueOf(IWorkspaceRoot root) {
		if (root == null) {
			return null;
		}
		return getRodinDB();
	}
	
	/**
	 * Convert a Rodin project to a new version. This method does not manipulate
	 * the project but only computes what is necessary for the conversion to be
	 * done. This is returned as a result (<code>IConversionResult</code>).
	 * 
	 * @param project
	 *            the Rodin project to be converted
	 * @param force
	 *            whether or not files are expected to be in synchrony
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting and cancellation are not desired
	 * @return the proposed conversion in an <code>IConversionResult</code>
	 * @throws RodinDBException
	 *             if there was a problem creating a conversion
	 */
	public static IConversionResult convert(IRodinProject project,
			boolean force, IProgressMonitor monitor) throws RodinDBException {
		final ConversionResult result = new ConversionResult(project);
		result.convert(force, monitor);
		return result;
	}

	/**
	 * Returns the Rodin database element.
	 * 
	 * @return the Rodin database
	 */
	public static IRodinDB getRodinDB() {
		return RodinDBManager.getRodinDBManager().getRodinDB();
	}

	/**
	 * Returns a fresh object that allows querying the indexer database.
	 * 
	 * @see IIndexQuery
	 * @return a fresh query object
	 */
	public static IIndexQuery makeIndexQuery() {
		return new IndexQuery();
	}

	/**
	 * Returns the occurrence kind declared with the given id in an extension of
	 * the extension point <code>org.rodinp.core.occurrenceKinds</code>. Returns
	 * <code>null</code> if no extension declares the given id.
	 * 
	 * @param id
	 *            the occurrence kind identifier
	 * @return the occurrence kind of the given id or <code>null</code> if there
	 *         is none
	 */
	public static IOccurrenceKind getOccurrenceKind(String id) {
		return OccurrenceKind.valueOf(id);
	}

	/**
	 * Returns the location pointing at the given element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin element
	 * @return the location pointing at the given element
	 */
	public static IRodinLocation getRodinLocation(IInternalElement element) {
		return new RodinLocation(element);
	}

	/**
	 * Returns the location pointing at the given internal element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin internal element
	 * @return the location pointing at the given element
	 */
	public static IInternalLocation getInternalLocation(IInternalElement element) {
		return new InternalLocation(element);
	}

	/**
	 * Returns the location pointing at the attribute of the given type in the
	 * given internal element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin internal element
	 * @param attributeType
	 *            the type of the attribute to point at
	 * @return the location pointing at the given attribute of the given element
	 */
	public static IAttributeLocation getInternalLocation(
			IInternalElement element, IAttributeType attributeType) {
		return new AttributeLocation(element, attributeType);
	}

	/**
	 * Returns the location pointing at the specified substring of the attribute
	 * of the given type in the given internal element. The substring is
	 * specified by giving its start and end positions.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            a Rodin internal element
	 * @param attributeType
	 *            the type of the attribute to point at
	 * @param start
	 *            the start position of the substring in the attribute value,
	 *            that is the index of the first character. Must be non-negative
	 *            and less than <code>end</code>
	 * @param end
	 *            the end position of the substring in the attribute value, that
	 *            is the index of the last character plus one. Must be greater
	 *            than <code>start</code>
	 * @return the location pointing at the specified substring of the given
	 *         attribute of the given element
	 * @throws IllegalArgumentException
	 *             if the constraints on <code>start</code> and <code>end</code>
	 *             are not fulfilled
	 */
	public static IAttributeSubstringLocation getInternalLocation(
			IInternalElement element, IAttributeType.String attributeType,
			int start, int end) {
		return new AttributeSubstringLocation(element, attributeType, start,
				end);
	}

}

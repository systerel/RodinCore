/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.internal.core.builder.RodinBuilder;
import org.rodinp.internal.core.util.DebugUtil;
import org.rodinp.internal.core.util.Util;


/**
 * This class is used by <code>RodinDBManager</code> to convert
 * <code>IResourceDelta</code>s into <code>IRodinElementDelta</code>s.
 * It also does some processing on the <code>RodinElement</code>s involved
 * (e.g. closing them).
 */
public class DeltaProcessor {

	/*package*/ static boolean DEBUG = false;
	/*package*/ static boolean VERBOSE = false;

	// must not collide with ElementChangedEvent event masks
	public static final int DEFAULT_CHANGE_EVENT = 0;
	
	/*
	 * The global state of delta processing.
	 */
	private final DeltaProcessingState state;
	
	/*
	 * The Rodin database
	 */
	RodinDBManager manager;
	
	/*
	 * The <code>RodinElementDelta</code> corresponding to the <code>IResourceDelta</code> being translated.
	 */
	private RodinElementDelta currentDelta;

	/* The Rodin element that was last created (see createElement(IResource)). 
	 * This is used as a stack of Rodin elements (using getParent() to pop it, and 
	 * using the various get*(...) to push it. */
	private Openable currentElement;
		
	/*
	 * Queue of deltas created explicily by the Rodin database that
	 * have yet to be fired.
	 */
	public ArrayList<IRodinElementDelta> rodinDBDeltas= new ArrayList<IRodinElementDelta>();
	
	/*
	 * Turns delta firing on/off. By default it is on.
	 */
	private boolean isFiring= true;
	
	/*
	 * Used to update the RodinDB for <code>IRodinElementDelta</code>s.
	 */
	private final DBUpdater dbUpdater = new DBUpdater();

	/* A set of RodinProject whose caches need to be reset */
	private HashSet<RodinProject> projectCachesToReset = new HashSet<RodinProject>();  

	/*
	 * Type of event that should be processed no matter what the real event type is.
	 */
	public int overridenEventType = -1;
		
	public DeltaProcessor(DeltaProcessingState state, RodinDBManager manager) {
		this.state = state;
		this.manager = manager;
	}

	/*
	 * Adds the given child handle to its parent's cache of children. 
	 */
	private void addToParentInfo(Openable child) {
		Openable parent = child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				RodinElementInfo info = parent.getElementInfo();
				info.addChild(child);
			} catch (RodinDBException e) {
				// do nothing - we already checked if open
			}
		}
	}
	
	/*
	 * Process the given delta and look for projects being added, opened, closed or
	 * with a Rodin nature being added or removed.
	 * Note that projects being deleted are checked in deleting(IProject).
	 * In all cases, add the project's dependents to the list of projects to update
	 * so that the build related markers can be updated.
	 */
	private void checkProjectsBeingAddedOrRemoved(IResourceDelta delta) {
		IResource resource = delta.getResource();
		boolean processChildren = false;

		switch (resource.getType()) {
			case IResource.ROOT :
				// Update cache of old projects 
				if (this.state.dbProjectsCache == null) {
					try {
						this.state.dbProjectsCache = this.manager.getRodinDB().getRodinProjects();
					} catch (RodinDBException e) {
						// Rodin database doesn't exist: never happens
					}
				}
				processChildren = true;
				break;
			case IResource.PROJECT :
				// NB: No need to check project's nature as if the project is not a Rodin project:
				//     - if the project is added or changed this is a noop for projectsBeingDeleted
				//     - if the project is closed, it has already lost its Rodin nature
				IProject project = (IProject)resource;
				RodinProject rodinProject = (RodinProject)RodinCore.valueOf(project);
				switch (delta.getKind()) {
					case IResourceDelta.ADDED :
						if (RodinProject.hasRodinNature(project)) {
							this.addToParentInfo(rodinProject);
						}
						break;
						
					case IResourceDelta.CHANGED : 
							if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
								if (project.isOpen()) {
									if (RodinProject.hasRodinNature(project)) {
										this.addToParentInfo(rodinProject);
									}
								} else {
									try {
										rodinProject.close();
									} catch (RodinDBException e) {
										// Rodin project doesn't exist: ignore
									}
									this.removeFromParentInfo(rodinProject);
									this.manager.removePerProjectInfo(rodinProject);
								}
							} else if ((delta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
								boolean wasRodinProject = this.manager.getRodinDB().findOldRodinProject(project) != null;
								boolean isRodinProject = RodinProject.hasRodinNature(project);
								if (wasRodinProject != isRodinProject) { 
									// workaround for bug 15168 circular errors not reported 
									if (isRodinProject) {
										this.addToParentInfo(rodinProject);
									} else {
										// remove classpath cache so that initializeRoots() will not consider the project has a classpath
										this.manager.removePerProjectInfo((RodinProject)RodinCore.valueOf(project));
										// close project
										try {
											rodinProject.close();
										} catch (RodinDBException e) {
											// java project doesn't exist: ignore
										}
										this.removeFromParentInfo(rodinProject);
									}
								} else {
									// in case the project was removed then added then changed (see bug 19799)
									if (isRodinProject) { // need nature check - 18698
										this.addToParentInfo(rodinProject);
									}
								}
							} else {
								// workaround for bug 15168 circular errors not reported 
								// in case the project was removed then added then changed
								if (RodinProject.hasRodinNature(project)) { // need nature check - 18698
									this.addToParentInfo(rodinProject);
								}						
							}		
							break;

					case IResourceDelta.REMOVED : 
						// remove classpath cache so that initializeRoots() will not consider the project has a classpath
						this.manager.removePerProjectInfo((RodinProject)RodinCore.valueOf(resource));
						break;
				}
				break;
			case IResource.FILE :
				// Nothing to do.
				break;
				
		}
		if (processChildren) {
			for (IResourceDelta child: delta.getAffectedChildren()) {
				checkProjectsBeingAddedOrRemoved(child);
			}
		}
	}
	
	/*
	 * Closes the given element, which removes it from the cache of open elements.
	 */
	private void close(Openable element) {
		try {
			element.close();
		} catch (RodinDBException e) {
			// do nothing
		}
	}
	/*
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * <li>An entry is made in the delta reporting a content change (K_CHANGE with F_CONTENT flag set).
	 * </ul>
	 * Delta argument could be null if processing an external JAR change
	 */
	private void contentChanged(Openable element) {

		close(element);
		removeFromBufferCache(element, false);
		int flags = IRodinElementDelta.F_CONTENT;
		currentDelta().changed(element, flags);
	}
	/*
	 * Creates the openables corresponding to this resource.
	 * Returns null if none was found.
	 */
	private Openable createElement(IResource resource, IElementType<?> elementType) {
		if (resource == null) return null;
		
		IRodinElement element = null;
		if (elementType == IRodinProject.ELEMENT_TYPE) {
			
			// note that non-java resources rooted at the project level will also enter this code with
			// an elementType JAVA_PROJECT (see #elementType(...)).
			if (resource instanceof IProject){
				
				if (this.currentElement != null 
						&& this.currentElement.getElementType() == IRodinProject.ELEMENT_TYPE
						&& ((IRodinProject)this.currentElement).getProject().equals(resource)) {
					return this.currentElement;
				}
				IProject proj = (IProject)resource;
				if (RodinProject.hasRodinNature(proj)) {
					element = RodinCore.valueOf(proj);
				} else {
					// Rodin project may have been been closed or removed (look for
					// element amongst old Rodin project s list).
					element = this.manager.getRodinDB().findOldRodinProject(proj);
				}
			}
		} else {
			element = RodinCore.valueOf(resource);
		}
		if (element == null) return null;
		this.currentElement = (Openable) element;
		return this.currentElement;
	}
	/*
	 * Check if external archives have changed and create the corresponding deltas.
	 * Returns whether at least on delta was created.
	 */
	
	private RodinElementDelta currentDelta() {
		if (this.currentDelta == null) {
			this.currentDelta = new RodinElementDelta(this.manager.getRodinDB());
		}
		return this.currentDelta;
	}
	/*
	 * Note that the project is about to be deleted.
	 */
	private void deleting(IProject project) {
		
		try {
			// discard indexing jobs that belong to this project so that the project can be 
			// deleted without interferences from the index manager
			// this.manager.indexManager.discardJobs(project.getName());

			RodinProject rodinProject = (RodinProject)RodinCore.valueOf(project);
			rodinProject.close();
			
			// Also update cache of old projects
			if (this.state.dbProjectsCache == null) {
				this.state.dbProjectsCache = this.manager.getRodinDB().getRodinProjects();
			}

			this.removeFromParentInfo(rodinProject);
		} catch (RodinDBException e) {
			// Rodin project doesn't exist: ignore
		}
	}
	/*
	 * Processing for an element that has been added:<ul>
	 * <li>If the element is a project, do nothing, and do not process
	 * children, as when a project is created it does not yet have any
	 * natures - specifically a java nature.
	 * <li>If the element is not a project, process it as added (see
	 * <code>basicElementAdded</code>.
	 * </ul>
	 */
	private void elementAdded(Openable element, IResourceDelta delta) {
		IElementType<?> elementType = element.getElementType();
		
		if (elementType == IRodinProject.ELEMENT_TYPE) {
			// project add is handled by RodinProject.configure() because
			// when a project is created, it does not yet have a java nature
			if (delta != null && RodinProject.hasRodinNature((IProject)delta.getResource())) {
				addToParentInfo(element);
				if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
					Openable movedFromElement = (Openable)element.getRodinDB().getRodinProject(delta.getMovedFromPath().lastSegment());
					currentDelta().movedTo(element, movedFromElement);
				} else {
					currentDelta().added(element);
				}
				
				// refresh pkg fragment roots and caches of the project (and its dependents)
				this.projectCachesToReset.add((RodinProject) element);
			}
		} else {			
			if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) {
				// regular element addition
					addToParentInfo(element);
					
					// Force the element to be closed as it might have been opened 
					// before the resource modification came in and it might have a new child
					// For example, in an IWorkspaceRunnable:
					// 1. create a package fragment p using a java model operation
					// 2. open package p
					// 3. add file X.java in folder p
					// When the resource delta comes in, only the addition of p is notified, 
					// but the package p is already opened, thus its children are not recomputed
					// and it appears empty.
					close(element);
			
					currentDelta().added(element);
			} else {
				// element is moved
				addToParentInfo(element);
				close(element);
				
				IPath movedFromPath = delta.getMovedFromPath();
				IResource res = delta.getResource();
				IFile movedFromFile = res.getWorkspace().getRoot().getFile(movedFromPath);
				IElementType<?> movedFromType = this.elementType(movedFromFile);
				Openable movedFromElement = this.createElement(movedFromFile, movedFromType);
				if (movedFromElement == null) {
					// moved from a non-Rodin file
					currentDelta().added(element);
				} else {
					currentDelta().movedTo(element, movedFromElement);
				}
			}
			
			// reset project's caches
			// TODO make that finer grained when project caches are implemented.
			final RodinProject project = (RodinProject) element.getRodinProject();
			this.projectCachesToReset.add(project);						
		}
	}
	/*
	 * Generic processing for a removed element:<ul>
	 * <li>Close the element, removing its structure from the cache
	 * <li>Remove the element from its parent's cache of children
	 * <li>Add a REMOVED entry in the delta
	 * </ul>
	 * Delta argument could be null if processing an external JAR change
	 */
	private void elementRemoved(Openable element, IResourceDelta delta) {
		
		IElementType<?> elementType = element.getElementType();
		if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
			// regular element removal
			close(element);
			removeFromParentInfo(element);
			removeFromBufferCache(element, true);
			currentDelta().removed(element);
		} else {
			// element is moved
			close(element);
			removeFromParentInfo(element);
			removeFromBufferCache(element, true);
			
			IPath movedToPath = delta.getMovedToPath();
			IResource res = delta.getResource();
			IResource movedToRes;
			switch (res.getType()) {
				case IResource.PROJECT:
					movedToRes = res.getWorkspace().getRoot().getProject(movedToPath.lastSegment());
					break;
				case IResource.FOLDER:
					movedToRes = res.getWorkspace().getRoot().getFolder(movedToPath);
					break;
				case IResource.FILE:
					movedToRes = res.getWorkspace().getRoot().getFile(movedToPath);
					break;
				default:
					return;
			}

			// find the element type of the moved from element
			IElementType<?> movedToType = this.elementType(movedToRes);

			// reset current element as it might be inside a nested root (popUntilPrefixOf() may use the outer root)
			this.currentElement = null;
			
			// create the moved To element
			Openable movedToElement = 
				elementType != IRodinProject.ELEMENT_TYPE
				&& movedToType == IRodinProject.ELEMENT_TYPE ? 
					null : // outside classpath
					this.createElement(movedToRes, movedToType);
			if (movedToElement == null) {
				// moved outside classpath
				currentDelta().removed(element);
			} else {
				currentDelta().movedFrom(element, movedToElement);
			}
		}

		if (elementType == IRodinDB.ELEMENT_TYPE) {
			// this.manager.indexManager.reset();
		} else if (elementType == IRodinProject.ELEMENT_TYPE) {
				this.projectCachesToReset.add((RodinProject) element);
		} else {
			final RodinProject project = (RodinProject) element.getRodinProject();
			this.projectCachesToReset.add(project);				
		}
	}
	/*
	 * Returns the type of the Rodin element the given delta matches to.
	 * Returns <code>null</code> if unknown (e.g. a non-Rodin resource)
	 */
	private IElementType<?> elementType(IResource res) {
		if (res instanceof IProject) {
			return IRodinProject.ELEMENT_TYPE;
		} else if (res.getType() == IResource.FILE) {
			final ElementTypeManager etManager = ElementTypeManager
					.getInstance();
			if (etManager.getFileAssociation((IFile) res) == null)
				return null;
			return IRodinFile.ELEMENT_TYPE;
		} else {
			return null;
		}
	}
	/*
	 * Flushes all deltas without firing them.
	 */
	public void flush() {
		this.rodinDBDeltas = new ArrayList<IRodinElementDelta>();
	}
	/* Returns the list of Rodin projects in the workspace.
	 * 
	 */
	IRodinProject[] getRodinProjects() {
		try {
			return this.manager.getRodinDB().getRodinProjects();
		} catch (RodinDBException e) {
			// java model doesn't exist
			return new IRodinProject[0];
		}
	}
	/*
	 * Fire Rodin database delta, flushing them after the fact after post_change notification.
	 * If the firing mode has been turned off, this has no effect. 
	 */
	public void fire(IRodinElementDelta customDelta, int eventType) {
		if (!this.isFiring) return;
		
		if (DEBUG) {
			System.out.println("-----------------------------------------------------------------------------------------------------------------------");//$NON-NLS-1$
		}

		IRodinElementDelta deltaToNotify;
		if (customDelta == null){
			deltaToNotify = this.mergeDeltas(this.rodinDBDeltas);
		} else {
			deltaToNotify = customDelta;
		}
			
		// Notification
	
		// Important: if any listener reacts to notification by updating the listeners list or mask, these lists will
		// be duplicated, so it is necessary to remember original lists in a variable (since field values may change under us)
		IElementChangedListener[] listeners = this.state.elementChangedListeners;
		int[] listenerMask = this.state.elementChangedListenerMasks;
		int listenerCount = this.state.elementChangedListenerCount;

		switch (eventType) {
			case DEFAULT_CHANGE_EVENT:
				firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
				break;
			case ElementChangedEvent.POST_CHANGE:
				firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
				break;
		}
	}

	private void firePostChangeDelta(
		IRodinElementDelta deltaToNotify,
		IElementChangedListener[] listeners,
		int[] listenerMask,
		int listenerCount) {
			
		// post change deltas
		if (DEBUG){
			System.out.println("FIRING POST_CHANGE Delta ["+Thread.currentThread()+"]:"); //$NON-NLS-1$//$NON-NLS-2$
			System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
		}
		if (deltaToNotify != null) {
			// flush now so as to keep listener reactions to post their own deltas for subsequent iteration
			this.flush();
			
			notifyListeners(deltaToNotify, ElementChangedEvent.POST_CHANGE, listeners, listenerMask, listenerCount);
		} 
	}		
	/*
	 * Returns whether a given delta contains some information relevant to the RodinDB,
	 * in particular it will not consider SYNC or MARKER only deltas.
	 */
	private boolean isAffectedBy(IResourceDelta rootDelta){
		//if (rootDelta == null) System.out.println("NULL DELTA");
		//long start = System.currentTimeMillis();
		if (rootDelta != null) {
			// use local exception to quickly escape from delta traversal
			class FoundRelevantDeltaException extends RuntimeException {
				private static final long serialVersionUID = 7137113252936111022L; // backward compatible
				// only the class name is used (to differenciate from other RuntimeExceptions)
			}
			try {
				rootDelta.accept(new IResourceDeltaVisitor() {
					@Override
					public boolean visit(IResourceDelta delta) /* throws CoreException */ {
						switch (delta.getKind()){
							case IResourceDelta.ADDED :
							case IResourceDelta.REMOVED :
								throw new FoundRelevantDeltaException();
							case IResourceDelta.CHANGED :
								// if any flag is set but SYNC or MARKER, this delta should be considered
								if (delta.getAffectedChildren().length == 0 // only check leaf delta nodes
										&& (delta.getFlags() & ~(IResourceDelta.SYNC | IResourceDelta.MARKERS)) != 0) {
									throw new FoundRelevantDeltaException();
								}
						}
						return true;
					}
				});
			} catch(FoundRelevantDeltaException e) {
				//System.out.println("RELEVANT DELTA detected in: "+ (System.currentTimeMillis() - start));
				return true;
			} catch(CoreException e) { // ignore delta if not able to traverse
			}
		}
		//System.out.println("IGNORE SYNC DELTA took: "+ (System.currentTimeMillis() - start));
		return false;
	}

	/*
	 * Merges all awaiting deltas.
	 */
	private IRodinElementDelta mergeDeltas(Collection<IRodinElementDelta> deltas) {
		if (deltas.size() == 0) return null;
		if (deltas.size() == 1) return deltas.iterator().next();
		
		if (VERBOSE) {
			System.out.println("MERGING " + deltas.size() + " DELTAS ["+Thread.currentThread()+"]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		Iterator<IRodinElementDelta> iterator = deltas.iterator();
		RodinElementDelta rootDelta = new RodinElementDelta(this.manager.rodinDB);
		boolean insertedTree = false;
		while (iterator.hasNext()) {
			RodinElementDelta delta = (RodinElementDelta)iterator.next();
			if (VERBOSE) {
				System.out.println(delta.toString());
			}
			IRodinElement element = delta.getElement();
			if (this.manager.rodinDB.equals(element)) {
				IRodinElementDelta[] children = delta.getAffectedChildren();
				for (int j = 0; j < children.length; j++) {
					RodinElementDelta projectDelta = (RodinElementDelta) children[j];
					rootDelta.insertDeltaTree(projectDelta.getElement(), projectDelta);
					insertedTree = true;
				}
				IResourceDelta[] resourceDeltas = delta.getResourceDeltas();
				if (resourceDeltas != null) {
					for (int i = 0, length = resourceDeltas.length; i < length; i++) {
						rootDelta.addResourceDelta(resourceDeltas[i]);
						insertedTree = true;
					}
				}
			} else {
				rootDelta.insertDeltaTree(element, delta);
				insertedTree = true;
			}
		}
		if (insertedTree) return rootDelta;
		return null;
	}	
	private void notifyListeners(IRodinElementDelta deltaToNotify, int eventType, IElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {
		final ElementChangedEvent extraEvent = new ElementChangedEvent(deltaToNotify, eventType);
		for (int i= 0; i < listenerCount; i++) {
			if ((listenerMask[i] & eventType) != 0){
				final IElementChangedListener listener = listeners[i];
				long start = -1;
				if (VERBOSE) {
					System.out.print("Listener #" + (i+1) + "=" + listener.toString());//$NON-NLS-1$//$NON-NLS-2$
					start = System.currentTimeMillis();
				}
				// wrap callbacks with Safe runnable for subsequent listeners to be called when some are causing grief
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void handleException(Throwable exception) {
						Util.log(exception, "Exception occurred in listener of Rodin element change notification"); //$NON-NLS-1$
					}
					@Override
					public void run() throws Exception {
						listener.elementChanged(extraEvent);
					}
				});
				if (VERBOSE) {
					System.out.println(" -> " + (System.currentTimeMillis()-start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	
	/*
	 * Generic processing for elements with changed contents:<ul>
	 * <li>The element is closed such that any subsequent accesses will re-open
	 * the element reflecting its new structure.
	 * <li>An entry is made in the delta reporting a content change (K_CHANGE with F_CONTENT flag set).
	 * </ul>
	 */
	private void nonRodinResourcesChanged(Openable element, IResourceDelta delta)
		throws RodinDBException {

		// reset non-Rodin resources if element was open
		if (element.isOpen()) {
			RodinElementInfo info = element.getElementInfo();
			if (element.getElementType() == IRodinDB.ELEMENT_TYPE) {
					((RodinDBInfo) info).nonRodinResources = null;
					currentDelta().addResourceDelta(delta);
					return;
			} else if (element.getElementType() ==  IRodinProject.ELEMENT_TYPE) {
					((RodinProjectElementInfo) info).setNonRodinResources(null);
			}
		}

		RodinElementDelta current = currentDelta();
		RodinElementDelta elementDelta = current.find(element);
		if (elementDelta == null) {
			// don't use find after creating the delta as it can be null (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=63434)
			elementDelta = current.changed(element, IRodinElementDelta.F_CONTENT);
		}
		elementDelta.addResourceDelta(delta);
	}
	
	/*
	 * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code> into
	 * the corresponding set of <code>IRodinElementDelta</code>, rooted in the
	 * relevant <code>RodinDB</code>s.
	 */
	private IRodinElementDelta processResourceDelta(IResourceDelta changes) {

		try {
			IRodinDB database = this.manager.getRodinDB();
			if (!database.isOpen()) {
				// force opening of the Rodin database so that Rodin element deltas are reported
				try {
					database.open(null);
				} catch (RodinDBException e) {
					Util.log(e, "Couldn't open the Rodin database");
					return null;
				}
			}
			this.currentElement = null;
			
			// get the workspace delta, and start processing there.
			for (IResourceDelta delta: changes.getAffectedChildren()) {
				IResource res = delta.getResource();
				
				// find out the element type
				IElementType<?> elementType;
				IProject proj = (IProject) res;
				boolean wasRodinProject = this.manager.getRodinDB().findOldRodinProject(proj) != null;
				boolean isRodinProject = RodinProject.hasRodinNature(proj);
				if (!wasRodinProject && !isRodinProject) {
					elementType = null;
				} else {
					elementType = IRodinProject.ELEMENT_TYPE; 

					// traverse delta
					this.traverseProjectDelta(delta, elementType);
				}
				
				if (elementType == null
						|| (wasRodinProject != isRodinProject && (delta.getKind()) == IResourceDelta.CHANGED)) {
					// project has changed nature (description or open/closed)
					try {
						// add child as non Rodin resource
						nonRodinResourcesChanged((RodinDB)database, delta);
					} catch (RodinDBException e) {
						// Rodin database could not be opened
					}
				}
			}
			resetProjectCaches();

			return this.currentDelta;
		} finally {
			this.currentDelta = null;
			this.projectCachesToReset.clear();
		}
	}

	/*
	 * Traverse the set of projects which have changed namespace, and reset their 
	 * caches and their dependents.
	 */
	private void resetProjectCaches() {
		// TODO implement when project caches are there
	}
	
	/*
	 * Registers the given delta with this delta processor.
	 */
	public void registerRodinDBDelta(IRodinElementDelta delta) {
		this.rodinDBDeltas.add(delta);
	}

	/*
	 * Removes any buffer associated to the given element. 
	 */
	private void removeFromBufferCache(Openable child, boolean force) {
		if (child instanceof RodinFile) {
			final RodinFile rodinFile = (RodinFile) child;
			final RodinDBManager rodinDBManager = RodinDBManager.getRodinDBManager();
			rodinDBManager.removeBuffer(rodinFile.getMutableCopy(), force);
		}
	}

	/*
	 * Removes the given element from its parents cache of children. If the
	 * element does not have a parent, or the parent is not currently open,
	 * this has no effect. 
	 */
	private void removeFromParentInfo(Openable child) {
		Openable parent = child.getParent();
		if (parent != null && parent.isOpen()) {
			try {
				RodinElementInfo info = parent.getElementInfo();
				info.removeChild(child);
			} catch (RodinDBException e) {
				// do nothing - we already checked if open
			}
		}
	}
	/*
	 * Notification that some resource changes have happened
	 * on the platform, and that the Rodin Model should update any required
	 * internal structures such that its elements remain consistent.
	 * Translates <code>IResourceDeltas</code> into <code>IRodinElementDeltas</code>.
	 *
	 * @see IResourceDelta
	 * @see IResource 
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		
		if (event.getSource() instanceof IWorkspace) {
			int eventType = this.overridenEventType == -1 ? event.getType() : this.overridenEventType;
			IResource resource = event.getResource();
			IResourceDelta delta = event.getDelta();
			
			if (VERBOSE) {
				System.out.println("-----------------------------------------------------------------------------------------------------------------------");//$NON-NLS-1$
				System.out.println("PROCESSING Resource Changed Event ["+Thread.currentThread()+"]:"); //$NON-NLS-1$//$NON-NLS-2$
				DebugUtil.printEvent(event);
				if (this.overridenEventType != -1) {
					System.out.println("  Event type is overriden to:" + //$NON-NLS-1$
							DebugUtil.eventTypeAsString(eventType));
				}
			}
			
			switch(eventType){
				case IResourceChangeEvent.PRE_DELETE :
					try {
						if(resource.getType() == IResource.PROJECT 
							&& ((IProject) resource).hasNature(RodinCore.NATURE_ID)) {
								
							deleting((IProject)resource);
						}
					} catch(CoreException e){
						// project doesn't exist or is not open: ignore
					}
					return;
					
				case IResourceChangeEvent.POST_CHANGE :
					if (isAffectedBy(delta)) { // avoid populating for SYNC or MARKER deltas
						try {
							try {
								stopDeltas();
								checkProjectsBeingAddedOrRemoved(delta);
								IRodinElementDelta translatedDelta = processResourceDelta(delta);
								if (translatedDelta != null) { 
									registerRodinDBDelta(translatedDelta);
								}
							} finally {
								startDeltas();
							}
							fire(null, ElementChangedEvent.POST_CHANGE);
						} finally {
							// Cleanup cache of old projects 
							this.state.dbProjectsCache = null;
						}
					}
					return;
					
				case IResourceChangeEvent.PRE_BUILD :
					// this.processPostChange = false;
					if(isAffectedBy(delta)) { // avoid populating for SYNC or MARKER deltas
						RodinBuilder.buildStarting();
					}
					// does not fire any deltas
					return;

				case IResourceChangeEvent.POST_BUILD :
					RodinBuilder.buildFinished();
					return;
			}
		}
	}
	/*
	 * Turns the firing mode to on. That is, deltas that are/have been
	 * registered will be fired.
	 */
	private void startDeltas() {
		this.isFiring = true;
	}
	/*
	 * Turns the firing mode to off. That is, deltas that are/have been
	 * registered will not be fired until deltas are started again.
	 */
	private void stopDeltas() {
		this.isFiring = false;
	}
	
	/*
	 * Converts an <code>IResourceDelta</code> rooted at a Rodin project and
	 * its children into the corresponding <code>IRodinElementDelta</code>s.
	 */
	private void traverseProjectDelta(IResourceDelta delta,
			IElementType<?> elementType) {
		
		IProject project = (IProject) delta.getResource();
		RodinProject rodinProject = (RodinProject) RodinCore.valueOf(project);
		
		// process current delta
		boolean processChildren = this.updateCurrentDeltaAndIndex(delta, elementType);

		// process children if needed
		if (processChildren) {
			for (IResourceDelta child: delta.getAffectedChildren()) {
				traverseDelta(child, rodinProject);
			}
		}
	}
	
	/*
	 * Converts an <code>IResourceDelta</code> and its children into
	 * the corresponding <code>IRodinElementDelta</code>s.
	 */
	private void traverseDelta(IResourceDelta delta, RodinProject rodinProject) {
		
		IResource res = delta.getResource();
		switch (res.getType()) {
		case IResource.FILE:
			IElementType<?> elementType = elementType(res);
			if (elementType != null) {
				this.updateCurrentDeltaAndIndex(delta, elementType);
			} else {
				try {
					// add child as non Rodin resource
					nonRodinResourcesChanged(rodinProject, delta);
				} catch (RodinDBException e) {
					// Rodin database could not be opened
				}
			}
			break;
		case IResource.FOLDER:
			try {
				// add child as non Rodin resource
				nonRodinResourcesChanged(rodinProject, delta);
			} catch (RodinDBException e) {
				// Rodin database could not be opened
			}
			break;
		default:
			assert false;
		}
	}
	
	/*
	 * Update the current delta (ie. add/remove/change the given element) and update the correponding index.
	 * Returns whether the children of the given delta must be processed.
	 * @throws a RodinDBException if the delta doesn't correspond to a java element of the given type.
	 */
	public boolean updateCurrentDeltaAndIndex(IResourceDelta delta,
			IElementType<?> elementType) {

		Openable element;
		switch (delta.getKind()) {
			case IResourceDelta.ADDED :
				IResource deltaRes = delta.getResource();
				element = createElement(deltaRes, elementType);
				if (element == null) {
					return false;
				}
				// updateIndex(element, delta);
				elementAdded(element, delta);
				return elementType == IRodinProject.ELEMENT_TYPE;
			case IResourceDelta.REMOVED :
				deltaRes = delta.getResource();
				element = createElement(deltaRes, elementType);
				if (element == null) {
					return false;
				}
				// updateIndex(element, delta);
				elementRemoved(element, delta);
	
				if (deltaRes.getType() == IResource.PROJECT){			
					// reset the corresponding project built state, since cannot reuse if added back
					if (RodinBuilder.DEBUG)
						System.out.println("Clearing last state for removed project : " + deltaRes); //$NON-NLS-1$
					this.manager.setLastBuiltState((IProject)deltaRes, null /*no state*/);
					
					// clean up previous session containers (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=89850)
					// this.manager.previousSessionContainers.remove(element);
				}
				return elementType == IRodinProject.ELEMENT_TYPE;
			case IResourceDelta.CHANGED :
				int flags = delta.getFlags();
				if ((flags & IResourceDelta.CONTENT) != 0 || (flags & IResourceDelta.ENCODING) != 0) {
					// content or encoding has changed
					element = createElement(delta.getResource(), elementType);
					if (element == null) return false;
					// updateIndex(element, delta);
					contentChanged(element);
				} else if (elementType == IRodinProject.ELEMENT_TYPE) {
					if ((flags & IResourceDelta.OPEN) != 0) {
						// project has been opened or closed
						IProject res = (IProject)delta.getResource();
						element = createElement(res, elementType);
						if (element == null) {
							return false;
						}
						if (res.isOpen()) {
							if (RodinProject.hasRodinNature(res)) {
								addToParentInfo(element);
								currentDelta().opened(element);

								// refresh pkg fragment roots and caches of the project (and its dependents)
								this.projectCachesToReset.add((RodinProject) element);
								
								// this.manager.indexManager.indexAll(res);
							}
						} else {
							RodinDB javaModel = this.manager.getRodinDB();
							boolean wasRodinProject = javaModel.findOldRodinProject(res) != null;
							if (wasRodinProject) {
								close(element);
								removeFromParentInfo(element);
								currentDelta().closed(element);
								// this.manager.indexManager.discardJobs(element.getElementName());
								// this.manager.indexManager.removeIndexFamily(res.getFullPath());
							}
						}
						return false; // when a project is open/closed don't process children
					}
					if ((flags & IResourceDelta.DESCRIPTION) != 0) {
						IProject res = (IProject)delta.getResource();
						RodinDB javaModel = this.manager.getRodinDB();
						boolean wasRodinProject = javaModel.findOldRodinProject(res) != null;
						boolean isRodinProject = RodinProject.hasRodinNature(res);
						if (wasRodinProject != isRodinProject) {
							// project's nature has been added or removed
							element = this.createElement(res, elementType);
							if (element == null) return false; // note its resources are still visible as roots to other projects
							if (isRodinProject) {
								elementAdded(element, delta);
								// this.manager.indexManager.indexAll(res);
							} else {
								elementRemoved(element, delta);
								// this.manager.indexManager.discardJobs(element.getElementName());
								// this.manager.indexManager.removeIndexFamily(res.getFullPath());
								// reset the corresponding project built state, since cannot reuse if added back
								if (RodinBuilder.DEBUG)
									System.out.println("Clearing last state for project loosing Rodin nature: " + res); //$NON-NLS-1$
								this.manager.setLastBuiltState(res, null /*no state*/);
							}
							return false; // when a project's nature is added/removed don't process children
						}
					}
					return true; // something changed within the project and we don't know what.
				}
				return true;
		}
		return true;
	}
	
	/*
	 * Update the Rodin database given some delta
	 */
	public void updateRodinDB(IRodinElementDelta customDelta) {

		if (customDelta == null) {
			for (IRodinElementDelta delta: this.rodinDBDeltas) {
				this.dbUpdater.processRodinDelta(delta);
			}
		} else {
			this.dbUpdater.processRodinDelta(customDelta);
		}
	}

}

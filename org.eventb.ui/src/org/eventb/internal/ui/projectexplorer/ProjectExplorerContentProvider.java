/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.projectexplorer.AbstractRodinContentProvider;
import org.eventb.ui.projectexplorer.TreeNode;
import org.rodinp.core.*;

/**
 * @author htson This class provide the content for the tree viewer in the
 *         Project Explorer.
 */
public class ProjectExplorerContentProvider implements
		IStructuredContentProvider, ITreeContentProvider,
		IElementChangedListener {

	// The invisible root of the tree viewer.
	private Object invisibleRoot = null;

	// The Project Explorer.
	ProjectExplorer explorer;

	// List of elements need to be refresh (when processing Delta of changes).
	private ArrayList<Object> toRefresh;

	private final static String CONTENTPROVIDER_ID = EventBUIPlugin.PLUGIN_ID
			+ ".contentProvider";
	private final static Map<IElementType<? extends IRodinElement>, ProjectExplorerContributionProxy> contentProviders = initExtensions();

	/**
	 * Constructor.
	 * 
	 * @param explorer
	 *            The Project Explorer
	 */
	public ProjectExplorerContentProvider(final ProjectExplorer explorer) {
		this.explorer = explorer;
		// elementsMap = new HashMap<IRodinFile, Object[]>();
	}

	/**
	 * This response for the delta changes from the Rodin Database
	 * <p>
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		toRefresh = new ArrayList<Object>();
		processDelta(event.getDelta());
		postRefresh(toRefresh, true);
	}

	/**
	 * Process the delta recursively and depend on the kind of the delta.
	 * <p>
	 * 
	 * @param delta
	 *            The Delta from the Rodin Database
	 */
	private void processDelta(final IRodinElementDelta delta) {
		int kind = delta.getKind();
		IRodinElement element = delta.getElement();
		if (kind == IRodinElementDelta.ADDED) {
			Object parent;
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			} else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}

		if (kind == IRodinElementDelta.REMOVED) {
			Object parent;
			if (element instanceof IRodinProject) {
				parent = invisibleRoot;
			} else {
				parent = element.getParent();
			}
			toRefresh.add(parent);
			return;
		}

		if (kind == IRodinElementDelta.CHANGED) {
			int flags = delta.getFlags();

			if ((flags & IRodinElementDelta.F_CHILDREN) != 0) {
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (IRodinElementDelta element2 : deltas) {
					processDelta(element2);
				}
				return;
			}

			if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
				toRefresh.add(element.getParent());
				return;
			}

			if ((flags & IRodinElementDelta.F_CONTENT) != 0) {
				toRefresh.add(element);
				return;
			}

			if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
				toRefresh.add(element);
				return;
			}
		}

	}

	/**
	 * Refresh the nodes.
	 * <p>
	 * 
	 * @param toRefresh2
	 *            List of node to refresh
	 * @param updateLabels
	 *            <code>true</code> if the label need to be updated as well
	 */
	private void postRefresh(final ArrayList<Object> toRefresh2,
			final boolean updateLabels) {
		UIUtils.asyncPostRunnable(new Runnable() {
			public void run() {
				TreeViewer viewer = explorer.getTreeViewer();
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object[] objects = viewer.getExpandedElements();
					for (Object elem : toRefresh2) {
						viewer.refresh(elem, updateLabels);
					}
					viewer.setExpandedElements(objects);
				}
			}
		}, explorer.getTreeViewer().getControl());
	}

	/**
	 * Register/De-register to the Rodin Core when the input is change
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(final Viewer v, final Object oldInput,
			final Object newInput) {
		if (oldInput == null && newInput != null) {
			RodinCore.addElementChangedListener(this);
		} else if (oldInput != null && newInput == null) {
			RodinCore.removeElementChangedListener(this);
		}
		invisibleRoot = newInput;

		explorer.setRoot(invisibleRoot);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		// Recursively dispose all instantiated content providers
		for (ProjectExplorerContributionProxy proxy : contentProviders.values()) {
			proxy.dispose();
		}
	}

	/**
	 * Return the list of elements for a particular parent
	 * <p>
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(final Object parent) {
		return getChildren(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(final Object child) {
		if (child instanceof TreeNode) {
			return ((TreeNode<?>) child).getParent();
		}
		if (child instanceof IRodinElement) {
			IRodinElement element = (IRodinElement) child;
			IRodinElement parent = (element).getParent();
			return parent;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(final Object parent) {

		if (hasProvider(parent)) {
			ITreeContentProvider provider = getProvider(parent);
			return provider.getChildren(parent);
		}

		if (parent instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) parent;
			try {
				ArrayList<IRodinElement> results = new ArrayList<IRodinElement>();
				IRodinElement[] rodinElements = prj.getChildren();
				for (IRodinElement rodinElement : rodinElements) {
					if (hasProvider(rodinElement)) {
						results.add(rodinElement);
					}
				}

				return results.toArray();
			} catch (RodinDBException e) {
				// If it is out of date then prompt the user to refresh
				if (!prj.getResource().isSynchronized(IResource.DEPTH_INFINITE)) {
					MessageDialog
							.openWarning(
									EventBUIPlugin.getActiveWorkbenchShell(),
									"Resource out of date",
									"Project "
											+ ((IRodinProject) parent)
													.getElementName()
											+ " is out of date with the file system and will be refresh.");
					ProjectExplorerActionGroup.refreshAction.refreshAll();
				} else { // Otherwise, there are problems, log an error
					// message
					e.printStackTrace();
					UIUtils.log(e, "Cannot read the Rodin project "
							+ prj.getElementName());
					return new Object[0];
				}
			}
		}

		try {
			if (parent instanceof IParent) {
				return ((IParent) parent).getChildren();
			}
		} catch (RodinDBException e) {
			// If the resource is out of date then prompt the user to refresh
			if (((IRodinElement) parent).getCorrespondingResource()
					.isSynchronized(IResource.DEPTH_INFINITE)) {
				MessageDialog
						.openWarning(
								EventBUIPlugin.getActiveWorkbenchShell(),
								"Resource out of date",
								"Element "
										+ ((IParent) parent).toString()
										+ " is out of date with the file system and will be refresh.");
				ProjectExplorerActionGroup.refreshAction.refreshAll();
			} else { // Otherwise, there are problems, log an error
				// message
				e.printStackTrace();
				UIUtils.log(e, "Cannot read the element " + parent);
			}
		}

		if (parent instanceof TreeNode) {
			return ((TreeNode<?>) parent).getChildren();
		}
		return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(final Object parent) {
		if (parent instanceof IRodinFile) {
			return true;
		}
		try {
			if (parent instanceof IParent) {
				return ((IParent) parent).hasChildren();
			}
			if (parent instanceof TreeNode) {
				return ((TreeNode<?>) parent).hasChildren();
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns the actual Contentprovider for a specific Type of Element
	 * 
	 * @param o
	 * @return
	 */
	private ITreeContentProvider getProvider(final Object o) {

		if (!((o instanceof IRodinElement))) {
			return createNullProvider();
		}

		IElementType<? extends IRodinElement> elementType = ((IRodinElement) o)
				.getElementType();

		if (elementType == null) {
			return createNullProvider();
		}

		ProjectExplorerContributionProxy providerProxy = contentProviders
				.get(elementType);

		if (providerProxy == null) {
			return createNullProvider();
		}
		return providerProxy.getProvider();
	}

	private static ITreeContentProvider createNullProvider() {
		return new AbstractRodinContentProvider() {
			@Override
			public Object[] getChildren(final Object parentElement) {
				return new Object[] {};
			}
		};
	}

	private static boolean hasProvider(final Object e) {

		if (!(e instanceof IRodinElement)) {
			return false;
		}

		IElementType<? extends IRodinElement> elementType = ((IRodinElement) e)
				.getElementType();
		if (elementType == null) {
			return false;
		}
		return contentProviders.containsKey(elementType);
	}

	/**
	 * Builds a map of Element Types and corresponding Content-Providers from
	 * the Extension-Registry
	 * 
	 * @return
	 */
	private static Map<IElementType<? extends IRodinElement>, ProjectExplorerContributionProxy> initExtensions() {

		HashMap<IElementType<? extends IRodinElement>, ProjectExplorerContributionProxy> returnValue = new HashMap<IElementType<? extends IRodinElement>, ProjectExplorerContributionProxy>();

		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry
				.getExtensionPoint(CONTENTPROVIDER_ID);
		if (extensionPoint == null) {
			EventBUIPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, EventBUIPlugin.PLUGIN_ID,
							"Internal Error, Extensionpoint "
									+ CONTENTPROVIDER_ID + " not found"));
		}
		IExtension[] extensions = extensionPoint.getExtensions();

		for (IExtension extension : extensions) {
			IConfigurationElement[] configurationElements = extension
					.getConfigurationElements();
			for (IConfigurationElement configurationElement : configurationElements) {
				if ("provider".equals(configurationElement.getName())) {
					String typeId = configurationElement
							.getAttribute("rodinElementId");
					if (typeId == null) {
						continue;
					}

					IElementType<? extends IRodinElement> elementType = RodinCore
							.getElementType(typeId);

					if (returnValue.containsKey(elementType)) {
						EventBUIPlugin
								.getDefault()
								.getLog()
								.log(
										new Status(
												IStatus.ERROR,
												EventBUIPlugin.PLUGIN_ID,
												"Duplicate Project Explorer Contribution for type "
														+ elementType.getName()
														+ ". The new extension replaces the old one"));
					}

					returnValue.put(elementType,
							new ProjectExplorerContributionProxy(
									configurationElement));
				}
			}
		}

		return returnValue;

	}

}

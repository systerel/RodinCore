/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.ElementUIRegistry;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.LabelProvider</code> and provides
 *         labels for different elements appeared in the UI
 */
public class ElementLabelProvider extends LabelProvider implements
		IFontProvider, IPropertyChangeListener, IResourceChangeListener {

	Viewer viewer;

	public ElementLabelProvider(Viewer viewer) {
		this.viewer = viewer;
		JFaceResources.getFontRegistry().addListener(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this,
				IResourceChangeEvent.POST_BUILD
						| IResourceChangeEvent.POST_CHANGE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object obj) {
		if (obj instanceof TreeNode)
			return obj.toString();
		return ElementUIRegistry.getDefault().getLabel(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof TreeNode)
			return getTreeNodeImage((TreeNode<?>) obj);
		if (obj instanceof IRodinElement)
			return EventBImage.getRodinImage((IRodinElement) obj);
		return null;
	}

	/*
	 * Getting the image corresponding to a tree node <p>
	 * 
	 * @param element a tree node @return the image for displaying corresponding
	 * to the tree node
	 */
	private Image getTreeNodeImage(TreeNode<?> node) {

		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();

		if (node.isType(IVariable.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_VARIABLES);
		if (node.isType(IInvariant.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_INVARIANTS);
		if (node.isType(ITheorem.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_THEOREMS);
		if (node.isType(IEvent.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_EVENTS);
		if (node.isType(ICarrierSet.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_CARRIER_SETS);
		if (node.isType(IConstant.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_CONSTANTS);
		if (node.isType(IAxiom.ELEMENT_TYPE))
			return registry.get(IEventBSharedImages.IMG_AXIOMS);

		return null;
	}

	public Font getFont(Object element) {
		return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
				viewer.refresh();
			}
		}
	}

	@Override
	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IMarkerDelta[] rodinProblemMakerDeltas = event.findMarkerDeltas(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		
		final Set<IResource> resources = new HashSet<IResource>();
		for (IMarkerDelta delta : rodinProblemMakerDeltas) {
			IResource resource = delta.getResource();
			resources.add(resource);
			if (resource instanceof IFile) {
				resources.add(resource.getParent());
			}
		}
		if (resources.size() != 0) {
			if (viewer instanceof StructuredViewer) {
				Display display = viewer.getControl().getDisplay();
				display.syncExec(new Runnable() {

					public void run() {
						for (IResource resource : resources) {							
							if (resource instanceof IProject) {
								((StructuredViewer) viewer)
								.update(
										RodinCore
												.valueOf((IProject) resource),
										new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER });
							}
							else {
								((StructuredViewer) viewer)
										.update(
												RodinCore
														.valueOf((IFile) resource),
												new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER });
							}
						}
					}

				});
			}
			else {
				Display display = viewer.getControl().getDisplay();
				display.syncExec(new Runnable() {

					public void run() {
						viewer.refresh();
					}
					
				});
			}
		}
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (property.equals(RodinMarkerUtil.RODIN_PROBLEM_MARKER))
			return true;
		return super.isLabelProperty(element, property);
	}

	
}
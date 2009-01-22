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
 *     Systerel - used ElementDescRegistry
 ******************************************************************************/

package org.eventb.internal.ui;

import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.LABEL;

import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.markers.MarkerUIRegistry;
import org.eventb.ui.projectexplorer.TreeNode;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.LabelProvider</code> and provides
 *         labels for different elements appeared in the UI
 */
public abstract class RodinElementStructuredLabelProvider extends LabelProvider implements
		IFontProvider, IPropertyChangeListener, IResourceChangeListener {

	StructuredViewer viewer;

	public RodinElementStructuredLabelProvider(StructuredViewer viewer) {
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
		// for project in "project explorer" and "obligation explorer"
		if (!(obj instanceof IInternalElement))
			return obj.toString();
		final IInternalElement element = (IInternalElement) obj;
		// for machine and context in "project explorer" and
		// "obligation explorer"
		if (!(element.getParent() instanceof IInternalElement))
			return element.getRodinFile().getBareName();
		final ElementDescRegistry reg = ElementDescRegistry.getInstance();
		return reg.getValueAtColumn(element, LABEL);
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
		int F_ERROR = 0x00002;
		int F_WARNING = 0x00004;
		int F_INFO = 0x00008;
		int overlay = 0;
		
		ImageDescriptor descriptor = EventBImage.getImageDescriptor(node
				.getType());
		
		try {
			int severity = MarkerUIRegistry.getDefault().getMaxMarkerSeverity(node);
			if (severity == IMarker.SEVERITY_ERROR) {
				overlay = overlay | F_ERROR;
			}
			else if (severity == IMarker.SEVERITY_WARNING) {
				overlay = overlay | F_WARNING;
			}
			if (severity == IMarker.SEVERITY_INFO) {
				overlay = overlay | F_INFO;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return EventBImage.getImage(descriptor, overlay);
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
		final Set<Object> elements = getRefreshElements(event);

		if (elements.size() != 0) {
			final String[] properties = new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER };
			Display display = viewer.getControl().getDisplay();
			display.syncExec(new Runnable() {

				public void run() {
					for (Object element : elements) {
						viewer.update(element, properties);
					}
				}

			});
		}
	}

	protected abstract Set<Object> getRefreshElements(IResourceChangeEvent event);

	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (property.equals(RodinMarkerUtil.RODIN_PROBLEM_MARKER))
			return true;
		return super.isLabelProperty(element, property);
	}
	
}
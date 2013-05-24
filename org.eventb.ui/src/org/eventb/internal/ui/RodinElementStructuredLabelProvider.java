/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used ElementDescRegistry
 *******************************************************************************/
package org.eventb.internal.ui;

import static org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry.Column.LABEL;

import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.keyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.LabelProvider</code> and provides
 *         labels for different elements appeared in the UI
 */
public abstract class RodinElementStructuredLabelProvider extends LabelProvider implements
		IFontProvider, IPropertyChangeListener, IResourceChangeListener {

	final StructuredViewer viewer;

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
		if (!(obj instanceof IInternalElement))
			return obj.toString();
		final ElementDescRegistry reg = ElementDescRegistry.getInstance();
		return reg.getValueAtColumn((IInternalElement) obj, LABEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object obj) {
		if (obj instanceof IRodinElement)
			return EventBImage.getRodinImage((IRodinElement) obj);
		return null;
	}

	@Override
	public Font getFont(Object element) {
		return JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.RODIN_MATH_FONT)) {
			if (event.getProperty().equals(PreferenceConstants.RODIN_MATH_FONT)) {
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

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		final Set<Object> elements = getRefreshElements(event);

		if (elements.size() != 0) {
			final String[] properties = new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER };
			Display display = viewer.getControl().getDisplay();
			display.syncExec(new Runnable() {

				@Override
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
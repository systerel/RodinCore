/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used ElementDescRegistry
 *     ETH Zurich - adapted to org.rodinp.keyboard
 ******************************************************************************/
package org.eventb.ui;

import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.LABEL;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.RodinElementTableLabelProvider;
import org.eventb.internal.ui.RodinElementTreeLabelProvider;
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
 *         labels for different elements appeared in the UI.
 *         </p>
 * @deprecated use {@link RodinElementTreeLabelProvider} or
 *             {@link RodinElementTableLabelProvider} instead.
 * @since 1.0
 * 
 */
@Deprecated
public class ElementLabelProvider extends LabelProvider implements
		IFontProvider, IPropertyChangeListener, IResourceChangeListener {

	final Viewer viewer;

	public ElementLabelProvider(Viewer viewer) {
		this.viewer = viewer;
		JFaceResources.getFontRegistry().addListener(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this,
				IResourceChangeEvent.POST_BUILD
						| IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public String getText(Object obj) {
		if (!(obj instanceof IInternalElement))
			return obj.toString();
		final IInternalElement element = (IInternalElement) obj;
		final ElementDescRegistry reg = ElementDescRegistry.getInstance();
		return reg.getValueAtColumn(element, LABEL);
	}

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
		IMarkerDelta[] rodinProblemMakerDeltas = event.findMarkerDeltas(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		
		final Set<Object> elements = new HashSet<Object>();
		for (IMarkerDelta delta : rodinProblemMakerDeltas) {
			Object element = RodinMarkerUtil.getElement(delta);
			if (element != null && !elements.contains(element)) { 
				elements.add(element);
				if (viewer instanceof TreeViewer) {
					element = ((ITreeContentProvider) ((TreeViewer) viewer)
							.getContentProvider()).getParent(element);
				}
				while (element != null) {
					elements.add(element);
					if (viewer instanceof TreeViewer) {
						element = ((ITreeContentProvider) ((TreeViewer) viewer)
								.getContentProvider()).getParent(element);
					}
				}
				
			}
		}
		if (elements.size() != 0) {
			if (viewer instanceof StructuredViewer) {
				Display display = viewer.getControl().getDisplay();
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						for (Object element : elements) {
							((StructuredViewer) viewer)
									.update(
											element,
											new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER });
						}
					}

				});
			}
			else {
				Display display = viewer.getControl().getDisplay();
				display.syncExec(new Runnable() {

					@Override
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
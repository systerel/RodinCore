/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - used ElementDescRegistry
 *     ETH Zurich - adapted to org.rodinp.keyboard
 *     Systerel - fixed bug #1824569
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

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
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.keyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This class provides the labels, fonts and colors for the Editable
 *         Tree Viewer.
 */
public class EventBTreeLabelProvider implements ITableLabelProvider,
		ITableFontProvider, ITableColorProvider, IPropertyChangeListener, IResourceChangeListener {

	// The associated Event-B Editor
	private IEventBEditor<?> editor;

	// The font used in the tree viewer
	private Font font = null;

	private EventBEditableTreeViewer viewer;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            An Event-B Editor
	 */
	public EventBTreeLabelProvider(IEventBEditor<?> editor, EventBEditableTreeViewer viewer) {
		this.editor = editor;
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
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex != 0)
			return null;
		if (element instanceof IRodinElement)
			return EventBImage.getRodinImage((IRodinElement) element);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof IInternalElement))
			return element.toString();
		final IInternalElement aElement = (IInternalElement) element;
		final ElementDescRegistry reg = ElementDescRegistry.getInstance();
		return reg.getValueAtColumn(aElement, Column.valueOf(columnIndex));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		if (property.equals(RodinMarkerUtil.RODIN_PROBLEM_MARKER))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object,
	 *      int)
	 */
	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof IRodinElement) {
			if (editor.isNewElement((IRodinElement) element))
				return EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW);
		}
		return EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object,
	 *      int)
	 */
	public Color getForeground(Object element, int columnIndex) {
		if (element instanceof IRodinElement) {
			if (editor.isNewElement((IRodinElement) element))
				return EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA);
		}
		return EventBSharedColor.getSystemColor(SWT.COLOR_BLACK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object,
	 *      int)
	 */
	public Font getFont(Object element, int columnIndex) {
		if (font == null) {
			font = JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
		}
		return font;
	}

	public void propertyChange(PropertyChangeEvent event) {
		font = JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
		viewer.refresh();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		final Set<Object> elements = getRefreshElements(event);
		if (elements.size() != 0) {
			final String[] properties = new String[] { RodinMarkerUtil.RODIN_PROBLEM_MARKER };
			Display display = viewer.getControl().getDisplay();
			final EventBEditableTreeViewer v = viewer;
			display.syncExec(new Runnable() {
				public void run() {
					for (Object element : elements) {
						v.update(element, properties);
					}
				}

			});
		}
	}

	protected Set<Object> getRefreshElements(IResourceChangeEvent event) {
		IMarkerDelta[] rodinProblemMakerDeltas = event.findMarkerDeltas(
				RodinMarkerUtil.RODIN_PROBLEM_MARKER, true);
		final Set<Object> elements = new HashSet<Object>();
		for (IMarkerDelta delta : rodinProblemMakerDeltas) {
			Object element = RodinMarkerUtil.getElement(delta);
			if (element != null && !elements.contains(element)) { 
				elements.add(element);
				element = ((ITreeContentProvider) ((TreeViewer) viewer)
						.getContentProvider()).getParent(element);
				while (element != null) {
					elements.add(element);
					element = ((ITreeContentProvider) ((TreeViewer) viewer)
							.getContentProvider()).getParent(element);
				}
			}
		}
		return elements;
	}

}

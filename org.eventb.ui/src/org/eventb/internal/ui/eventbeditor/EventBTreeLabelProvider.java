/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - used ElementDescRegistry
 *     Systerel - fixed bug #1824569
 *     Systerel - refactored and fixed update for Rodin problem markers
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.rodinp.core.RodinMarkerUtil.RODIN_PROBLEM_MARKER;
import static org.rodinp.core.RodinMarkerUtil.getElement;
import static org.rodinp.keyboard.preferences.PreferenceConstants.RODIN_MATH_FONT;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

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

	private final EventBEditableTreeViewer viewer;

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
	@Override
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
	@Override
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
	@Override
	public void addListener(ILabelProviderListener listener) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
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
	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (property.equals(RODIN_PROBLEM_MARKER))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object,
	 *      int)
	 */
	@Override
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
	@Override
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
	@Override
	public Font getFont(Object element, int columnIndex) {
		if (font == null) {
			font = JFaceResources.getFont(RODIN_MATH_FONT);
		}
		return font;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		font = JFaceResources.getFont(RODIN_MATH_FONT);
		viewer.refresh();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		final TreeLabelUpdater tlu = new TreeLabelUpdater(event, viewer);
		tlu.performUpdate();
	}

	/**
	 * Processes resource change events for <code>RODIN_PROBLEM_MARKER</code>
	 * and updates the corresponding elements in the tree viewer.
	 */
	private static class TreeLabelUpdater implements Runnable {

		private static final String[] PROPERTIES = new String[] { RODIN_PROBLEM_MARKER };

		private final EventBEditableTreeViewer viewer;
		private final ITreeContentProvider contentProvider;
		private final IResourceChangeEvent event;

		private final Set<Object> elementsToUpdate;

		public TreeLabelUpdater(IResourceChangeEvent event,
				EventBEditableTreeViewer viewer) {
			this.viewer = viewer;
			this.contentProvider = (ITreeContentProvider) viewer
					.getContentProvider();
			this.event = event;
			this.elementsToUpdate = new HashSet<Object>();
		}

		public void performUpdate() {
			computeElementsToUpdate();
			if (!elementsToUpdate.isEmpty()) {
				updateTreeLabels();
			}
		}

		private void computeElementsToUpdate() {
			final IMarkerDelta[] deltas = event.findMarkerDeltas(
					RODIN_PROBLEM_MARKER, true);
			for (final IMarkerDelta delta : deltas) {
				addElementAndAncestors(getElement(delta));
			}
		}

		private void addElementAndAncestors(Object element) {
			while (element != null) {
				final boolean wasNew = elementsToUpdate.add(element);
				if (!wasNew)
					break;
				element = contentProvider.getParent(element);
			}
		}

		private void updateTreeLabels() {
			viewer.getControl().getDisplay().syncExec(this);
		}

		@Override
		public void run() {
			for (final Object element : elementsToUpdate) {
				viewer.update(element, PROPERTIES);
			}
		}
	}
}

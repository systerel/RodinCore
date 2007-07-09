/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.ElementUIRegistry;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This class provides the labels, fonts and colors for the Editable
 *         Tree Viewer.
 */
public class EventBTreeLabelProvider implements ITableLabelProvider,
		ITableFontProvider, ITableColorProvider, IPropertyChangeListener {

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
		// try {

		if (columnIndex == 0) {
			return ElementUIRegistry.getDefault().getLabel(element);
			/*
			 * if (element instanceof ISeesContext) return ((ISeesContext)
			 * element).getSeenContextName(); if (element instanceof
			 * IRefinesMachine) return ((IRefinesMachine)
			 * element).getAbstractMachineName(); if (element instanceof
			 * IExtendsContext) return ((IExtendsContext)
			 * element).getAbstractContextName(); if (element instanceof
			 * IRefinesEvent) return ((IRefinesEvent)
			 * element).getAbstractEventLabel();
			 * 
			 * if (element instanceof ILabeledElement) return ((ILabeledElement)
			 * element).getLabel(null);
			 * 
			 * if (element instanceof IIdentifierElement) return
			 * ((IIdentifierElement) element).getIdentifierString();
			 * 
			 * if (element instanceof IVariant) return "Variant";
			 */

			// if (rodinElement instanceof IInternalElement)
			// return ((IInternalElement)
			// rodinElement).getElementName();
			// return "";
		}

		if (columnIndex == 1) {
			return ElementUIRegistry.getDefault().getLabelAtColumn(viewer.getColumnID(columnIndex), element);
		}

		return element.toString();

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
		// Do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
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
		Display display = Display.getCurrent();
		if (element instanceof IRodinElement) {
			if (editor.isNewElement((IRodinElement) element))
				return display.getSystemColor(SWT.COLOR_YELLOW);
		}
		return display.getSystemColor(SWT.COLOR_WHITE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object,
	 *      int)
	 */
	public Color getForeground(Object element, int columnIndex) {
		Display display = Display.getCurrent();
		if (element instanceof IRodinElement) {
			if (editor.isNewElement((IRodinElement) element))
				return display.getSystemColor(SWT.COLOR_DARK_MAGENTA);
		}
		return display.getSystemColor(SWT.COLOR_BLACK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object,
	 *      int)
	 */
	public Font getFont(Object element, int columnIndex) {
		if (font == null) {
			font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}
		return font;
	}

	public void propertyChange(PropertyChangeEvent event) {
		font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		viewer.refresh();
	}

}

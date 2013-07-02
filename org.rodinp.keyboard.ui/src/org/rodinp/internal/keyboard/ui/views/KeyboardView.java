/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - retrieves the modify listener for the plugin
 *******************************************************************************/
package org.rodinp.internal.keyboard.ui.views;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.rodinp.internal.keyboard.ui.RodinModifyListener;
import org.rodinp.keyboard.ui.RodinKeyboardUIPlugin;
import org.rodinp.keyboard.ui.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This simple view allow you to enter a mathematical expression of
 *         Event-B language and translate that into Unicode.
 *         </p>
 */
public class KeyboardView extends ViewPart implements IPropertyChangeListener {

	private static final String CONTEXT_HELP_ID = RodinKeyboardUIPlugin.PLUGIN_ID
			+ ".keyboardView";

	private Text formula;

	private RodinModifyListener listener;

	/**
	 * The constructor.
	 */
	public KeyboardView() {
		JFaceResources.getFontRegistry().addListener(this);
	}

	/**
	 * This is a callback that will allow us to create the text area and the
	 * check button and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		parent.setLayout(layout);

		Label label = new Label(parent, SWT.NULL);
		label.setText("Formula:");
		label.setToolTipText("Enter an Event-B formula");
		formula = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER
				| SWT.H_SCROLL);
		GridData gD = new GridData(GridData.FILL_BOTH);
		formula.setLayoutData(gD);
		final RodinKeyboardUIPlugin kPlugin = RodinKeyboardUIPlugin.getDefault();
		listener = (RodinModifyListener) kPlugin.getRodinModifyListener();
		formula.addModifyListener(listener);

		// Using a special fonts for showing Event-B symbols.
		Font font = JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
		formula.setFont(font);

		// set context help id
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				CONTEXT_HELP_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.RODIN_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.RODIN_MATH_FONT);
			formula.setFont(font);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		formula.setFocus();
	}

	/**
	 * @return The Text inside the View
	 */
	public Text getWidget() {
		return formula;
	}

	/**
	 * @return The ModifyListener associate with the Text
	 */
	public ModifyListener getListener() {
		return listener;
	}

}
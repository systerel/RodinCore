/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.part.ViewPart;

/**
 * ViewPart for displaying proof skeletons.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSkeletonView extends ViewPart implements IPropertyChangeListener {

	public static boolean DEBUG;
	
	protected PrfSklMasterDetailsBlock masterDetailsBlock;
	private InputManager selManager;
	private ManagedForm managedForm;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		managedForm = new ManagedForm(parent);
		masterDetailsBlock = new PrfSklMasterDetailsBlock(getSite());
		masterDetailsBlock.createContent(managedForm);

		selManager = new InputManager(this);
		selManager.register();
		
		JFaceResources.getFontRegistry().addListener(this);
	}

	@Override
	public void setFocus() {
		managedForm.getForm().setFocus();
	}

	@Override
	public void dispose() {
		getSite().setSelectionProvider(null);
		selManager.unregister();
		JFaceResources.getFontRegistry().removeListener(this);
		managedForm.dispose();
		super.dispose();
	}

	public void switchOrientation() {
		masterDetailsBlock.switchOrientation();
	}

	public void setInput(IViewerInput input) {
		if (managedForm != null) {
			if(!managedForm.getForm().isDisposed()) {
				managedForm.setInput(input);
			}
		}
		setTitleToolTip(input.getTitleTooltip());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			final Font font = JFaceResources.getFont(RODIN_MATH_FONT);
			masterDetailsBlock.setFont(font);
		}
		
	}

}

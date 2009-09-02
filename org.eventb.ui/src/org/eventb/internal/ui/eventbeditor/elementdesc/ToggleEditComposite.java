/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
 package org.eventb.internal.ui.eventbeditor.elementdesc;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Button;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.eventb.internal.ui.markers.MarkerUIRegistry;

public class ToggleEditComposite extends AbstractEditComposite {

	private TwoStateControl control;

	public ToggleEditComposite(ToggleDesc attrDesc) {
		super(attrDesc);
	}

	@Override
	public void initialise(boolean refreshMarker) {
		createToggle();
		control.refresh();
		if (refreshMarker)
			displayMarkers();
	}

	public void edit(int charStart, int charEnd) {
		// do nothing
	}

	private void createToggle() {
		if (control != null)
			return;

		assert manipulation instanceof AbstractBooleanManipulation;
		
		control = new TwoStateControl(
				(AbstractBooleanManipulation) manipulation);
		control.setElement(element);
		control.createToggle(composite);
	}

	private void displayMarkers() {
		final Button checkBox = control.getButton();
		try {
			final int maxSeverity = MarkerUIRegistry.getDefault()
					.getMaxMarkerSeverity(element, attrDesc.getAttributeType());
			if (maxSeverity == IMarker.SEVERITY_ERROR) {
				checkBox.setBackground(RED);
				checkBox.setForeground(YELLOW);
				return;
			} else if (maxSeverity == IMarker.SEVERITY_WARNING) {
				checkBox.setBackground(YELLOW);
				checkBox.setForeground(RED);
				return;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		checkBox.setBackground(WHITE);
		checkBox.setForeground(BLACK);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		if (control != null) {
			control.getButton().setEnabled(!readOnly);
		}
	}

}
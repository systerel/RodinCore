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

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Carrier Set Mirror 'page'.
 */
public class ConstantMirrorPage extends EventBMirrorPage implements
		IConstantMirrorPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The Event-B Editor
	 */
	public ConstantMirrorPage(EventBEditor editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBMirrorPage#getFormString()
	 */
	protected String getFormString() {
		String formString = "<form>";
		try {
			IRodinElement[] constants = editor.getRodinInput()
					.getChildrenOfType(IConstant.ELEMENT_TYPE);
			formString = formString + "<li style=\"text\" value=\"\">";
			for (int i = 0; i < constants.length; i++) {
				if (i != 0)
					formString = formString + ", ";
				formString = formString
						+ UIUtils.makeHyperlink(((IConstant) constants[i])
								.getIdentifierString());
			}
			formString = formString + "</li>";
		} catch (RodinDBException e) {
			// TODO Exception handle
			e.printStackTrace();
		}
		formString = formString + "</form>";

		return formString;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBMirrorPage#createHyperlinkListener()
	 */
	protected HyperlinkAdapter createHyperlinkListener() {
		return (new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				IRodinFile rodinFile = editor.getRodinInput();
				try {
					IConstant[] constants = ((IContextFile) rodinFile)
							.getConstants();
					for (int i = 0; i < constants.length; i++) {
						if (e.getHref().equals(
								constants[i].getIdentifierString())) {
							editor.edit(constants[i]);
						}
					}
				} catch (RodinDBException exception) {
					// TODO Exception handle
					exception.printStackTrace();
				}
			}
		});
	}

}
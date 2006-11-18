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
import org.eventb.core.ITheorem;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Invariant Mirror 'page'.
 */
public class TheoremMirrorPage extends EventBMirrorPage implements
		ITheoremMirrorPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            an Event-B Editor
	 */
	public TheoremMirrorPage(IEventBEditor editor) {
		super(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBMirrorPage#getFormString()
	 */
	@Override
	protected String getFormString() {
		String formString = "<form>";
		try {
			IRodinElement[] theorems = editor.getRodinInput()
					.getChildrenOfType(ITheorem.ELEMENT_TYPE);
			for (int i = 0; i < theorems.length; i++) {
				formString = formString
						+ "<li style=\"bullet\">"
						+ UIUtils.makeHyperlink(((ITheorem) theorems[i])
								.getLabel()) + ": ";
				formString = formString
						+ UIUtils.XMLWrapUp(((ITheorem) theorems[i])
								.getPredicateString());
				formString = formString + "</li>";
			}
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
	@Override
	protected HyperlinkAdapter createHyperlinkListener() {
		return (new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					// UIUtils.debug("Event: " + e.toString());
					// UIUtils.debug("Data: " + e.data);
					// UIUtils.debug("Here: " + e.getSource());
					IRodinElement[] theorems = editor.getRodinInput()
							.getChildrenOfType(ITheorem.ELEMENT_TYPE);
					for (int i = 0; i < theorems.length; i++) {
						if (e.getHref().equals(
								((ITheorem) theorems[i]).getLabel())) {
							editor.edit(theorems[i]);
							return;
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
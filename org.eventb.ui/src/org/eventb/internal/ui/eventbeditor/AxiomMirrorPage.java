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
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Axiom Mirror 'page'.
 */
public class AxiomMirrorPage extends EventBMirrorPage implements
		IAxiomMirrorPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            An Event-B Editor.
	 */
	public AxiomMirrorPage(EventBEditor editor) {
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
			IRodinElement[] axioms = editor.getRodinInput().getChildrenOfType(
					IAxiom.ELEMENT_TYPE);
			for (IRodinElement axiom : axioms) {
				formString = formString + "<li style=\"bullet\">"
						+ UIUtils.makeHyperlink(((IAxiom) axiom).getLabel(null))
						+ ": ";
				formString = formString
						+ UIUtils.XMLWrapUp(((IAxiom) axiom)
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
	protected HyperlinkAdapter createHyperlinkListener() {
		return (new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				try {
					IRodinElement[] axioms = editor.getRodinInput()
							.getChildrenOfType(IAxiom.ELEMENT_TYPE);
					for (int i = 0; i < axioms.length; i++) {
						if (e.getHref().equals(((IAxiom) axioms[i]).getLabel(null))) {
							editor.edit(axioms[i]);
							break;
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
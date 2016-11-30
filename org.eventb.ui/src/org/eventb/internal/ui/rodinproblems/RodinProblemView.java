/*******************************************************************************
 * Copyright (c) 2007, 2016 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.rodinproblems;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.internal.views.markers.ProblemsView;
import org.rodinp.keyboard.ui.preferences.PreferenceConstants;

/**
 * Overriding of the regular Problems view for Rodin.
 * <p>
 * The problem with the Problems view of <code>org.eclipse.ide</code> is that
 * it doesn't allow clients to set the font used to display problem description.
 * However, the Rodin tools sometimes create messages that contain mathematical
 * symbols, which are not displayed properly with the default font, most notably
 * on Windows computers.
 * </p>
 * <p>
 * That's why this view has been created, although it violates Eclipse rules, by
 * extending an internal class.
 * </p>
 * 
 * @author Laurent Voisin
 */
@SuppressWarnings("restriction")
public class RodinProblemView extends ProblemsView {
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		final Control[] children = parent.getChildren();
		for (Control control : children) {
			if (control instanceof Tree) {
				final Tree tree = (Tree) control;
				final Font font = JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
				tree.setFont(font);
			}
		}
	}

}

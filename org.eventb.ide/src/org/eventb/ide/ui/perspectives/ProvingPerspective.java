/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.ide.ui.perspectives;

import static fr.systerel.explorer.ExplorerPlugin.NAVIGATOR_ID;
import static org.eventb.ui.EventBUIPlugin.PROOF_CONTROL_VIEW_ID;
import static org.eventb.ui.EventBUIPlugin.PROOF_TREE_VIEW_ID;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author htson
 *         <p>
 *         This is the proving perspective for Event-B UI.
 */
public class ProvingPerspective implements IPerspectiveFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		defineLayout(layout);
	}

	/**
	 * Define the initial (default) layout for this perspective.
	 * <p>
	 * 
	 * @param layout
	 *            the page layout
	 */
	public void defineLayout(IPageLayout layout) {
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		// Place the Obligation/Project explorer to left of editor area.
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.25f, editorArea);
		left.addView(PROOF_TREE_VIEW_ID);

		// Place the ProofTree / Outline to right of editor area.
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.75f, editorArea);
		right.addView(NAVIGATOR_ID);

		// Place the Proof Control / Problems to the bottom of the editor area.
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.60f, editorArea);
		bottom.addView(PROOF_CONTROL_VIEW_ID);
	}

}

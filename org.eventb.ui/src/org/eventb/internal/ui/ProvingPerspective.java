/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.prover.ProofInformation;

/**
 * @author htson
 *         <p>
 *         This is the proving perspective for Event-B UI.
 */
public class ProvingPerspective implements IPerspectiveFactory {

	/**
	 * The identifier of the proving perspective (value
	 * <code>"org.eventb.ui.perspective.Proving"</code>).
	 */
	public static final String PERSPECTIVE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".perspective.Proving";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
		defineLayout(layout);
		layout.addPerspectiveShortcut(ModellingPerspective.PERSPECTIVE_ID);
	}

	/**
	 * Add the shortcuts to different views.
	 * <p>
	 * 
	 * @param layout
	 *            the page layout
	 */
	public void defineActions(IPageLayout layout) {
		// Add "show views".
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(ObligationExplorer.VIEW_ID);
		layout.addShowViewShortcut(ProofTreeUI.VIEW_ID);
		layout.addShowViewShortcut(ProofControl.VIEW_ID);
		layout.addShowViewShortcut(ProofInformation.VIEW_ID);
		layout.addShowViewShortcut(ProjectExplorer.VIEW_ID);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
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
		left.addView(ProofTreeUI.VIEW_ID);
		left.addView(ProjectExplorer.VIEW_ID);

		// Place the ProofTree / Outline to right of editor area.
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.75f, editorArea);
		right.addView(ObligationExplorer.VIEW_ID);
		right.addView(IPageLayout.ID_OUTLINE);

		// Place the Proof Control / Problems to the bottom of the editor area.
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.75f, editorArea);
		bottom.addView(ProofControl.VIEW_ID);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);

		// Place the Proof Information to the bottom left of the editor area.
		IFolderLayout bottomRight = layout.createFolder("bottomRight",
				IPageLayout.BOTTOM, 0.60f, "right");
		bottomRight.addView(ProofInformation.VIEW_ID);
	}

}

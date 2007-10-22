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

package org.eventb.internal.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.rodinproblems.RodinProblemView;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         This is the modelling perspective for Event-B UI.
 */

public class EventBPerspective implements IPerspectiveFactory {

	/**
	 * The identifier of the modelling perspective (value
	 * <code>"org.eventb.ui.perspective.eventb"</code>).
	 */
	public static final String PERSPECTIVE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".perspective.eventb";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
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

		// Place the project explorer to left of editor area.
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.20f, editorArea);
		left.addView(ProjectExplorer.VIEW_ID);

		// Place the Problems / Task to the bottom of the editor area.
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.75f, editorArea);
		bottom.addView(RodinProblemView.VIEW_ID);

		// Place the outline to right of editor area.
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
				0.8f, editorArea);
		right.addView(IPageLayout.ID_OUTLINE);
	}
}

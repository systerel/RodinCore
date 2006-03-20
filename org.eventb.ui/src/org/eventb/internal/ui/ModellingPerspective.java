/**
 * 
 */
package org.eventb.internal.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;

/**
 * @author htson
 *
 */
public class ModellingPerspective implements IPerspectiveFactory {

	public static final String PERSPECTIVE_ID = EventBUIPlugin.PLUGIN_ID + ".perspective.Modelling";

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
	    defineActions(layout);
	    defineLayout(layout);
	    layout.addPerspectiveShortcut(ProvingPerspective.PERSPECTIVE_ID);
	}
	
	public void defineActions(IPageLayout layout) {
        // Add "new wizards".
        layout.addNewWizardShortcut(NewProjectWizard.WIZARD_ID);
        layout.addNewWizardShortcut(NewComponentWizard.WIZARD_ID);

        // Add "show views".
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(ProjectExplorer.VIEW_ID);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}
	
	public void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Place the project explorer to left of editor area.
        IFolderLayout left =
                layout.createFolder("left", IPageLayout.LEFT, 0.20f, editorArea);
        left.addView(ProjectExplorer.VIEW_ID);
        
        // Place the Problems / Task to the bottom of the editor area.
        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
        bottom.addView(IPageLayout.ID_TASK_LIST);

        // Place the outline to right of editor area.
        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.8f, editorArea);
        right.addView(IPageLayout.ID_OUTLINE);
	}
}

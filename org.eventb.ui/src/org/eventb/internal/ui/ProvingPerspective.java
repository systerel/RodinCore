/**
 * 
 */
package org.eventb.internal.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eventb.internal.ui.obligationexplorer.ObligationExplorer;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.prover.ProofControl;
import org.eventb.internal.ui.prover.ProofInformation;
import org.eventb.internal.ui.prover.ProofTreeUI;

/**
 * @author htson
 *
 */
public class ProvingPerspective implements IPerspectiveFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
	    defineActions(layout);
	    defineLayout(layout);
	}
	
	public void defineActions(IPageLayout layout) {
        // Add "new wizards".
//        layout.addNewWizardShortcut(NewProjectWizard.WIZARD_ID);
//        layout.addNewWizardShortcut(NewConstructWizard.WIZARD_ID);

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
	
	public void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Place the Obligation/Project explorer to left of editor area.
        IFolderLayout left =
                layout.createFolder("left", IPageLayout.LEFT, 0.20f, editorArea);
        left.addView(ObligationExplorer.VIEW_ID);
        left.addView(ProjectExplorer.VIEW_ID);
        
        // Place the ProofTree / Outline to right of editor area.
        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.7f, editorArea);
        right.addView(ProofTreeUI.VIEW_ID);
        right.addView(IPageLayout.ID_OUTLINE);

        // Place the Proof Control / Problems to the bottom of the editor area.
        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
        bottom.addView(ProofControl.VIEW_ID);
        bottom.addView(IPageLayout.ID_PROBLEM_VIEW);

        // Place the Proof Information to the bottom left of the editor area.
        IFolderLayout bottomLeft =
            layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.60f, "left");
        bottomLeft.addView(ProofInformation.VIEW_ID);
	}
}

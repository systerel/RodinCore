package org.eventb.internal.ui.prover;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;

public class PenguinDanceDialog  extends Dialog {
    
	public PenguinDanceDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        Button ok = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        ok.setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
    }
    
    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
//        composite.setLayoutData(gd);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		Image image = registry.get(EventBImage.IMG_PENGUIN);
        
        Browser browser = new Browser(composite, Window.getDefaultOrientation());
        
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = image.getBounds().width + 20;
        gd.heightHint = image.getBounds().height + 20;

        browser.setText("<html><body><img align=\"center\" src=\"/home/htson/work/workspace/org.eventb.ui/icons/penguins-dancing.gif\" alt=\"Penguin tumbler\"></body></html>");
        browser.setLayoutData(gd);	        
        
        applyDialogFont(composite);
        return composite;
    }
    
}
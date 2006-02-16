/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.prover;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactic;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBUIPlugin;

/**
 * @author htson
 * <p>
 * An abstract class to create an input row (a label and a text field) 
 * for editing Rodin elements (e.g. name, content, attribute, etc.).
 */
public class HypothesisRow
{
	
	// The text input area.
	private Button checkBox;
	private Composite buttonComposite;
	private Text hypothesisText;
	
	private Hypothesis hyp;
	
	private class HypothesisTacticHyperlinkAdapter extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent e) {

		}
    	
    }

	/**
	 * Contructor.
	 * @param page The detail page
	 * @param toolkit The Form Toolkit to create this row
	 * @param parent The composite parent
	 * @param label The label of the input row
	 * @param tip The tip for the input row
	 * @param style The style
	 */
	public HypothesisRow(FormToolkit toolkit, Composite parent, Hypothesis hyp) {
		GridData gd;
		this.hyp = hyp;
		
		checkBox = toolkit.createButton(parent, "", SWT.CHECK);
//		gd = new GridData();
//		gd.horizontalAlignment = SWT.CENTER;
//		checkBox.setLayoutData(gd);
		
		buttonComposite = toolkit.createComposite(parent);
		buttonComposite.setLayout(new GridLayout());
		gd = new GridData();

		// TODO Extra buttons will be added here to buttonComposite depends on 
		// the type of hypothesis
		FormText formText = toolkit.createFormText(buttonComposite, true);
		gd = new GridData();
		gd.widthHint = 25;
		gd.horizontalAlignment = SWT.CENTER;
		formText.setLayoutData(gd);
		HyperlinkSettings hyperlinkSettings = new HyperlinkSettings(EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench().getDisplay());
		hyperlinkSettings.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
		formText.setHyperlinkSettings(hyperlinkSettings);
		formText.addHyperlinkListener(new HypothesisTacticHyperlinkAdapter());
        gd = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd.widthHint = 100;
        formText.setLayoutData(gd);
        toolkit.paintBordersFor(formText);
        createHyperlinks(formText);
        
        
		hypothesisText = toolkit.createText(parent, hyp.toString(), SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		hypothesisText.setLayoutData(gd);
	}

	private void createHyperlinks(FormText formText) {
		String formString = "<form><li style=\"text\" value=\"\">";

		List<Tactic> tactics = UserSupport.getApplicableToHypothesis(hyp);
		for (Iterator<Tactic> it = tactics.iterator(); it.hasNext();) {
			Tactic t = it.next();
			formString = formString + "<a href=\"" + markedUpTactic(t) + "\">" + markedUpTactic(t) +"</a> ";
		}
		
		formString = formString + "</li></form>";
		formText.setText(formString, true, false);
		formText.redraw();
		buttonComposite.pack(true);

		return;
	}
	
	private String markedUpTactic(Tactic t) {
		if (t.equals(Tactics.conjI)) return "∧";
		if (t.equals(Tactics.impI)) return "⇒";
		if (t.equals(Tactics.hyp)) return "hp";
		if (t.equals(Tactics.allI)) return "∀";
		if (t.equals(Tactics.trivial)) return "⊤";
		return "notac";
	}
	
	
	public void dispose() {
		checkBox.dispose();
		buttonComposite.dispose();
		hypothesisText.dispose();
	}
	
	protected boolean isSelected() {return checkBox.getSelection();}
	
	protected Hypothesis getHypothesis() {return hyp;}

}

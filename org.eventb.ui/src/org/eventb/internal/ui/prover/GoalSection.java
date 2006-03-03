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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IGoalChangeEvent;
import org.eventb.core.pm.IGoalChangedListener;
import org.eventb.core.pm.IGoalDelta;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

public class GoalSection
	extends SectionPart
	implements IGoalChangedListener
{
	private static final String SECTION_TITLE = "Goal";
	private static final String SECTION_DESCRIPTION = "The current goal";	
	
    private FormPage page;
    private IEventBFormText formText;
    private FormToolkit toolkit;
    private ScrolledForm scrolledForm;
    private Composite composite;
    private List<EventBMath> textBoxes;
    
    private class GoalITacticHyperlinkAdapter extends HyperlinkAdapter {
		@Override
		public void linkActivated(HyperlinkEvent e) {
			try {
				if (e.getHref().equals(UIUtils.CONJI_SYMBOL)) {
					((ProverUI) GoalSection.this.page.getEditor()).getUserSupport().applyTactic(Tactics.conjI());
					return;
				}
				if (e.getHref().equals(UIUtils.IMPI_SYMBOL)) {
					((ProverUI) GoalSection.this.page.getEditor()).getUserSupport().applyTactic(Tactics.impI());
					return;
				}
					
				if (e.getHref().equals(UIUtils.ALLI_SYMBOL)) {
					((ProverUI) GoalSection.this.page.getEditor()).getUserSupport().applyTactic(Tactics.allI());
					return;
				}
	
				if (e.getHref().equals(UIUtils.EXI_SYMBOL)) {
					String [] inputs = new String[textBoxes.size()];
					int i = 0;
					for (EventBMath text : textBoxes) {
						inputs[i++] = text.getTextWidget().getText();
					}
					((ProverUI) GoalSection.this.page.getEditor()).getUserSupport().applyTactic(Tactics.exI(inputs));
					return;
				}

				if (e.getHref().equals(UIUtils.NEG_SYMBOL)) {
					((ProverUI) GoalSection.this.page.getEditor()).getUserSupport().applyTactic(Tactics.removeNegGoal());
					return;
				}
			}
			catch (RodinDBException exception) {
				exception.printStackTrace();
			}
		}
    	
    }
    
    // Contructor
	public GoalSection(FormPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
		((ProverUI) page.getEditor()).getUserSupport().addGoalChangedListener(this);
	}

	public void createClient(Section section, FormToolkit toolkit) {
		this.toolkit = toolkit;
        section.setText(SECTION_TITLE);
        section.setDescription(SECTION_DESCRIPTION);
        scrolledForm = toolkit.createScrolledForm(section);
        
		Composite comp = scrolledForm.getBody();
        GridLayout layout = new GridLayout();
        layout.numColumns  = 2;
        layout.verticalSpacing = 5;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
        toolkit.paintBordersFor(scrolledForm);
		
        formText = new EventBFormText(toolkit.createFormText(comp, true));
		HyperlinkSettings hyperlinkSettings = new HyperlinkSettings(EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench().getDisplay());
		hyperlinkSettings.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
		FormText ft = formText.getFormText();
		ft.setHyperlinkSettings(hyperlinkSettings);
		ft.addHyperlinkListener(new GoalITacticHyperlinkAdapter());
        GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
        gd.widthHint = 100;
        ft.setLayoutData(gd);
        toolkit.paintBordersFor(comp);
	}

	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 75;
			gd.minimumHeight = 50;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		else {
			GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}

	private void createSimpleText(String text) {
		composite.setLayout(new GridLayout());
	    IEventBFormText textWidget = new EventBFormText(toolkit.createFormText(composite, true));
	    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
	    textWidget.getFormText().setText(text, true, false);
	    textWidget.getFormText().setLayoutData(gd);
	}
	
	public void setGoal(IProofTreeNode pt) {
		if (composite != null) composite.dispose();
        composite = toolkit.createComposite(scrolledForm.getBody());
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        composite.setLayoutData(gd);

        if (pt == null) {
			clearFormText();
			createSimpleText("<form><p>No current goal</p></form>");
			scrolledForm.reflow(true);		
		}
        else if (!pt.isOpen()) {
			clearFormText();
			createSimpleText("<form><p>Tactic applied</p></form>");
			scrolledForm.reflow(true);
		}
		else {
			Predicate goal = pt.getSequent().goal();
			setFormText(goal);
			
			if (Lib.isExQuant(goal)) {
				String goalString = goal.toString();
				IParseResult parseResult = Lib.ff.parsePredicate(goalString);
				assert parseResult.isSuccess();
				QuantifiedPredicate qpred = 
					(QuantifiedPredicate) parseResult.getParsedPredicate();
				
				BoundIdentDecl []  idents = qpred.getBoundIdentifiers();

				GridLayout gl = new GridLayout();
				gl.numColumns = idents.length * 2 + 2;
				composite.setLayout(gl);

				toolkit.createLabel(composite, "\u2203 ");
				
				int i = 0;
		        textBoxes = new ArrayList<EventBMath>();
				for (BoundIdentDecl ident : idents) {
					SourceLocation loc = ident.getSourceLocation();
					String image = goalString.substring(loc.getStart(), loc.getEnd());
					if (i++ != 0) toolkit.createLabel(composite, ", " + image);
					else toolkit.createLabel(composite, image);
					EventBMath mathBox = new EventBMath(toolkit.createText(composite, ""));
					gd = new GridData();
					gd.widthHint = 25;
					mathBox.getTextWidget().setLayoutData(gd);
					toolkit.paintBordersFor(composite);
					textBoxes.add(mathBox);
				}
		        
				IEventBFormText formText = new EventBFormText(toolkit.createFormText(composite, false));
		        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		        formText.getFormText().setLayoutData(gd);
				SourceLocation loc = qpred.getPredicate().getSourceLocation();
				String image = goalString.substring(loc.getStart(), loc.getEnd());
				formText.getFormText().setText(" \u00b7 " + image, false, false);
				scrolledForm.reflow(true);
			}
			else {
				createSimpleText("<form><p>" + UIUtils.XMLWrapUp(goal.toString()) + "</p></form>");
			}
		}
		scrolledForm.reflow(true);
		return;
	}

	private void clearFormText() {
		formText.getFormText().setText("<form></form>", true, false);
		scrolledForm.reflow(true);
		return;
	}

	private void setFormText(Predicate goal) {
		String formString = "<form><li style=\"text\" value=\"\">";
		List<String> tactics = UIUtils.getApplicableToGoal(goal);
		
		for (Iterator<String> it = tactics.iterator(); it.hasNext();) {
			String t = it.next();
			formString = formString + "<a href=\"" + t + "\">" + t +"</a> ";
		}
		
		formString = formString + "</li></form>";
		formText.getFormText().setText(formString, true, false);
		scrolledForm.reflow(true);
		
		return;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IGoalChangedListener#goalChanged(org.eventb.core.pm.IGoalChangeEvent)
	 */
	public void goalChanged(IGoalChangeEvent e) {
		final IGoalDelta delta = e.getDelta();
		
		Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
		display.syncExec (new Runnable () {
			public void run () {
				setGoal(delta.getProofTreeNode());
			}
		});
	}

}
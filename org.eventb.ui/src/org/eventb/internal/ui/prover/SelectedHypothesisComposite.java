package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

public class SelectedHypothesisComposite extends HypothesisComposite {

	ToolItem removeItem;
	
	ToolItem inverseSelection;

	ToolItem selectAll;
	
	ToolItem selectNone;
	
	ScrolledForm parentForm;
	public SelectedHypothesisComposite(IUserSupport userSupport, ScrolledForm parentForm) {
		super(userSupport, IProofStateDelta.F_NODE
				| IProofStateDelta.F_PROOFTREE);
		this.parentForm = parentForm;
	}

	@Override
	public void createItems(ToolBar toolBar) {
		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem.setToolTipText("Remove hypotheses");
		removeItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				assert userSupport != null;
				Set<Predicate> deselected = SelectedHypothesisComposite.this.getSelectedHyps();
				ITactic t = Tactics.mngHyp(ProverFactory.makeDeselectHypAction(deselected));
				try {
					userSupport.applyTacticToHypotheses(t, deselected, true,
							new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		selectAll = new ToolItem(toolBar, SWT.PUSH);
		selectAll.setImage(EventBImage.getImage(IEventBSharedImages.IMG_SELECT_ALL));
		selectAll.setToolTipText("Select all");
		selectAll.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				SelectedHypothesisComposite.this.selectAllHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		inverseSelection = new ToolItem(toolBar, SWT.PUSH);
		inverseSelection.setImage(EventBImage.getImage(IEventBSharedImages.IMG_INVERSE));
		inverseSelection.setToolTipText("Inverse selection");
		inverseSelection.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				assert userSupport != null;
				SelectedHypothesisComposite.this.inverseSelectedHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		selectNone = new ToolItem(toolBar, SWT.PUSH);
		selectNone.setImage(EventBImage.getImage(IEventBSharedImages.IMG_SELECT_NONE));
		selectNone.setToolTipText("Deselect all");
		selectNone.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				SelectedHypothesisComposite.this.deselectAllHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

	}

	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		if (ps != null) {
			IProofTreeNode currentNode = ps.getCurrentNode();
			if (currentNode != null)
				return currentNode.getSequent().selectedHypIterable();
		}
		return new ArrayList<Predicate>();
	}

	@Override
	public void updateToolbarItems() {
		removeItem.setEnabled(!this.getSelectedHyps().isEmpty());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		updateToolbarItems();
	}

	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

	@Override
	void refresh() {
		super.refresh();
		parentForm.reflow(true);
		scrollToBottom();
	}

	
}

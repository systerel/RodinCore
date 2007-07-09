package org.eventb.internal.ui.searchhypothesis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

public class SearchHypothesisComposite extends HypothesisComposite {

	ToolItem addItem;
	
	ToolItem removeItem;

	ToolItem inverseSelection;

	ToolItem selectAll;
	
	ToolItem selectNone;
	
	public SearchHypothesisComposite(IUserSupport userSupport,
			ProverUI proverUI) {
		super(userSupport, IProofStateDelta.F_NODE
				| IProofStateDelta.F_PROOFTREE | IProofStateDelta.F_SEARCH,
				proverUI);
	}

	@Override
	public void createItems(ToolBar toolBar) {
		addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		addItem.setToolTipText("Select hypotheses");
		addItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = SearchHypothesisComposite.this.getUserSupport();
				assert userSupport != null;
				
				Set<Predicate> selected = SearchHypothesisComposite.this.getSelectedHyps();
				ITactic t = Tactics.mngHyp(ProverFactory.makeSelectHypAction(selected));
				try {
					userSupport.applyTacticToHypotheses(t, selected, true,
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

		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem.setToolTipText("Remove hypotheses");
		removeItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = SearchHypothesisComposite.this.getUserSupport();
				assert userSupport != null;
				
				Set<Predicate> deselected = SearchHypothesisComposite.this.getSelectedHyps();
				userSupport.removeSearchedHypotheses(deselected);
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
				SearchHypothesisComposite.this.selectAllHyps();
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
				SearchHypothesisComposite.this.inverseSelectedHyps();
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
				SearchHypothesisComposite.this.deselectAllHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

	}

	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		Collection<Predicate> searched = new ArrayList<Predicate>();
		if (ps != null) {
			searched = ps.getSearched();
		}
		Collection<Predicate> validSearched = new ArrayList<Predicate>();
		for (Predicate search : searched) {
			if (ps.getCurrentNode().getSequent().containsHypothesis(search))
				validSearched.add(search);
		}
		return validSearched;
	}

	@Override
	public void updateToolbarItems() {
		addItem.setEnabled(!this.getSelectedHyps().isEmpty());
		removeItem.setEnabled(!this.getSelectedHyps().isEmpty());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		updateToolbarItems();
	}

	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

}

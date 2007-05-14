package org.eventb.internal.ui.cachehypothesis;

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
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

public class CacheHypothesisComposite extends HypothesisComposite {

	ToolItem addItem;
	
	ToolItem removeItem;

	public CacheHypothesisComposite(IUserSupport userSupport) {
		super(userSupport, IProofStateDelta.F_NODE
				| IProofStateDelta.F_PROOFTREE | IProofStateDelta.F_CACHE);
	}

	@Override
	public void createItems(ToolBar toolBar) {
		addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		addItem.setToolTipText("Select hypotheses");
		addItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = CacheHypothesisComposite.this.getUserSupport();
				assert userSupport != null;
				
				Set<Predicate> selected = CacheHypothesisComposite.this.getSelectedHyps();
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
				IUserSupport userSupport = CacheHypothesisComposite.this.getUserSupport();
				assert userSupport != null;
				
				Set<Predicate> deselected = CacheHypothesisComposite.this.getSelectedHyps();
				userSupport.removeCachedHypotheses(deselected);
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
	}

	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		Collection<Predicate> cached = new ArrayList<Predicate>();
		if (ps != null) {
			cached = ps.getCached();
		}
		Collection<Predicate> validCached = new ArrayList<Predicate>();
		for (Predicate cache : cached) {
			if (ps.getCurrentNode().getSequent().containsHypothesis(cache))
				validCached.add(cache);
		}
		return validCached;
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

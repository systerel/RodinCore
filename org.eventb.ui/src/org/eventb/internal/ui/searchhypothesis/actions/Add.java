package org.eventb.internal.ui.searchhypothesis.actions;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesis;
import org.rodinp.core.RodinDBException;

public class Add implements IViewActionDelegate {

	private SearchHypothesis fView;

	public void init(IViewPart view) {
		fView = (SearchHypothesis) view;
	}

	public void run(IAction action) {
		IUserSupport userSupport = fView.getUserSupport();
		assert userSupport != null;
		
		Set<Predicate> selected = fView.getSelectedHyps();
		ITactic t = Tactics.mngHyp(ProverFactory.makeSelectHypAction(selected));
		try {
			userSupport.applyTacticToHypotheses(t, selected,
					new NullProgressMonitor());
		} catch (RodinDBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}

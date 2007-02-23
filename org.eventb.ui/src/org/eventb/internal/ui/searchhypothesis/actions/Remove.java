package org.eventb.internal.ui.searchhypothesis.actions;

import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesis;

public class Remove implements IViewActionDelegate {

	private SearchHypothesis fView;

	public void init(IViewPart view) {
		fView = (SearchHypothesis) view;
	}

	public void run(IAction action) {
		IUserSupport userSupport = fView.getUserSupport();
		assert userSupport != null;
		
		Set<Predicate> deselected = fView.getSelectedHyps();
		userSupport.removeSearchedHypotheses(deselected);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}

package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.IAxiom;

public class AutoAxmNaming extends AutoElementNaming {

	public void run(IAction action) {
		rename(IAxiom.ELEMENT_TYPE, "axm");
	}

}
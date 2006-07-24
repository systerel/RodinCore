package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eventb.core.ITheorem;

public class AutoThmNaming extends AutoElementNaming {

	public void run(IAction action) {
		rename(ITheorem.ELEMENT_TYPE, "thm");
	}

}
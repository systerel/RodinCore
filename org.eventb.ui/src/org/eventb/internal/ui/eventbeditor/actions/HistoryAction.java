package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.Action;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinFile;

public abstract class HistoryAction extends Action {

	protected final IRodinFile file;
	protected final History history;

	public HistoryAction(IEventBEditor<?> editor) {
		super();
		this.file = editor.getRodinInput();
		history = History.getInstance();
	}
	
	@Override
	public abstract void run();

}

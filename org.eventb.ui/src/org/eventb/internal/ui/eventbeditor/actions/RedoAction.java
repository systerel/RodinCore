package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;

public class RedoAction extends HistoryAction {

	public RedoAction(IEventBEditor<?> editor) {
		super(editor);
	}

	@Override
	public void run() {
		IUndoContext context = OperationFactory.getContext(file);
		History.getInstance().redo(context);
	}

}

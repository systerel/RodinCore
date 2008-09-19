package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

public class History {
	private static History singleton = null;
	final private IOperationHistory history;

	private History() {
		history = OperationHistoryFactory.getOperationHistory();
	}

	public static History getInstance() {
		if (singleton == null) {
			singleton = new History();
		}
		return singleton;
	}

	public void addOperation(AtomicOperation operation) {
		try {
			if(operation != null){
				history.execute(operation, null, null);
			}
		} catch (ExecutionException e) {
			// TODO traiter l'exception
			e.getCause().printStackTrace();
		}
	}

	public void redo(IUndoContext context) {
		try {
			history.redo(context, null, null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.getCause().printStackTrace();
		}
	}

	public void undo(IUndoContext context) {
		try {
			history.undo(context, null, null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.getCause().printStackTrace();
		}
	}
}
